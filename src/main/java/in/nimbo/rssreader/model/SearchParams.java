package in.nimbo.rssreader.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class SearchParams {
    private String title;
    private String date;
    private String newsAgency;
    private String text;
    private String source;
    private String rssUrl;

    @Override
    public String toString() {
        return "SearchParams{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", newsAgency='" + newsAgency + '\'' +
                ", text='" + text + '\'' +
                ", source='" + source + '\'' +
                ", rssUrl='" + rssUrl + '\'' +
                '}';
    }
}
