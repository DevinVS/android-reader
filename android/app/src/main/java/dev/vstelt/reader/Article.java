package dev.vstelt.reader;

import java.io.Serializable;

public class Article implements Serializable {
    public String title;
    public String content;
    public String preview;
    public boolean opened;
    public String published;

    public Article(String title, String content, boolean opened, String published) {
        this.title = title;
        this.content = content;
        this.opened = opened;
        this.published = published;
        this.preview = content
                .replaceAll("\\<.*?\\>", " ")
                .replaceAll("\n", " ")
                .replaceAll("&rsquo;", "'")
                .replaceAll("&ldquo;", "\"")
                .replaceAll("&rdquo;", "\"")
                .substring(0, 250);
    }
}
