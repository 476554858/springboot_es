package com.zjx.repository.es;

import com.zjx.entity.es.EsBlog;
import com.zjx.entity.es.JDPojo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface JDPojoRepository extends ElasticsearchRepository<JDPojo,String> {

    Long countByPrice(String price);

    Long countAllById(String id);


}
