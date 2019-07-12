package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.RangedSearchParams;
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
import java.util.List;

@Service
@Slf4j
public class DbService {
    @Value("${spring.datasource.username}")
    public static final String USER = "postgres";
    @Value("${spring.datasource.password}")
    public static final String PASSWORD = "1234";
    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"date\",\"description\",\"newsagency\",\"category\",\"source\",\"rssurl\")\n" +
            "VALUES ('%s','%s','%s','%s','%s','%s','%s')";
    @Value("${data-base-name}")
    public String dataBaseName = "news";
    @Value("${postgres-port}")
    public String port = "5432";
    @Value("${server-name}")
    public String serverName = "localhost";
    public PGPoolingDataSource source;
    private QueryBuilder queryBuilder;
    @Autowired
    public DbService(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    @PostConstruct
    public void initialConnections() {
        source = new PGPoolingDataSource();
        source.setDataSourceName("dbService-dataSource");
        source.setPortNumber(Integer.parseInt(port));
        source.setInitialConnections(5);
        source.setServerName(serverName);
        source.setDatabaseName(dataBaseName);
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setMaxConnections(25);
    }

    public void addFeedToPostgres(List<News> newsList) throws Exception {// todo use executor and thread pool to increase performance
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
                    log.info("news add successfully , news: ", news.toString());
                } catch (Exception e) {
                    log.error("DbService.addToPostgres()", e);
                }
            }
        } catch (Exception e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
    }

    public List search(SearchParams params) throws Exception {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.buildSearchQuery(connection, params);
            ResultSet result = preparedStatement.executeQuery();
            List resultList = parseResult(result, News.class);
            result.close();
            return resultList;
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
    }

    public int countSearch(SearchParams params) throws Exception {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.buildCountQuery(connection, params);
            try (ResultSet result = preparedStatement.executeQuery()) {
                if (result.next()) {
                    return result.getInt("count");
                }
            }
        } catch (Exception e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
        return 0;
    }

    private  <T> List parseResult(ResultSet resultSet, Class clazz) {
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

    public int getNumberOfNews() throws Exception {
        return getDistinctCountOfColumn("title");
    }

    private int getDistinctCountOfColumn(String columnName) throws Exception {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.distinctCountQuery(connection, Arrays.asList(columnName), "news");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
        return -1;
    }

    public int getNumberOfNewsAgency() throws Exception {
        return getDistinctCountOfColumn("newsagency");
    }

    public List rangedSearch(RangedSearchParams rangedSearch) throws Exception {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.buildRangedSearchQuery(connection, rangedSearch);
            ResultSet result = preparedStatement.executeQuery();
            List resultList = parseResult(result, News.class);
            result.close();
            return resultList;
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
    }
}