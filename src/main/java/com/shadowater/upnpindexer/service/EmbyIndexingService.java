package com.shadowater.upnpindexer.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class EmbyIndexingService implements MediaIndexerI {
    @Value("${video.emby.apiKey}")
    private String apiKey;
    @Value("${video.emby.server}")
    private String serverUri;
    @Override
    public void startScheduledIndex() {
        // TODO Auto-generated method stub

    }

    @Override
    public void startManualIndex() {
        this.startIndex();
    }

    protected void startIndex() {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet movieGet = new HttpGet(serverUri+"Items?api_key="+apiKey+"&Recursive=true&IncludeItemTypes=Movie&Fields=Path&SortBy=Name&SortOrder=Ascending");
        try {
            CloseableHttpResponse response = httpClient.execute(movieGet);
            HttpEntity entity = response.getEntity();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode movieJson = mapper.readTree(entity.getContent());
            movieJson.get("Items").forEach(movieNode -> {
                System.out.println("title: "+movieNode.get("Name").toString());
                System.out.println("path: "+movieNode.get("Path").toString());
                System.out.println("---------------");
            });
            EntityUtils.consume(entity);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
