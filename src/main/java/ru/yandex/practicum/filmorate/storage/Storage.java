package ru.yandex.practicum.filmorate.storage;

import java.util.List;

public interface Storage<T> {
    List<T> findAll();

    T create(T too);

    T update(T too);

    T findById(long id);

}
