package ru.sicampus.bootcamp2026.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sicampus.bootcamp2026.dto.fromApp.NewUserDTO;
import ru.sicampus.bootcamp2026.dto.toApp.UserDTO;
import ru.sicampus.bootcamp2026.entity.User;
import ru.sicampus.bootcamp2026.exception.LoginDataEmailAlreadyTakenException;
import ru.sicampus.bootcamp2026.exception.UserNotFoundException;
import ru.sicampus.bootcamp2026.repository.LoginDataRepository;
import ru.sicampus.bootcamp2026.repository.UserRepository;
import ru.sicampus.bootcamp2026.service.UserService;
import ru.sicampus.bootcamp2026.util.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final LoginDataRepository loginDataRepository;

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(UserMapper::convertToDto)
                .orElseThrow(() -> new UserNotFoundException("Пользователь (id: " + id + ") не найден."));
    }


    @Override
    @Transactional
    public UserDTO createUser(NewUserDTO dto) {
        if (loginDataRepository.existsByEmail(dto.getEmail())) {
            throw new LoginDataEmailAlreadyTakenException("Email (" + dto.getEmail() + ") уже занят.");
        }

        User user = UserMapper.convertToDomain(dto);
        return UserMapper.convertToDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public UserDTO updateUser(UserDTO dto) {
        User user = userRepository.findById(dto.getId()).orElseThrow(() ->
                        new UserNotFoundException("Пользователь (id: " + dto.getId() + ") не найден."));

        if (!user.getLoginData().getEmail().equals(dto.getEmail())) {
            if (loginDataRepository.existsByEmail(dto.getEmail())) {
                throw new LoginDataEmailAlreadyTakenException("Email (" + dto.getEmail() + ") уже занят.");
            }

            user.getLoginData().setEmail(dto.getEmail());
        }
        user.setFullName(dto.getFullName());

        return UserMapper.convertToDto(userRepository.save(user));
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("Пользователь (id: " + id + ") не найден.");
        }
        userRepository.deleteById(id);
    }
}
