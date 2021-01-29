package com.zjx;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zjx.entity.es.Article;
import com.zjx.entity.es.Book;
import com.zjx.entity.es.Person;
import com.zjx.entity.es.User;
import com.zjx.repository.es.BookRepository;
import com.zjx.repository.es.PersonRepository;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.DeleteIndex;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootEsApplicationTests {

    @Autowired
    JestClient jestClient;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    PersonRepository personRepository;


    @Autowired
    RestHighLevelClient client;

    @Test
    public void test02(){
        Book book = new Book();
        book.setId(1);
        book.setBookName("西游记");
        book.setAuthor("吴承恩");
        bookRepository.index(book);
    }

    @Test
    public void test03(){
        List<Book> list = bookRepository.findByBookNameLike("西");
        System.out.println(list.get(0));
    }


    @Test
    public void test04(){
        Person p = new Person();
        p.setId(1);
        p.setName("张三");
        p.setAge(90);
        personRepository.index(p);
    }


    @Test
    public void contextLoads() {
        //1.给es中索引保存一个文档
        Article article = new Article();
        article.setId(1);
        article.setTitle("好消息");
        article.setAuthor("zhangsan");
        article.setContent("Hello world");

        //构建一个索引功能
        Index index = new Index.Builder(article).index("atguigu").type("news").build();

        try {
            jestClient.execute(index);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //测试搜索
    @Test
    public void search(){
        //查询表达式
        String json ="{\n" +
                "    \"query\" : {\n" +
                "        \"match\" : {\n" +
                "            \"content\" : \"hello\"\n" +
                "        }\n" +
                "    }\n" +
                "}";
        //构建搜索功能
        Search search = new Search.Builder(json).addIndex("atguigu").addType("news").build();

        try {
            SearchResult searchResult = jestClient.execute(search);
            System.out.println(searchResult.getJsonString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    ///新版本的ES操作
    @Test
    public void testCreateIndex() throws Exception{
        //创建索引请求
        CreateIndexRequest request = new CreateIndexRequest("zjx_index");
        //2.客户端执行请求，indicesClient,请求后获取相应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        System.out.println(createIndexResponse);
    }

    //测试获取索引
    @Test
    public void testExistIndex() throws Exception{
        GetIndexRequest request = new GetIndexRequest().indices("zjx_index");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试删除索引
    @Test
    public void testDeleteIndex() throws Exception{
        DeleteIndexRequest request = new DeleteIndexRequest().indices("zjx_index");
        DeleteIndexResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    //测试添加文档
    @Test
    public void testAddDocument()throws Exception{
        //创建对象
        User user = new User("张三", 3);
        //创建请求
        IndexRequest request = new IndexRequest("zjx_index", "_doc");

        // 规则 put /kuang_index/_doc/1
        request.id("1");
        request.timeout(TimeValue.timeValueSeconds(1));
        //将我们的数据放入请求 json
        request.source(JSON.toJSONString(user), XContentType.JSON);
        //客户端发送请求，获取相应的结果
        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    //获取文档，判断是否存在get/index/doc/1
    @Test
    public void testIsExists() throws Exception{
        GetRequest getRequest = new GetRequest("zjx_index").id("1");
        //不获取返回的_source的上下文了
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    //获取文档的信息
    @Test
    public void testGetDocment() throws Exception{
        GetRequest getRequest = new GetRequest("zjx_index").id("1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString()); //打印文档内容
        System.out.println(getResponse);
    }

    //更新文档的信息
    @Test
    public void testUpdateRequest()throws Exception{
        UpdateRequest updateRequest = new UpdateRequest().index("zjx_index").type("_doc").id("1");
        updateRequest.timeout("1s");

        User user = new User("张建祥", 28);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(response.status());
    }

    //删除文档记录
    @Test
    public void testDeleteRequest() throws Exception{
        DeleteRequest request = new DeleteRequest("zjx_index").type("_doc").id("1");
        request.timeout("1s");

        DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(deleteResponse.status());
    }

    //批量插入数据
    @Test
    public void testBulkRequest()throws Exception{
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");

        List<User> userList = new ArrayList<User>();
        userList.add(new User("zhang1", 1));
        userList.add(new User("zhang2", 2));
        userList.add(new User("zhang3", 3));

        //批处理请求
        for(int i = 0; i < userList.size(); i++){
            //批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add(new IndexRequest("zjx_index").type("_doc")
                    .id(String.valueOf((i+1)))
                    .source(JSON.toJSONString(userList.get(i)), XContentType.JSON));
        }

        BulkResponse bulkItemResponses = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(bulkItemResponses.hasFailures());
    }

    //查询
    @Test
    public void testSearch()throws Exception{
        SearchRequest searchRequest =  new SearchRequest("zjx_index");
        //构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "zhang1");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(1, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);

        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
        System.out.println(JSONObject.toJSONString(searchResponse.getHits()));
        System.out.println("============================");
        for(SearchHit documentFields : searchResponse.getHits().getHits()){
            System.out.println(documentFields.getSourceAsMap());
        }

    }

    @Test
    public void testSave(){
        Book book = new Book();
        book.setId(1);
        book.setBookName("西游记");
        book.setAuthor("吴承恩3");
        bookRepository.save(book);
    }



}
