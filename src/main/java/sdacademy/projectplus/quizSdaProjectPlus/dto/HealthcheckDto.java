package sdacademy.projectplus.quizSdaProjectPlus.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class HealthcheckDto {
    private boolean status;
    private String message;
}
