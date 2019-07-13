package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CrawlerService {
    public static Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?([-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b)([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)");

    @Scheduled(fixedRate = 30000)
    public void crawl() {
        log.info("start adding new feeds");
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        FeedService feedService = new FeedService();
        DbService dbService = DbService.getInstance();
        try {
            dbService.getRssUrls().forEach(uri -> queue.add(uri));
        } catch (Exception e) {
            log.error("can't connect to database and get rss links.", e);
        }

        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(8);
        for (int i = 0; i < 8; i++)
            scheduledThreadPoolExecutor.submit(() -> {
                while (!queue.isEmpty()) {
                    String uri = queue.remove();
                    log.info("uri: " + uri + " rss added.");
                    List<News> newsList = new ArrayList<>();
                    try {
                        newsList = feedService.getFeeds(uri);
                    } catch (Exception e) {
                        log.error("getting feeds error.", e);
                    }
                    try {
                        dbService.addFeedToPostgres(newsList);
                    } catch (Exception e) {
                        log.error("can't add feed to database", e);
                    }
                }
            });
    }


    public List<String> getRssUrlList(String siteUrl) {
        List<String> result = new ArrayList<>();

        String rssListPageLink = getRssListPageLink(siteUrl);
        try {
            Document doc = Jsoup.connect(rssListPageLink).get();
            doc.getElementsByTag("a").forEach(element -> {
                try {
                    String href = element.attributes().get("href");
                    href = getAbsoluteLink(siteUrl, href);
                    if (!href.toLowerCase().contains("rss"))
                        return;

                    result.add(href);
                } catch (Exception e) {
                    log.error("CrawlerService.getRssUrlList()", e);
                }
            });
        } catch (IOException e) {
            log.error("CrawlerService.getRssUrlList()", e);
        }
        return result;
    }

    public String getAbsoluteLink(String siteUrl, String href) {
        String link;
        Matcher matcher = urlPattern.matcher(href);
        if (matcher.find())
            link = href;
        else {
            if (siteUrl.endsWith("/") || href.startsWith("/"))
                link = siteUrl + href;
            else
                link = siteUrl + "/" + href;
        }
        return link;
    }

    public String getRssListPageLink(String siteUrl) {
        AtomicReference<String> link = new AtomicReference<>("");
        try {
            Document doc = Jsoup.connect(siteUrl).get();
            doc.getElementsByTag("a").forEach(element -> {
                String href = element.attributes().get("href");
                if (href.contains("rss")) {
                    link.set(getAbsoluteLink(siteUrl, href));
                }
            });
        } catch (IOException e) {
            log.error("CrawlerService.getRssUrlList()", e);
        }
        return link.get();
    }
}
