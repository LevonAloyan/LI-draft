package com.gagsn;

import com.gagsn.db.log.LogSearcher;
import com.gagsn.db.service.DbUpdaterService;
import com.gagsn.db.service.SolrDeleteService;
import com.gagsn.finder.Parser;
import com.gagsn.finder.Searcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

@SpringBootApplication
public class Application {
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class);

        DbUpdaterService dbUpdaterService = (DbUpdaterService) context.getBean("dbUpdaterService");
        dbUpdaterService.getAllDiscplayCodes();



    }
}
