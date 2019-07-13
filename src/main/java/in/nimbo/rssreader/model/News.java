package in.nimbo.rssreader.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

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
    private String hash;


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
                ", hash='" + hash + '\'' +
                '}';
    }

    public String getHash() {
        if(title == null || source == null)
            this.hash = "";
        else
            this.hash = org.apache.commons.codec.digest.DigestUtils.md5Hex(this.title + this.source);
        return this.hash;
    }
}
