package com.gagsn.db.config;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class WebConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        return builder
                .setConnectTimeout(Duration.ofMillis(900000000))
                .setReadTimeout(Duration.ofMillis(900000000))
                .build();
    }


    @Bean
    public SolrClient solrClient(){
        final String solrUrl = "http://li-prd-solr-master:8080/solr/#/PRD_Legal_Master";
        return new HttpSolrClient.Builder(solrUrl)
                .build();
    }

}
