package sdacademy.projectplus.quizSdaProjectPlus.database.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sdacademy.projectplus.quizSdaProjectPlus.database.entities.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, Long> {

}
