package net.dreamfteam.quiznet.data.rowmappers;

import net.dreamfteam.quiznet.data.entities.FriendsActivity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FriendsActivityMapper implements RowMapper<FriendsActivity> {
	@Override
	public FriendsActivity mapRow(ResultSet resultSet, int i) throws SQLException {

		return FriendsActivity.builder()
				.activityId(resultSet.getString("activity_id"))
				.content(resultSet.getString("content"))
				.datetime(resultSet.getTimestamp("datetime"))
				.userId(resultSet.getString("user_id"))
				.username(resultSet.getString("username"))
				.activityTypeId(resultSet.getInt("type_id"))
				.linkInfo(resultSet.getString("link_info"))
				.imageContent(resultSet.getBytes("image_content"))
				.build();
	}
}
