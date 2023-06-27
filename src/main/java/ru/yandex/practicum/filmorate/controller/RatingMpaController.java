package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingMpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
@Slf4j
public class RatingMpaController {
    private final RatingMpaService ratingMpaService;

    @GetMapping
    public List<Mpa> findAll() {
        log.debug("Получен запрос GET на получение всех рейтингов");
        return ratingMpaService.findAll();
    }

    @GetMapping("/{id}")
    public Mpa findById(@PathVariable int id) {
        log.debug("Получен запрос GET на получение рейтинга по id {}", id);
        return ratingMpaService.findById(id);
    }
}
