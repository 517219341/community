package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscusspostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscusspostRepository discusspostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchOperations operations;

    public void saveDiscussPost(DiscussPost discussPost) {
        discusspostRepository.save(discussPost);
    }

    public void deleteDiscussPost(int id) {
        discusspostRepository.deleteById(id);
    }

    public SearchHits<DiscussPost> searchDiscussPost(String keyWord, int current, int limit) {
        Sort sort = Sort.by("type").descending()
                .and(Sort.by("score").descending())
                .and(Sort.by("createTime").descending())
                .and(Sort.by("id").descending());
        Pageable pageable = PageRequest.of(current, limit).withSort(sort);
//        List<HighlightField> fields = new ArrayList<>();
//        HighlightField title = new HighlightField("title");
//        HighlightField content = new HighlightField("content");
//        fields.add(title);
//        fields.add(content);
//        HighlightQuery highlightQuery = new HighlightQuery(new Highlight(fields,));
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("title")
                                .field("content")
                                .query(keyWord)
                        ))
                //.withSort(sort)
                .withPageable(pageable)
                //.withHighlightQuery(highlightQuery)
                .build();
        SearchHits<DiscussPost> searchHits = operations.search(query, DiscussPost.class);
        for (SearchHit hit : searchHits) {
            System.out.println(hit.getContent());
        }
        return searchHits;
    }
}


