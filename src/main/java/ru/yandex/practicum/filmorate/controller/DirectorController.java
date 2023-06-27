package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/directors")
class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public List<Director> findAll() {
        log.debug("Получен запрос GET на получение всех режиссеров");
        return directorService.findAll();
    }

    @PostMapping
    public Director create(@Valid @RequestBody Director director) {
        log.debug("Получен запрос POST на создание режиссера {}", director.toString());
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Valid @RequestBody Director director) {
        log.debug("Получен запрос PUT на обновление режиссера {}", director.toString());
        return directorService.update(director);
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable long id) {
        log.debug("Получен запрос GET на получение режиссера по id {}", id);
        return directorService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void removeDirectorById(@PathVariable long id) {
        log.debug("Получен запрос DELETE на удаление режиссера по id {}", id);
        directorService.removeDirector(id);
    }
}
