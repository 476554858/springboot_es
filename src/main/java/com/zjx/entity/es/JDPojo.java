package com.zjx.entity.es;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Document(indexName = "jd_pojo",type="_doc",
        useServerConfiguration = true,createIndex = true)
public class JDPojo {

    @Id
    private String id;

    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;

    private String img;

    private String price;
}
