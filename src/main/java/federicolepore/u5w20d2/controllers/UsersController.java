package federicolepore.u5w20d2.controllers;

import federicolepore.u5w20d2.entities.User;
import federicolepore.u5w20d2.payloads.UserDTO;
import federicolepore.u5w20d2.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService usersService) {
        this.userService = usersService;
    }


    // 2. GET http://localhost:3001/users
    @GetMapping
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    public Page<User> getUsers(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size,
                               @RequestParam(defaultValue = "surname") String sortBy) {
        return this.userService.findAll(page, size, sortBy);
    }

    @GetMapping("/me")
    public User getOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        return currentAuthenticatedUser;
    }

    @PutMapping("/me")
    public User updateOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser, @RequestBody UserDTO body) {
        return this.userService.findByIdAndUpdate(currentAuthenticatedUser.getUserId(), body);
    }

    @DeleteMapping("/me")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOwnProfile(@AuthenticationPrincipal User currentAuthenticatedUser) {
        this.userService.findByIdAndDelete(currentAuthenticatedUser.getUserId());
    }


    @GetMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    public User getById(@PathVariable UUID userId) {
        return this.userService.findById(userId);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    public User getByIdAndUpdate(@PathVariable UUID userId, @RequestBody UserDTO body) {
        return this.userService.findByIdAndUpdate(userId, body);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @PreAuthorize("hasAnyAuthority('SUPERADMIN', 'ADMIN')")
    public void getByIdAndDelete(@PathVariable UUID userId) {
        this.userService.findByIdAndDelete(userId);
    }

}
