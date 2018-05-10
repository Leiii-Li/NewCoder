package com.lei.service;

import com.lei.model.Question;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by John on 2018/4/21.
 */
@Service
public class SearchService {

    private static final String SOLR_URL = "http://127.0.0.1:8983/solr/bbs";
    private HttpSolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
    private static final String QUESTION_ID_FIELD = "id";
    private static final String QUESTION_TITLE_FILELD = "question_title";
    private static final String QUESTION_CONTENT_FILELD = "question_content";

    public List<Question> searchQuestion(String keyword, int offset, int count, String hlPre, String hlPos) throws IOException, SolrServerException {
        List<Question> questionList = new ArrayList<>();
        SolrQuery solrQuery = new SolrQuery(keyword);
        solrQuery.setRows(count);
        solrQuery.setStart(offset);
        solrQuery.setHighlight(true);
        solrQuery.setHighlightSimplePre(hlPre);
        solrQuery.setHighlightSimplePost(hlPos);
        solrQuery.set("hl.fl", QUESTION_CONTENT_FILELD + "," + QUESTION_TITLE_FILELD);
        QueryResponse response = client.query(solrQuery);
        for (Map.Entry<String, Map<String, List<String>>> entry : response.getHighlighting().entrySet()) {
            Question q = new Question();
            q.setId(entry.getKey());
            if (entry.getValue().containsKey(QUESTION_CONTENT_FILELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_CONTENT_FILELD);
                if (contentList.size() > 0) {
                    q.setContent(contentList.get(0));
                }
            }
            if (entry.getValue().containsKey(QUESTION_TITLE_FILELD)) {
                List<String> contentList = entry.getValue().get(QUESTION_TITLE_FILELD);
                if (contentList.size() > 0) {
                    q.setTitle(contentList.get(0));
                }
            }
            questionList.add(q);
        }
        return questionList;
    }

    public boolean addQuestionIndex(String id, String title, String content) throws IOException, SolrServerException {
        SolrInputDocument doc = new SolrInputDocument();
        doc.setField(QUESTION_ID_FIELD, id);
        doc.setField(QUESTION_TITLE_FILELD, title);
        doc.setField(QUESTION_CONTENT_FILELD, content);
        UpdateResponse response = client.add(doc, 1000);
        return response != null && response.getStatus() == 0;
    }

    public void deleteQuestionIndex(String id) throws IOException, SolrServerException {
        client.deleteById(id);
    }
}
