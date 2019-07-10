package in.nimbo.rssreader.service;
import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGPoolingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DbService {
    @Value("${data-base-name}")
    public String dataBaseName = "news";

    @Value("${postgres-port}")
    public String port = "5432";

    @Value("${server-name}")
    public String serverName = "localhost";

    @Value("${spring.datasource.username}")
    public static final String USER = "postgres";

    @Value("${spring.datasource.password}")
    public static final String PASSWORD = "1234";

    public PGPoolingDataSource source;

    @PostConstruct
    public void initialConnections() {
        source = new PGPoolingDataSource();
        source.setDataSourceName("dbServie-dataSource");
        source.setPortNumber(Integer.parseInt(port));
        source.setInitialConnections(5);
        source.setServerName(serverName);
        source.setDatabaseName(dataBaseName);
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setMaxConnections(25);
    }


    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"date\",\"description\",\"newsagency\",\"category\",\"source\",\"rssurl\")\n" +
            "VALUES ('%s','%s','%s','%s','%s','%s','%s')";
    private QueryBuilder queryBuilder;

    @Autowired
    public DbService(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    public void addFeedToPostgres(List<News> newsList) {
        try (Connection connection = source.getConnection()) {
            Statement statement = connection.createStatement();
            for (News news : newsList) {
                try {
                    String title = news.getTitle();
                    String description = news.getDescription();
                    if (description == null)
                        description = "";
                    String newsAgency = news.getNewsAgency();
                    if (newsAgency == null) newsAgency = "";
                    if (title == null) title = "";
                    String category = news.getCategory(); // !entry.getCategories().isEmpty() ? entry.getCategories().get(0).getName() : ""

                    String query = String.format(insertQuery, title, news.getDate(), description, newsAgency, category, news.getSource(), news.getRssUrl());

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
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.buildSearchQuery(connection, params);
            ResultSet result = preparedStatement.executeQuery();
            List resultList = parseResult(result, News.class);
            return resultList;
        } catch (Exception e) {
            log.error("DbService.addToPostgres()", e);
        }
        return Collections.emptyList();
    }

    public <T> List parseResult(ResultSet resultSet, Class clazz) {
        List<T> list = new ArrayList();
        while (true) {
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
                list.add((T) clazzObject);
            } catch (SQLException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                log.error("DBService.parseResult()", e);
            }
        }
        return list;
    }

    public int getNumberOfNews() {
        return getDistinctCountOfcolumn("title");
    }

    private int getDistinctCountOfcolumn(String columnName) {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.distictCountQuery(connection, Arrays.asList(columnName), "news");
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getInt("total");
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
        }
        return -1;
    }

    public int getNumberOfNewsagency() {
        return getDistinctCountOfcolumn("newsagency");
    }
}