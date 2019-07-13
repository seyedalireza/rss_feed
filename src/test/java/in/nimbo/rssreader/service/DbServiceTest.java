package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.SearchParams;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static in.nimbo.rssreader.service.DbService.PASSWORD;
import static in.nimbo.rssreader.service.DbService.USER;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DbServiceTest {
    @Mock
    QueryBuilder queryBuilder;
    @Mock
    Logger log;
    @InjectMocks
    DbService dbService;

    @Before
    public void setUp() throws SQLException {
        dbService.createConnectionPool("test");
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void after() {
        dbService.closeConnections();
    }

    @Test
    public void testAddFeedToPostgres() throws Exception {
        dbService.addFeedToPostgres(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "2019-1-1", "source", "rssUrl", "hash")));
    }

    @Test
    public void testSearch() throws Exception {
        when(queryBuilder.buildSearchQuery(any(), any())).thenCallRealMethod();
        List result = dbService.search(SearchParams.builder().source("test").build());
        Assert.assertEquals(Collections.emptyList(), result);
    }

    @Test
    public void testCountSearch() throws Exception {
        when(queryBuilder.buildCountQuery(any(), any())).thenCallRealMethod();
        int result = dbService.countSearch(SearchParams.builder().source("test").build());
        Assert.assertEquals(0, result);
    }

//    @Test
//    public void testParseResult() throws Exception {
//        List result = dbService.parseResult(null, Class.forName("in.nimbo.rssreader.service.DbService"));
//        Assert.assertEquals(Arrays.asList("String"), result);
//    }
//
//    @Test
//    public void testGetNumberOfNews() throws Exception {
//        when(queryBuilder.distinctCountQuery(any(), any(), anyString())).thenCallRealMethod();
//        int result = dbService.getNumberOfNews();
//        Assert.assertEquals(0, result);
//    }
//
//    @Test
//    public void testGetNumberOfNewsagency() throws Exception {
//        when(queryBuilder.distinctCountQuery(any(), any(), anyString())).thenCallRealMethod();
//        int result = dbService.getNumberOfNewsAgency();
//        Assert.assertEquals(0, result);
//    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme