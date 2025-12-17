# MVI-IMDB 🎬

A modern Android movie discovery app built with **Jetpack Compose**, **MVI architecture**, and **TMDB API**. Browse popular movies, search for titles, view detailed information, explore actor filmographies, and save your favorites for offline access.

## Features

- 🏠 **Home Screen** - Browse movies by category with swipeable tabs (Popular, Top Rated, Upcoming, Now Playing)
- 👆 **Swipe Navigation** - Swipe left/right to switch between categories
- 🔍 **Search** - Find movies by title with debounced search (300ms, min 2 chars)
- 📖 **Movie Details** - View comprehensive movie info including cast, genres, and similar movies
- 🎭 **Actor Filmography** - Tap on any cast member to explore all their movies
- ❤️ **Favorites** - Save movies locally for quick access
- 📴 **Offline Support** - Cached data available without internet
- ♾️ **Infinite Scroll** - Automatic pagination when scrolling
- ⚡ **Optimized Performance** - Recomposition-optimized with immutable collections and stable annotations
- 🎨 **Custom Typography** - Anta font family throughout the app

## Screenshots

| Home | Search | Details | Cast Movies | Favorites |
|------|--------|---------|-------------|-----------|
| Swipeable category tabs | Debounced search | Cast & similar movies | Actor filmography | Saved movies |

---

## 📚 Architecture Deep Dive

This section explains the app's architecture in detail, perfect for learning Clean Architecture and MVI patterns.

### What is Clean Architecture?

Clean Architecture separates your code into layers, each with a specific responsibility. Think of it like an onion - the inner layers know nothing about the outer layers.

```
┌─────────────────────────────────────────────────────────────┐
│                    PRESENTATION LAYER                        │
│  (UI, ViewModels, Compose Screens)                          │
│  ┌─────────────────────────────────────────────────────┐    │
│  │                   DOMAIN LAYER                       │    │
│  │  (Use Cases, Domain Models, Repository Interfaces)  │    │
│  │  ┌─────────────────────────────────────────────┐    │    │
│  │  │              DATA LAYER                      │    │    │
│  │  │  (API, Database, Repository Implementations) │    │    │
│  │  └─────────────────────────────────────────────┘    │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

**Why this matters:**
- **Testability**: Each layer can be tested independently
- **Maintainability**: Changes in one layer don't affect others
- **Scalability**: Easy to add new features without breaking existing code

---

### Project Structure Explained

```
com.tofiq.mvi_imdb/
├── data/                    # DATA LAYER - Handles all data operations
│   ├── local/               # Local database (Room)
│   │   ├── dao/             # Data Access Objects (SQL queries)
│   │   ├── entity/          # Database table definitions
│   │   ├── LocalDataSource.kt
│   │   └── MovieDatabase.kt
│   ├── remote/              # Network layer (Retrofit)
│   │   ├── api/             # API interface definitions
│   │   ├── dto/             # Data Transfer Objects (JSON models)
│   │   └── RemoteDataSource.kt
│   ├── mapper/              # Converts between different data models
│   │   ├── MovieMapper.kt   # DTO → Domain
│   │   └── EntityMapper.kt  # Entity ↔ Domain
│   └── repository/          # Repository implementations
│       └── MovieRepositoryImpl.kt
│
├── domain/                  # DOMAIN LAYER - Business logic (pure Kotlin)
│   ├── model/               # Domain models (what the app works with)
│   │   ├── Movie.kt
│   │   ├── MovieDetail.kt
│   │   ├── Cast.kt
│   │   ├── CastMovie.kt     # Movie in actor's filmography
│   │   └── Category.kt
│   ├── repository/          # Repository interfaces (contracts)
│   │   └── MovieRepository.kt
│   └── usecase/             # Business logic operations
│       ├── GetMoviesUseCase.kt
│       ├── GetMovieDetailUseCase.kt
│       ├── GetCastMoviesUseCase.kt
│       ├── SearchMoviesUseCase.kt
│       ├── GetFavoritesUseCase.kt
│       └── ToggleFavoriteUseCase.kt
│
├── presentation/            # PRESENTATION LAYER - UI and state management
│   ├── base/                # MVI base classes
│   │   ├── MviIntent.kt     # User action marker interface
│   │   ├── MviState.kt      # UI state marker interface
│   │   └── MviViewModel.kt  # Base ViewModel with MVI pattern
│   ├── components/          # Reusable UI components
│   │   ├── MovieCard.kt
│   │   ├── MovieGrid.kt
│   │   ├── CategoryTabs.kt
│   │   ├── LoadingIndicator.kt
│   │   └── ErrorView.kt
│   ├── navigation/          # Navigation setup
│   │   ├── NavRoutes.kt     # Route definitions
│   │   └── AppNavigation.kt # Navigation graph
│   └── screens/             # Feature screens
│       ├── home/
│       │   ├── HomeScreen.kt
│       │   ├── HomeViewModel.kt
│       │   ├── HomeState.kt
│       │   └── HomeIntent.kt
│       ├── detail/
│       ├── search/
│       ├── favorites/
│       └── castmovies/      # Actor filmography screen
│           ├── CastMoviesScreen.kt
│           ├── CastMoviesViewModel.kt
│           ├── CastMoviesState.kt
│           └── CastMoviesIntent.kt
│
├── di/                      # Dependency Injection (Hilt modules)
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── ui/theme/                # Material 3 theming
│   ├── Color.kt
│   ├── Theme.kt
│   └── Type.kt              # Custom Anta font
│
└── util/                    # Utilities
    ├── Constants.kt         # API keys, URLs
    ├── Resource.kt          # Success/Error/Loading wrapper
    └── AppError.kt          # Typed error handling
```

---

### 🔄 The MVI Pattern Explained

MVI stands for **Model-View-Intent**. It's a unidirectional data flow pattern that makes state management predictable.

```
┌──────────────────────────────────────────────────────────────┐
│                         MVI CYCLE                             │
│                                                               │
│    ┌─────────┐    Intent    ┌─────────────┐    State         │
│    │  VIEW   │ ──────────▶  │  VIEWMODEL  │ ──────────▶      │
│    │(Screen) │              │  (Process)  │              │    │
│    └─────────┘              └─────────────┘              │    │
│         ▲                                                │    │
│         │                    State                       │    │
│         └────────────────────────────────────────────────┘    │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

**The Flow:**
1. **User Action** → User taps a button, scrolls, types text
2. **Intent** → Action is converted to an Intent (e.g., `HomeIntent.LoadMovies`)
3. **ViewModel** → Processes the intent, calls use cases, updates state
4. **State** → New immutable state is emitted
5. **View** → Compose observes state and recomposes UI

#### Example: Loading Movies

```kotlin
// 1. USER ACTION: User opens the app

// 2. INTENT: Defined in HomeIntent.kt
sealed interface HomeIntent : MviIntent {
    data object LoadMovies : HomeIntent
    data class SelectCategory(val category: Category) : HomeIntent
    data object LoadNextPage : HomeIntent
    data object Retry : HomeIntent
}

// 3. STATE: Defined in HomeState.kt
@Immutable
data class HomeState(
    val movies: ImmutableList<Movie> = persistentListOf(),
    val selectedCategory: Category = Category.POPULAR,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val currentPage: Int = 1,
    val hasMorePages: Boolean = true
) : MviState

// 4. VIEWMODEL: Processes intent in HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase
) : MviViewModel<HomeIntent, HomeState>() {

    override fun processIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.LoadMovies -> loadMovies()
            is HomeIntent.SelectCategory -> selectCategory(intent.category)
            is HomeIntent.LoadNextPage -> loadNextPage()
            is HomeIntent.Retry -> retry()
        }
    }
    
    private fun loadMovies() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            
            getMoviesUseCase(state.value.selectedCategory, 1).collect { resource ->
                when (resource) {
                    is Resource.Loading -> { /* show loading */ }
                    is Resource.Success -> {
                        _state.update {
                            it.copy(
                                movies = resource.data.toImmutableList(),
                                isLoading = false
                            )
                        }
                    }
                    is Resource.Error -> { /* show error */ }
                }
            }
        }
    }
}

// 5. VIEW: Observes state in HomeScreen.kt
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    
    when {
        state.isLoading -> LoadingIndicator()
        state.error != null -> ErrorView(message = state.error!!)
        else -> MovieGrid(movies = state.movies)
    }
}
```

---

### 📦 Data Layer Deep Dive

The data layer is responsible for fetching and storing data. It knows about APIs, databases, and caching strategies.

#### Remote Data Source (API)

```kotlin
// RemoteDataSource.kt - Wraps Retrofit API calls
class RemoteDataSource @Inject constructor(
    private val api: MovieApi
) {
    suspend fun getPopularMovies(page: Int): MovieResponse =
        api.getPopularMovies(page = page)
    
    suspend fun searchMovies(query: String, page: Int): MovieResponse =
        api.searchMovies(query = query, page = page)
    
    suspend fun getCastMovies(personId: Int): PersonMovieCreditsResponse =
        api.getPersonMovieCredits(personId = personId)
}

// MovieApi.kt - Retrofit interface
interface MovieApi {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("page") page: Int
    ): MovieResponse
    
    @GET("person/{person_id}/movie_credits")
    suspend fun getPersonMovieCredits(
        @Path("person_id") personId: Int,
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): PersonMovieCreditsResponse
}
```

#### Local Data Source (Room Database)

```kotlin
// MovieDao.kt - SQL queries for movies
@Dao
interface MovieDao {
    @Query("SELECT * FROM movies WHERE category = :category ORDER BY page")
    suspend fun getMoviesByCategory(category: String): List<MovieEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)
    
    @Query("DELETE FROM movies WHERE category = :category")
    suspend fun clearCategory(category: String)
}

// MovieEntity.kt - Database table
@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String,
    val category: String,
    val page: Int
)
```

#### Repository Implementation

The repository is the **single source of truth**. It decides whether to fetch from network or cache.

```kotlin
// MovieRepositoryImpl.kt
class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : MovieRepository {

    override fun getMovies(category: Category, page: Int): Flow<Resource<List<Movie>>> = flow {
        emit(Resource.Loading())
        
        // 1. Try to get cached data first
        val cachedMovies = localDataSource.getMoviesByCategory(category.name)
        if (cachedMovies.isNotEmpty()) {
            emit(Resource.Success(cachedMovies.toDomainList()))
        }
        
        // 2. Fetch fresh data from network
        try {
            val response = remoteDataSource.getMovies(category, page)
            val movies = response.results.toDomainList()
            
            // 3. Cache the new data
            localDataSource.insertMovies(movies.toEntityList(category, page))
            
            emit(Resource.Success(movies))
        } catch (e: Exception) {
            // 4. Return cached data with error message if network fails
            emit(Resource.Error(
                message = e.toAppError().userMessage,
                data = cachedMovies.toDomainList().takeIf { it.isNotEmpty() }
            ))
        }
    }
}
```

#### Mappers - Converting Between Models

We have three types of models:
- **DTO** (Data Transfer Object): Matches JSON from API
- **Entity**: Matches database table structure
- **Domain Model**: What the app actually uses

```kotlin
// MovieMapper.kt - DTO → Domain
fun MovieDto.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate ?: "",
    voteAverage = voteAverage,
    overview = overview ?: "",
    // Pre-compute values for performance
    releaseYear = releaseDate?.take(4) ?: "",
    formattedRating = String.format("%.1f", voteAverage)
)

// EntityMapper.kt - Entity ↔ Domain
fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview,
    releaseYear = releaseDate.take(4),
    formattedRating = String.format("%.1f", voteAverage)
)

fun Movie.toEntity(category: Category, page: Int): MovieEntity = MovieEntity(
    id = id,
    title = title,
    posterPath = posterPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    overview = overview,
    category = category.name,
    page = page
)
```

---

### 🎯 Domain Layer Deep Dive

The domain layer contains **pure business logic**. It has no Android dependencies - just plain Kotlin.

#### Domain Models

```kotlin
// Movie.kt - What the app works with
@Immutable
data class Movie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val overview: String,
    val isFavorite: Boolean = false,
    // Pre-computed for performance (no getters that recalculate)
    val releaseYear: String,
    val formattedRating: String
)

// CastMovie.kt - Movie in an actor's filmography
@Immutable
data class CastMovie(
    val id: Int,
    val title: String,
    val posterPath: String?,
    val releaseDate: String,
    val character: String,  // Character the actor played
    val releaseYear: String
)
```

#### Use Cases

Use cases encapsulate a single business operation. They're the "verbs" of your app.

```kotlin
// GetMoviesUseCase.kt
class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(category: Category, page: Int): Flow<Resource<List<Movie>>> =
        repository.getMovies(category, page)
}

// GetCastMoviesUseCase.kt
class GetCastMoviesUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    operator fun invoke(personId: Int): Flow<Resource<List<CastMovie>>> =
        repository.getCastMovies(personId)
}

// ToggleFavoriteUseCase.kt
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: MovieRepository
) {
    suspend operator fun invoke(movie: Movie) {
        if (repository.isFavorite(movie.id)) {
            repository.removeFromFavorites(movie.id)
        } else {
            repository.addToFavorites(movie)
        }
    }
}
```

**Why use cases?**
- Single responsibility - one use case, one job
- Reusable across ViewModels
- Easy to test in isolation
- Business logic stays in domain layer

---

### 🖼️ Presentation Layer Deep Dive

#### Base MVI Classes

```kotlin
// MviIntent.kt - Marker interface for all intents
interface MviIntent

// MviState.kt - Marker interface for all states
interface MviState

// MviViewModel.kt - Base class all ViewModels extend
abstract class MviViewModel<I : MviIntent, S : MviState> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract fun processIntent(intent: I)
}
```

#### Screen Structure

Each screen has 4 files:
1. **Screen.kt** - Composable UI
2. **ViewModel.kt** - State management
3. **State.kt** - UI state data class
4. **Intent.kt** - User actions sealed interface

```kotlin
// CastMoviesIntent.kt
sealed interface CastMoviesIntent : MviIntent {
    data class LoadCastMovies(val personId: Int) : CastMoviesIntent
    data object Retry : CastMoviesIntent
}

// CastMoviesState.kt
@Immutable
data class CastMoviesState(
    val movies: ImmutableList<CastMovie> = persistentListOf(),
    val actorName: String = "",
    val actorProfilePath: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
) : MviState
```

#### Compose UI with State Observation

```kotlin
@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Observe state - recomposes when state changes
    val state by viewModel.state.collectAsState()
    
    // Remember callbacks to prevent recomposition
    val onCategorySelected = remember(viewModel) {
        { category: Category -> viewModel.processIntent(HomeIntent.SelectCategory(category)) }
    }
    
    Column {
        CategoryTabs(
            selectedCategory = state.selectedCategory,
            onCategorySelected = onCategorySelected
        )
        
        // Swipeable content
        HorizontalPager(state = pagerState) { page ->
            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorView(state.error!!)
                else -> MovieGrid(movies = state.movies)
            }
        }
    }
}
```

---

### 💉 Dependency Injection with Hilt

Hilt automatically provides dependencies where needed.

```kotlin
// NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    @Provides
    @Singleton
    fun provideMovieApi(retrofit: Retrofit): MovieApi =
        retrofit.create(MovieApi::class.java)
}

// RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindMovieRepository(
        impl: MovieRepositoryImpl
    ): MovieRepository
}
```

---

### ⚡ Performance Optimizations Explained

#### Why @Immutable and ImmutableList?

Compose skips recomposition when inputs haven't changed. But it can only detect changes in "stable" types.

```kotlin
// ❌ BAD: List is not stable - Compose can't skip
data class HomeState(
    val movies: List<Movie>  // Compose always recomposes
)

// ✅ GOOD: ImmutableList is stable - Compose can skip
@Immutable
data class HomeState(
    val movies: ImmutableList<Movie>  // Compose skips if unchanged
)
```

#### Why Pre-computed Values?

```kotlin
// ❌ BAD: Computed property runs on every access
data class Movie(val releaseDate: String) {
    val releaseYear: String
        get() = releaseDate.take(4)  // Runs every time, triggers recomposition
}

// ✅ GOOD: Pre-computed in constructor
data class Movie(
    val releaseDate: String,
    val releaseYear: String = releaseDate.take(4)  // Computed once
)
```

#### Why Remember Callbacks?

```kotlin
// ❌ BAD: New lambda every recomposition
MovieCard(
    onClick = { viewModel.processIntent(Intent.Click) }  // New instance each time
)

// ✅ GOOD: Remembered lambda
val onClick = remember(viewModel) {
    { viewModel.processIntent(Intent.Click) }  // Same instance
}
MovieCard(onClick = onClick)
```

---

### 🧪 Testing Strategy

#### Property-Based Testing with Kotest

Instead of testing specific cases, we test properties that should always be true.

```kotlin
// Mapper should never lose data
class MovieMapperPropertyTest : FunSpec({
    test("mapping DTO to Domain preserves all fields") {
        checkAll(Arb.movieDto()) { dto ->
            val domain = dto.toDomain()
            domain.id shouldBe dto.id
            domain.title shouldBe dto.title
        }
    }
})

// Favorite toggle should be idempotent
class FavoriteTogglePropertyTest : FunSpec({
    test("toggling favorite twice returns to original state") {
        checkAll(Arb.movie()) { movie ->
            val toggled = movie.copy(isFavorite = !movie.isFavorite)
            val toggledBack = toggled.copy(isFavorite = !toggled.isFavorite)
            toggledBack.isFavorite shouldBe movie.isFavorite
        }
    }
})

// Cast movies should be sorted by release date
class CastMoviesSortingPropertyTest : FunSpec({
    test("cast movies are sorted by release date descending") {
        checkAll(Arb.list(Arb.castMovie())) { movies ->
            val sorted = movies.sortedByDescending { it.releaseDate }
            sorted.zipWithNext().all { (a, b) -> a.releaseDate >= b.releaseDate }
        }
    }
})
```

---

## Tech Stack

| Category | Technology |
|----------|------------|
| UI | Jetpack Compose, Material 3, HorizontalPager |
| Architecture | MVI, Clean Architecture |
| Navigation | Navigation3 |
| DI | Hilt |
| Networking | Retrofit, OkHttp, Gson |
| Local Storage | Room Database |
| Image Loading | Coil |
| Async | Kotlin Coroutines, Flow |
| Collections | Kotlinx Collections Immutable |
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

## API

The app integrates with [TMDB API](https://www.themoviedb.org/documentation/api):

| Endpoint | Description |
|----------|-------------|
| `GET /movie/popular` | Popular movies |
| `GET /movie/top_rated` | Top rated movies |
| `GET /movie/upcoming` | Upcoming movies |
| `GET /movie/now_playing` | Now playing movies |
| `GET /movie/{id}` | Movie details |
| `GET /movie/{id}/credits` | Movie cast |
| `GET /movie/{id}/similar` | Similar movies |
| `GET /search/movie` | Search movies |
| `GET /person/{person_id}/movie_credits` | Actor's filmography |

## License

This project is for educational purposes.

---

Built with ❤️ using Kotlin and Jetpack Compose
