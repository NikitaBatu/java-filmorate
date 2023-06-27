package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final UserService userService;

    @GetMapping
    public List<Film> findAll() {
        log.debug("Получен запрос GET на получение всех фильмов");
        return filmService.findAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.debug("Получен запрос POST на создание фильма {}", film.toString());
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("Получен запрос PUT на обновление фильма {}", film.toString());
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable long id) {
        log.debug("Получен запрос GET на получение фильма по id {}", id);
        return filmService.findById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addNewLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос на добавление лайка от пользователя по id {}", userId);
        filmService.findById(id);
        userService.findById(userId);
        filmService.addNewLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос на удаление лайка от пользователя по id {}", userId);
        filmService.findById(id);
        userService.findById(userId);
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getTopPopularFilms(@RequestParam(defaultValue = "10") @Positive Integer count,
                                         @RequestParam(required = false, name = "genreId") Integer genreId,
                                         @RequestParam(required = false, name = "year") Integer year) {
        log.debug("Получен запрос GET на получение популярных фильмов с ограничением на {} ", count);
        return filmService.getTopPopularFilms(count, genreId, year);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        log.debug("Получен запрос DELETE на удаление фильма по id {}", id);
        filmService.deleteFilm(id);
    }

    @GetMapping("/common")
    public List<Film> getCommonPopularFilms(@RequestParam("userId") long userId,
                                            @RequestParam("friendId") long friendId) {
        log.debug("Получен запрос GET на получение общих популярных фильмов пользователей userId {} и friendId {}",
                userId, friendId);
        userService.findById(userId);
        userService.findById(friendId);
        return filmService.getCommonPopularFilms(userId, friendId);
    }

    @GetMapping("director/{id}")
    public List<Film> getSortedDirectorFilms(@PathVariable long id,
                                             @RequestParam(defaultValue = "year") @NotBlank String sortBy) {
        log.debug("Получен запрос GET на получение отсортированных фильмов по {}", sortBy);
        return filmService.getSortedDirectorFilms(id, sortBy);
    }

    @GetMapping("/search")
    public List<Film> getSearch(@RequestParam String query, @RequestParam List<String> by) {
        log.debug("Получен запрос GET на поиск фильмов по {}", by.toString());
        return filmService.search(query, by);
    }

}
