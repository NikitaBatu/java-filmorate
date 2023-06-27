package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<User> findAll() {
        log.debug("Получен запрос GET на получение всех пользователей");
        return userService.findAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Получен запрос POST на создание пользователя {}", user.toString());
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Получен запрос PUT на обновление пользователя {}", user.toString());
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        log.debug("Получен запрос GET на получение пользователя по id {}", id);
        return userService.findById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addNewFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос PUT на добавление друга по id {}", friendId);
        userService.addNewFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable long id, @PathVariable long friendId) {
        log.debug("Получен запрос DELETE друга по id {}", friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable long id) {
        log.debug("Получен запрос GET на получение друзей пользователя по id {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable long id, @PathVariable long otherId) {
        log.debug("Получен запрос GET на получение общих друзей, id первого пользователя {} и " +
                "id второго пользователя {}", id, otherId);
        return userService.getMutualFriends(id, otherId);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        log.debug("Получен запрос DELETE для пользователя по id {}", id);
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendFilms(@PathVariable long id) {
        log.debug("Получен запрос GET на получение рекомендуемых фильмов от пользователя по id {}", id);
        return userService.getRecommendFilms(id);
    }

    @GetMapping("/{id}/feed")
    public List<Event> findFeedEvents(@PathVariable long id) {
        log.debug("Получен запрос GET на поиск событий пользователя по id {}", id);
        return userService.findFeedEvents(id);
    }
}
