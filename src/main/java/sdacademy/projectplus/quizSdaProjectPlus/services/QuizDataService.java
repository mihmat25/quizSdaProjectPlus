package sdacademy.projectplus.quizSdaProjectPlus.services;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import sdacademy.projectplus.quizSdaProjectPlus.dto.CategoriesDto;
import sdacademy.projectplus.quizSdaProjectPlus.dto.QuestionsDto;

import java.net.URI;
import java.util.List;

@Service
@Log
@Getter
public class QuizDataService {

    public List<CategoriesDto.CategoryDto> getQuizCategories() {
        RestTemplate restTemplate = new RestTemplate();
        CategoriesDto result = restTemplate.getForObject("https://opentdb.com/api_category.php", CategoriesDto.class);
        log.info("Quiz categories: " + result.getTrivia_categories());
        return result.getTrivia_categories();
    }

    public void getQuizQuestions(){
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://opentdb.com/api.php")
                .queryParam("amount", 2)
                .queryParam("category", 25)
                .queryParam("difficulty", "medium")
                .build().toUri();
        log.info("Quiz question retrieve URL: " + uri);

        QuestionsDto result = restTemplate.getForObject(uri, QuestionsDto.class);
        log.info("Quiz questions:" + result.getResults());
    }
}
