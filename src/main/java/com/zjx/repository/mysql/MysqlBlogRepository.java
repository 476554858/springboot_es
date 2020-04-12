package com.zjx.repository.mysql;

import com.zjx.entity.mysql.MysqlBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MysqlBlogRepository extends JpaRepository<MysqlBlog,Integer>{

    @Query("select e from MysqlBlog e order by e.createTime desc ")
    List<MysqlBlog> queryAll();

    @Query("select e from MysqlBlog e where e.title like concat('%',:keyword,'%') or " +
            "e.content like concat('%',:keyword,'%') ")
    List<MysqlBlog> queryBlogs(@Param("keyword") String keyword);
}
