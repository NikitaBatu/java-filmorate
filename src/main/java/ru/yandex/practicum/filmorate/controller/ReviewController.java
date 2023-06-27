package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public Review add(@Valid @RequestBody Review review) {
        log.debug("Получен запрос POST на добавление отзыва {}", review.toString());
        return reviewService.create(review);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable long id) {
        log.debug("Получен запрос GET на получение отзыва по id {}", id);
        return reviewService.findById(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false) Long filmId,
                                   @RequestParam(defaultValue = "10") long count) {
        log.debug("Получен запрос GET на получение всех отзывов");
        return reviewService.findAll(filmId, count);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.debug("Получен запрос PUT на обновление отзыва {}", review.toString());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable long id) {
        log.debug("Получен запрос DELETE на удаление отзыва по id {}", id);
        return reviewService.delete(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public boolean addLike(@PathVariable long id,
                           @PathVariable long userId) {
        log.debug("Получен запрос PUT на добавление лайка от пользователя по id {}", userId);
        return reviewService.addLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{userId}")
    public boolean addDislike(@PathVariable long id,
                              @PathVariable long userId) {
        log.debug("Получен запрос PUT на добавление дизлайка от пользователя по id {}", userId);
        return reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public boolean deleteLike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос DELETE на удаление лайка от пользователя по id {}", userId);
        return reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public boolean deleteDislike(@PathVariable long id, @PathVariable long userId) {
        log.debug("Получен запрос DELETE на удаление дизлайка от пользователя по id {}", userId);
        return reviewService.deleteDislike(id, userId);
    }
}
