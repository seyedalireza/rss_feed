package service;

import com.rometools.rome.feed.atom.Feed;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DbService {

    public static final String URL = "jdbc:postgresql://localhost:5432/news";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1234";
    public static final String addQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"time\",\"description\",\"news_agency\",\"category\")\n" +
            "VALUES ('%s','%s','%s','%s','%s')";

    public void addFeddToPostgres(SyndFeed feed) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            for (SyndEntry entry : feed.getEntries()) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    String strDate = dateFormat.format(entry.getPublishedDate());
                    statement.executeQuery(String.format(addQuery,
                            entry.getTitle(),
                            strDate,
                            entry.getDescription().getValue(),
                            feed.getTitle(),
                            entry.getCategories().isEmpty() ? entry.getCategories().get(0).getName() : ""));
                } catch (Exception e) {
//                    logger.info("news is not valid");
                    //todo add logger
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

