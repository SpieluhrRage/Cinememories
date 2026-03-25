> Mobile Android application for tracking watched movies, building a personal collection, and discovering new films.

---

## About the Project

**Cinememories** — это Android-приложение для ведения списка просмотренных фильмов с возможностью добавления фильмов вручную или через поиск по API.

Приложение сочетает в себе:
- персональную коллекцию фильмов  
- интеграцию с внешним API (TMDB)  
- локальное хранение данных  
- базовую систему пользователей и сессий  

Проект построен с упором на понятную архитектуру и разделение ответственности между слоями.

---

## Features

  -  Добавление фильмов:
  - вручную  
  - через поиск (TMDB API)

-  Личная коллекция фильмов  
-  Избранные фильмы  
-  Поиск и фильтрация  
-  Пользовательская система (логин + сессия)  
-  Локализация (RU / EN)  
-  Навигация через Bottom Navigation  

---

## Architecture

Проект реализован с использованием **MVVM-подхода**

### Основные слои:

- **UI Layer**
  - Fragments (Add, Search, Favorites, Profile, Detail)
  - Activities (Login, Main)

- **ViewModel Layer**
  - Управление состоянием UI
  - Работа с Repository

- **Data Layer**
  - Room (локальная БД)
  - Retrofit (сетевые запросы)
  - Repository (абстракция данных)

---

## Tech Stack

### Android
- Java
- Android SDK
- Fragments + Navigation Component

### Архитектура
- MVVM
- Repository pattern

### Работа с данными
- Room (SQLite)
- DAO (MovieDao, UserDao)

### Сеть
- Retrofit
- TMDB API

### Прочее
- ViewBinding / XML layouts
- SessionManager (SharedPreferences)

## Data Sources

### 1. Local Database (Room)
- хранение фильмов  
- хранение пользователей  
- избранное  

### 2. Remote API (TMDB)
- поиск фильмов  
- получение информации о фильмах  

---

## Authentication

Реализована базовая система авторизации:

- LoginActivity  
- хранение сессии через `SessionManager`  
- использование SharedPreferences  

