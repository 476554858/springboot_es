package com.zjx.entity.mysql;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_blog")
public class MysqlBlog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String author;
    @Column(columnDefinition = "mediumtext")
    private String content;

    private Date createTime;

    private Date updateTime;

}
