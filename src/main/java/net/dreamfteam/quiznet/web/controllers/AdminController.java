package net.dreamfteam.quiznet.web.controllers;

import net.dreamfteam.quiznet.configs.constants.Constants;
import net.dreamfteam.quiznet.configs.security.IAuthenticationFacade;
import net.dreamfteam.quiznet.data.entities.Role;
import net.dreamfteam.quiznet.data.entities.User;
import net.dreamfteam.quiznet.exception.ValidationException;
import net.dreamfteam.quiznet.service.SettingsService;
import net.dreamfteam.quiznet.service.UserService;
import net.dreamfteam.quiznet.web.dto.DtoAdminActivation;
import net.dreamfteam.quiznet.web.dto.DtoAdminSignUp;
import net.dreamfteam.quiznet.web.dto.DtoEditAdminProfile;
import net.dreamfteam.quiznet.web.dto.DtoUser;
import net.dreamfteam.quiznet.web.validators.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping(Constants.ADMIN_URLS)
public class AdminController {

    private final SettingsService settingsService;

    final private UserService userService;
    final private IAuthenticationFacade authenticationFacade;

    @Autowired
    public AdminController(UserService userService, IAuthenticationFacade authenticationFacade,
                           SettingsService settingsService) {
        this.userService = userService;
        this.authenticationFacade = authenticationFacade;
        this.settingsService = settingsService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PatchMapping("/edit/{field}")
    public ResponseEntity<?> editAdmin(@PathVariable("field") String field, @RequestBody DtoEditAdminProfile editAdminProfile) {

        User currentUser = userService.getById(authenticationFacade.getUserId());
        User otherUser = userService.getById(editAdminProfile.getId());

        if (otherUser == null) {
            throw new ValidationException(Constants.USER_NOT_FOUND_WITH_ID + editAdminProfile.getId());
        }

        if (field.equals("role") && !StringUtils.isEmpty(editAdminProfile.getRole())) {
            if (currentUser.getRole() != Role.ROLE_SUPERADMIN) {
                throw new ValidationException(Constants.NOT_HAVE_CAPABILITIES);
            }

            otherUser.setRole(Role.valueOf(editAdminProfile.getRole()));
            userService.update(otherUser);
        }

        if (currentUser.getRole().ordinal() <= otherUser.getRole().ordinal()) {
            throw new ValidationException(Constants.NOT_HAVE_CAPABILITIES);
        }

        if (field.equals("aboutMe")) {
            otherUser.setAboutMe(editAdminProfile.getAboutMe());
            userService.update(otherUser);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PatchMapping("/edit/image")
    public ResponseEntity<?> activate(@RequestParam("key") MultipartFile image, @RequestParam("userId") String userId) {

        User currentUser = userService.getById(authenticationFacade.getUserId());
        User otherUser = userService.getById(userId);

        if (currentUser.getRole().ordinal() <= otherUser.getRole().ordinal()) {
            throw new ValidationException(Constants.NOT_HAVE_CAPABILITIES);
        }

        try {
            otherUser.setImage(image.getBytes());
        } catch (IOException e) {
            throw new ValidationException(Constants.IMAGE_BROKEN);
        }

        userService.update(otherUser);

        return new ResponseEntity<>(HttpStatus.OK);
    }


    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping
    public ResponseEntity<DtoUser> create(@RequestBody DtoAdminSignUp newAdmin) {

        User currentUser = userService.getById(authenticationFacade.getUserId());
        UserValidator.validate(newAdmin);
        User user = userService.getByEmail(newAdmin.getEmail());

        if (user != null) {
            throw new ValidationException(Constants.EMAIl_TAKEN + newAdmin.getEmail());
        }

        if (userService.getByUsername(newAdmin.getUsername()) != null) {
            throw new ValidationException(Constants.USERNAME_TAKEN + newAdmin.getUsername());
        }

        if (currentUser.getRole().ordinal() <= Role.valueOf(newAdmin.getRole()).ordinal()) {
            throw new ValidationException(Constants.NOT_HAVE_CAPABILITIES);
        }

        User saved = userService.saveAdmin(authenticationFacade.getUsername(), newAdmin.toUser());

        settingsService.initSettings(saved.getId(), saved.getRole(), "en");

        return new ResponseEntity<>(DtoUser.fromUser(saved), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPERADMIN')")
    @PostMapping("/activation")
    public ResponseEntity<?> activation(@RequestBody DtoAdminActivation admin) {

        UserValidator.validate(admin);

        User currentUser = userService.getById(authenticationFacade.getUserId());
        User userToChange = userService.getById(admin.getId());

        if (currentUser.getRole().ordinal() > userToChange.getRole().ordinal()) {
            userToChange.setActivated(admin.isActivated());
            userService.update(userToChange);
        } else throw new ValidationException(Constants.NOT_HAVE_CAPABILITIES);

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
