package com.workerholic.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequestBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "file:src/main/webapp/WEB-INF/spring/root-context.xml" })
public class BoardServiceElasticTest {

	private Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create();

	private String hostname = "localhost";
	private Integer port = 9200;

	@Bean
	public RestHighLevelClient restHighLevelClient() {
		return new RestHighLevelClient(RestClient.builder(new HttpHost(hostname, port, "http")));
	}

	private final RestHighLevelClient client = restHighLevelClient();

	String indexName = "board";

	@Test
	public void getBoardList() {

		List<Map<String, Object>> boardList = new ArrayList<Map<String, Object>>();

		// 쿼리문
		SearchRequest searchRequest = new SearchRequest(indexName);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		sourceBuilder.from(0);
		sourceBuilder.size(1000);

		searchRequest.source(sourceBuilder);

		try {
			SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

			for (SearchHit hit : searchResponse.getHits().getHits()) {
				Map<String, Object> sourceMap = hit.getSourceAsMap();
				boardList.add(sourceMap);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println(gson.toJson(boardList));
	}

	@Test
	public void insertBoard() {

		Map<String, Object> boardVO = new HashMap<String, Object>();

		for (int i = 0; i < 1000; i++) {
			boardVO.put("bno", i);
			boardVO.put("boardType", "live" + i);
			boardVO.put("title", "테스트제목" + i);
			boardVO.put("content", "테스트내용" + i);
			boardVO.put("cnt", i);
			boardVO.put("writer", "testUser" + i);
			boardVO.put("registDate", new Date());

			try {
				IndexRequest request = new IndexRequest(indexName).id(indexName+i).source(boardVO);

				client.index(request, RequestOptions.DEFAULT);

			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void updateBoard() {
		
		int bno = 1;
		
		Map<String, Object> boardVO = new HashMap<String, Object>();
		
		boardVO.put("bno", bno);
		boardVO.put("boardType", "live");
		boardVO.put("title", "변경테스트제목");
		boardVO.put("content", "변경테스트내용");
		boardVO.put("cnt", 1);
		boardVO.put("writer", "변경User" );
		boardVO.put("registDate", new Date());
		
		try {
			UpdateRequest request = new UpdateRequest(indexName, indexName+bno).doc(boardVO);
			
			client.update(request, RequestOptions.DEFAULT);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
