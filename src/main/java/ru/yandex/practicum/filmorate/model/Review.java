package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Validated
@Builder
public class Review {
    private long reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    @NotNull
    private Long userId;
    @NotNull
    private Long filmId;
    private int useful;

    public void setReviewId(long reviewId) {
        this.reviewId = reviewId;
    }
}
