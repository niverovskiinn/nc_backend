package net.dreamfteam.quiznet.service.impl;

import net.dreamfteam.quiznet.data.dao.GameDao;
import net.dreamfteam.quiznet.data.dao.GameSessionDao;
import net.dreamfteam.quiznet.data.dao.QuizDao;
import net.dreamfteam.quiznet.data.entities.Game;
import net.dreamfteam.quiznet.data.entities.GameSession;
import net.dreamfteam.quiznet.data.entities.Question;
import net.dreamfteam.quiznet.exception.ValidationException;
import net.dreamfteam.quiznet.service.GameService;
import net.dreamfteam.quiznet.service.SseService;
import net.dreamfteam.quiznet.web.dto.DtoGame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameServiceImpl implements GameService {

    private final GameDao gameDao;
    private final QuizDao quizDao;
    private final GameSessionDao gameSessionDao;
    private final SseService sseService;


    @Autowired
    public GameServiceImpl(GameDao gameDao, QuizDao quizDao, GameSessionDao gameSessionDao, SseService sseService) {
        this.gameDao = gameDao;
        this.quizDao = quizDao;
        this.gameSessionDao = gameSessionDao;
        this.sseService = sseService;
    }

    @Override
    public Game createGame(DtoGame dtoGame, String userId) {
        checkQuizExistance(dtoGame.getQuizId());

        Game game = Game.builder()
                .roundDuration(dtoGame.getRoundDuration())
                .numberOfQuestions(dtoGame.getNumberOfQuestions())
                .maxUsersCount(dtoGame.getMaxUsersCount())
                .additionalPoints(dtoGame.isAdditionalPoints())
                .breakTime(dtoGame.getBreakTime())
                .quizId(dtoGame.getQuizId())
                .build();

        game = gameDao.createGame(game);

        // CREATING SESSION OF CREATOR
        GameSession gameSession = GameSession.builder()
                .userId(userId)
                .gameId(game.getId())
                .score(0)
                .winner(false)
                .creator(true)
                .savedByUser(true)   // REFACTOR FORM ANONYMOUS
                .durationTime(0)
                .build();

        gameSessionDao.createSession(gameSession);

        return game;
    }

    @Override
    public void updateGame(DtoGame dtoGame) {
        Game game = Game.builder()
                .id(dtoGame.getId())
                .startDatetime(dtoGame.getStartDatetime())
                .roundDuration(dtoGame.getRoundDuration())
                .numberOfQuestions(dtoGame.getNumberOfQuestions())
                .maxUsersCount(dtoGame.getMaxUsersCount())
                .additionalPoints(dtoGame.isAdditionalPoints())
                .breakTime(dtoGame.getBreakTime())
                .quizId(dtoGame.getQuizId())
                .build();

        gameDao.updateGame(game);
    }

    @Override
    public Game getGameByAccessId(String accessId) {
        return gameDao.getGameByAccessId(accessId);
    }

    @Override
    public Game getGameById(String gameId) {
        return gameDao.getGame(gameId);
    }

    @Override
    public void startGame(String gameId) {
        sseService.send(gameId,"start");
        gameDao.startGame(gameId);
    }

    private void checkQuizExistance(String quizId) {
        if (quizDao.getQuiz(quizId) == null) {
            throw new ValidationException("Quiz with id: " + quizId + " not exists");
        }
    }

    public Question getQuestion(String gameId) {
        Question question = gameDao.getQuestion(gameId);
        if (question == null) {
            throw new NullPointerException("No questions for " + gameId);
        }
        return quizDao.loadAnswersForQuestion(gameDao.getQuestion(gameId), 0);
    }

}
