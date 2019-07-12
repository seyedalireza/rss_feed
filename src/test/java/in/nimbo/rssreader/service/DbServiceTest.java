package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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
import java.util.List;

import static in.nimbo.rssreader.service.DbService.PASSWORD;
import static in.nimbo.rssreader.service.DbService.USER;
import static org.mockito.Mockito.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class DbServiceTest {
    static PGPoolingDataSource source;
    @Mock
    QueryBuilder queryBuilder;
    @Mock
    Logger log;
    @InjectMocks
    DbService dbService;

    public static void initialConnections() throws SQLException {
        source = new PGPoolingDataSource();
        source.setDataSourceName("dbService-dataSource");
        source.setPortNumber(5431);
        source.setInitialConnections(5);
        source.setServerName("localhost");
        source.setDatabaseName("news");
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setMaxConnections(25);
        source.getConnection();
    }


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testInitialConnections() throws Exception {
        dbService.initialConnections();
    }

    @Test
    public void testAddFeedToPostgres() throws Exception {
        dbService.addFeedToPostgres(Arrays.<News>asList(new News("title", "description", "newsAgency", "category", "date", "source", "rssUrl")));
    }

    @Test
    public void testSearch() throws Exception {
        when(queryBuilder.buildSearchQuery(any(), any())).thenReturn(null);

        List result = dbService.search(null);
        Assert.assertEquals(Arrays.asList("String"), result);
    }

    @Test
    public void testCountSearch() throws Exception {
        when(queryBuilder.buildCountQuery(any(), any())).thenReturn(null);

        int result = dbService.countSearch(null);
        Assert.assertEquals(0, result);
    }

    @Test
    public void testParseResult() throws Exception {

//        List result = dbService.parseResult(null, Class.forName("in.nimbo.rssreader.service.DbService"));
//        Assert.assertEquals(Arrays.asList("String"), result);
    }

    @Test
    public void testGetNumberOfNews() throws Exception {
        when(queryBuilder.distinctCountQuery(any(), any(), anyString())).thenReturn(null);

        int result = dbService.getNumberOfNews();
        Assert.assertEquals(0, result);
    }

    @Test
    public void testGetNumberOfNewsagency() throws Exception {
        when(queryBuilder.distinctCountQuery(any(), any(), anyString())).thenReturn(null);

        int result = dbService.getNumberOfNewsAgency();
        Assert.assertEquals(0, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme