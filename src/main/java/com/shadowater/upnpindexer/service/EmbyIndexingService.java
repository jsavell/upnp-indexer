package com.shadowater.upnpindexer.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.DOMException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shadowater.upnpindexer.model.MediaI;
import com.shadowater.upnpindexer.model.Video;

@Service
public class EmbyIndexingService implements MediaIndexerI {
    @Value("${video.emby.apiKey}")
    private String apiKey;
    @Value("${video.emby.server}")
    private String serverUri;
    @Autowired
    private SolrIndexingService solrIndexingService;
    static Logger log = LoggerFactory.getLogger(UpnpVideoIndexingService.class.getName());


    @Override
    @Scheduled(cron = "${video.indexer.schedule.cron}")
    public void startScheduledIndex() {
        // TODO Auto-generated method stub

    }

    @Override
    public void startManualIndex() {
        this.startIndex();
    }

    protected void startIndex() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet movieGet = new HttpGet(serverUri+"Items?api_key="+apiKey+"&Recursive=true&IncludeItemTypes=Movie&Fields=Path,Overview,PremiereDate,Genres,MediaStreams,PrimaryImageAspectRatio&SortBy=Name&SortOrder=Ascending");
        try {
            CloseableHttpResponse response = httpClient.execute(movieGet);
            HttpEntity entity = response.getEntity();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode movieJson = mapper.readTree(entity.getContent());
            List<MediaI> videos = new ArrayList<MediaI>();
            Map<String,Integer> groupSize = new HashMap<String,Integer>();
            movieJson.get("Items").forEach(movieNode -> {
                String groupName = movieNode.get("Name").asText();
                if (!groupSize.containsKey(groupName)) {
                    groupSize.put(groupName, 1);
                } else {
                    groupSize.put(groupName, groupSize.get(groupName)+1);
                }
            });

            movieJson.get("Items").forEach(movieNode -> {
                final String id = movieNode.get("Id").asText();
                final String thumbnail = serverUri+"Items/"+movieNode.get("Id").asText()+"/Images/Primary?maxWidth=107&tag=39740e29ca688f2aa5244b951e0f0ac9&quality=90";
                final String description = movieNode.has("Overview") ? movieNode.get("Overview").asText():"";
                final String genre = movieNode.get("Genres").get(0) != null ? movieNode.get("Genres").get(0).asText():"";
                final String path = movieNode.get("Path").asText();

                final String embyName = movieNode.get("Name").asText();

                final String group = embyName;
                String title = group;
                if (groupSize.get(group) > 1) {
                    String[] pathParts = path.split("/");
                    String rawTitleSuffix = pathParts[pathParts.length-1];
                    rawTitleSuffix = rawTitleSuffix.substring(0, rawTitleSuffix.length()-4);
                    if (rawTitleSuffix.contains("-")) {
                        String[] titleSuffixParts = rawTitleSuffix.split("-");
                        title += " -"+titleSuffixParts[titleSuffixParts.length-1];
                    } else {
                        title += " - "+movieNode.get("MediaStreams").get(0).get("DisplayTitle").asText();
                    }
                }
                Date releaseDate = null;
                if (movieNode.has("PremiereDate")) {
                    String date = movieNode.get("PremiereDate").asText();
                    releaseDate = new Date();
                    String datePattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSXXX";
                    try {
                        releaseDate = new SimpleDateFormat(datePattern).parse(date);
                    } catch (DOMException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                final Integer width = movieNode.get("MediaStreams").get(0).get("Width").asInt();
                final Integer height = movieNode.get("MediaStreams").get(0).get("Height").asInt();
                String quality = "HD";
                if (width < 1200 && height < 720) {
                    quality = "SD";
                }
                videos.add(new Video(id, title, thumbnail, description, releaseDate, genre, path, quality, group));
                log.debug("title: "+title);
                log.debug("path: "+path);
                log.debug("group: "+group);
                log.debug("---------------");
            });
            EntityUtils.consume(entity);
            solrIndexingService.writeMedia(videos);
        } catch (IOException | SolrServerException e) {
            e.printStackTrace();
        }
    }
}
