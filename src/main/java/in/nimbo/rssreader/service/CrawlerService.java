package in.nimbo.rssreader.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

@Service
public class CrawlerService {
    private Pattern urlPattern = Pattern.compile("https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&\\/=]*)");

    public List<String> getRssUrlList(String siteUrl) {
        List<String> result = new ArrayList<>();


        return result;
    }

    public void getRssListPageLink(String siteUrl) {
        try {
            Document doc = Jsoup.connect(siteUrl).get();
            doc.getElementsByTag("a").forEach(element -> {
                String href = element.attributes().get("href");
                if(href.contains("rss")) {

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getPageContent(String siteUrl) {
        try {
            URL url = new URL(siteUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            Scanner scanner = new Scanner(con.getInputStream());
            StringBuilder stringBuilder = new StringBuilder();
            while(scanner.hasNext()) {
                stringBuilder.append(scanner.nextLine() + "\n");
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
