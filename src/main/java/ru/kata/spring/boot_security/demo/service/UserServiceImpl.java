package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserDao userDao, RoleDao roleDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public void add(User user, List<Long> roleIds) {
        validate(user, true);
        if (userDao.findByUsername(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email is already in use");
        }
        user.setUsername(user.getEmail());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(resolveRoles(roleIds));
        userDao.add(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> listUsers() {
        return userDao.listUsers();
    }

    @Transactional(readOnly = true)
    @Override
    public User getById(Long id) {
        return userDao.getById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Transactional
    @Override
    public void update(User user, List<Long> roleIds) {
        validate(user, false);
        User existing = userDao.getById(user.getId());
        if (existing == null) {
            throw new IllegalArgumentException("User not found");
        }
        User sameEmail = userDao.findByUsername(user.getEmail());
        if (sameEmail != null && !sameEmail.getId().equals(existing.getId())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        existing.setFirstName(user.getFirstName());
        existing.setLastName(user.getLastName());
        existing.setAge(user.getAge());
        existing.setEmail(user.getEmail());
        existing.setUsername(user.getEmail());
        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        existing.setRoles(resolveRoles(roleIds));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (userDao.getById(id) == null) {
            throw new IllegalArgumentException("User not found");
        }
        userDao.delete(id);
    }

    private void validate(User user, boolean passwordRequired) {
        if (user.getFirstName() == null || user.getFirstName().isBlank()) {
            throw new IllegalArgumentException("First name is required");
        }
        user.setFirstName(user.getFirstName().trim());
        if (user.getLastName() == null || user.getLastName().isBlank()) {
            throw new IllegalArgumentException("Last name is required");
        }
        user.setLastName(user.getLastName().trim());
        if (user.getAge() == null || user.getAge() < 0) {
            throw new IllegalArgumentException("Age is required");
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        user.setEmail(user.getEmail().trim());
        if (passwordRequired && (user.getPassword() == null || user.getPassword().isBlank())) {
            throw new IllegalArgumentException("Password is required");
        }
    }

    private Set<Role> resolveRoles(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new IllegalArgumentException("Select at least one role");
        }
        List<Role> roles = roleDao.findByIds(roleIds);
        if (roles.size() != new HashSet<>(roleIds).size()) {
            throw new IllegalArgumentException("Unknown role");
        }
        return new HashSet<>(roles);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("Пользователь не найден: " + username);
        }
        return user;
    }
}
