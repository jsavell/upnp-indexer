package com.shadowater.upnpindexer.model.upnp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "DIDL-Lite")
@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataEntries {
    @JacksonXmlProperty(localName = "container")
    @JacksonXmlElementWrapper(useWrapping = false)
    private Metadata[] metadataEntries;

	public Metadata[] getMetadataEntries() {
		return metadataEntries;
	}
}
