package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.RegisterDto;
import org.example.dto.UserDto;
import org.example.entity.Role;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return repository.findAll();
    }

    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public User findByEmail(String email) {
        return repository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Transactional
    public User register(RegisterDto dto) {
        String email = dto.getEmail().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        User u = new User();
        u.setName(dto.getName().trim());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        u.setRole(Role.USER);
        u.setEnabled(true);
        return repository.save(u);
    }

    @Transactional
    public User create(UserDto dto, String rawPassword) {
        String email = dto.getEmail().trim().toLowerCase();
        if (repository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Пользователь с таким email уже существует");
        }
        User u = new User();
        u.setName(dto.getName());
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRole(dto.getRole() != null ? dto.getRole() : Role.USER);
        u.setEnabled(dto.getEnabled() == null || dto.getEnabled());
        return repository.save(u);
    }

    @Transactional
    public User update(Long id, UserDto dto) {
        User user = findById(id);
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getEmail() != null) {
            String email = dto.getEmail().trim().toLowerCase();
            if (!email.equalsIgnoreCase(user.getEmail()) && repository.existsByEmailIgnoreCase(email)) {
                throw new IllegalArgumentException("Пользователь с таким email уже существует");
            }
            user.setEmail(email);
        }
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getEnabled() != null) user.setEnabled(dto.getEnabled());
        return repository.save(user);
    }

    @Transactional
    public void changePassword(Long id, String rawPassword) {
        User user = findById(id);
        user.setPassword(passwordEncoder.encode(rawPassword));
        repository.save(user);
    }

    public void delete(Long id) {
        findById(id);
        repository.deleteById(id);
    }
}