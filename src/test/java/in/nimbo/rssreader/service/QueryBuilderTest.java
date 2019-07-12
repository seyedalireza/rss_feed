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
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

import static in.nimbo.rssreader.service.DbService.PASSWORD;
import static in.nimbo.rssreader.service.DbService.USER;
import static org.mockito.Mockito.*;
import static ru.yandex.qatools.embed.postgresql.distribution.Version.Main.V9_6;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class QueryBuilderTest {
    @Mock
    Logger log;
    @InjectMocks
    QueryBuilder queryBuilder;
    static EmbeddedPostgres postgres;
    static PGPoolingDataSource source;

    public static void initialConnections() throws SQLException {
        source = new PGPoolingDataSource();
        source.setDataSourceName("dbService-dataSource");
        source.setPortNumber(5432);
        source.setInitialConnections(5);
        source.setServerName("localhost");
        source.setDatabaseName("news");
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setMaxConnections(25);
        source.getConnection();
    }

    @BeforeClass
    public static void setUpDataBase() throws IOException, SQLException {
        initialConnections();
//        postgres = new EmbeddedPostgres(V9_6, "./embeddedPostgres");
//        final String url = postgres.start("localhost", 5431, "dbName", "postgres-test", "1234");
    }

    @AfterClass
    public static void closeDataBase() {
//        postgres.close();
    }

    @Before
    public void setUp() {
//        final EmbeddedPostgres postgres = new EmbeddedPostgres(V9_6);
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testBuildSearchQuery() throws Exception {
        Connection connection = source.getConnection();
        PreparedStatement result = queryBuilder.buildSearchQuery(connection, SearchParams.builder().title("اختلاس").text("ایران").build());
        Assert.assertEquals("Pooled statement wrapping physical statement SELECT * FROM news.news WHERE title like '%اختلاس%' and text like '%ایران%' ", result.toString());
    }

    @Test
    public void testBuildCountQuery() throws Exception {
        Connection connection = source.getConnection();
        PreparedStatement result = queryBuilder.buildCountQuery(connection, SearchParams.builder().title("اختلاس").text("ایران").build());
        Assert.assertEquals("Pooled statement wrapping physical statement SELECT COUNT(*) FROM news.news WHERE title like '%اختلاس%' and text like '%ایران%' ", result.toString());
    }

    @Test
    public void testDistinctCountQuery() throws Exception {
        Connection connection = source.getConnection();
        PreparedStatement result = queryBuilder.distinctCountQuery(connection, Arrays.<String>asList("String"), "tableName");
        Assert.assertEquals("Pooled statement wrapping physical statement SELECT COUNT(DISTINCT String) FROM tableName", result.toString());
    }

    @Test
    public void testBuildInsertQuery() throws Exception {
        Connection connection = source.getConnection();
        PreparedStatement result = queryBuilder.buildInsertQuery(connection, "tableName", News.builder().title("hasan").build());
        Assert.assertEquals("Pooled statement wrapping physical statement INSERT INTO \"news\".\"tableName\"(title VALUES ('hasan');", result.toString());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme