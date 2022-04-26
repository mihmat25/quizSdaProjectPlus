package sdacademy.projectplus.quizSdaProjectPlus;

import ch.qos.logback.classic.PatternLayout;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import sdacademy.projectplus.quizSdaProjectPlus.database.entities.PlayerEntity;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.List;

@Component
@Log
public class StartupRunner implements CommandLineRunner {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String...args) throws Exception {
        log.info("Executing startup actions...");
        PlayerEntity player = new PlayerEntity("John");
        log.info("Created player: " + player);

        entityManager.persist(player);
        log.info("Same player after persist: " + player);

        log.info("List of players from database:");
        Query q = entityManager.createQuery("SELECT p FROM PLAYERS p");
        List<PlayerEntity> playersFromDb = (List<PlayerEntity>)q.getResultList();

        for (PlayerEntity playerFromDb : playersFromDb) {
            log.info("Player from DB: " + playerFromDb);
        }
    }
}
