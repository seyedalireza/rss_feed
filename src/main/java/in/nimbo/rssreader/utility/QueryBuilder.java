package in.nimbo.rssreader.utility;

import in.nimbo.rssreader.model.SearchParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;

@Service
@Slf4j
public class QueryBuilder {

    public String buildSearchQuery(SearchParams params) {
        StringBuilder query = new StringBuilder("SELECT * FROM news.news WHERE ");
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
                        .append(" AND ");
        }
        return query.substring(0, query.length() - 4) + ";";
    }
}
