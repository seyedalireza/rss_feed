import com.rometools.rome.io.FeedException;
import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import in.nimbo.rssreader.service.FeedService;
import in.nimbo.rssreader.service.QueryBuilder;

import java.io.IOException;
import java.util.List;

public class Main {
    public static final String URL = "jdbc:postgresql://localhost:5432/news";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1234";

    public static void main(String[] args) throws IOException, FeedException {
//        FeedService feedService = new FeedService();
//        DbService dbService = new DbService(new QueryBuilder());
//        dbService.initialConnections();
//        for(int i = 0; i < 500; i++) {
//            String uri = "https://www.khabaronline.ir/rss/tp/" + i;
//            System.out.println(uri);
//            try {
//                List<News> newsList = feedService.getFeeds(uri);
//                dbService.addFeedToPostgres(newsList);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
    }
}