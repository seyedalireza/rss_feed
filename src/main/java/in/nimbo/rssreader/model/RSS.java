package in.nimbo.rssreader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RSS {
    private String site;
    private String url;
    private int usecount;


    @Override
    public String toString() {
        return "RSS{" +
                "site='" + site + '\'' +
                ", url='" + url + '\'' +
                ", usecount=" + usecount +
                '}';
    }
}
