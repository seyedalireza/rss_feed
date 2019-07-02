package in.nimbo.rssreader.utility;

import in.nimbo.rssreader.model.SearchParams;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class QueryTest {
    Query query = new Query();

    @Test
    public void testBuildSearchQuery() throws Exception {
        SearchParams build = SearchParams.builder().newsAgency("ag").title("newsTitle").build();
        String result = query.buildSearchQuery(build);
        Assert.assertEquals("SELECT * FROM news WHERE title='newsTitle' and newsagency='ag' ;", result);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme