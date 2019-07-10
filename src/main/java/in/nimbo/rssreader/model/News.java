package in.nimbo.rssreader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class News {
    private String title;
    private String description;
    private String newsAgency;
    private String category;
    private String date;
    private String source;
    private String rssUrl;


    @Override
    public String toString() {
        return "News{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", newsAgency='" + newsAgency + '\'' +
                ", category='" + category + '\'' +
                ", date='" + date + '\'' +
                ", source='" + source + '\'' +
                ", rssUrl='" + rssUrl + '\'' +
                '}';
    }
}
