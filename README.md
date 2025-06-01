# School-Microservices

## Оглавление
1. [Описание проекта](#описание-проекта)
2. [Как запустить](#как-запустить)
3. [Swagger-документация](#swagger-документация)
4. [Получение JWT](#получение-jwt)
5. [Структура БД и связи (кратко)](#структура-бд-и-связи-кратко)


## Описание проекта

- `Account Service` - управление пользователями и JWT.
- `School Service` - управление школами, учителями, учениками.
- `Timetable Service` - расписания и запись на занятия.
- `Document Service` - посещение и их история.

Все сервисы используют авторизацию на основе JWT и взаимодействуют между собой через HTTP.
Все сервисы запускаются локально из исходников.

## Как запустить
1. Клонируйте репозиторий:
```bash 
git clone https://github.com/Progress-ux/School-Microservices.git
cd School-Microservices
```
2. Запустите все микросервисы:
```bash 
docker-compose up -d --build
```
3. Проверьте Swagger(см. ниже).

## Swagger-документация

| Сервис                    | Swagger UI URL                        |
|---------------------------|---------------------------------------|
| `Account Service`                   | http://localhost:8080/swagger-ui.html |
| `School Service`                  | http://localhost:8081/swagger-ui.html |
| `Timetable Service` | http://localhost:8082/swagger-ui.html |
| `Document Service`                  | http://localhost:8083/swagger-ui.html |

Все запросы с авторизацией требуют JWT-токен в поле `Authorize`

## Получение JWT
- После входа через `/api/v1/auth/login`, пользователь получает JWT.
- JWT передаётся в заголовке:
```http 
Authorization: Bearer <ваш токен>
```
-Все микросервисы используют `GET /api/v1/auth/validate` из `Account Service` для валидации токена.

## Структура БД и связи (кратко)
- `users` (Account)
- `schools`, `school_teachers`, `school_students` (School)
- `timetables`, `timetable_bookings` (Timetable)
- `documents` (Document)