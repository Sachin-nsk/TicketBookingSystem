package com.baja.TicketBookingSystem.service;

import com.baja.TicketBookingSystem.dto.UserDto;
import com.baja.TicketBookingSystem.entity.User;
import com.baja.TicketBookingSystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User registerUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        user.setPhoneNumber(userDto.getPhoneNumber());

        return userRepository.save(user);
    }
}
