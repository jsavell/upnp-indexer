package com.shadowater.upnpindexer.model.upnp;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoItem {
	@JacksonXmlProperty(isAttribute=true)
	private String id;
	@JacksonXmlProperty(localName="title")
	private String title;
	@JacksonXmlProperty(localName="longDescription")
	private String description;	
	@JacksonXmlProperty(localName="albumArtURI")
	@JacksonXmlElementWrapper(useWrapping = false)
	private AlbumArtData albumArtData;	
	@JacksonXmlProperty(localName="date")
	private Date releaseDate;
	@JacksonXmlProperty(localName="genre")
	private String genre;

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}

	public AlbumArtData getAlbumArtData() {
		return albumArtData;
	}

	public Date getReleaseDate() {
		return releaseDate;
	}

	public String getGenre() {
		return genre;
	}
}
