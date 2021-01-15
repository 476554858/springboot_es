package com.zjx.repository.es;


import com.zjx.entity.es.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PersonRepository extends ElasticsearchRepository<Person,Integer>{
}
