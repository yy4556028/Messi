package com.yuyang.messi.bean.riffsy;


public class RiffsyResultBean {

    private String[] tags;
    private String url;
    private RiffsyMediaBean[] media;
    private String created;
    private RiffsyMediaBean composite;
    private boolean hasaudio;
    private String title;
    private String id;

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RiffsyMediaBean[] getMedia() {
        return media;
    }

    public void setMedia(RiffsyMediaBean[] media) {
        this.media = media;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public RiffsyMediaBean getComposite() {
        return composite;
    }

    public void setComposite(RiffsyMediaBean composite) {
        this.composite = composite;
    }

    public boolean isHasaudio() {
        return hasaudio;
    }

    public void setHasaudio(boolean hasaudio) {
        this.hasaudio = hasaudio;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
