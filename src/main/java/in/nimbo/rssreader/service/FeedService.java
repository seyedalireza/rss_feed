package in.nimbo.rssreader.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.rssreader.model.News;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class FeedService {
    public List<News> getFeeds(String rssUrl) {
        ArrayList<News> result = new ArrayList<>();
        try {
            URL url = new URL(rssUrl);
            XmlReader xmlReader = new XmlReader(url);
            SyndFeed syndFeed = new SyndFeedInput().build(xmlReader);
            for (SyndEntry entry : syndFeed.getEntries()) {
                String title = entry.getTitle();
                String description = entry.getDescription().getValue();
                String newsAgency = syndFeed.getTitle();
                DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                String strDate = dateFormat.format(entry.getPublishedDate());
                String category = !entry.getCategories().isEmpty() ? entry.getCategories().get(0).getName() : "";
                News news = new News(title, description, newsAgency, category, strDate);
                result.add(news);
            }
        } catch (IOException | FeedException e) {
            log.error("FeedService.getFeeds{}", e);
        }
        return result;
    }
}
