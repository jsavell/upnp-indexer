package com.shadowater.upnpindexer.model.upnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "DIDL-Lite")
@JsonIgnoreProperties(ignoreUnknown = true)
public class VideoItems {
    @JacksonXmlProperty(localName = "item")
    @JacksonXmlElementWrapper(useWrapping = false)
    private VideoItem[] videoItems;

	public VideoItem[] getVideoItems() {
		return videoItems;
	}

	public void setMediaEntries(VideoItem[] mediaEntries) {
		this.videoItems = mediaEntries;
	}
}
