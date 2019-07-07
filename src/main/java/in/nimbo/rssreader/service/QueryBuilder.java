package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class QueryBuilder {

    public static final String insertQuery = "INSERT INTO \"news\".\"news\" (\"title\",\"date\",\"description\",\"newsagency\",\"category\")\n" +
            "VALUES ('%s','%s','%s','%s','%s')";

    public PreparedStatement buildSearchQuery(Connection connection, SearchParams params) throws SQLException {
        String select = "SELECT * FROM news WHERE ";
        return buildLikeQueryFromParameters(connection, params, select);
    }

    public PreparedStatement buildCountQuery(Connection connection, SearchParams params) throws SQLException {
        String count = "SELECT COUNT(*) FROM news WHERE ";
        return buildLikeQueryFromParameters(connection, params, count);
    }

    public PreparedStatement buildInsertQuery(Connection connection, String tableName, Object instance) throws SQLException {//todo test this function
        String count = "INSERT INTO \"news\".\"" + tableName + "\"(";
        StringBuilder queryBuilder = new StringBuilder(count);
        HashMap<Integer, String> map = new HashMap<>();
        int counter = 1;
        for (Field field : instance.getClass().getDeclaredFields()) {
            Object value = null;
            value = getValueOfField(instance, field, value);
            if (value != null) {
                queryBuilder.append(field.getName().toLowerCase()).append(" , ");
                map.put(counter, value.toString());
                counter++;
            }
        }
        String query = queryBuilder.substring(0, queryBuilder.length() - 3) + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            preparedStatement.setString(entry.getKey(), entry.getValue());
        }
        return preparedStatement;
    }

    private Object getValueOfField(Object instance, Field field, Object value) {
        try {
            boolean accessible = field.isAccessible();
            field.setAccessible(true);
            value = field.get(instance);
            field.setAccessible(accessible);
        } catch (IllegalAccessException e) {
            //ignored
        }
        return value;
    }

    private PreparedStatement buildLikeQueryFromParameters(Connection connection, SearchParams params, String prefix) throws SQLException {
        StringBuilder queryBuilder = new StringBuilder(prefix);
        Map<Integer, String> map = new HashMap<>();
        int counter = 1;
        for (Field field : SearchParams.class.getDeclaredFields()) {
            Object value = null;
            value = getValueOfField(params, field, value);
            if (value != null) {
                queryBuilder.append(field.getName().toLowerCase()).append("=").append("?")
                        .append(" and ");
                map.put(counter, value.toString());
                counter++;
            }
        }
        String query = queryBuilder.substring(0, queryBuilder.length() - 4) + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(query));

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            preparedStatement.setString(entry.getKey(), entry.getValue());
        }
        return preparedStatement;
    }


}
