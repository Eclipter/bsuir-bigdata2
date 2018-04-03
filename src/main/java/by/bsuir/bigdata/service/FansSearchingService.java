package by.bsuir.bigdata.service;

import by.bsuir.bigdata.aggregation.service.HadoopAggregationService;
import by.bsuir.bigdata.exception.FansSearchingException;
import me.postaddict.instagram.scraper.Instagram;
import me.postaddict.instagram.scraper.model.Account;
import me.postaddict.instagram.scraper.model.Media;
import me.postaddict.instagram.scraper.model.PageObject;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FansSearchingService {

    @Autowired
    private OkHttpClient okHttpClient;

    @Autowired
    private HadoopAggregationService hadoopAggregationService;

    @Value("${pages.count}")
    private Integer pagesCount;

    @Value("${likes.files.directory}")
    private String filesDirectory;

    public Map<String, Integer> searchFans(String accountName)
    {
        Instagram instagram = new Instagram(okHttpClient);

        try {

            PageObject<Media> medias = instagram.getMedias(accountName, pagesCount);

            medias.getNodes().forEach(media ->
            {
                try {

                    PageObject<Account> likes = instagram.getMediaLikes(media.getShortcode(), pagesCount);

                    if(!Files.exists(Paths.get(filesDirectory + accountName)))
                    {
                        Files.createDirectories(Paths.get(filesDirectory + accountName));
                    }

                    Files.write(Paths.get(filesDirectory + accountName + "/" + media.getId()),
                            likes.getNodes().stream().map(Account::getUsername).collect(Collectors.toList()),
                            StandardOpenOption.CREATE);

                } catch (IOException e) {
                    throw new FansSearchingException(e);
                }
            });

            return getStatistics(accountName);

        } catch (IOException e) {
            throw new FansSearchingException(e);
        }
    }

    public Map<String, Integer> getStatistics(String accountName)
    {
        try
        {
            hadoopAggregationService.aggregate(filesDirectory + accountName + "/",
                    filesDirectory + accountName + "/out");

            Map<String, Integer> result = new LinkedHashMap<>();

            Files.readAllLines(Paths.get(filesDirectory + accountName + "/out/part-r-00000"))
                    .stream()
                    .filter(line -> line.split("\t").length > 1)
                    .collect(Collectors.toMap(line -> line.split("\t")[0],
                            line -> Integer.parseInt(line.split("\t")[1]))).entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEachOrdered(x -> result.put(x.getKey(), x.getValue()));

            return result;
        } catch (IOException e) {
            throw new FansSearchingException(e);
        }
    }
}
