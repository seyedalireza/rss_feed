package in.nimbo.rssreader.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.utility.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

@Service
@Slf4j
public class DbService {
    public static final String URL = "jdbc:postgresql://localhost:5432/news";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1234";
    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"time\",\"description\",\"news_agency\",\"category\")\n" +
            "VALUES ('%s','%s','%s','%s','%s')";
    private QueryBuilder queryBuilder;

    @Autowired
    public DbService(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void addFeedToPostgres(SyndFeed feed) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            for (SyndEntry entry : feed.getEntries()) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    String strDate = dateFormat.format(entry.getPublishedDate());
                    String query = String.format(insertQuery,
                            entry.getTitle(),
                            strDate,
                            entry.getDescription().getValue(),
                            feed.getTitle(),
                            entry.getCategories().isEmpty() ? entry.getCategories().get(0).getName() : "");
                    ResultSet result = statement.executeQuery(query);
                } catch (Exception e) {
                    log.error("DbService.addToPostgres()", e);
                }
            }
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
        }
    }

    public void search(SearchParams params) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            String query = queryBuilder.buildSearchQuery(params);
            ResultSet result = statement.executeQuery(query);

        } catch (Exception e) {
            log.error("DbService.addToPostgres()", e);
        }
    }
}
