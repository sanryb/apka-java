package pl.karpik.todoapp.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.karpik.todoapp.model.User;
import pl.karpik.todoapp.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public boolean register(String username, String password) {

        if (userRepository.findByUsername(username).isPresent()){
            return false;
        }
        String encodedPassword = passwordEncoder .encode(password);

        userRepository.save(new User(username,encodedPassword));

        return true;
    }


    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    public boolean changePassword (String username, String oldPassword, String newPassword) {
        User user = userRepository.findByUsername(username).orElse(null);

        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return true;
    }
}

