package com.shadowater.upnpindexer.model;

import java.util.Date;

public interface MediaI {
	public String getId();
	public String getTitle();
	public String getThumbnail();
	public String getDescription();
	public Date getReleaseDate();
/*	public String getCollection();*/
	public String getGenre();
}
