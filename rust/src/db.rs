use std::path::Path;
use std::error::Error;

use chrono::{DateTime, FixedOffset};
use rss::Channel;
use sqlite::{Connection, State};
use log::info;

pub struct Feed {
    pub url: String,
    pub name: String,
    pub description: String,
    pub last_modified: String
}

pub struct Article {
    pub title: String,
    pub content: String,
    pub opened: bool,
    pub published: DateTime<FixedOffset>
}

// Create database along with the up to date schema
fn create_database(path: &Path) -> Result<Connection, Box<dyn Error>> {
    info!("Creating Database: {}", path.to_str().unwrap());
    let conn = sqlite::open(path)?;

    conn.execute(include_str!("./sql/init_db.sql"))?;

    Ok(conn)
}

// Create database if it doesn't exist, then connect
fn connect(path: &Path) -> Result<Connection, Box<dyn Error>> {
    info!("Connecting to Database");
    if !path.exists() {
        Ok(create_database(path)?)
    } else {
        Ok(sqlite::open(path)?)
    }
}

// Add a feed to the database
fn add_feed(conn: &Connection, url: &str, name: &str, description: &str, last_modified: &str) -> Result<(), Box<dyn Error>> {
    info!("Adding Feed: {url}");
    let mut stmt = conn.prepare(include_str!("./sql/create_feed.sql"))?;

    stmt.bind(1, url)?;
    stmt.bind(2, name)?;
    stmt.bind(3, description)?;
    stmt.bind(4, last_modified)?;

    while let Ok(state) = stmt.next() {
        if state==State::Done { break; }
    }

    Ok(())
}

// Add an article to the database
fn add_article(conn: &Connection, feed_url: &str, title: &str, content: &str, opened: bool, published: &str) -> Result<(), Box<dyn Error>> {
    info!("Adding Article: {title}");
    let mut stmt = conn.prepare(include_str!("./sql/create_article.sql"))?;

    stmt.bind(1, feed_url)?;
    stmt.bind(2, title)?;
    stmt.bind(3, content)?;
    stmt.bind(4, if opened { 1 } else {0})?;
    stmt.bind(5, published)?;

    while let Ok(state) = stmt.next() {
        if state==State::Done { break; }
    }

    Ok(())
}

// Download a feed and parse it
fn download_feed(url: &str) -> Result<Channel, Box<dyn Error>> {
    info!("Downloading Feed: {url}");
    let res = reqwest::blocking::get(url)?.bytes()?;
    Ok(Channel::read_from(&res[..])?)
}

// Import a feed and its articles into the database
pub fn import_feed(db_url: &Path, url: &str) -> Result<(), Box<dyn Error>> {
    info!("Importing Feed: {url}");
    let channel = download_feed(url)?;

    let title = channel.title();
    let description = channel.description();
    let last_modified = channel.last_build_date().unwrap();

    let conn = connect(db_url)?;
    add_feed(&conn, url, title, description, last_modified)?;

    for item in channel.items() {
        add_article(
            &conn,
            url,
            item.title().unwrap_or(""),
            item.description().unwrap_or(""),
            true,
            item.pub_date().unwrap()
        )?;
    }

    Ok(())
}

// Update the last_modified time for a feed
fn update_modified(
    conn: &Connection,
    url: &str,
    date: &str
) -> Result<(), Box<dyn Error>> {
    let mut stmt = conn.prepare(include_str!("./sql/update_modified.sql"))?;

    stmt.bind(1, date)?;
    stmt.bind(2, url)?;

    while let Ok(state) = stmt.next() {
        if state==State::Done { break; }
    }

    Ok(())
}

// Sync all feeds with the internet, saving new articles to the database
pub fn sync_feeds(db_url: &Path) -> Result<(), Box<dyn Error>> {
    info!("Syncing Feeds with Database");
    let conn = connect(db_url)?;

    let mut cursor = conn
        .prepare(include_str!("./sql/get_feeds.sql"))?
        .into_cursor();

    // For each feed we are watching
    while let Some(row) = cursor.next()? {
        let url = row[0].as_string().unwrap();
        let channel = download_feed(&url)?;
        let last_modified = row[3].as_string().unwrap();

        let old_date = DateTime::parse_from_rfc2822(&last_modified)?;
        let new_date = DateTime::parse_from_rfc2822(&channel.last_build_date().unwrap())?;

        // If the last time we checked it is before its last modifications
        if old_date != new_date {
            // Check for items created after the last time we checked it
            for item in channel.items() {
                let updated = DateTime::parse_from_rfc2822(item.pub_date().unwrap())?;
                if updated > old_date {
                    add_article(&conn, url, item.title().unwrap(), item.description().unwrap(), false, item.pub_date().unwrap())?;
                }
            }

            update_modified(&conn, url, channel.last_build_date().unwrap())?;
        }
    }

    Ok(())
}

pub fn get_feeds(db_url: &Path) -> Result<Vec<Feed>, Box<dyn Error>> {
    info!("Retrieving Feeds");
    let mut feeds = Vec::new();

    let conn = connect(db_url)?;

    let mut cursor = conn
        .prepare(include_str!("./sql/get_feeds.sql"))?
        .into_cursor();

    while let Some(row) = cursor.next()? {
        let url = row[0].as_string().unwrap().to_string();
        let name = row[1].as_string().unwrap().to_string();
        let description = row[2].as_string().unwrap().to_string();
        let last_modified = row[3].as_string().unwrap().to_string();

        feeds.push(Feed {
            url,
            name,
            description,
            last_modified
        });
    }

    Ok(feeds)
}

pub fn get_articles(db_url: &Path) -> Result<Vec<Article>, Box<dyn Error>> {
    info!("Retrieving Articles");
    let mut articles = Vec::new();

    let conn = connect(db_url)?;

    let mut cursor = conn
        .prepare(include_str!("./sql/get_articles.sql"))?
        .into_cursor();

    while let Some(row) = cursor.next()? {
        let title = row[1].as_string().unwrap().to_string();
        let content = row[2].as_string().unwrap().to_string();
        let opened = row[3].as_integer().unwrap() == 1;
        let published = row[4].as_string().unwrap().to_string();

        articles.push(Article {
            title,
            content,
            opened,
            published: DateTime::parse_from_rfc2822(&published).unwrap()
        });
    }

    articles.sort_by_key(|a| a.published);
    articles.reverse();

    Ok(articles)
}
