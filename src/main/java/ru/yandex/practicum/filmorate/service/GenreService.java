package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorGenreException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public Genre findById(int genreId) {
        contains(genreId);
        return genreStorage.findById(genreId);
    }

    public List<Genre> findAll() {
        return genreStorage.findAll();
    }

    private void contains(int genreId) {
        try {
            genreStorage.findById(genreId);
        } catch (EmptyResultDataAccessException exception) {
            log.debug("Жанр с id {} не найден", genreId);
            throw new ErrorGenreException("Жанр не найден");
        }
    }
}
