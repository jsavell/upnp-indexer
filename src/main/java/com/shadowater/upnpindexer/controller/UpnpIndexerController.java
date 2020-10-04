package com.shadowater.upnpindexer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shadowater.upnpindexer.service.UpnpVideoIndexingService;

@RestController
@RequestMapping("upnp-indexer")
public class UpnpIndexerController {
	@Autowired
	UpnpVideoIndexingService upnpVideoIndexingService;
	static Logger log = LoggerFactory.getLogger(UpnpIndexerController.class.getName());

	@GetMapping("start-video-index")
	public String indexVideos() {
		log.trace("Triggering video index from API");
		Thread newThread = new Thread(() -> {
			upnpVideoIndexingService.startManualIndex();
		});
		newThread.start();
		return "Started manual Video Index";
	}
}
