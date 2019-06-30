import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import service.DbService;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Main {
    public static void main(String[] args) throws IOException, FeedException {
        String url = "https://www.isna.ir/rss";
        SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
        DbService dbService = new DbService();
        dbService.addFeddToPostgres(feed);
        System.out.println(feed.getEntries().get(0).getTitle());
    }
}
