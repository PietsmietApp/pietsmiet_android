package de.pscom.pietsmiet.model;


import java.util.ArrayList;
import java.util.List;

public class YoutubeRoot {

    private String kind;
    private String nextPageToken;
    private List<YoutubeItem> items = new ArrayList<>();

    /**
     * @return The kind
     */
    public String getKind() {
        return kind;
    }

    /**
     * @param kind The kind
     */
    public void setKind(String kind) {
        this.kind = kind;
    }

    /**
     * @return The nextPageToken
     */
    public String getNextPageToken() {
        return nextPageToken;
    }

    /**
     * @param nextPageToken The nextPageToken
     */
    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    /**
     * @return The items
     */
    public List<YoutubeItem> getItems() {
        return items;
    }

    /**
     * @param items The items
     */
    public void setItems(List<YoutubeItem> items) {
        this.items = items;
    }

}
