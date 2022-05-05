package com.gagsn.db.service;

import com.gagsn.db.log.LogParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.util.StreamUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolrDeleteService {

    private final RestTemplate restTemplate;
    private final JdbcTemplate jdbcTemplate;
    private final LogParser logParser;
    private boolean finished = false;
    private int count = 0;
    private int iteration = 0;
    private List<String> allIds = new ArrayList<>();

    @SneakyThrows
    public void delete() {
        /*List<String> solrIds = findSolrIds();
        System.out.println(solrIds);*/

        List<String> ids = logParser.parse("file/solr_2.txt");

        int start = 0;
        for (String id : ids) {
            int end = start + 10;
            System.out.println("Start: " + start + " End: " + end);
            deleteFromDB(ids.subList(start, end));
            System.out.println("updated");
            start = end;
        }

        System.out.println("#### DONE!");

    }

    @SneakyThrows
    public void deleteRepoItem() {
        int start = 0;
        var solrIds = findSolrIds();
        System.out.println("SIZE: " + solrIds.size());

        var objects = new ArrayList<String>();
        for (int i = 0; i <= solrIds.size(); i++) {
            String id = solrIds.get(i-1);
            objects.add(id);
            System.out.println(i + " DELETING: " + id);
            if (i % 25 == 0) {
                var b = deleteRepoItem(objects);
                System.out.println();
                System.out.println("EXECUTED: " + Arrays.toString(b));
                System.out.println();
                objects.clear();
            }
        }
        System.out.println("#### DONE!");

    }


    private int deleteFromDB(List<String> ids) {
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        String newSql = String.format("delete ContentRepos.Packs where ID in (select p.id from ContentRepos.Packs p inner join ContentRepos.PackEntries pe on p.ID = pe.packId inner join ContentRepos.Repository r on pe.repositoryId = r.id where r.documentId in (%s))", inSql);

        System.out.println(newSql);

        return jdbcTemplate.update(newSql, ids.toArray());
    }

    private int[] deleteRepoItem(List<String> id) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (String s : id) {
            batchArgs.add(new Object[]{s});
        }
        String query = "delete ContentRepos.Repository where documentId = ?";
        return jdbcTemplate.batchUpdate(query, batchArgs);
    }

    private int[] deleteRepoItem1(List<String> id) {
        List<Object[]> batchArgs = new ArrayList<>();
        for (String s : id) {
            batchArgs.add(new Object[]{s});
        }
        String query = "delete ContentRepos.Repository where documentId = ?";
        return jdbcTemplate.batchUpdate(query, batchArgs);
    }


    private String deleteFromSolr(String id) {
        System.out.println("Deleting " + id + " from solr");
        return restTemplate.getForObject("http://li-prd-solr-master:8080/solr/PRD_Legal_Master/update?commit=true&stream.body=<delete><query>ID:" + id + "</query></delete>", String.class);
    }

    private List<String> findSolrIds() {
        List<String> ids = new ArrayList<>();
        String docs = findDocs();

        final org.jsoup.nodes.Document xmlDoc = Jsoup.parse(docs);
        Elements doc = xmlDoc.select("doc");

        for (Element element : doc) {
            String id = element.getElementsByAttributeValue("name", "ID").text();
            ids.add(id);
        }

        return ids;
    }

    private String findDocs() {
        return restTemplate.getForObject("http://li-prd-solr-master:8080/solr/PRD_Legal_Master/select?indent=on&q=AccessCode:Internal_132_NW AND DocumentVisibility:DEFAULT&wt=xml&rows=100000000", String.class);
    }
}
