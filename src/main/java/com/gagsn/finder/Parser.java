package com.gagsn.finder;

import com.gagsn.util.FileReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class Parser {

    public static final String SYMPHONY_FILE_LOCATION = "file/symphony.xml";
    public static final String SOLR_FILE_LOCATION = "file/solr.txt";

    private final FileReader fileReader;
    private final ElasticIndexer indexer;

    public void parseXml() throws IOException, XPathExpressionException {
        List<String> idsFromXml = new ArrayList<>();

        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();
        File file = fileReader.readFile(SYMPHONY_FILE_LOCATION);
        InputSource xml = new InputSource(new FileInputStream(file));
        NodeList nodes = (NodeList) xpath.compile("/LookupTitleInfoResponse/TitleInfo/titleID").evaluate(xml, XPathConstants.NODESET);

        int counter = 0;

        if (nodes != null && nodes.getLength() > 0) {
            for (int i = 0; i < nodes.getLength(); i++) {
                String id = nodes.item(i).getTextContent();
                idsFromXml.add(id);
                //indexer.indexFile(id);
                System.out.println("Indexed " + counter++  + " files " + " id: " + id);
            }
        }

        List<String> ids = readIds();
        idsFromXml.removeAll(ids);

        System.out.println("Missed ids: " + idsFromXml.size());

        System.out.println("#");
        idsFromXml.forEach(System.out::println);
        System.out.println("#");
    }
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        list.add("a,a,a,b,v,sa,sd,egs,dg,sreg,rsg,srg,s");



        list.forEach(syn -> syn = syn.replace(",", " "));

        for (String s : list) {
            System.out.println(s);
        }

    }

    private List<String> readIds() throws IOException {
        File file = fileReader.readFile(SOLR_FILE_LOCATION);
        return FileUtils.readLines(file, "UTF-8");
    }
}
