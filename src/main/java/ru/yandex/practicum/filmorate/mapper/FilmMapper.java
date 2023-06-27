package ru.yandex.practicum.filmorate.mapper;

import lombok.NonNull;
import org.springframework.jdbc.core.RowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

public class FilmMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(@NonNull ResultSet rS, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rS.getInt("rating_id"));

        return Film.builder()
                .id(rS.getLong("film_id"))
                .name(rS.getString("name"))
                .description(rS.getString("description"))
                .releaseDate(rS.getDate("release_date").toLocalDate())
                .duration(rS.getInt("duration"))
                .mpa(mpa)
                .build();
    }
}
