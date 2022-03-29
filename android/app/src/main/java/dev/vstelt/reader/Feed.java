package dev.vstelt.reader;

import java.io.Serializable;

public class Feed implements Serializable {
    public String url;
    public String title;
    public String description;
    public String last_modified;

    public Feed(String url, String title, String description, String last_modified) {
        this.url = url;
        this.title = title;
        this.description = description;
        this.last_modified = last_modified;
    }
}
