package sdacademy.projectplus.quizSdaProjectPlus.frontend;

import lombok.Data;

@Data
public class GameOptions {
    private int categoryId;
    private int numberOfQuestions;
    private Difficulty difficulty;
}
