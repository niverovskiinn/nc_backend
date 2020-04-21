package net.dreamfteam.quiznet.data.rowmappers;


import net.dreamfteam.quiznet.data.entities.Quiz;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class QuizMapper implements RowMapper<Quiz> {
    @Override
    public Quiz mapRow(ResultSet rs, int rowNum) throws SQLException {
        Quiz quiz = Quiz.builder()
                .id(rs.getString("quiz_id"))
                .title(rs.getString("title"))
                .description(rs.getString("description"))
                .imageRef(rs.getString("image_ref"))
                .creationDate(rs.getDate("ver_creation_datetime"))
                .creatorId(rs.getString("creator_id"))
                .activated(rs.getBoolean("activated"))
                .validated(rs.getBoolean("validated"))
                .language(rs.getString("quiz_lang"))
                .adminComment(rs.getString("admin_commentary"))
                .rating(rs.getFloat("rating"))
                .isFavourite(false)
                .build();

        return quiz;
    }
}
