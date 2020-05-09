package net.dreamfteam.quiznet.service.impl;

import net.dreamfteam.quiznet.data.dao.GameSessionDao;
import net.dreamfteam.quiznet.data.entities.GameSession;
import net.dreamfteam.quiznet.exception.ValidationException;
import net.dreamfteam.quiznet.service.GameSessionService;
import net.dreamfteam.quiznet.web.dto.DtoGameSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameSessionServiceImpl implements GameSessionService {

    private final GameSessionDao gameSessionDao;

    @Autowired
    public GameSessionServiceImpl(GameSessionDao gameSessionDao) {
        this.gameSessionDao = gameSessionDao;
    }

    @Override
    public GameSession joinGame(String accessId, String userId) {

        if (!gameSessionDao.gameHasAvailableSlots(accessId)) {
            throw new ValidationException("Sorry, no slots are available");
        }

        return gameSessionDao.getSessionByAccessId(accessId, userId);
    }

    @Override
    public void setResult(DtoGameSession dtoGameSession) {

        GameSession gameSession =
                GameSession.builder()
                        .score(dtoGameSession.getScore())
                        .winner(false)
                        .durationTime(dtoGameSession.getDurationTime())
                        .id(dtoGameSession.getSessionId())
                        .build();

        gameSessionDao.updateSession(gameSession);
    }


}
