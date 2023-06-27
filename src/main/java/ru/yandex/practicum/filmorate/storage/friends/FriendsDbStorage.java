package ru.yandex.practicum.filmorate.storage.friends;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friends;
import ru.yandex.practicum.filmorate.mapper.FriendsMapper;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FriendsDbStorage implements FriendsStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void add(long userId, long friendId, boolean isMutual) {
        jdbcTemplate.update(""
                + "INSERT INTO friends (user_id, friend_id, is_mutual) "
                + "VALUES(?, ?, ?)", userId, friendId, isMutual);
        log.debug("Добавлена новая дружба {}", get(userId, friendId));
    }

    @Override
    public void delete(long userId, long friendId) {
        Friends result = Objects.requireNonNull(get(userId, friendId));
        jdbcTemplate.update("DELETE FROM friends "
                + "WHERE user_id=? "
                + "AND friend_id=?", userId, friendId);
        if (result.isMutual()) {
            jdbcTemplate.update(""
                    + "UPDATE friends "
                    + "SET is_mutual=false "
                    + "WHERE user_id=? "
                    + "AND friend_id=?", userId, friendId);
            log.debug("Дружба между {} и {} перестала быть взаимной.",
                    userId, friendId);
        }
    }

    @Override
    public Set<Long> getFriends(long userId) {
        return jdbcTemplate.query(format("SELECT user_id, friend_id, is_mutual FROM friends WHERE user_id=%d",
                        userId), new FriendsMapper())
                .stream()
                .map(Friends::getFriendId)
                .collect(Collectors.toSet());
    }

    @Override
    public Friends get(long userId, long friendId) {
        try {
            return jdbcTemplate.queryForObject(format("SELECT user_id, friend_id, is_mutual "
                    + "FROM friends "
                    + "WHERE user_id=%d "
                    + "AND friend_id=%d", userId, friendId), new FriendsMapper());
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }
}
