package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ErrorUserException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.events.EventsStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendsStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.ratingMpa.RatingMpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.yandex.practicum.filmorate.enums.Operation.*;
import static ru.yandex.practicum.filmorate.enums.EventType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendsStorage friendsStorage;
    private final LikeStorage likeStorage;
    private final FilmStorage filmStorage;
    private final EventsStorage eventsStorage;
    private final RatingMpaStorage ratingMpaStorage;

    public void addNewFriend(long id, long friendId) {
        contains(id);
        contains(friendId);
        boolean isMutual = friendsStorage.get(friendId, id) != null;
        friendsStorage.add(id, friendId, isMutual);
        eventsStorage.addEvent(id, friendId, FRIEND.name(), ADD.name());
    }

    public void removeFriend(long id, long friendId) {
        friendsStorage.delete(id, friendId);
        eventsStorage.addEvent(id, friendId, FRIEND.name(), REMOVE.name());
    }

    public List<User> getFriends(long id) {
        contains(id);
        return friendsStorage.getFriends(id).stream()
                .mapToLong(Long::valueOf)
                .mapToObj(this::findById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(long id, long otherId) {
        contains(id);
        contains(otherId);
        List<User> friendsOfOtherId = new ArrayList<>(getFriends(otherId));
        return getFriends(id).stream()
                .filter(friendsOfOtherId::contains)
                .collect(Collectors.toList());
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        makeNameALoginIfNameIsEmpty(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        contains(user.getId());
        makeNameALoginIfNameIsEmpty(user);
        return userStorage.update(user);
    }

    public User findById(long id) {
        contains(id);
        return userStorage.findById(id);
    }

    public List<Event> findFeedEvents(long userId) {
        contains(userId);
        return eventsStorage.getUserEvents(userId);
    }

    public List<Film> getRecommendFilms(long userId) {
        List<Long> users = getIntersectionList(userId);
        List<Long> userIdFilms = getLikedFilmsForUser(userId);
        List<Film> films = new ArrayList<>();
        users.forEach(id -> getLikedFilmsForUser(id).stream()
                .mapToLong(filmId -> filmId)
                .filter(filmId -> !userIdFilms.contains(filmId))
                .forEachOrdered(filmId -> {
                    userIdFilms.add(filmId);
                    Film film = filmStorage.findById(filmId);
                    film.setGenres(filmStorage.findGenres(filmId));
                    film.setMpa(ratingMpaStorage.findById(film.getMpa().getId()));
                    films.add(film);
                }));
        return films;
    }

    public void deleteUser(long id) {
        contains(id);
        userStorage.delete(id);
    }

    private void makeNameALoginIfNameIsEmpty(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }

    private void contains(long userId) {
        try {
            userStorage.findById(userId);
        } catch (EmptyResultDataAccessException exception) {
            log.debug("Пользователь с id {} не найден", userId);
            throw new ErrorUserException("Пользователь не найден");
        }
    }

    private List<Long> getLikedFilmsForUser(long id) {
        return likeStorage.getFilmIdLikes(id);
    }

    private Integer getIntersection(long userId, long anotherUserId) {
        List<Long> intersection = getLikedFilmsForUser(anotherUserId).stream()
                .mapToLong(id -> id)
                .filter(id -> getLikedFilmsForUser(userId).contains(id))
                .boxed()
                .collect(Collectors.toList());
        return intersection.size();
    }

    private List<Long> getIntersectionList(long userId) {
        return userStorage.findAll().stream()
                .map(User::getId)
                .filter(id -> id != userId)
                .sorted((id1, id2) -> getIntersection(userId, id1) - getIntersection(userId, id2))
                .collect(Collectors.toList());
    }
}