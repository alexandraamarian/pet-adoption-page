package org.ubb.adoption_service.service.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.ubb.adoption_service.api.LoginRequest;
import org.ubb.adoption_service.api.RegisterRequest;
import org.ubb.adoption_service.exception.UsernameTakenException;
import org.ubb.adoption_service.model.UserEntity;
import org.ubb.adoption_service.repository.UserRepository;

@Service
public class UserService
{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void registerUser(RegisterRequest request)
    {
        if (userRepository.findByUsername(request.userName()).isPresent())
        {
            throw new UsernameTakenException("Username already taken!");
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.userName());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        userRepository.save(user);
    }

    public UserEntity getUserByUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public void authenticateUser(LoginRequest loginRequest)
    {
        UserEntity userEntity = getUserByUsername(loginRequest.userName());
        // Password is not encoded correctly
        if (!passwordEncoder.matches(loginRequest.password(), userEntity.getPassword()))
        {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
