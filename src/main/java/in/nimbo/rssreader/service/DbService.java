package in.nimbo.rssreader.service;

import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.utility.QueryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DbService {
    public static final String URL = "jdbc:postgresql://localhost:5432/news";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1234";
    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"date\",\"description\",\"newsagency\",\"category\")\n" +
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
                    String title = entry.getTitle();
                    SyndContent descriptionObject = entry.getDescription();
                    String description = "";
                    if(descriptionObject != null)
                        description = descriptionObject.getValue();
                    String newsAgency = feed.getTitle();
                    if(newsAgency == null) newsAgency = "";
                    if(title == null) title = "";
                    String query = String.format(insertQuery,
                            title,
                            strDate,
                            description,
                            newsAgency,
                            !entry.getCategories().isEmpty() ? entry.getCategories().get(0).getName() : "");
                    statement.executeQuery(query);
                } catch (Exception e) {
                    log.error("DbService.addToPostgres()", e);
                }
            }
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
        }
    }

    public List search(SearchParams params) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            Statement statement = connection.createStatement();
            String query = queryBuilder.buildSearchQuery(params);
            ResultSet result = statement.executeQuery(query);
            List resultList = parseResult(result, News.class);
            return resultList;
        } catch (Exception e) {
            log.error("DbService.addToPostgres()", e);
        }
        return Collections.emptyList();
    }

    public List parseResult(ResultSet resultSet, Class clazz) {
        List list = new ArrayList();
        while(true) {
            try {
                if (!resultSet.next())
                    break;
                Object clazzObject = clazz.getConstructor().newInstance();
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    String name = field.getName();
                    String value = resultSet.getString(name);
                    field.set(clazzObject, value);

                    field.setAccessible(false);
                }
                list.add(clazzObject);
            } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("DBService.parseResult()", e);
            }
        }
        return Collections.emptyList();
    }



}
