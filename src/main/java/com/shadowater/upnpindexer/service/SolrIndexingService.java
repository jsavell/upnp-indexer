package com.shadowater.upnpindexer.service;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.shadowater.upnpindexer.model.MediaI;

@Service
public class SolrIndexingService {
	@Value("#{'${solr.url}'+'/'+'${solr.core}'+'/'}")
	private String solrUrl;
	static Logger log = LoggerFactory.getLogger(SolrIndexingService.class.getName());

	public void writeMedia(List<MediaI> media) throws SolrServerException, IOException {
    	log.trace("Connecting to SOLR server: "+solrUrl);
		SolrClient solrClient = new HttpSolrClient.Builder(solrUrl).build();
		log.trace("Writing "+media.size()+" media entries to SOLR:");
		media.forEach(m -> {
        	log.trace("Indexing: "+m.getTitle());
			SolrInputDocument sid = new SolrInputDocument();
			sid.addField("id", m.getId());
			sid.addField("title_ss", m.getTitle());
			sid.addField("description_ss", m.getDescription());
			sid.addField("thumbnail_ss", m.getThumbnail().toString());
			sid.addField("releaseDate_ss", m.getReleaseDate());
			sid.addField("genre_ss", m.getGenre());
			sid.addField("url_ss", m.getUrl());
			try {
				solrClient.add(sid);
			} catch (SolrServerException | IOException e) {
				log.error("Error adding movie to SOLRInputDocument: "+m.getTitle());
				e.printStackTrace();
			}
		});
		solrClient.commit();
	}
}
