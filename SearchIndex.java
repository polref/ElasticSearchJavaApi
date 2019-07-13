import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;

public class SearchIndex {
    private TransportClient client;
    @Before
    public void init() throws  Exception{
        //创建一个Setting对象
        Settings settings = Settings.builder()
                .put("cluster.name","elasticsearch")
                .build();
        //创建一个TranSportClient对象
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),9300));

    }

    private  void  search(QueryBuilder queryBuilder) throws  Exception{
        //执行查询
        SearchResponse searchResponse =  client.prepareSearch("methodbody")                               //  设置查询的index
                .setTypes("java")                                                                                  //  设置查询的type
                .setQuery(queryBuilder)
                .get();
        //取查询结果
        SearchHits searchHits = searchResponse.getHits();
        // 总记录数
        System.out.println("查询结果总记录数：" + searchHits.getTotalHits());

        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        while(iterator.hasNext()){
            SearchHit searchHit = iterator.next();
            //打印文档对象，以jason格式输出
           // System.out.println(searchHit.getSourceAsString());
            //取文档属性
            System.out.println("-----------文档的属性");
            Map<String ,Object> document  = searchHit.getSource();
            System.out.println(document.get("method"));
           // System.out.println(document.get("title"));
           // System.out.println(document.get("content"));
        }

        //关闭client
        client.close();
    }

    @Test
    //根据id查询

    public void  testQueryById(String Id) throws  Exception{                                                    //传入查询Id
        //创建一个client对象
        //创建一个查询对象
        QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds(Id);
        search(queryBuilder);
    }

    @Test
    //根据关键字查询
    public void  testQueryByTerm(String Term) throws Exception{                                                 //传入查询Term
        //创建一个queryBuilder
        //参数1 要搜索的字段
        //参数2 以搜索的关键词
        QueryBuilder queryBuilder = QueryBuilders.termQuery("methodBody",Term);
        //执行查询
        search(queryBuilder);
    }
    @Test
    public void testQueryStringQuery(String querySring) throws Exception{                                        //传入查询queryString
        //创建一个QueryBulider对象
        QueryBuilder queryBuilder = QueryBuilders.queryStringQuery(querySring)
                .defaultField("methodBody");
        //执行查询
        search(queryBuilder);
    }
}
