package com.shadowater.upnpindexer.model;

import java.util.Date;

public class Video implements MediaI {
    private String id;
    private String title;
    private String thumbnail;
    private String description;
    private Date releaseDate;
    private String genre;
    private String playBackId;
    private String quality;
    private String group;

    public Video(String id, String title, String thumbnail, String description, Date releaseDate, String genre, String playBackId, String quality, String group) {
        setId(id);
        setTitle(title);
        setThumbnail(thumbnail);
        setDescription(description);
        setReleaseDate(releaseDate);
        setGenre(genre);
        setPlayBackId(playBackId);
        setQuality(quality);
        setGroup(group);
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Override
    public String getPlayBackId() {
        return playBackId;
    }

    public void setPlayBackId(String playBackId) {
        this.playBackId = playBackId;
    }

    @Override
    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    /*
	public static Video fromVideoItem(VideoItem videoItem) {
		return new Video(videoItem.getId(), videoItem.getTitle(), videoItem.getAlbumArtData().getAlbumArtURI(), videoItem.getDescription(), videoItem.getReleaseDate(), videoItem.getGenre());
	}*/
}
