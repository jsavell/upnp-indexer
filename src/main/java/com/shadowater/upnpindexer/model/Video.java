package com.shadowater.upnpindexer.model;

import java.util.Date;

public class Video implements MediaI {
	private String id;
	private String title;
	private String thumbnail;
	private String description;
	private Date releaseDate;
	private String genre;

	public Video(String id, String title, String thumbnail, String description, Date releaseDate, String genre) {
		setId(id);
		setTitle(title);
		setThumbnail(thumbnail);
		setDescription(description);
		setReleaseDate(releaseDate);
		setGenre(genre);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Date releaseDate) {
		this.releaseDate = releaseDate;
	}
	public String getGenre() {
		return genre;
	}
	public void setGenre(String genre) {
		this.genre = genre;
	}
/*
	public static Video fromVideoItem(VideoItem videoItem) {
		return new Video(videoItem.getId(), videoItem.getTitle(), videoItem.getAlbumArtData().getAlbumArtURI(), videoItem.getDescription(), videoItem.getReleaseDate(), videoItem.getGenre());
	}*/
}
