package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.Storage;

import java.util.List;

public interface ReviewStorage extends Storage<Review> {
    List<Review> getAllReviewsByFilm(Long filmId);

    boolean delete(Long id);

    boolean addLike(Long id, Long userId);

    boolean addDislike(Long id, Long userId);

    boolean deleteLike(Long id, Long userId);

    boolean deleteDislike(Long id, Long userId);

    Review containsReview(Review review);
}
