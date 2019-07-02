package in.nimbo.rssreader.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Setter
@Getter
@Builder
public class SearchParams {
    private String title;
    private String date;
    private String newsAgency;
    private String text;
}
