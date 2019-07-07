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
    private String date;
}
