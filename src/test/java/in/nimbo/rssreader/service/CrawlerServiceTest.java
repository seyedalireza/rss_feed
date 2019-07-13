package in.nimbo.rssreader.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class CrawlerServiceTest {
    @Mock
    Logger log;
    @InjectMocks
    CrawlerService crawlerService;

//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }
//
//    @Test
//    public void testCrawl() throws Exception {
//        crawlerService.crawl();
//    }
//
//    @Test
//    public void testGetRssUrlList() throws Exception {
//        List<String> result = crawlerService.getRssUrlList("siteUrl");
//        Assert.assertEquals(Arrays.<String>asList("String"), result);
//    }
//
//    @Test
//    public void testGetAbsoluteLink() throws Exception {
//        String result = crawlerService.getAbsoluteLink("siteUrl", "href");
//        Assert.assertEquals("replaceMeWithExpectedResult", result);
//    }
//
//    @Test
//    public void testGetRssListPageLink() throws Exception {
//        String result = crawlerService.getRssListPageLink("siteUrl");
//        Assert.assertEquals("replaceMeWithExpectedResult", result);
//    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme