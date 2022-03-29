package dev.vstelt.reader;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class Reader {
    static {
        System.loadLibrary("reader");
        initLogging();
    }

    private static native void importFeed(String dbPath, String url);
    private static native Object[] getFeeds(String dbPath);
    private static native Object[] getArticles(String dbPath);
    private static native void sync(String dbPath);
    private static native void initLogging();

    public static void importFeed(Context ctx, String url) {
        String dbPath = ctx.getFilesDir() + "/reader.db";

        new Thread() {
            public void run() {
                importFeed(dbPath, url);
                populateFeeds(ctx);
                populateArticles(ctx);
            }
        }.start();
    }

    public static void populateArticles(Context ctx) {
        String dbPath = ctx.getFilesDir() + "/reader.db";

        new Thread() {
            public void run() {
                Article[] articles = (Article[]) getArticles(dbPath);
                Intent i = new Intent("ARTICLES_UPDATED");
                i.putExtra("ARTICLES", articles);
                ctx.sendBroadcast(i);
            }
        }.start();
    }

    public static void populateFeeds(Context ctx) {
        String dbPath = ctx.getFilesDir() + "/reader.db";

        new Thread() {
            public void run() {
                Feed[] feeds = (Feed[]) getFeeds(dbPath);
                Intent i = new Intent("FEEDS_UPDATED");
                i.putExtra("FEEDS", feeds);
                ctx.sendBroadcast(i);
            }
        }.start();
    }

    public static void sync(Context ctx) {
        String dbPath = ctx.getFilesDir() + "/reader.db";

        new Thread() {
            public void run() {
                sync(dbPath);
                populateArticles(ctx);
            }
        }.start();
    }
}
