package com.shadowater.upnpindexer.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.solr.client.solrj.SolrServerException;
import org.jupnp.DefaultUpnpServiceConfiguration;
import org.jupnp.UpnpServiceImpl;
import org.jupnp.controlpoint.ActionCallback;
import org.jupnp.model.action.ActionArgumentValue;
import org.jupnp.model.action.ActionInvocation;
import org.jupnp.model.meta.Action;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.model.meta.RemoteService;
import org.jupnp.model.types.UDAServiceId;
import org.jupnp.model.types.UnsignedIntegerFourBytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.shadowater.upnpindexer.model.Device;
import com.shadowater.upnpindexer.model.MediaI;
import com.shadowater.upnpindexer.model.upnp.MetadataEntries;

@Service
public abstract class AbstractUpnpIndexingService implements MediaIndexerI {
    @Autowired
    private SolrIndexingService solrIndexingService;
    private UpnpServiceImpl upnpService;

    static Logger log = LoggerFactory.getLogger(UpnpVideoIndexingService.class.getName());

    @Override
    public abstract void startScheduledIndex();
    @Override
    public abstract void startManualIndex();
    protected abstract MediaI buildMediaFromXml(Node nNode);
    protected abstract String getMediaObjectId();
    protected abstract String getMediaBatchSize();


    protected void writeMediaToSolr(List<MediaI> media) throws SolrServerException, IOException {
        solrIndexingService.writeMedia(media);
    }

    protected ActionInvocation takeAction(RemoteService remoteService, String action, Map<String,String> aiInput) {

        Action<RemoteService> browseAction = remoteService.getAction(action);

        ActionInvocation ai = new ActionInvocation(browseAction);
        aiInput.forEach((k,v) -> {
            ai.setInput(k,v);
        });
        new ActionCallback.Default(ai, upnpService.getControlPoint()).run();
        return ai;
    }

    protected void indexUpnpMedia() throws URISyntaxException, JsonMappingException, JsonProcessingException {
        DefaultUpnpServiceConfiguration config = new DefaultUpnpServiceConfiguration();
        upnpService = new UpnpServiceImpl(new DefaultUpnpServiceConfiguration());
        upnpService.startup();
        log.trace("Waiting 10 seconds before shutting down UPNP listener...");
        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
        }
        List<Device> devices = new ArrayList<Device>();

        Optional<RemoteDevice> potentialMediaServer = upnpService.getRegistry().getRemoteDevices().stream().filter(d -> d.getType().getType().contentEquals("MediaServer")).findFirst();

        if (potentialMediaServer.isPresent()) {
            RemoteDevice mediaServer = potentialMediaServer.get();
            log.trace("Found Media Server: "+mediaServer.getDisplayString());
            RemoteService remoteService = mediaServer.findService(new UDAServiceId("ContentDirectory"));

            Map<String, String> aiInput = new HashMap<String,String>();
            //TODO Provide an API mechanism for listing and configuring the ObjectId for indexing
            aiInput.put("ObjectID",getMediaObjectId());
            aiInput.put("BrowseFlag","BrowseMetadata");
            aiInput.put("StartingIndex","0");
            aiInput.put("RequestedCount", "50");
            ActionInvocation ai = takeAction(remoteService, "Browse", aiInput);

            ActionArgumentValue result = ai.getOutput("Result");

            XmlMapper xmlMapper = new XmlMapper();
            MetadataEntries me = xmlMapper.readValue(result.getValue().toString(), MetadataEntries.class);
            Integer mediaCount = Integer.valueOf(me.getMetadataEntries()[0].getChildCount());
            log.trace("Received "+mediaCount+" entries");

            try {
                writeMediaToSolr(getMedia(remoteService, 0L, mediaCount, new ArrayList<MediaI>()));
            } catch (SolrServerException | IOException | InterruptedException e) {
                log.error("Error writing to SOLR");
                e.printStackTrace();
            }
        } else {

        }
        upnpService.shutdown();
    }

    protected NodeList getContainerMediaEntriesById(RemoteService remoteService, String id) throws ParserConfigurationException, SAXException, IOException {
        Map<String, String> aiInput = new HashMap<String,String>();
        aiInput = new HashMap<String,String>();
        aiInput.put("ObjectID",id);
        aiInput.put("BrowseFlag","BrowseDirectChildren");
        aiInput.put("StartingIndex","0");
        aiInput.put("RequestedCount", "50");
        ActionInvocation<?> ai = takeAction(remoteService, "Browse", aiInput);

        ActionArgumentValue<?> result = ai.getOutput("Result");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        builder = factory.newDocumentBuilder();
        log.trace("Getting container media entries by id: "+id);
        Document doc = builder.parse(new InputSource(new StringReader(result.getValue().toString())));
        return doc.getElementsByTagName("item");
    }

    protected List<MediaI> getMedia(RemoteService remoteService, Long startWith, Integer mediaCount, List<MediaI> mediaEntries) throws JsonMappingException, JsonProcessingException, InterruptedException {
        Map<String, String> aiInput = new HashMap<String,String>();
        aiInput = new HashMap<String,String>();
        aiInput.put("ObjectID",getMediaObjectId());
        aiInput.put("BrowseFlag","BrowseDirectChildren");
        aiInput.put("StartingIndex",startWith.toString());
        aiInput.put("RequestedCount", getMediaBatchSize());
        ActionInvocation<?> ai = takeAction(remoteService, "Browse", aiInput);

        ActionArgumentValue<?> result = ai.getOutput("Result");

        //TODO Get XmlObjectMapper to quit skipping item entries so we don't have to brute parse
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            try {
                Document doc = builder.parse(new InputSource(new StringReader(result.getValue().toString())));

                //we have to do some extra work when we have multiple instances of the same movie
                NodeList containers = doc.getElementsByTagName("container");
                for (int x = 0; x < containers.getLength(); x++) {
                    Node nNode = containers.item(x);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        NodeList cNodeList = getContainerMediaEntriesById(remoteService, nNode.getAttributes().item(0).getTextContent());
                        for (int y = 0; y < cNodeList.getLength(); y++) {
                            Node cNode = cNodeList.item(y);
                            mediaEntries.add(buildMediaFromXml(cNode));
                        }
                    }
                }

                NodeList items = doc.getElementsByTagName("item");
                for (int x = 0; x < items.getLength(); x++) {
                    Node nNode = items.item(x);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        mediaEntries.add(buildMediaFromXml(nNode));
                    }
                }


            } catch (SAXException | IOException e) {
                log.error("Error parsing items from XML");
                e.printStackTrace();
            }
        } catch (ParserConfigurationException e1) {
            log.error("Error building XML parser");
            e1.printStackTrace();
        }

        Map<String,Long> actionResults = new HashMap<String,Long>();
        for (ActionArgumentValue<?> aav : ai.getOutput()) {
            if (aav.getDatatype().toString().contentEquals("(UnsignedIntegerFourBytesDatatype)")) {
                UnsignedIntegerFourBytes value = (UnsignedIntegerFourBytes) aav.getValue();
                actionResults.put(aav.getArgument().getName(), value.getValue());
            }
        }
        Long batchSize = actionResults.get("NumberReturned");

        log.trace("Received "+batchSize+" movie(s) in batch");
        Long nextStartingPoint = startWith+batchSize;
        log.trace("Starting next batch at: "+nextStartingPoint);
        if (nextStartingPoint < mediaCount) {
            getMedia(remoteService, nextStartingPoint, mediaCount, mediaEntries);
        }
        return mediaEntries;
    }
}
