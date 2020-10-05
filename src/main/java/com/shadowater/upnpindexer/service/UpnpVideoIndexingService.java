package com.shadowater.upnpindexer.service;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shadowater.upnpindexer.model.MediaI;
import com.shadowater.upnpindexer.model.Video;

@Service
public class UpnpVideoIndexingService extends AbstractUpnpIndexingService {
    @Value("${video.indexer.objectId}")
    private String mediaObjectId;
    @Value("${video.indexer.batchSize}")
    private String mediaBatchSize;

    static Logger log = LoggerFactory.getLogger(UpnpVideoIndexingService.class.getName());

    @Override
    public String getMediaObjectId() {
        return mediaObjectId;
    }

    @Override
    public String getMediaBatchSize() {
        return mediaBatchSize;
    }

    @Override
    @Scheduled(cron = "${video.indexer.schedule.cron}")
    public void startScheduledIndex() {
        log.trace("Starting scheduled video index");
        try {
            this.indexUpnpMedia();
        } catch (JsonProcessingException | URISyntaxException e) {
            log.error("Error with scheduled video index");
            e.printStackTrace();
        }
    }

    @Override
    public void startManualIndex() {
        log.trace("Starting manual video index");
        try {
            this.indexUpnpMedia();
        } catch (JsonProcessingException | URISyntaxException e) {
            log.error("Error with manual video index");
            e.printStackTrace();
        }

    }

    @Override
    protected MediaI buildMediaFromXml(Node nNode) {
        Element eElement = (Element) nNode;

        String id = nNode.getAttributes().item(0).getTextContent();

        String title = eElement.getElementsByTagName("dc:title").item(0).getTextContent();
        String thumbnail = eElement.getElementsByTagName("upnp:albumArtURI").item(0).getTextContent();
        String description = eElement.getElementsByTagName("upnp:longDescription").item(0).getTextContent();
        Date releaseDate = new Date();
        try {
            releaseDate = new SimpleDateFormat("yyyy-MM-dd").parse(eElement.getElementsByTagName("dc:date").item(0).getTextContent());
        } catch (DOMException | ParseException e) {
            log.error("Error parsing item from XML");
            e.printStackTrace();
        }
        String genre = "";
        if (eElement.getElementsByTagName("upnp:genre").item(0) != null) {
            genre = eElement.getElementsByTagName("upnp:genre").item(0).getTextContent();
        }

        NodeList resNodes = eElement.getElementsByTagName("res");
        List<String> videoFormats = Arrays.asList("mp4","mkv");
        StringBuilder videoUrlBuilder = new StringBuilder();
        for (int x=0;x<resNodes.getLength();x++) {
            Node resNode = resNodes.item(x);
            NamedNodeMap resAttributes = resNode.getAttributes();
            for (int y=0;y<resAttributes.getLength();y++) {
                Node resAttributeNode = resAttributes.item(y);
                videoFormats.forEach(f -> {
                    if (videoUrlBuilder.length() == 0 && resAttributeNode.getTextContent().contains(f)) {
                        videoUrlBuilder.append(resNode.getTextContent());
                    }
                });
            }
        }
        return new Video(id, title, thumbnail, description, releaseDate, genre, videoUrlBuilder.toString());
    }
}
