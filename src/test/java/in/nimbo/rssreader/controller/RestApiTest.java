package in.nimbo.rssreader.controller;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.RangedSearchParams;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import in.nimbo.rssreader.service.FeedService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class RestApiTest {
    @Mock
    DbService dbService;
    @Mock
    FeedService feedService;
    @Mock
    CrawlerService crawler;
    @Mock
    Logger log;
    @InjectMocks
    RestApi restApi;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAdd() throws Exception {
        when(feedService.getFeeds(anyString())).thenReturn(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "date", "source", "rssUrl", "hash")));
        ResponseEntity<String> result = restApi.add("uri");
        Assert.assertEquals(null, result.getBody());
    }

    @Test
    public void testSearch() throws Exception {
        when(dbService.search(any())).thenReturn(Arrays.asList("String"));
        ResponseEntity<List> result = restApi.search(SearchParams.builder().title("ali").build());
        Assert.assertEquals(Arrays.asList("String"), result.getBody());
    }

    @Test
    public void testRangedSearch() throws Exception {
        when(dbService.rangedSearch(any())).thenReturn(Arrays.asList("String"));
        ResponseEntity<List> result = restApi.Rangedsearch(RangedSearchParams.builder().startDate("2019-10-1").endDate("2011-10-9").build());
        Assert.assertEquals(Arrays.asList("String"), result.getBody());
    }

    @Test
    public void testCountSearch() throws Exception {
        when(dbService.countSearch(any())).thenReturn(10);
        ResponseEntity<Integer> result = restApi.countSearch(SearchParams.builder().title("test").build());
        Assert.assertEquals(new Integer(10), result.getBody());
    }

    @Test
    public void testGetNumberOfDuplicateNews() throws Exception {
        when(dbService.getNumberOfNews()).thenReturn(10);
        ResponseEntity<Integer> result = restApi.getNumberOfDuplicateNews();
        Assert.assertEquals(new Integer(10), result.getBody());
    }

    @Test
    public void testGetNumberOfNewsAgency() throws Exception {
        when(dbService.getNumberOfNewsAgency()).thenReturn(10);
        ResponseEntity<Integer> result = restApi.getNumberOfNewsAgency();
        Assert.assertEquals(new Integer(10), result.getBody());
    }

    @Test
    public void testGetNewsOfDay() throws Exception {
        when(dbService.search(any())).thenReturn(Arrays.asList("[]"));
        ResponseEntity<List> result = restApi.getNewsOfDay("2019-10-11");
        Assert.assertEquals(Arrays.asList("[]"), result.getBody());
    }

//    @Test
//    public void testCrawl() throws Exception {// todo ali
//        when(feedService.getFeeds(anyString())).thenReturn(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "date", "source", "rssUrl")));
//        when(crawler.getRssUrlList(anyString())).thenReturn(Arrays.<String>asList("String"));
//
//        ResponseEntity<String> result = restApi.crawl("uri");
//        Assert.assertEquals(null, result);
//    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme