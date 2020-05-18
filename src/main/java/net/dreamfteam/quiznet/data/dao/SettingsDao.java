package net.dreamfteam.quiznet.data.dao;

import net.dreamfteam.quiznet.data.entities.Setting;
import net.dreamfteam.quiznet.data.entities.Settings;
import net.dreamfteam.quiznet.web.dto.DtoSettings;

import java.util.List;

public interface SettingsDao {
    void initSettings(String userId);
    void editSettings(List<DtoSettings> settings, String userId);
    List<Setting> getSettings(String userId);
}
