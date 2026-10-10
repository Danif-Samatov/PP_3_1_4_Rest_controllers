package ru.kata.spring.boot_security.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.kata.spring.boot_security.demo.dto.RoleDto;
import ru.kata.spring.boot_security.demo.dto.UserDto;
import ru.kata.spring.boot_security.demo.dto.UserMapper;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminRestController {

    private final UserService userService;
    private final RoleService roleService;

    public AdminRestController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public List<UserDto> users() {
        return userService.listUsers().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{id}")
    public UserDto user(@PathVariable Long id) {
        return UserMapper.toDto(requiredUser(id));
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody UserDto dto) {
        User user = UserMapper.toEntity(dto);
        userService.add(user, dto.getRoleIds());
        return UserMapper.toDto(user);
    }

    @PutMapping("/users/{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto dto) {
        User user = UserMapper.toEntity(dto);
        user.setId(id);
        userService.update(user, dto.getRoleIds());
        return UserMapper.toDto(requiredUser(id));
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @GetMapping("/roles")
    public List<RoleDto> roles() {
        return roleService.listRoles().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    private User requiredUser(Long id) {
        User user = userService.getById(id);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        return user;
    }
}
