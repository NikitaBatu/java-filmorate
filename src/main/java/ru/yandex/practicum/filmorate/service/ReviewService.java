package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorFilmException;
import ru.yandex.practicum.filmorate.exception.ErrorReviewException;
import ru.yandex.practicum.filmorate.exception.ErrorUserException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.events.EventsStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.enums.Operation.*;
import static ru.yandex.practicum.filmorate.enums.EventType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final EventsStorage eventsStorage;

    public Review create(Review review) {
        containsUser(review.getUserId());
        containsFilm(review.getFilmId());

        Review review1 = reviewStorage.create(review);
        eventsStorage.addEvent(review1.getUserId(), review1.getReviewId(), REVIEW.name(), ADD.name());
        return review1;
    }

    public Review findById(long id) {
        try {
            return reviewStorage.findById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ErrorReviewException("Такого отзыва нет!");
        }
    }

    public List<Review> findAll(Long filmId, long count) {
        if (filmId == null) {
            return reviewStorage.findAll().stream()
                    .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                    .limit(count)
                    .collect(Collectors.toList());
        }
        return reviewStorage.getAllReviewsByFilm(filmId).stream()
                .sorted(Comparator.comparingLong(Review::getUseful).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    public Review update(Review review) {
        Review review1 = findById(review.getReviewId());
        eventsStorage.addEvent(review1.getUserId(), review1.getReviewId(), REVIEW.name(), UPDATE.name());
        return reviewStorage.update(review);
    }

    public boolean delete(long id) {
        Review review = findById(id);
        eventsStorage.addEvent(review.getUserId(), review.getReviewId(), REVIEW.name(), REMOVE.name());
        return reviewStorage.delete(id);
    }

    public boolean addLike(long id, long userId) {
        containsUser(userId);
        return reviewStorage.addLike(id, userId);
    }

    public boolean addDislike(long id, long userId) {
        return reviewStorage.addDislike(id, userId);
    }

    public boolean deleteLike(long id, long userId) {
        return reviewStorage.deleteLike(id, userId);
    }

    public boolean deleteDislike(long id, long userId) {
        return reviewStorage.deleteDislike(id, userId);
    }

    private void containsFilm(long filmId) {
        try {
            filmStorage.findById(filmId);
        } catch (EmptyResultDataAccessException exception) {
            log.debug("Фильм с id {} не найден", filmId);
            throw new ErrorFilmException("Фильм не найден");
        }
    }

    private void containsUser(long userId) {
        try {
            userStorage.findById(userId);
        } catch (EmptyResultDataAccessException exception) {
            log.debug("Пользователь с id {} не найден", userId);
            throw new ErrorUserException("Пользователь не найден");
        }
    }
}
