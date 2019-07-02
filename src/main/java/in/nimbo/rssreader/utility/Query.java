package in.nimbo.rssreader.utility;

import in.nimbo.rssreader.model.SearchParams;

import java.lang.reflect.Field;

public class Query {

    public String buildSearchQuery(SearchParams params) {
        StringBuilder query = new StringBuilder("SELECT * FROM news WHERE ");
        for (Field field : SearchParams.class.getDeclaredFields()) {
            Object value = null;
            try {
                field.setAccessible(true);
                value = field.get(params);
                field.setAccessible(false);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (value != null)
                query.append(field.getName().toLowerCase()).append("=").append("\'").append(value.toString()).append("\'")
                        .append(" and ");
        }
        return query.substring(0, query.length() - 4) + ";";
    }
}
