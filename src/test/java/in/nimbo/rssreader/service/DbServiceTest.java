package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.postgresql.ds.PGPoolingDataSource;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public class DbServiceTest {
    @Mock
    PGPoolingDataSource source;
    @Mock
    QueryBuilder queryBuilder;
    @Mock
    Logger log;
    @InjectMocks
    DbService dbService;

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
        List result = dbService.parseResult(null, Class.forName("in.nimbo.rssreader.service.DbService"));
        Assert.assertEquals(Arrays.asList("String"), result);
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

        int result = dbService.getNumberOfNewsagency();
        Assert.assertEquals(0, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme