package ru.yandex.practicum.filmorate.storage.ratingMpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.mapper.RatingMpaMapper;

import java.util.LinkedList;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class RatingMpaDbStorage implements RatingMpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Mpa findById(int id) {
        return jdbcTemplate.queryForObject(format("SELECT rating_id, name FROM ratings_mpa WHERE rating_id=%d", id),
                new RatingMpaMapper());
    }

    @Override
    public List<Mpa> findAll() {
        return new LinkedList<>(jdbcTemplate.query("SELECT rating_id, name FROM ratings_mpa ORDER BY rating_id",
                new RatingMpaMapper()));
    }
}
