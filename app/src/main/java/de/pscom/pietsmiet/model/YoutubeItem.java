package de.pscom.pietsmiet.model;


public class YoutubeItem {

    private String kind;
    private String id;
    private YoutubeSnippet snippet;
    private YoutubeContent contentDetails;

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
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
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

    public YoutubeContent getContentDetails() {
        return contentDetails;
    }

    public void setContentDetails(YoutubeContent contentDetails) {
        this.contentDetails = contentDetails;
    }
}
