package com.zjx.repository.es;

import com.zjx.entity.es.EsBlog;
import com.zjx.entity.es.JDPojo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface JDPojoRepository extends ElasticsearchRepository<JDPojo,String> {
}
