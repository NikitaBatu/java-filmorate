# Filmorate
> Функционал сервиса для кинотеки:
>* Пользователь может выбрать фильм, поставить ему лайк или дизлайк, написать отзыв о фильме; 
>* Можно посмотреть топ популярных фильмов; 
>* Можно добавлять друзей;
>* Получать рекомендации на основе предпочтений друзей;
>* Просматривать список общих с друзьями фильмов;
>* Пользователь может просматривать ленту событий (добавление и удаление друзей, добавление и удаление отзыва о фильме, добавление и удаление лайка фильму); 
>* Поиск: по названию фильма, по режиссёру.

## Схема базы данных проекта
![](src/main/resources/schema.png)
</details>

<details>
    <summary><h3>Описание схемы</h3></summary>
    
* films - cодержит данные о фильмах
* genres - cодержит данные о существующих жанрах
* film_genres - cодержит данные о жанре конкретного фильма
* ratings_mpa - cодержит данные о существующих рейтингах МРА
* likes - cодержит данные о том, какой пользователь какой фильм лайкнул
* users - cодержит данные о пользователях
* friends - cодержит данные о взаимности дружбы
* reviews - cодержит данные об отзывах
* review_like - cодержит данные о лайках, поставленных на отзыв
* directors - cодержит данные о режиссерах
* films_directors - cодержит данные о режиссерах конкретного фильма
* events - cодержит данные ленты событий
</details>

## Примеры SQL запросов к БД:

<details>
    <summary><h3>Топ 5 самых популярных фильмов</h3></summary>
    
```SQL
SELECT
films.name
FROM films
WHERE film_id IN (SELECT film_id
                   FROM likes
                   GROUP BY film_id
                   ORDER BY COUNT(user_id) DESC
                   LIMIT 5);
``` 
</details>

<details>
    <summary><h3>Все жанры конкретного фильма</h3></summary>
    
```SQL
SELECT
f.genre_id,
g.name 
FROM film_genres AS f 
LEFT OUTER JOIN genres AS g ON f.genre_id = g.genre_id 
WHERE f.film_id=%d 
ORDER BY g.genre_id;
```
</details>

<details>
    <summary><h3>Возвращает фильм по ид</h3></summary>
    
```SQL
SELECT f.film_id,
       f.name,
       f.description,
       f.release_date,
       f.duration,
       mp.name AS mpa_rating,
       g.name  AS genre
FROM films f
         JOIN ratings_mpa mp ON f.rating_id = mp.rating_id
         JOIN film_genres fg ON f.film_id = fg.film_id
         JOIN genres g ON fg.genre_id = g.genre_id
WHERE f.film_id = ?;
```
</details>

## Валидация

Входные данные, поступающие в запросе,
должны соответствовать определенным критериям:

<details>
    <summary><h3>Для фильмов:</h3></summary>

* Название фильма должно быть указано и не может быть пустым
* Максимальная длина описания фильма не должна превышать 200 символов
* Дата релиза фильма должна быть не раньше 28 декабря 1895 года[^1]
* Продолжительность фильма должна быть положительной
* Рейтинг фильма должен быть указан

</details>

<details>
    <summary><h3>Для пользователей:</h3></summary>

* Электронная почта пользователя должна быть указана и соответствовать формату email
* Логин пользователя должен быть указан и не содержать пробелов
* Дата рождения пользователя должна быть указана и не может быть в будущем

</details>

## Технологический стек

- Java 11
- Spring boot 2
- JDBC, SQL, H2
- Apache Maven
- Lombok

[^1]: 28 декабря 1895 года считается днём рождения кино.
