#![allow(non_snake_case)]

mod db;

use std::path::Path;
use std::ffi::CStr;
use db::{Feed, Article};
use log::{error, info};
use jni::{JNIEnv, objects::{JObject, JString}};

use jni::sys::jobjectArray;
use jni::objects::JValue;

// Convert a java string to a rust string
unsafe fn convert_string(env: &JNIEnv, s: JString) -> String {
    CStr::from_ptr(
        env.get_string(s).unwrap().as_ptr()
    ).to_str()
    .unwrap()
    .to_string()
}

#[no_mangle]
pub unsafe extern fn Java_dev_vstelt_reader_Reader_initLogging(_: JNIEnv, _: JObject) {
    android_log::init("Reader").unwrap();
}

// import a feed including all of its articles
#[no_mangle]
pub unsafe extern fn Java_dev_vstelt_reader_Reader_importFeed(env: JNIEnv, _: JObject, j_db: JString, j_url: JString) {
    info!("Start Importing Feed");

    let db = convert_string(&env, j_db);
    let url = convert_string(&env, j_url);

    match db::import_feed(&Path::new(&db), &url) {
        Ok(_) => {},
        Err(e) => error!("{e}")
    }
}

// Get all the feeds
#[no_mangle]
pub unsafe extern fn Java_dev_vstelt_reader_Reader_getFeeds(env: JNIEnv, _: JObject, j_db: JString) -> jobjectArray {

    info!("Start Getting Feeds");

    let db = convert_string(&env, j_db);
    let feeds = db::get_feeds(&Path::new(&db));

    let class = env.find_class("dev/vstelt/reader/Feed").unwrap();

    if let Ok(feeds) = feeds {
        let arr = env.new_object_array(feeds.len() as i32, class, JObject::null()).unwrap();

        for (i, feed) in feeds.iter().enumerate() {
            let f = feed_to_JFeed(&env, feed);
            env.set_object_array_element(arr, i as i32, f).unwrap();
        }

        arr
    } else {
        error!("{}", feeds.err().unwrap());
        env.new_object_array(0, class, JObject::null()).unwrap()
    }
}

// Get the articles
#[no_mangle]
pub unsafe extern fn Java_dev_vstelt_reader_Reader_getArticles(env: JNIEnv, _: JObject, j_db: JString) -> jobjectArray {

    info!("Start Getting Articles");

    let db = convert_string(&env, j_db);
    let articles = db::get_articles(&Path::new(&db));

    let class = env.find_class("dev/vstelt/reader/Article").unwrap();

    if let Ok(articles) = articles {
        let arr = env.new_object_array(articles.len() as i32, class, JObject::null()).unwrap();

        for (i, article) in articles.iter().enumerate() {
            let a = article_to_JArticle(&env, &article);
            env.set_object_array_element(arr, i as i32, a).unwrap();
        }

        arr
    } else {
        error!("{}", articles.err().unwrap());
        env.new_object_array(0, class, JObject::null()).unwrap()
    }

}

// Sync the database with the internet
#[no_mangle]
pub unsafe extern fn Java_dev_vstelt_reader_Reader_sync(env: JNIEnv, _: JObject, j_db: JString) {

    info!("Syncing the database");

    let db = convert_string(&env, j_db);
    match db::sync_feeds(&Path::new(&db)) {
        Ok(_) => {}
        Err(e) => {
            error!("{e}");
        }
    }
}

// Convert a Feed struct to a Java Feed Object
pub unsafe fn feed_to_JFeed<'e>(env: &'e JNIEnv, feed: &Feed) -> JObject<'e> {
    let class = env.find_class("dev/vstelt/reader/Feed").unwrap();
    let args: [JValue<'e>; 4] = [
        JValue::Object(JObject::from(env.new_string(feed.url.clone()).unwrap())),
        JValue::Object(JObject::from(env.new_string(feed.name.clone()).unwrap())),
        JValue::Object(JObject::from(env.new_string(feed.description.clone()).unwrap())),
        JValue::Object(JObject::from(env.new_string(feed.last_modified.clone()).unwrap()))
    ];

    env.new_object(
        class,
        "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
        &args
    ).unwrap()
}

// Convert a Article struct to a Java Article Object
pub unsafe fn article_to_JArticle<'e>(env: &'e JNIEnv, article: &Article) -> JObject<'e> {
    let class = env.find_class("dev/vstelt/reader/Article").unwrap();

    let args: [JValue<'e>; 4] = [
        JValue::Object(JObject::from(env.new_string(article.title.clone()).unwrap())),
        JValue::Object(JObject::from(env.new_string(article.content.clone()).unwrap())),
        JValue::Bool(article.opened as u8),
        JValue::Object(JObject::from(env.new_string(article.published.format("%F").to_string()).unwrap()))
    ];

    env.new_object(
        class,
        "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;)V",
        &args
    ).unwrap()
}
