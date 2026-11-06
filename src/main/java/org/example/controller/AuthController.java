package org.example.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.dto.RegisterDto;
import org.example.dto.UserDto;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto register(@Valid @RequestBody RegisterDto dto) {
        User user = userService.register(dto);
        UserDto out = new UserDto();
        out.setId(user.getId());
        out.setName(user.getName());
        out.setEmail(user.getEmail());
        return out;
    }

    @GetMapping("/me")
    public UserDto me(Authentication auth) {
        User user = userService.findByEmail(auth.getName());
        UserDto out = new UserDto();
        out.setId(user.getId());
        out.setName(user.getName());
        out.setEmail(user.getEmail());
        out.setRole(user.getRole());
        out.setEnabled(user.isEnabled());
        return out;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrf(HttpServletRequest request) {
        CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (token == null) {
            token = (CsrfToken) request.getAttribute("_csrf");
        }
        String value = token != null ? token.getToken() : "";
        return Map.of(
                "headerName", "X-XSRF-TOKEN",
                "parameterName", "_csrf",
                "token", value
        );
    }
}