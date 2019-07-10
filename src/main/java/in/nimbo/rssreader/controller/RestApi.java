package in.nimbo.rssreader.controller;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import lombok.extern.slf4j.Slf4j;
import in.nimbo.rssreader.service.FeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<String> add(@RequestParam String uri) {// todo invalid url and happen exception
        try {
            List<News> newsList = feedService.getFeeds(uri);
            dbService.addFeedToPostgres(newsList);
        } catch (Exception e) {
            log.error("Api.add():  uri =" + uri, e);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<List> search(@RequestBody SearchParams params) {//todo invalid params and happen exception
        List searchResult = dbService.search(params);
        return new ResponseEntity<List>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/number-of-news")
    public ResponseEntity<Integer> getNumberOfDuplicateNews() {
        int numberOfNews = dbService.getNumberOfNews();
        if (numberOfNews < 0) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(numberOfNews, HttpStatus.OK);
    }

    @GetMapping("/number-of-newsagents")
    public ResponseEntity<Integer> getNumberOfNewsAgency() {
        int numberOfNewsagency = dbService.getNumberOfNewsagency();
        if (numberOfNewsagency < 0) {
            return new ResponseEntity<>(-1, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<Integer>(numberOfNewsagency, HttpStatus.OK);
    }

    @GetMapping("/news-in-day")
    public ResponseEntity<List> getNewsOfDay(@RequestParam String day) {//happen exception
        SearchParams searchParams = SearchParams.builder().date(day).build();
        List searchResult = dbService.search(searchParams);
        return new ResponseEntity<List>(searchResult, HttpStatus.OK);
    }

    @GetMapping("/crawl")
    public ResponseEntity<String> crawl(@RequestParam String uri) {
        List<String> rssUrlList = crawler.getRssUrlList(uri);
        for (String rssUrl : rssUrlList) {
            try {
                List<News> newsList = feedService.getFeeds(rssUrl);
                dbService.addFeedToPostgres(newsList);
            } catch (Exception e) {
                log.error("Api.add(): ", e);
            }
        }
        return new ResponseEntity<String>("site rss added", HttpStatus.OK);
    }
}
