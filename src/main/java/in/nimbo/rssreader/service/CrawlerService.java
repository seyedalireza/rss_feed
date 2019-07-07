package in.nimbo.rssreader.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
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

        String rssListPageLink = getRssListPageLink(siteUrl);

        try {
            Document doc = Jsoup.connect(rssListPageLink).get();
            doc.getElementsByTag("a").forEach(element -> {
                try {
                    String href = element.attributes().get("href");
                    href = getAbsoluteLink(siteUrl, href);
                    if(!href.toLowerCase().contains("rss"))
                        return;
//                    URL url = new URL(href);
//                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                    connection.setConnectTimeout(1000);
//                    String contentType = connection.getContentType();
//                    if(contentType == null)
//                        return;
//                    if(!contentType.contains("xml") && !contentType.contains("rss"))
//                        return;

                    result.add(href);
                } catch (Exception e) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String getAbsoluteLink(String siteUrl, String href) {
        String link;
        Matcher matcher = urlPattern.matcher(href);
        if(matcher.find())
            link = href;
        else {
            if(siteUrl.endsWith("/") || href.startsWith("/"))
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
                if(href.contains("rss")) {
                    link.set(getAbsoluteLink(siteUrl, href));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return link.get();
    }
}
