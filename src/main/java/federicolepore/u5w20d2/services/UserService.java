package federicolepore.u5w20d2.services;

import federicolepore.u5w20d2.entities.User;
import federicolepore.u5w20d2.exceptions.BadRequestException;
import federicolepore.u5w20d2.exceptions.NotFoundException;
import federicolepore.u5w20d2.payloads.UserDTO;
import federicolepore.u5w20d2.repositories.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder bcrypt;

    public UserService(UsersRepository usersRepository, PasswordEncoder bcrypt) {
        this.usersRepository = usersRepository;
        this.bcrypt = bcrypt;
    }


    public User save(UserDTO body) {

        if (this.usersRepository.existsByEmail(body.email()))
            throw new BadRequestException("L'indirizzo email " + body.email() + " è già in uso!");

        User newUser = new User(body.name(), body.surname(), body.email(), this.bcrypt.encode(body.password()), body.dateOfBirth());
        User savedUser = this.usersRepository.save(newUser);

        log.info("L'utente con id " + savedUser.getUserId() + " è stato salvato correttamente!");

        return savedUser;
    }


    public Page<User> findAll(int page, int size, String sortBy) {
        if (size > 100 || size < 0) size = 10;
        if (page < 0) page = 0;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        return this.usersRepository.findAll(pageable);
    }


    public User findById(UUID userId) {
        return this.usersRepository.findById(userId).orElseThrow(() -> new NotFoundException(userId));
    }


    public User findByIdAndUpdate(UUID userId, UserDTO body) {
        // 1. Cerchiamo l'utente tramite userId
        User found = this.findById(userId);

        // 2. Controllo che email non sia già in uso (lo faccio solo se sta effettivamente cambiando email)
        if (!found.getEmail().equals(body.email())) {
            if (this.usersRepository.existsByEmail(body.email()))
                throw new BadRequestException("L'indirizzo email " + body.email() + " è già in uso!");
        }

        // 3. Modifico l'utente trovato
        found.setName(body.name());
        found.setSurname(body.surname());
        found.setEmail(body.email());
        found.setPassword(body.password());
        found.setDateOfBirth(body.dateOfBirth());
        found.setAvatarURL("https://ui-avatars.com/api?name=" + body.name() + "+" + body.surname());

        // 4. Salvo
        User updateUser = this.usersRepository.save(found);

        // 5. Log
        log.info("L'utente " + updateUser.getUserId() + " è stato modificato correttamente");

        // 6. Ritorno l'utente modificato
        return updateUser;
    }


    public void findByIdAndDelete(UUID userId) {
        User found = this.findById(userId);
        this.usersRepository.delete(found);
    }

    
    public User findByEmail(String email) {
        return this.usersRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("L'utente con email " + email + " non è stato trovato!"));
    }


}
