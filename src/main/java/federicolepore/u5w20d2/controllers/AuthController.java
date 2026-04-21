package federicolepore.u5w20d2.controllers;

import federicolepore.u5w20d2.entities.User;
import federicolepore.u5w20d2.exceptions.ValidationException;
import federicolepore.u5w20d2.payloads.LoginDTO;
import federicolepore.u5w20d2.payloads.LoginRespDTO;
import federicolepore.u5w20d2.payloads.NewUserRespDTO;
import federicolepore.u5w20d2.payloads.UserDTO;
import federicolepore.u5w20d2.services.AuthService;
import federicolepore.u5w20d2.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    public AuthController(AuthService authService, UserService usersService) {

        this.authService = authService;
        this.userService = usersService;
    }

    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody LoginDTO body) {
        return new LoginRespDTO(this.authService.checkCredentialsAndGenerateToken(body));
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED) // 201
    public NewUserRespDTO saveUser(@RequestBody @Validated UserDTO body, BindingResult validationResult) {

        if (validationResult.hasErrors()) {
            List<String> errors = validationResult.getFieldErrors().stream().map(error -> error.getDefaultMessage()).toList();
            throw new ValidationException(errors);
        }

        User newUser = this.userService.save(body);
        return new NewUserRespDTO(newUser.getUserId());
    }

}
