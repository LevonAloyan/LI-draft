package com.gagsn.db.service;

import com.gagsn.db.dto.PublicationTitle;
import com.gagsn.db.dto.mapper.PublicationTitleMapper;
import com.gagsn.db.log.Document;
import com.gagsn.db.log.LogSearcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.MapSolrParams;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class DbUpdaterService {

    private final JdbcTemplate jdbcTemplate;
    private final LogSearcher logSearcher;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private SolrClient solrClient;

    public void updateRecords() {

        List<Document> documents = logSearcher.search();

        int successCounter = 0;
        int doNotUpdatedCounter = 0;
        int failedCounter = 0;
        List<Document> failed = new ArrayList<>();
        for (Document document : documents) {

            int updated = 0;
            try {
                updated += jdbcTemplate.update("Update DossierDoc Set DocumentID=? WHERE DocumentID=?",
                        document.getFinalId(),
                        document.getDraftId());
                updated += jdbcTemplate.update("Update DossierNote Set DocumentID=? WHERE DocumentID=?",
                        document.getFinalId(),
                        document.getDraftId());
                updated += jdbcTemplate.update("Update DocumentBookmark Set DocumentId=? WHERE DocumentId=?",
                        document.getFinalId(),
                        document.getDraftId());
                updated += jdbcTemplate.update("Update LibraryCatalog Set DocumentId=? WHERE DocumentId=?",
                        document.getFinalId(),
                        document.getDraftId());
                updated += jdbcTemplate.update("Update UploadedDocument Set DocumentID=? WHERE DocumentID=?",
                        document.getFinalId(),
                        document.getDraftId());
            } catch (Exception e) {
                failedCounter++;
                log.error("FailedCounter: {}", failedCounter);
                log.error("Failed doc with FinalId {} DraftId {}", document.getFinalId(), document.getDraftId());
                failed.add(document);
            }
            if (updated != 0) {
                successCounter++;
                log.info("SuccessCounter: {}", successCounter);
                log.info("Updated Document with with FinalId {} DraftId {}",
                        document.getFinalId(),
                        document.getDraftId());
            } else {
                doNotUpdatedCounter++;
                log.info("DoNotUpdatedCounter: {}", doNotUpdatedCounter);
                log.info("Do not updated Document with with FinalId {} DraftId {}",
                        document.getFinalId(),
                        document.getDraftId());
            }
        }

        log.info("####################################################################################################");
        log.info("Failed documents count {}", failedCounter);
        failed.forEach(document -> log.error("FinalId {} DraftId {}", document.getFinalId(), document.getDraftId()));
    }

    public void checkIfAccessCodePresent() throws IOException {
        var doc = Jsoup.parse(new File("/Users/Sergey_Avdalyan/IdeaProjects/WKL/db-updater/src/main/resources/file/a.xml"), "UTF-8");
        List<String> list = new ArrayList<>();

        for (Element element : doc.select("arr[name=AccessCode]")) {
            list.addAll(Arrays.asList(element.text().split(" ")));
        }

        String query = "SELECT count(*) FROM AccessCodes WHERE AccessCode = ?";

        for (String s : list) {
            int count = jdbcTemplate.queryForObject(query, new Object[]{s}, Integer.class);

            System.out.println("EXIST :" + s + " " + ((count > 0) ? "YES" : "NO"));
        }

        System.out.println("DONE");
    }

    public void getAllDiscplayCodes() throws Exception {
        var res = jdbcTemplate.query("select ID, Displaycode from PublicationTitles where publisherid=1", new PublicationTitleMapper());
        var result = new ArrayList<PublicationTitle>();

        for (PublicationTitle p : res) {
            String solrQuery = "http://li-prd-solr-master:8080/solr/PRD_Legal_Master/select?indent=on&q=Source:\"" + URLEncoder.encode(p.getDisplayCode(), "UTF-8") + "\" AND NOT DocumentVisibility:SOURCE&wt=xml";
            solrQuery = solrQuery.replace(" ", "%20");
            org.jsoup.nodes.Document doc = null;
            try {
                doc = Jsoup.connect(solrQuery).userAgent("Mozilla").get();
                var count = doc.select("result").attr("numFound");
                if ("0".equals(count)) {
                    System.out.println("not have" + p.getDisplayCode());
                    result.add(p);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("FAILED: " + p.getDisplayCode());
            }

        }

        System.out.println("finished");

        for (PublicationTitle o : result) {
            System.out.println(o.getId() + ":" + o.getDisplayCode());
        }
    }


}
