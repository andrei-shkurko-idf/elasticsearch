package com.example.elasticsearch.service

import com.example.elasticsearch.model.Site
import com.fasterxml.jackson.databind.ObjectMapper
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.springframework.stereotype.Service
import java.util.*

@Service
class EsService(val esClient: RestHighLevelClient) {
  private val indexName = "articles"
  val mapper = ObjectMapper()
  fun updateArticle(url: String, title: String, content: String) {
    val site = Site(url, title, content)
    val id = UUID.randomUUID().toString()
    val indexRequest = IndexRequest(indexName)
    indexRequest.id(id)
    indexRequest.source(mapper.writeValueAsString(site), XContentType.JSON)

    esClient.index(indexRequest, RequestOptions.DEFAULT)
  }

  fun search(searchString: String): List<String> {
    val searchRequest = SearchRequest(indexName)
    val searchSourceBuilder = SearchSourceBuilder()
    searchSourceBuilder.query(QueryBuilders.matchQuery("content", searchString))

    searchRequest.source(searchSourceBuilder)
    val sites = mutableListOf<Site>()
    val searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT)
    for (hit in searchResponse.hits.hits) {
      val url = hit.sourceAsMap["url"] as String
      val title = hit.sourceAsMap["title"] as String
      val content = hit.sourceAsMap["content"] as String
      sites.add(Site(url, title, content))
    }
    return sites.map { "${it.url} ${it.title}" }
  }
}
