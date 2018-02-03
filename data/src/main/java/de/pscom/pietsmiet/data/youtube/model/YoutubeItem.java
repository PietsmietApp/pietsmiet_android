package de.pscom.pietsmiet.data.youtube.model;


public class YoutubeItem {

    private String kind;
    private YoutubeId id;
    private YoutubeSnippet snippet;
    private String etag;

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
     * @return The id
     */
    public YoutubeId getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(YoutubeId id) {
        this.id = id;
    }

    /**
     * @return The snippet
     */
    public YoutubeSnippet getSnippet() {
        return snippet;
    }

    public void setSnippet(YoutubeSnippet snippet) {
        this.snippet = snippet;
    }
}
