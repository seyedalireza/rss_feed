package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.News;
import in.nimbo.rssreader.model.RangedSearchParams;
import in.nimbo.rssreader.model.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.ds.PGPoolingDataSource;
import org.postgresql.util.PSQLException;
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
    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"date\",\"description\",\"newsagency\",\"category\",\"source\",\"rssurl\",\"hash\")\n" +
            "VALUES ('%s','%s','%s','%s','%s','%s','%s','%s')";
    @Value("${data-base-name}")
    public String dataBaseName = "news";
    @Value("${postgres-port}")
    public String port = "5432";
    @Value("${server-name}")
    public String serverName = "localhost";
    public PGPoolingDataSource source;
    private static DbService instance;
    private QueryBuilder queryBuilder;
    @Autowired
    public DbService(QueryBuilder queryBuilder) {
        this.queryBuilder = queryBuilder;
    }

    { instance = this; }

    public static DbService getInstance() {
        return instance;
    }

    @PostConstruct
    public void initialConnections() throws SQLException {
        createConnectionPool("dbservice0");
    }

    public void createConnectionPool(String sourceName) throws SQLException {
        if (source != null) {
            source.close();
            source = null;
        }
        source = new PGPoolingDataSource();
        source.setDataSourceName(sourceName);
        source.setPortNumber(Integer.parseInt(port));
        source.setInitialConnections(5);
        source.setServerName(serverName);
        source.setDatabaseName(dataBaseName);
        source.setUser(USER);
        source.setPassword(PASSWORD);
        source.setMaxConnections(25);
        source.getConnection();
    }


    public void closeConnections() {
        source.close();
    }

    public void addFeedToPostgres(List<News> newsList) throws Exception {// todo use executor and thread pool to increase performance
        try (Connection connection = source.getConnection()) {
//            Statement statement = connection.createStatement();
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

//                    String query = String.format(insertQuery, title, news.getDate(), description, newsAgency, category, news.getSource(), news.getRssUrl(), news.getHash());
//
//                    statement.executeQuery(query);


                    PreparedStatement newsQuery = queryBuilder.buildInsertQuery(connection, "news", news);
                    newsQuery.executeQuery();



                    log.info("news add successfully , news: ", news.toString());
                } catch (PSQLException e) {
                    if(!e.getMessage().contains("duplicate"))
                        log.error("can't add news. news may be exists.", e);
                } catch (Exception e) {
                    log.error("DbService.addToPostgres()", e);
//                    e.printStackTrace();
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

    public void insert(List<News> newsList) throws Exception {
        try (Connection connection = source.getConnection()) {
            for (News news : newsList) {
                PreparedStatement preparedStatement = queryBuilder.buildInsertQuery(connection, "news", news);
                ResultSet result = preparedStatement.executeQuery();
                // List resultList = parseResult(result, News.class);
                result.close();
            }

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

    public int getNumberOfNews() throws Exception {
        return getDistinctCountOfColumn("title");
    }

    private int getDistinctCountOfColumn(String columnName) throws Exception {
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.distinctCountQuery(connection, Arrays.asList(columnName), "news.news");
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

    private List<String> getDistinctValuesColumn(String columnName) throws Exception {
        List<String> result = new ArrayList<>();
        try (Connection connection = source.getConnection()) {
            PreparedStatement preparedStatement = queryBuilder.distinctValueQuery(connection, Arrays.asList(columnName), "news.news");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(resultSet.getString(columnName));
                }
            }
        } catch (SQLException e) {
            log.error("DbService.addToPostgres()", e);
            throw new Exception("internal error");
        }
        return result;
    }

    public List<String> getRssUrls() throws Exception {
        return getDistinctValuesColumn("rssurl");
    }


    public int getNumberOfNewsAgency() throws Exception {
        return getDistinctCountOfColumn("source");
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