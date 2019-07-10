package in.nimbo.rssreader.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.springframework.boot.test.context.SpringBootTest;

import java.sql.PreparedStatement;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@SpringBootTest
public class QueryBuilderTest {
    @Mock
    Logger log;
    @InjectMocks
    QueryBuilder queryBuilder;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildSearchQuery() throws Exception {
        PreparedStatement result = queryBuilder.buildSearchQuery(null, null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildCountQuery() throws Exception {
        PreparedStatement result = queryBuilder.buildCountQuery(null, null);
        Assert.assertEquals(null, result);
    }

    @Test
    public void testDistinctCountQuery() throws Exception {
        PreparedStatement result = queryBuilder.distinctCountQuery(null, Arrays.<String>asList("String"), "tableName");
        Assert.assertEquals(null, result);
    }

    @Test
    public void testBuildInsertQuery() throws Exception {
        PreparedStatement result = queryBuilder.buildInsertQuery(null, "tableName", "instance");
        Assert.assertEquals(null, result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme