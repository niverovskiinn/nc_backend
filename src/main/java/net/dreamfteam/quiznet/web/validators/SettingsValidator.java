package net.dreamfteam.quiznet.web.validators;

import net.dreamfteam.quiznet.exception.ValidationException;
import net.dreamfteam.quiznet.web.dto.DtoSettings;
import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsValidator {

    private static final String BOOLEAN_REGEX = "^(true|false)$";

    private static final String EMPTY_PROPERTY_EXCEPTION_MESSAGE = "Field '%s' must be provided";
    private static final String REGEX_EXCEPTION_MESSAGE = "Field '%s' must fit this pattern %s";

    public static void validate(DtoSettings settings) throws ValidationException {
        isNotEmpty(settings.getSeeFriendsActivities(), "see friends activities");
        isNotEmpty(settings.getSeeAnnouncements(), "see announcements");
        validateWithRegularExpression(settings.getSeeFriendsActivities(), BOOLEAN_REGEX, "see friends activities");
        validateWithRegularExpression(settings.getSeeAnnouncements(), BOOLEAN_REGEX, "see announcements");
    }

    private static void isNotEmpty(Object value, String propertyName) {
        if (value == null || StringUtils.isEmpty(value)) {
            throw new ValidationException(String.format(EMPTY_PROPERTY_EXCEPTION_MESSAGE, propertyName));
        }

    }

    private static void validateWithRegularExpression(Object value, String regex, String propertyName) {

        Matcher matcher = Pattern.compile(regex).matcher(String.valueOf(value));
        if (!matcher.matches()) {
            throw new ValidationException(String.format(REGEX_EXCEPTION_MESSAGE, propertyName, regex));
        }

    }

}
