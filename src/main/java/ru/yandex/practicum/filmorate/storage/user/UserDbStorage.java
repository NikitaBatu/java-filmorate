package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.mapper.UserMapper;

import java.sql.Date;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAll() {
        List<User> users = jdbcTemplate.query("SELECT * FROM users", new UserMapper());
        log.debug("Все пользователи: {}", users);
        return users;
    }

    @Override
    public User create(User user) {
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()));
        log.debug("Добавлен пользователь: {}.", getUser(user));
        return getUser(user);
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update("UPDATE users SET email=?, login=?, name=?, birthday=? WHERE user_id=?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId());
        log.debug("Обновлён пользователь: {}", getUser(user));
        return getUser(user);
    }

    @Override
    public User findById(long id) {
        return jdbcTemplate.queryForObject(format("SELECT user_id, email, login, name, birthday "
                + "FROM users WHERE user_id=%d", id), new UserMapper());
    }

    private User getUser(User user) {
        return jdbcTemplate.queryForObject(format("SELECT user_id, email, login, name, birthday FROM users "
                + "WHERE email='%s'", user.getEmail()), new UserMapper());
    }

    @Override
    public void delete(long userId) {
        log.debug("Удаляется пользователь по id {}", userId);
        jdbcTemplate.update("DELETE FROM users WHERE user_id=?", userId);
    }
}
