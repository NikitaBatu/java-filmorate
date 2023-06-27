package ru.yandex.practicum.filmorate.storage.like;

import java.util.List;

public interface LikeStorage {
    void add(long filmId, long userId);

    void delete(long filmId, long userId);

    int check(long filmId);

    List<Long> getFilmIdLikes(long userId);
}
