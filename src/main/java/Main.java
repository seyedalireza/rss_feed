import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.rssreader.service.DbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException, FeedException {
        for(int i = 0; i < 1000; i++) {
            System.out.println(i);
            try {
                String url = "https://aftabnews.ir/fa/rss/" + i;
                SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
                DbService dbService = new DbService();
                dbService.addFeedToPostgres(feed);
            } catch (Exception ignored) {}
        }
    }
}