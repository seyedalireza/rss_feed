package in.nimbo.rssreader.controller;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.RangedSearchParams;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import lombok.extern.slf4j.Slf4j;
import in.nimbo.rssreader.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RequestMapping("/api/v1")
@RestController
@Slf4j
public class RestApi {
    private DbService dbService;
    private FeedService feedService;
    private CrawlerService crawler;

    @Autowired
    public RestApi(DbService dbService, FeedService feedService, CrawlerService crawler) {
        this.crawler = crawler;
        this.dbService = dbService;
        this.feedService = feedService;
    }

    @PostMapping("/add-feed")
    public ResponseEntity<String> add(@RequestParam String uri) {
        log.info("add feed request received with uri: " + uri);
        try {
            List<News> newsList = feedService.getFeeds(uri);
            dbService.addFeedToPostgres(newsList);
            log.info("add feed request finished with uri: " + uri);
        } catch (Exception e) {
            log.error("Api.add():  uri =" + uri, e);
            return new ResponseEntity<String>("internal error", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List> search(@RequestBody SearchParams params) {
        log.info("search request received with params: " + params.toString());
        if (params.getDate() != null && params.getDate().matches("(\\d+)-(\\d+)-(\\d+)")) {
            params.setDate(null);
        }
        List searchResult = null;
        try {
            searchResult = dbService.search(params);
        } catch (Exception e) {
            return new ResponseEntity<List>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List>(searchResult, HttpStatus.OK);
    }

    @PostMapping("/ranged-search")
    public ResponseEntity<List> Rangedsearch(@RequestBody RangedSearchParams params) {
        log.info("ranged search request received with params: " + params.toString());
        if (params.getStartDate() == null || !params.getStartDate().matches("(\\d+)-(\\d+)-(\\d+)")) {// todo check invalid pattern
            params.setStartDate("0-0-0");
        }
        if (params.getEndDate() == null || !params.getStartDate().matches("(\\d+)-(\\d+)-(\\d+)")) {// todo check invalid pattern
            params.setEndDate("9999-12-29");
        }
        List searchResult = null;
        try {
            searchResult = dbService.rangedSearch(params);
        } catch (Exception e) {
            return new ResponseEntity<List>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<List>(searchResult, HttpStatus.OK);
    }

    @PostMapping("/count-search")
    public ResponseEntity<Integer> countSearch(@RequestBody SearchParams params) {

        log.info("count search request received with params: " + params.toString());
        Integer searchResult = null;
        try {
            searchResult = dbService.countSearch(params);
        } catch (Exception e) {
            return new ResponseEntity<>(0, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/number-of-news")
    public ResponseEntity<Integer> getNumberOfDuplicateNews() {
        log.info("number of newsagents request received");

        int numberOfNews = 0;
        try {
            numberOfNews = dbService.getNumberOfNews();
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(numberOfNews, HttpStatus.OK);
    }

    @GetMapping("/number-of-newsagents")
    public ResponseEntity<Integer> getNumberOfNewsAgency() {
        log.info("number of newsagents request received");

        int numberOfNewsagency = 0;
        try {
            numberOfNewsagency = dbService.getNumberOfNewsAgency();
        } catch (Exception e) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        if (numberOfNewsagency < 0) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Integer>(numberOfNewsagency, HttpStatus.OK);
    }

    @GetMapping("/news-in-day")
    public ResponseEntity<List> getNewsOfDay(@RequestParam String day) {//happen exception
        log.info("news of a day request received with day: " + day);
        SearchParams searchParams = SearchParams.builder().date(day).build();
        List searchResult = null;
        try {
            searchResult = dbService.search(searchParams);
        } catch (Exception e) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/crawl")
    public ResponseEntity<String> crawl(@RequestParam String uri) {
        log.info("crawl request received with uri: " + uri);
        List<String> rssUrlList = crawler.getRssUrlList(uri);
        for (String rssUrl : rssUrlList) {
            try {
                List<News> newsList = feedService.getFeeds(rssUrl);
                dbService.addFeedToPostgres(newsList);
            } catch (Exception e) {
                log.error("Api.add(): ", e);
            }
        }
        log.info("crawl request finished with uri: " + uri);
        return new ResponseEntity<String>("site rss added", HttpStatus.OK);
    }
}
