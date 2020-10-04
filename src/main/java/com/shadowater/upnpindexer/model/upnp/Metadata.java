package com.shadowater.upnpindexer.model.upnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {
	@JacksonXmlProperty(isAttribute=true)	
	private String childCount;
	
	public String getChildCount() {
		return childCount;
	}
}
