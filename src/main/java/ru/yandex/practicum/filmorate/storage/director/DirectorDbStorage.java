package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ErrorDirectorException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> findAll() {
        String sqlQuery = "SELECT * FROM directors";
        return jdbcTemplate.query(sqlQuery, new DirectorMapper());
    }

    @Override
    public Director create(Director director) {
        String sqlQuery = "INSERT INTO directors(name) " +
                "VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement stmt = con.prepareStatement(sqlQuery, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        int idKey = Objects.requireNonNull(keyHolder.getKey()).intValue();
        director.setId(idKey);
        return director;
    }

    @Override
    public Director update(Director director) {
        int dirId = director.getId();
        if (findById(dirId) == null) {
            throw new ErrorDirectorException("Невозможно обновить несуществующего режиссёра");
        }
        String sqlQuery = "UPDATE directors SET name = ? " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery,
                director.getName(),
                director.getId());
        return director;
    }

    @Override
    public Director findById(long id) {
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet("SELECT * FROM directors WHERE director_id = ?", id);
        if (!directorRows.first()) {
            throw new ErrorDirectorException("Режиссёр с id " + id + " не найден");
        }
        return new Director(
                directorRows.getInt("director_id"),
                directorRows.getString("name")
        );
    }

    @Override
    public void removeDirector(long id) {
        if (findById(id) == null) {
            throw new ErrorDirectorException("Невозможно удалить несуществующего режиссёра");
        }
        String sqlQuery1 = "DELETE FROM films_directors WHERE director_id = ?";
        String sqlQuery2 = "DELETE FROM directors " +
                "WHERE director_id = ?";
        jdbcTemplate.update(sqlQuery1, id);
        jdbcTemplate.update(sqlQuery2, id);
    }
}
