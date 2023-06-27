package ru.yandex.practicum.filmorate.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.*;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        log.debug("Получен статус 400 Bad Request {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final ErrorUserException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleFilmNotFoundException(final ErrorFilmException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleLoginAlreadyExistException(final LoginAlreadyExistException exception) {
        log.debug("Получен статус 400 Bad Request {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleFilmAlreadyExistException(final FilmAlreadyExistException exception) {
        log.debug("Получен статус 400 Bad Request {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable exception) {
        log.debug("Получен статус 500 Internal Server Error {}", exception.getMessage(), exception);
        return new ErrorResponse(
                "Произошла непредвиденная ошибка."
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final ConstraintViolationException exception) {
        log.debug("Получен статус 400 Bad Request {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleGenreNotFoundException(final ErrorGenreException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRatingMpaNotFoundException(final ErrorRatingMpaException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleReviewNotFoundException(final ErrorReviewException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleDirectorNotFoundException(final ErrorDirectorException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleErrorSearchException(final ErrorSearchException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return new ErrorResponse(
                exception.getMessage()
        );
    }
}
