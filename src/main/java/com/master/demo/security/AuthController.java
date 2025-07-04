package com.master.demo.security;

import com.master.demo.entity.Employee;
import com.master.demo.entity.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        log.info("Login attempt for employee: {}", username);

        Optional<Employee> empOpt = employeeRepository.findAll().stream()
                .filter(emp -> emp.getName().equalsIgnoreCase(username))
                .findFirst();

        if (empOpt.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid username"));
        }

        Employee employee = empOpt.get();
        if (!passwordEncoder.matches(password, employee.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid password"));
        }

        String token = jwtUtil.generateToken(username, employee.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", username);
        response.put("id", employee.getId());
        response.put("role", employee.getRole());
        response.put("expiresInMinutes", 30);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out — discard token on client side."));
    }
}
