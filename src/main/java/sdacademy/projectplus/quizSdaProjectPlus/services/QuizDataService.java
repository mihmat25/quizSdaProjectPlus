package sdacademy.projectplus.quizSdaProjectPlus.services;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import sdacademy.projectplus.quizSdaProjectPlus.dto.CategoriesDto;
import sdacademy.projectplus.quizSdaProjectPlus.dto.CategoryQuestionCountInfoDto;
import sdacademy.projectplus.quizSdaProjectPlus.dto.QuestionsDto;
import sdacademy.projectplus.quizSdaProjectPlus.frontend.Difficulty;
import sdacademy.projectplus.quizSdaProjectPlus.frontend.GameOptions;

import java.net.URI;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    public List<QuestionsDto.QuestionDto> getQuizQuestions(GameOptions gameOptions) {
        CategoryQuestionCountInfoDto categoryQuestionCount = getCategoryQuestionCount(gameOptions.getCategoryId());
        int availableQuestionCount = categoryQuestionCount.getQuestionCountForDifficulty(gameOptions.getDifficulty());
        if (availableQuestionCount >= gameOptions.getNumberOfQuestions()) {
            return getQuizQuestions(gameOptions.getNumberOfQuestions(), gameOptions.getCategoryId(), gameOptions.getDifficulty());
        } else {
            return getQuizQuestions(availableQuestionCount, gameOptions.getCategoryId(), gameOptions.getDifficulty());
        }
    }

    private List<QuestionsDto.QuestionDto> getQuizQuestions(int numberOfQuestions, int categoryId, Difficulty difficulty) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://opentdb.com/api.php")
                .queryParam("amount", numberOfQuestions)
                .queryParam("category", categoryId)
                .queryParam("difficulty", difficulty.name().toLowerCase())
                .build().toUri();
        log.info("Quiz question retrieve URL: " + uri);

        QuestionsDto result = restTemplate.getForObject(uri, QuestionsDto.class);
        log.info("Quiz questions: Open Trivia DB response code = " + result.getResponseCode() + ". Content: " + result.getResults());
        return result.getResults();
    }

    private CategoryQuestionCountInfoDto getCategoryQuestionCount(int categoryId) {
        RestTemplate restTemplate = new RestTemplate();

        URI uri = UriComponentsBuilder.fromHttpUrl("https://opentdb.com/api_count.php")
                .queryParam("category", categoryId)
                .build().toUri();
        log.info("Quiz category question count retrieve URL: " + uri);
        CategoryQuestionCountInfoDto result = restTemplate.getForObject(uri, CategoryQuestionCountInfoDto.class);
        log.info("Quiz category question count content: " + result);
        return result;
    }

    static Map<Difficulty, Integer> calculateEachDifficultyQuestionCount(int numberOfQuestions, Difficulty difficulty, CategoryQuestionCountInfoDto categoryQuestionCount) {
        Map<Difficulty, Integer> eachDifficultyQuestionCount = new EnumMap<>(Difficulty.class);
        eachDifficultyQuestionCount.put(difficulty, Math.min(numberOfQuestions, categoryQuestionCount.getQuestionCountForDifficulty(difficulty)));

        int missingQuestions = numberOfQuestions - eachDifficultyQuestionCount.values().stream().mapToInt(i -> i).sum();
        while (missingQuestions > 0) {
            Difficulty closestDifficulty = Difficulty.calculateNextDifficulty(eachDifficultyQuestionCount.keySet());
            if (closestDifficulty == null) {
                log.warning("Not enough question in given category!");
                break;
            }
            eachDifficultyQuestionCount.put(closestDifficulty, Math.min(missingQuestions, categoryQuestionCount.getQuestionCountForDifficulty(closestDifficulty)));

            missingQuestions = numberOfQuestions - eachDifficultyQuestionCount.values().stream().mapToInt(i -> i).sum();
        }

        if (difficulty == Difficulty.MEDIUM) {
            Difficulty otherDifficulty = Difficulty.calculateNextDifficulty(eachDifficultyQuestionCount.keySet());
            if (otherDifficulty != null) {
                Difficulty filledDifficulty = difficulty.getClosestDifficulty();
                final int numberToTransfer = Math.min(eachDifficultyQuestionCount.get(filledDifficulty) / 2, categoryQuestionCount.getQuestionCountForDifficulty(otherDifficulty));
                eachDifficultyQuestionCount.computeIfPresent(filledDifficulty, (d, count) -> count - numberToTransfer);
                eachDifficultyQuestionCount.put(otherDifficulty, numberToTransfer);
            }
        }

        return eachDifficultyQuestionCount;
    }
}
