package in.nimbo.rssreader.service;

import in.nimbo.rssreader.model.RangedSearchParams;
import in.nimbo.rssreader.model.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class QueryBuilder {
    public PreparedStatement buildSearchQuery(Connection connection, SearchParams params) throws SQLException {
        String select = "SELECT * FROM news.news WHERE ";
        return buildLikeQueryFromParameters(connection, params, select, null);
    }

    public PreparedStatement buildRangedSearchQuery(Connection connection, RangedSearchParams searchQuery)
            throws SQLException {
        String select = "SELECT * FROM news.news WHERE date BETWEEN ? and ? and ";
        HashMap<Integer, String> prePs = new HashMap<>();
        prePs.put(1, searchQuery.getStartDate());
        prePs.put(1, searchQuery.getEndDate());
        return buildLikeQueryFromParameters(connection, searchQuery.getParams(), select, prePs);
    }

    public PreparedStatement buildCountQuery(Connection connection, SearchParams params) throws SQLException {
        String count = "SELECT COUNT(*) FROM news.news WHERE ";
        return buildLikeQueryFromParameters(connection, params, count, null);
    }

    public PreparedStatement distinctCountQuery(Connection connection, List<String> columnNames, String tableName)
            throws SQLException {
        StringBuilder c = new StringBuilder();
        for (int i = 0; i < columnNames.size(); i++) {
            c.append(columnNames.get(i)).append(", ");
        }
        String co = c.substring(0, c.length() - 2);
        String query = "SELECT COUNT(DISTINCT " + co + ") FROM " + tableName + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement;
    }

    public PreparedStatement distinctValueQuery(Connection connection, List<String> columnNames, String tableName)
            throws SQLException {
        StringBuilder c = new StringBuilder();
        for (int i = 0; i < columnNames.size(); i++) {
            c.append(columnNames.get(i)).append(", ");
        }
        String co = c.substring(0, c.length() - 2);
        String query = "SELECT DISTINCT " + co + " FROM " + tableName + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement;
    }

    public PreparedStatement buildInsertQuery(Connection connection, String tableName, Object instance)
            throws SQLException {
        String count = "INSERT INTO news." + tableName + " (";
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
        queryBuilder = new StringBuilder(queryBuilder.substring(0, queryBuilder.length() - 3) + ") VALUES (");
        for (int i = 0; i < map.size(); i++) {
            if (i == map.size() - 1)
                queryBuilder.append("?);");
            else
                queryBuilder.append("?, ");
        }
        PreparedStatement preparedStatement = connection.prepareStatement(queryBuilder.toString());

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            if (entry.getValue().matches("(\\d+)-(\\d+)-(\\d+)"))
                preparedStatement.setDate(entry.getKey(), Date.valueOf(entry.getValue()));
            else
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

    private PreparedStatement buildLikeQueryFromParameters(Connection connection, SearchParams params,
                                                           String prefix, Map<Integer, String> prePS) throws SQLException {
        if (prePS == null) {
            prePS = new HashMap<>();
        }
        StringBuilder queryBuilder = new StringBuilder(prefix);
        Map<Integer, String> map = new HashMap<>(prePS);
        ArrayList<Integer> keys = new ArrayList<>(map.keySet());
        int counter = 1;
        for (Integer key : keys) {
            if (key >= counter) {
                counter = key + 1;
            }
        }
        for (Field field : SearchParams.class.getDeclaredFields()) {
            Object value = null;
            value = getValueOfField(params, field, value);
            if (value != null) {
                queryBuilder.append(field.getName().toLowerCase()).append(" like ").append("?")
                        .append(" and ");
                map.put(counter, value.toString());
                counter++;
            }
        }
        String query = queryBuilder.substring(0, queryBuilder.length() - 4) + ";";
        PreparedStatement preparedStatement = connection.prepareStatement(String.valueOf(query));

        for (Map.Entry<Integer, String> entry : map.entrySet()) {
            preparedStatement.setString(entry.getKey(), "%" + entry.getValue() + "%");
        }
        return preparedStatement;
    }
}
