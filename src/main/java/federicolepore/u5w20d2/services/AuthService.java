package federicolepore.u5w20d2.services;

import federicolepore.u5w20d2.entities.User;
import federicolepore.u5w20d2.exceptions.NotFoundException;
import federicolepore.u5w20d2.exceptions.UnauthorizedException;
import federicolepore.u5w20d2.payloads.LoginDTO;
import federicolepore.u5w20d2.security.TokenTools;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final TokenTools tokenTools;
    private final PasswordEncoder bcrypt;

    public AuthService(UserService userService, TokenTools tokenTools, PasswordEncoder bcrypt) {

        this.userService = userService;
        this.tokenTools = tokenTools;
        this.bcrypt = bcrypt;
    }

    public String checkCredentialsAndGenerateToken(LoginDTO body) {

        try {
            User found = this.userService.findByEmail(body.email());
            if (this.bcrypt.matches(body.password(), found.getPassword())) {

                return this.tokenTools.generateToken(found);

            } else {
                throw new UnauthorizedException("Credenziali errate");
            }
        } catch (NotFoundException ex) {
            throw new UnauthorizedException("Credenziali errate");
        }

    }
}
