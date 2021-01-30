package com.zjx.controller;

import com.zjx.common.PageDTO;
import com.zjx.entity.es.EsBlog;
import com.zjx.entity.es.JDPojo;
import com.zjx.repository.es.JDPojoRepository;
import com.zjx.util.HtmlParseUtil;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/jd/data")
public class JDController {

    @Autowired
    private JDPojoRepository jdPojoRepository;

    @Autowired
    RestHighLevelClient client;
    /**
     * 京东数据爬取
     * @param keyword
     * @return
     */
    @ResponseBody
    @RequestMapping("/crawling")
    public String jdDataCrawling(@RequestParam(value = "keyword", required = false) String keyword){
        if(!StringUtils.isEmpty(keyword)){
            List<JDPojo> jdPojoList = HtmlParseUtil.getJDPojoList(keyword);
            jdPojoRepository.saveAll(jdPojoList);
        }
        return "ok";
    }

    @ResponseBody
    @RequestMapping("/page")
    public Object jdDataPage(@RequestParam(value = "keyword",defaultValue = "") String keyword,
                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)throws Exception{
        if(pageNum < 1){
            pageNum = 1;
        }

        SearchRequest searchRequest = new SearchRequest("jd_pojo").types("_doc");

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from((pageNum - 1) * pageSize);
        sourceBuilder.size(pageSize);

        if(!StringUtils.isEmpty(keyword)){
//            TermQueryBuilder termQuery = QueryBuilders.termQuery("title", keyword);
            BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
            boolQuery.should(QueryBuilders.matchQuery("title", keyword));
            sourceBuilder.query(boolQuery);
        }
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);//多个高亮显示
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);
        //执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        //解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        for(SearchHit hit : response.getHits().getHits()){
            //拿出高亮字段
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();//原来的结果
            //解析高亮的字段，将原来的字段换为我们高亮的字段即可
            if(title != null){
                Text[] fragments = title.fragments();
                String n_title = "";
                for(Text text : fragments){
                    n_title += text;
                }
                sourceAsMap.put("title", n_title);
            }
            list.add(sourceAsMap);
        }
        return list;

    }

    @ResponseBody
    @RequestMapping("/page2")
    public Object jdDataPage2(@RequestParam(value = "keyword",defaultValue = "") String keyword,
                             @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                             @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize)throws Exception{

        BoolQueryBuilder builder = QueryBuilders.boolQuery();

        if(pageNum < 1){
            pageNum = 1;
        }
        //常规查法
        if(!StringUtils.isEmpty(keyword)){
            builder.should(QueryBuilders.matchQuery("title", keyword));
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "price");
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);

        Page<JDPojo> pojos = jdPojoRepository.search(builder, pageRequest);
        return pojos;

    }

}
