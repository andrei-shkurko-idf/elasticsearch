package com.example.elasticsearch.controller

import com.example.elasticsearch.service.EsService
import org.springframework.web.bind.annotation.*

@RestController
class ElasticController(val esService: EsService) {

  @PutMapping("/articles")
    fun addArticle(@RequestParam ("url") url:String,
      @RequestParam("title") title: String,
      @RequestParam("content") text: String):String {
    esService.updateArticle(url, title, text)
    return url
  }

  @CrossOrigin("http://localhost:63342")
  @PostMapping("/search")
  fun search(@RequestBody input: Input): List<String> {
    return esService.search(input.input).ifEmpty { mutableListOf("") }

  }
}
data class Input(val input:String)
