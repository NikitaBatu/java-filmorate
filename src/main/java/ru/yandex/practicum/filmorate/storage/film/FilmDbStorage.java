package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAll() {
        List<Film> films = jdbcTemplate.query("SELECT * FROM films", new FilmMapper());
        log.debug("Количество фильмов: {}", films.size());
        return films;
    }

    @Override
    public Film create(Film film) {
        jdbcTemplate.update("INSERT INTO films (name, description, release_date, duration, rating_id) "
                        + "VALUES(?, ?, ?, ?, ?)",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId());
        log.debug("Добавлен новый фильм: {}", getFilm(film));
        return getFilm(film);
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(""
                        + "UPDATE films "
                        + "SET name=?, description=?, release_date=?, duration=?, rating_id=? "
                        + "WHERE film_id=?",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        log.debug("Информация о фильме обновлена: {}", getFilm(film));
        return getFilm(film);
    }

    @Override
    public Film findById(long filmId) {
        return jdbcTemplate.queryForObject(format("SELECT * FROM films WHERE film_id = %d", filmId),
                new FilmMapper());
    }

    @Override
    public void addGenres(long filmId, Set<Genre> genres) {
        if (genres != null) {
            genres.forEach(genre -> {
                jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                        filmId, genre.getId());
                log.debug("Фильму {} добавлен жанр {}", filmId, genre.getId());
            });
        }
    }

    @Override
    public void updateGenres(long filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id=?", filmId);
        addGenres(filmId, genres);
        log.debug("Обновлены жанры для фильма {}", filmId);
    }

    @Override
    public Set<Genre> findGenres(long filmId) {
        log.debug("Все жанры для фильма {}", filmId);
        return new HashSet<>(jdbcTemplate.query(format("SELECT f.genre_id, g.name "
                + "FROM film_genres AS f "
                + "LEFT OUTER JOIN genres AS g ON f.genre_id = g.genre_id "
                + "WHERE f.film_id=%d "
                + "ORDER BY g.genre_id", filmId), new GenreMapper()));
    }

    @Override
    public void delete(long filmId) {
        log.debug("Удаляется фильм по id {}", filmId);
        jdbcTemplate.update("DELETE FROM films WHERE film_id=?", filmId);
    }

    @Override
    public void addDirectors(long filmId, Set<Director> directors) {
        if (directors != null) {
            directors.forEach(director -> {
                jdbcTemplate.update("INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                        filmId, director.getId());
                log.debug("Фильму {} добавлен режиссёр {}", filmId, director.getId());
            });
        }
    }

    @Override
    public void updateDirectors(long filmId, Set<Director> directors) {
        jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", filmId);
        addDirectors(filmId, directors);
        log.debug("Обновлены режиссёры для фильма {}", filmId);
    }

    @Override
    public Set<Director> findDirectors(long filmId) {
        log.debug("Все режиссеры фильма {}", filmId);
        return new HashSet<>(jdbcTemplate.query(format("SELECT fd.director_id, d.name " +
                "FROM films_directors AS fd " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE fd.film_id = %d", filmId), new DirectorMapper()));
    }

    @Override
    public List<Film> getAllDirectorFilms(long dirId) {
        String sqlQuery = "SELECT * FROM films AS f " +
                "INNER JOIN films_directors AS fd ON f.film_id = fd.film_id " +
                "WHERE fd.director_id = ?";
        return jdbcTemplate.query(sqlQuery, new FilmMapper(), dirId);
    }

    private Film getFilm(Film film) {
        return jdbcTemplate.queryForObject(format("SELECT "
                        + "film_id, name, description, release_date, duration, rating_id "
                        + "FROM films "
                        + "WHERE name='%s' "
                        + "AND description='%s' "
                        + "AND release_date='%s' "
                        + "AND duration=%d "
                        + "AND rating_id=%d",
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                film.getMpa().getId()), new FilmMapper());
    }

    @Override
    public List<Film> searchFilms(String query, List<String> by) {
        String sqlQuery =
                "SELECT F.FILM_ID                                                        AS film_id, " +
                        "       F.NAME                                                           AS name, " +
                        "       F.DESCRIPTION                                                    AS description, " +
                        "       F.RELEASE_DATE                                                   AS release_date, " +
                        "       F.DURATION                                                       AS duration, " +
                        "       RM.RATING_ID                                                     AS mpa_id, " +
                        "       RM.NAME                                                          AS mpa_name, " +
                        "       GROUP_CONCAT(D.DIRECTOR_ID ORDER BY D.DIRECTOR_ID SEPARATOR ';') AS director_id, " +
                        "       GROUP_CONCAT(D.NAME ORDER BY D.NAME SEPARATOR ';')               AS director_name, " +
                        "       GROUP_CONCAT(G.GENRE_ID ORDER BY D.DIRECTOR_ID SEPARATOR ';')    AS genre_id, " +
                        "       GROUP_CONCAT(G.NAME ORDER BY D.NAME SEPARATOR ';')               AS genre_name, " +
                        "       COUNT(L.FILM_ID)                                                 AS like_count " +
                        "FROM PUBLIC.FILMS AS F " +
                        "         LEFT JOIN PUBLIC.LIKES L ON F.FILM_ID = L.FILM_ID " +
                        "         LEFT JOIN PUBLIC.FILMS_DIRECTORS FD ON F.FILM_ID = FD.FILM_ID " +
                        "         LEFT JOIN PUBLIC.DIRECTORS AS D ON FD.DIRECTOR_ID = D.DIRECTOR_ID " +
                        "         LEFT JOIN PUBLIC.RATINGS_MPA RM ON F.RATING_ID = RM.RATING_ID " +
                        "         LEFT JOIN PUBLIC.FILM_GENRES FG ON F.FILM_ID = FG.FILM_ID " +
                        "         LEFT JOIN PUBLIC.GENRES G ON FG.GENRE_ID = G.GENRE_ID " +
                        "GROUP BY F.FILM_ID " +
                        "HAVING " +
                        " %s " +
                        "ORDER BY COUNT(L.FILM_ID) DESC";

        String sqlHaving = "";
        String logicalOperator = " OR ";

        if (by.contains("title")) {
            sqlHaving = "F.DESCRIPTION LIKE '%" + query.toLowerCase() + "%'";
            if (by.size() == 2) {
                sqlHaving = sqlHaving + logicalOperator;
            }
        }

        if (by.contains("director")) {
            sqlHaving = sqlHaving + "D.NAME LIKE '%" + query.toLowerCase() + "%'";
        }

        List<Film> films = jdbcTemplate.query(format(sqlQuery, sqlHaving), new FilmMapper());

        log.debug("Количество фильмов: {}", films.size());
        return films;
    }

}
