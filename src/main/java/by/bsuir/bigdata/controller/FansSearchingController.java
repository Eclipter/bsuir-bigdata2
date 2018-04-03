package by.bsuir.bigdata.controller;

import by.bsuir.bigdata.service.FansSearchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fans")
public class FansSearchingController {

    @Autowired
    public FansSearchingService fansSearchingService;

    @GetMapping(value = "/search")
    public Map<String, Integer> searchFans(@RequestParam String accountName)
    {
        return fansSearchingService.searchFans(accountName);
    }
}
