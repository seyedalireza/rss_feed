package in.nimbo.rssreader.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
public class Api {

    @GetMapping("/hello")
    public String helloWorld() {
        return "HelloWorld";
    }
}
