package com.zjx.controller;

import com.zjx.entity.es.EsBlogRepository;
import com.zjx.entity.mysql.MysqlBlog;
import com.zjx.repository.mysql.MysqlBlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class IndexController {

    @Autowired
    MysqlBlogRepository mysqlBlogRepository;

    @Autowired
    EsBlogRepository esBlogRepository;

    @RequestMapping("/")
    public String index(){
      /*  List<MysqlBlog> all = mysqlBlogRepository.findAll();
        System.out.println(all.size());*/
        System.out.println( esBlogRepository.count());
        return "index.html";
    }
}
