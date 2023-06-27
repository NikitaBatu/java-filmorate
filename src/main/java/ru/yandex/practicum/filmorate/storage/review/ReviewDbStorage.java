package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ErrorReviewException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);
        Map<String, Object> reviewFields = getReviewFields(review);
        Number generatedReviewId = jdbcInsert.withTableName("REVIEWS")
                .usingGeneratedKeyColumns("ID")
                .executeAndReturnKey(reviewFields);
        review.setReviewId(generatedReviewId.longValue());
        return review;
    }

    @Override
    public Review update(Review review) {
        if (review == null) {
            throw new ErrorReviewException("Передан пустой аргумент");
        }
        String sql = "UPDATE REVIEWS SET DESCRIPTION = ?, IS_POSITIVE = ? WHERE ID = ?";
        int rowsUpdate = jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        if (rowsUpdate == 0) {
            throw new ErrorReviewException("Такого отзыва нет!");
        }
        return findById(review.getReviewId());
    }

    @Override
    public List<Review> findAll() {
        String sql = "SELECT * FROM REVIEWS";
        return jdbcTemplate.query(sql, this::mapRowToReview);
    }

    @Override
    public List<Review> getAllReviewsByFilm(Long filmId) {
        String sql = "SELECT * FROM REVIEWS WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId);
    }

    @Override
    public boolean delete(Long id) {
        String sql = "DELETE FROM REVIEWS WHERE ID = ?";
        return jdbcTemplate.update(sql, id) > 0;
    }

    @Override
    public Review findById(long id) {
        String sql = "SELECT * FROM REVIEWS WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
    }

    @Override
    public boolean addLike(Long id, Long userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS R " +
                "USING (SELECT " + id + " AS REVIEW_ID, " + userId + " AS USER_ID) AS S ON R.REVIEW_ID = S.REVIEW_ID AND R.USER_ID = S.USER_ID " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT VALUES(?, ?, 1) " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET R.RATING = 1 ";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean addDislike(Long id, Long userId) {
        String sql = "MERGE INTO REVIEW_LIKES AS R " +
                "USING (SELECT " + id + " AS REVIEW_ID, " + userId + " AS USER_ID) AS S ON R.REVIEW_ID = S.REVIEW_ID AND R.USER_ID = S.USER_ID " +
                "WHEN NOT MATCHED THEN " +
                "   INSERT VALUES(?, ?, -1) " +
                "WHEN MATCHED THEN " +
                "   UPDATE SET R.RATING = -1 ";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean deleteLike(Long id, Long userId) {
        String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND RATING = 1";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    @Override
    public boolean deleteDislike(Long id, Long userId) {
        String sql = "DELETE FROM REVIEW_LIKES WHERE REVIEW_ID = ? AND USER_ID = ? AND RATING = -1";
        return jdbcTemplate.update(sql, id, userId) > 0;
    }

    public Review containsReview(Review review) {
        try {
            String sql = "SELECT * FROM reviews WHERE user_id = ? AND film_id = ?";
            return jdbcTemplate.queryForObject(sql, this::mapRowToReview, review.getUserId(), review.getFilmId());
        } catch (EmptyResultDataAccessException exception) {
            return null;
        }
    }

    private Map<String, Object> getReviewFields(Review review) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("DESCRIPTION", review.getContent());
        fields.put("IS_POSITIVE", review.getIsPositive());
        fields.put("USER_ID", review.getUserId());
        fields.put("FILM_ID", review.getFilmId());
        return fields;
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(resultSet.getLong("ID"))
                .content(resultSet.getString("DESCRIPTION"))
                .isPositive(resultSet.getBoolean("IS_POSITIVE"))
                .userId(resultSet.getLong("USER_ID"))
                .filmId(resultSet.getLong("FILM_ID"))
                .useful(calculateUseful(resultSet.getInt("ID")))
                .build();
    }

    private int calculateUseful(int id) {
        String sql = "SELECT SUM(RATING) FROM REVIEW_LIKES WHERE REVIEW_ID = ?";
        Integer reviewRating = jdbcTemplate.queryForObject(sql, Integer.class, id);
        if (reviewRating == null) {
            return 0;
        }
        return reviewRating;
    }

}
