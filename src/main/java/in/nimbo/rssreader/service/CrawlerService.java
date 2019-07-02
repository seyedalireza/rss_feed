package in.nimbo.rssreader.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    private Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)");

    public List<String> getRssUrlList(String siteUrl) {
        List<String> result = new ArrayList<>();


        return result;
    }

    public String getRssListPageLink(String siteUrl) {
        AtomicReference<String> link = new AtomicReference<>("");
        try {
            Document doc = Jsoup.connect(siteUrl).get();
            doc.getElementsByTag("a").forEach(element -> {
                String href = element.attributes().get("href");
                if(href.contains("rss")) {
                    Matcher matcher = urlPattern.matcher(href);
                    if(matcher.find())
                        link.set(href);
                    else {
                        if(siteUrl.endsWith("/") || href.startsWith("/"))
                            link.set(siteUrl + href);
                        else
                            link.set(siteUrl + "/" + href);
                    }

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return link.get();
    }
}
