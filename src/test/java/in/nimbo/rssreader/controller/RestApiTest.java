package in.nimbo.rssreader.controller;

import in.nimbo.rssreader.model.News;
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
        when(feedService.getFeeds(anyString())).thenReturn(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "date", "source", "rssUrl")));

        ResponseEntity<String> result = restApi.add("uri");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testSearch() throws Exception {
        when(dbService.search(any())).thenReturn(Arrays.asList("String"));

        ResponseEntity<List> result = restApi.search(null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testCountSearch() throws Exception {
        when(dbService.countSearch(any())).thenReturn(0);

        ResponseEntity<Integer> result = restApi.countSearch(null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetNumberOfDuplicateNews() throws Exception {
        when(dbService.getNumberOfNews()).thenReturn(0);

        ResponseEntity<Integer> result = restApi.getNumberOfDuplicateNews();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetNumberOfNewsAgency() throws Exception {
        when(dbService.getNumberOfNewsagency()).thenReturn(0);

        ResponseEntity<Integer> result = restApi.getNumberOfNewsAgency();
        Assert.assertEquals(null, result);
    }

    @Test
    public void testGetNewsOfDay() throws Exception {
        when(dbService.search(any())).thenReturn(Arrays.asList("String"));

        ResponseEntity<List> result = restApi.getNewsOfDay("day");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testCrawl() throws Exception {
        when(feedService.getFeeds(anyString())).thenReturn(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "date", "source", "rssUrl")));
        when(crawler.getRssUrlList(anyString())).thenReturn(Arrays.<String>asList("String"));

        ResponseEntity<String> result = restApi.crawl("uri");
        Assert.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme