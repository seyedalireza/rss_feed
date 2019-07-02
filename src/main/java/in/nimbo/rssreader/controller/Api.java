package in.nimbo.rssreader.controller;


import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import in.nimbo.rssreader.model.SearchParams;
import in.nimbo.rssreader.service.DbService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;

@RequestMapping("/api/v1")
@RestController
public class Api {
    private Logger logger;
    private DbService dbService;

    @Autowired
    public Api(Logger logger, DbService dbService) {
        this.logger = logger;
        this.dbService = dbService;
    }

    @PostMapping("/add-feed")
    public ResponseEntity<String> add(@RequestParam String url) {
        try {
            SyndFeed feed = new SyndFeedInput().build(new XmlReader(new URL(url)));
            dbService.addFeedToPostgres(feed);
        } catch (Exception e) {
            logger.error("Api.add(): ", e);
        }
        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @PostMapping("/search")
    public ResponseEntity<String> search(@RequestBody SearchParams params) {

        return new ResponseEntity<String>(HttpStatus.OK);
    }

    @GetMapping("/number-of-duplicate-news")
    public ResponseEntity<Integer> getNumberOfDuplicateNews() {
        return new ResponseEntity<Integer>(HttpStatus.OK);
    }

    @GetMapping("/number-of-newsagents")
    public ResponseEntity<Integer> getNumberOfNewsAgency() {
        return new ResponseEntity<Integer>(HttpStatus.OK);
    }

    @GetMapping("/number-of-news")
    public ResponseEntity<Integer> getNumberOfNews() {
        return new ResponseEntity<Integer>(HttpStatus.OK);
    }

    @GetMapping("/news-in-day")
    public ResponseEntity<String> getNewsOfDat() {
        return new ResponseEntity<String>(HttpStatus.OK);
    }

}
