package pl.karpik.todoapp.service;

import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import pl.karpik.todoapp.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername (String username)
        throws UsernameNotFoundException {

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User .builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();
    }
}
