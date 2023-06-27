package ru.yandex.practicum.filmorate.storage.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long filmId, long userId) {
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", filmId, userId);
        log.debug("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    @Override
    public void delete(long filmId, long userId) {
        jdbcTemplate.update("DELETE FROM likes WHERE film_id=? AND user_id=?", filmId, userId);
        log.debug("У фильма {} удалён лайк от пользователя {}", filmId, userId);
    }

    @Override
    public int check(long filmId) {
        return Objects.requireNonNull(
                jdbcTemplate.queryForObject(format("SELECT COUNT(*) FROM likes WHERE film_id=%d",
                        filmId), Integer.class));
    }

    @Override
    public List<Long> getFilmIdLikes(long userId) {
        String sql = "SELECT film_id FROM likes WHERE user_id = ?";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
}
