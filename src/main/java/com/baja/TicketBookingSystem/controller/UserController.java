package com.baja.TicketBookingSystem.controller;

import com.baja.TicketBookingSystem.dto.UserDto;
import com.baja.TicketBookingSystem.entity.User;
import com.baja.TicketBookingSystem.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> registerUser(@RequestBody @Valid UserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }
}
