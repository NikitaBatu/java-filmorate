package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorRatingMpaException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.ratingMpa.RatingMpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RatingMpaService {
    private final RatingMpaStorage ratingMpaStorage;

    public Mpa findById(int ratingId) {
        contains(ratingId);
        return ratingMpaStorage.findById(ratingId);
    }

    public List<Mpa> findAll() {
        return ratingMpaStorage.findAll();
    }

    private void contains(int ratingId) {
        try {
            ratingMpaStorage.findById(ratingId);
        } catch (EmptyResultDataAccessException exception) {
            log.debug("Рейтинг MPA с id {} не найден", ratingId);
            throw new ErrorRatingMpaException("Рейтинг MPA не найден");
        }
    }
}
