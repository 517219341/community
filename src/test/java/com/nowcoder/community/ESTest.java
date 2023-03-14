package com.nowcoder.community;


import co.elastic.clients.elasticsearch.core.GetRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import com.alibaba.fastjson2.JSONObject;
import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.elasticsearch.DiscusspostRepository;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.util.CommunityUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ESTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscusspostRepository discusspostRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchOperations operations;

    // 单独插入某些数据
    @Test
    public void testInsert() {
        discusspostRepository.save(discussPostMapper.selectDiscussPostByID(241));
        discusspostRepository.save(discussPostMapper.selectDiscussPostByID(242));
        discusspostRepository.save(discussPostMapper.selectDiscussPostByID(243));
    }


    // 批量插入数据
    @Test
    public void testInsertList() {
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(101,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(102,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(103,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(111,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(112,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(131,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(132,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(133,0,100));
        discusspostRepository.saveAll(discussPostMapper.selectDiscussPosts(134,0,100));
    }

    // 修改数据
    @Test
    public void updata() {
        DiscussPost post = discussPostMapper.selectDiscussPostByID(231);
        post.setContent("我是新人，使劲灌水");
        discusspostRepository.save(post);
    }

    // 删除数据
    @Test
    public void testDelete() {
        discusspostRepository.deleteById(231);
        // 删除所有数据
        //discusspostRepository.deleteAll();
    }

    // 搜索功能
    @Test
    public void testSearchByTemplate() {
//        Criteria criteria = new Criteria("id").is(231);
//        Query query = new CriteriaQuery(criteria);
//        SearchHits<DiscussPost> searchHits = operations.search(query, DiscussPost.class);
//        System.out.println(JSONObject.toJSONString(searchHits));

        Sort sort =  Sort.by("type").descending()
                .and(Sort.by("score").descending())
                .and(Sort.by("createTime").descending())
                .and(Sort.by("id").descending());

        //HighlightQuery highlightQuery = new HighlightQuery();
        Pageable pageable = PageRequest.of(3, 10).withSort(sort);
        Query query = NativeQuery.builder()
                .withQuery(q -> q
                        .match(m -> m
                                .field("title")
                                .field("content")
                                .query("互联网寒冬")
                        ))
                //.withSort(sort)
                .withPageable(pageable)
                .build();
        SearchHits<DiscussPost> searchHits = operations.search(query, DiscussPost.class);
//        SearchHits<DiscussPost> s = discusspostRepository.findByTitleOrContent("互联网寒冬","互联网寒冬");
//        for (SearchHit hit : s) {
//            System.out.println(hit.getContent());
//        }
//        List<DiscussPost> list = new ArrayList<>();
        System.out.println(searchHits.getTotalHits());
        for (SearchHit hit : searchHits) {

            System.out.println(hit.getContent());
        }
    }

}
