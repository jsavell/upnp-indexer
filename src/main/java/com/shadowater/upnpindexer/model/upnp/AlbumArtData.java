package com.shadowater.upnpindexer.model.upnp;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlbumArtData {
	@JacksonXmlProperty(isAttribute=true)	
	private String profileID;
	@JacksonXmlText
	private URI albumArtURI;
	
	public String getProfileID() {
		return profileID;
	}
	public URI getAlbumArtURI() {
		return albumArtURI;
	}
}
