CREATE TABLE feeds(
    url             TEXT PRIMARY KEY,
    name            TEXT,
    description     TEXT,
    last_modified   TEXT
);

CREATE TABLE articles(
    feed_url    TEXT,
    title       TEXT,
    content     TEXT,
    opened      INTEGER,
    published   TEXT,
    FOREIGN KEY (feed_url) REFERENCES feeds(url)
);
