import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, FeedException {
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/news", "postgres", "1234")) {

            System.out.println("Java JDBC PostgreSQL Example");
            // When this class first attempts to establish a connection, it automatically loads any JDBC 4.0 drivers found within
            // the class path. Note that your application must manually load any JDBC drivers prior to version 4.0.
//          Class.forName("org.postgresql.Driver");

            System.out.println("Connected to PostgreSQL database!");
            Statement statement = connection.createStatement();
            System.out.println("Reading car records...");
            System.out.printf("%-30.30s  %-30.30s%n", "Model", "Price");
            ResultSet resultSet = statement.executeQuery("SELECT * FROM news.news");
            while (resultSet.next()) {
                System.out.printf("%-30.30s  %-30.30s%n", resultSet.getString("title"), resultSet.getString("news_agency"));
            }

            String url = "https://www.irna.ir/rss";
            SyndFeed feeds = new SyndFeedInput().build(new XmlReader(new URL(url)));
            for (SyndEntry entry : feeds.getEntries()) {
                try {
                    DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd");
                    String strDate = dateFormat.format(entry.getPublishedDate());
                    statement.executeQuery(String.format("INSERT INTO \"news\".\"news\" (\"title\",\"time\",\"description\",\"news_agency\",\"category\")\n" +
                            "\t\t\t\t\tVALUES ('%s','%s','%s','%s','%s')",
                            entry.getTitle(),
                            strDate,
                            entry.getDescription().getValue(),
                            feeds.getTitle(),
                            entry.getCategories().size() > 0 ? entry.getCategories().get(0).getName() : "" ));
                }catch (Exception e) {
                    logger.info("news is not valid");
                }
            }

        } /*catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC driver not found.");
            e.printStackTrace();
        }*/ catch (SQLException e) {
            System.out.println("Connection failure.");
            e.printStackTrace();
        }
    }
}