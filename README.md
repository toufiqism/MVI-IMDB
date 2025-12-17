# MVI-IMDB 🎬

A modern Android movie discovery app built with **Jetpack Compose**, **MVI architecture**, and **TMDB API**. Browse popular movies, search for titles, view detailed information, and save your favorites for offline access.

## Features

- 🏠 **Home Screen** - Browse movies by category (Popular, Top Rated, Upcoming, Now Playing)
- 🔍 **Search** - Find movies by title with debounced search
- 📖 **Movie Details** - View comprehensive movie info including cast, genres, and similar movies
- ❤️ **Favorites** - Save movies locally for quick access
- 📴 **Offline Support** - Cached data available without internet
- ♾️ **Infinite Scroll** - Automatic pagination when scrolling

## Architecture

The app follows **Clean Architecture** with **MVI (Model-View-Intent)** pattern:

```
com.tofiq.mvi_imdb/
├── data/                    # Data layer
│   ├── local/               # Room database, DAOs, entities
│   ├── remote/              # Retrofit API, DTOs
│   ├── mapper/              # DTO ↔ Entity ↔ Domain mappers
│   └── repository/          # Repository implementations
├── domain/                  # Domain layer
│   ├── model/               # Domain models (Movie, MovieDetail)
│   ├── repository/          # Repository interfaces
│   └── usecase/             # Business logic use cases
├── presentation/            # Presentation layer
│   ├── base/                # MVI base classes
│   ├── components/          # Reusable Compose components
│   ├── navigation/          # Navigation3 setup
│   └── screens/             # Feature screens (home, detail, search, favorites)
├── di/                      # Hilt dependency injection modules
└── util/                    # Constants, utilities, error handling
```

## Tech Stack

| Category | Technology |
|----------|------------|
| UI | Jetpack Compose, Material 3 |
| Architecture | MVI, Clean Architecture |
| Navigation | Navigation3 |
| DI | Hilt |
| Networking | Retrofit, OkHttp, Gson |
| Local Storage | Room Database |
| Image Loading | Coil |
| Async | Kotlin Coroutines, Flow |
| Testing | JUnit, Kotest (Property-based), MockK, Turbine |

## Requirements

- Android Studio Ladybug or newer
- Min SDK: 25 (Android 7.1)
- Target SDK: 36
- Kotlin 2.2+
- JDK 11

## Setup

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/MVI-IMDB.git
   ```

2. Open the project in Android Studio

3. Sync Gradle and build the project

4. Run on an emulator or physical device

> **Note:** The app uses a pre-configured TMDB API key. For production use, replace the key in `Constants.kt`.

## Project Structure

### Screens

| Screen | Description |
|--------|-------------|
| `HomeScreen` | Displays categorized movie lists with tab navigation |
| `SearchScreen` | Search movies with 300ms debounce, min 2 characters |
| `DetailScreen` | Full movie details with cast, genres, similar movies |
| `FavoritesScreen` | Locally saved favorite movies |

### Key Components

- **MviViewModel** - Base ViewModel handling Intent → State flow
- **MovieRepository** - Single source of truth for movie data
- **Resource** - Wrapper for Success/Error/Loading states
- **AppError** - Typed error handling with user-friendly messages

## Testing

The project includes property-based tests using Kotest:

```bash
./gradlew test
```

Test coverage includes:
- Mapper round-trip consistency
- Pagination behavior
- Favorite toggle idempotence
- State transitions
- Error handling

## API

The app integrates with [TMDB API](https://www.themoviedb.org/documentation/api):

- `GET /movie/popular` - Popular movies
- `GET /movie/top_rated` - Top rated movies
- `GET /movie/upcoming` - Upcoming movies
- `GET /movie/now_playing` - Now playing movies
- `GET /movie/{id}` - Movie details
- `GET /movie/{id}/credits` - Movie cast
- `GET /movie/{id}/similar` - Similar movies
- `GET /search/movie` - Search movies

## License

This project is for educational purposes.

---

Built with ❤️ using Kotlin and Jetpack Compose
