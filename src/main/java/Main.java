import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.rssreader.service.CrawlerService;
import in.nimbo.rssreader.service.DbService;
import in.nimbo.rssreader.utility.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException, FeedException {
        QueryBuilder queryBuilder = new QueryBuilder();
        DbService dbService = new DbService(queryBuilder);


        CrawlerService crawler = new CrawlerService();
        crawler.getRssUrlList("http://www.rajanews.com").forEach(System.out::println);
    }
}