import com.rometools.rome.io.FeedException;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import in.nimbo.rssreader.service.QueryBuilder;

import java.io.IOException;

public class Main {
    public static final String URL = "jdbc:postgresql://localhost:5432/news";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1234";

    public static void main(String[] args) throws IOException, FeedException {
        QueryBuilder queryBuilder = new QueryBuilder();
        DbService dbService = new DbService(queryBuilder);
        //        for(int i = 0; i < 1000; i++) {
//            System.out.println(i);
//            try {
//                String url = "https://aftabnews.ir/fa/rss/" + i;
//                SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
//                DbService dbService = new DbService();
//                dbService.addFeedToPostgres(feed);
//            } catch (Exception ignored) {}
//        }

//        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
//        QueryBuilder queryBuilder = new QueryBuilder();
//        SearchParams build = SearchParams.builder().newsAgency("ag").title("newsTitle").build();

//        PreparedStatement preparedStatement = queryBuilder.buildSearchQuery(connection, build);

        CrawlerService crawler = new CrawlerService();
        crawler.getRssUrlList("http://www.isna.ir").forEach(System.out::println);
    }
}