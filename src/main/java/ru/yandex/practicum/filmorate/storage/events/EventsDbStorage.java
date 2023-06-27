package ru.yandex.practicum.filmorate.storage.events;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.mapper.EventsMapper;

import java.util.Calendar;
import java.util.List;

import static java.lang.String.format;

@Slf4j
@Repository
@RequiredArgsConstructor
public class EventsDbStorage implements EventsStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<Event> getUserEvents(long userId) {
        return jdbcTemplate.query("SELECT * FROM events WHERE user_id = ?", new EventsMapper(), userId);
    }

    public void addEvent(long userId, long entityId, String eventType, String operation) {
        long timestamp = Calendar.getInstance().getTimeInMillis();

        jdbcTemplate.update("INSERT INTO events (timestamp, user_id, event_type, operation, entity_id) " +
                        "VALUES (?, ?, ?, ?, ?)",
                timestamp,
                userId,
                eventType,
                operation,
                entityId);
        log.debug("Добавлено событие {} ", getEvent(timestamp, userId, eventType, operation, entityId));
    }

    private Event getEvent(long timestamp, long userId, String eventType, String operation, long entityId) {
        return jdbcTemplate.queryForObject(format("SELECT "
                        + "event_id, timestamp, user_id, event_type, operation, entity_id "
                        + "FROM events "
                        + "WHERE timestamp='%d' "
                        + "AND user_id='%d' "
                        + "AND event_type='%s' "
                        + "AND operation='%s' "
                        + "AND entity_id='%d'",
                timestamp,
                userId,
                eventType,
                operation,
                entityId), new EventsMapper());
    }
}
