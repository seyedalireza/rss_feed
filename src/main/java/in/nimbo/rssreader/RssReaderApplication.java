package in.nimbo.rssreader;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class RssReaderApplication {
	public static void main(String[] args) {
		SpringApplication.run(RssReaderApplication.class, args);
	}

}
