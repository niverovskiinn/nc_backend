package net.dreamfteam.quiznet.data.dao;

import net.dreamfteam.quiznet.data.entities.Game;
import net.dreamfteam.quiznet.data.entities.Question;
import net.dreamfteam.quiznet.data.entities.QuizCreatorFullStatistics;
import net.dreamfteam.quiznet.data.entities.UserCategoryAchievementInfo;
import net.dreamfteam.quiznet.web.dto.DtoGameCount;
import net.dreamfteam.quiznet.web.dto.DtoGameWinner;

import java.util.List;

public interface GameDao {

    Game createGame(Game game);

    Game getGameByAccessId(String accessId);

    Game getGame(String id);

    void startGame(String gameId);

    void updateGame(Game game);

    void deleteGame(String gameId);

    Question getQuestion(String gameId);

    List<UserCategoryAchievementInfo> getUserGamesInCategoryInfo(String userId, String gameId);

    QuizCreatorFullStatistics getAmountOfPlayedGamesCreatedByCreatorOfGame(String gameId);

    List<DtoGameWinner> getWinnersOfTheGame(String gameId);

    List<DtoGameCount> getGamesAmountForDay();

    int gameTime(String gameId);

}
