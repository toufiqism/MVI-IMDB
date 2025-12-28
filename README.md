# MVI-IMDB 🎬

A modern Android movie discovery app built with **Jetpack Compose**, **MVI architecture**, and **TMDB API**. Browse popular movies, search for titles, view detailed information, explore actor filmographies, and save your favorites for offline access.

## Features

- 🏠 **Home Screen** - Browse movies by category with swipeable tabs (Popular, Top Rated, Upcoming, Now Playing)
- 👆 **Swipe Navigation** - Swipe left/right to switch between categories
- 🔍 **Search** - Find movies by title with debounced search (300ms, min 2 chars)
- 📖 **Movie Details** - View comprehensive movie info including cast, genres, and similar movies
- 🎭 **Actor Filmography** - Tap on any cast member to explore all their movies (sorted by release date)
- ❤️ **Favorites** - Save movies locally for quick access with confirmation feedback
- 📴 **Offline Support** - Cached data available without internet
- ♾️ **Infinite Scroll** - Automatic pagination when scrolling
- ⚡ **Optimized Performance** - Recomposition-optimized with immutable collections and stable annotations
- 🎨 **Custom Typography** - Anta font family throughout the app
- 🔔 **MVI Effects** - Clean separation of one-time events (navigation, toasts) from persistent UI state
- 🌈 **Vibrant Colorful Theme** - Beautiful Material 3 color scheme with vibrant blues, purples, pinks, and accent colors
- ✨ **Professional Animations** - Delightful, cinema-quality animations throughout the app including:
  - Card press animations with scale, rotation, and elevation effects
  - Staggered grid item entrance animations (fade + scale + slide)
  - Gradient pill indicator for category tabs with smooth transitions
  - Multi-ring loading spinner with gradient colors and pulse effects
  - Parallax backdrop scrolling on detail screens
  - Animated favorite button with rotation and pulse
  - Bouncy navigation bar icons with accent colors
  - Shimmer loading placeholders for professional loading states
  - Animated empty states and error views
  - Screen entrance animations with slide and fade
- 🔧 **Optimized for Configuration Changes** - All animations and states properly survive screen rotations

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
│   │   ├── MviEffect.kt     # One-time event marker interface
│   │   └── MviViewModel.kt  # Base ViewModel with MVI + Effects
│   ├── components/          # Reusable UI components
│   │   ├── MovieCard.kt        # Enhanced card with animations
│   │   ├── MovieGrid.kt        # Optimized lazy grid
│   │   ├── CategoryTabs.kt     # Gradient pill indicator tabs
│   │   ├── LoadingIndicator.kt # Multi-ring animated spinner
│   │   ├── ErrorView.kt        # Animated error with retry
│   │   └── ShimmerEffect.kt    # Shimmer loading placeholders
│   ├── navigation/          # Navigation setup
│   │   ├── NavRoutes.kt     # Route definitions
│   │   └── AppNavigation.kt # Navigation graph
│   └── screens/             # Feature screens
│       ├── home/
│       │   ├── HomeScreen.kt
│       │   ├── HomeViewModel.kt
│       │   ├── HomeState.kt
│       │   ├── HomeIntent.kt
│       │   └── HomeEffect.kt
│       ├── detail/          # + DetailEffect.kt
│       ├── search/          # + SearchEffect.kt
│       ├── favorites/       # + FavoritesEffect.kt
│       └── castmovies/      # Actor filmography screen
│           ├── CastMoviesScreen.kt
│           ├── CastMoviesViewModel.kt
│           ├── CastMoviesState.kt
│           ├── CastMoviesIntent.kt
│           └── CastMoviesEffect.kt
│
├── di/                      # Dependency Injection (Hilt modules)
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   ├── DatabaseModule.kt
│   └── RepositoryModule.kt
│
├── ui/theme/                # Material 3 theming
│   ├── Color.kt             # Vibrant color palette
│   ├── Theme.kt             # Material 3 theme configuration
│   ├── Animation.kt         # Animation utilities and specs
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
│         ▲                          │                     │    │
│         │         State            │ Effect (one-time)   │    │
│         └──────────────────────────┴─────────────────────┘    │
│                                                               │
└──────────────────────────────────────────────────────────────┘
```

**The Flow:**
1. **User Action** → User taps a button, scrolls, types text
2. **Intent** → Action is converted to an Intent (e.g., `HomeIntent.LoadMovies`)
3. **ViewModel** → Processes the intent, calls use cases, updates state
4. **State** → New immutable state is emitted (persistent UI data)
5. **Effect** → One-time events emitted via SharedFlow (navigation, toasts)
6. **View** → Compose observes state and collects effects

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

// MviEffect.kt - Marker interface for one-time events
interface MviEffect

// MviViewModel.kt - Base class all ViewModels extend
abstract class MviViewModel<I : MviIntent, S : MviState, E : MviEffect> : ViewModel() {
    abstract val state: StateFlow<S>
    abstract val effect: SharedFlow<E>  // One-time events (navigation, toasts)
    abstract fun processIntent(intent: I)
    protected fun emitEffect(effect: E) { /* ... */ }
}
```

#### Screen Structure

Each screen has 5 files:
1. **Screen.kt** - Composable UI
2. **ViewModel.kt** - State management
3. **State.kt** - UI state data class
4. **Intent.kt** - User actions sealed interface
5. **Effect.kt** - One-time events (navigation, messages)

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

// DetailEffect.kt - One-time events for detail screen
sealed interface DetailEffect : MviEffect {
    data class NavigateToMovie(val movieId: Int) : DetailEffect
    data class ShowFavoriteMessage(val added: Boolean) : DetailEffect
    data class NavigateToCastMovies(val castId: Int, val name: String) : DetailEffect
}
```

#### Compose UI with State Observation

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    // Observe state - recomposes when state changes
    val state by viewModel.state.collectAsState()
    
    // Collect one-time effects (navigation, toasts)
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HomeEffect.NavigateToDetail -> navController.navigate("detail/${effect.movieId}")
                is HomeEffect.ShowError -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }
    
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
| Animations | Compose Animation APIs (Spring, Tween) |
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

## 🎨 Theme & Animations

### Colorful Theme

The app features a vibrant Material 3 color scheme with:
- **Primary**: Vibrant Blue (#2196F3)
- **Secondary**: Vibrant Purple (#9C27B0)
- **Tertiary**: Vibrant Pink (#E91E63)
- **Accent Colors**: Orange, Teal, Green, Red, Yellow, Cyan
- **Gradient Colors**: Cinema gradients, sunset gradients, ocean gradients
- **Dark Theme**: Rich dark colors optimized for OLED displays

The theme automatically adapts to light/dark mode while maintaining the vibrant color palette.

### Professional Animations

The app includes cinema-quality, GPU-accelerated animations throughout:

1. **Movie Card Animations**
   - **Press Effect**: Scale down to 95% with subtle rotation
   - **Elevation Animation**: Dynamic shadow depth on interaction
   - **Entrance Animation**: Staggered fade + scale + slide from bottom
   - **Rating Badge**: Gradient background with star icon
   - **Poster Loading**: Shimmer placeholder effect

2. **Grid & List Animations**
   - **Staggered Entrance**: 30ms delay per item (capped at 400ms)
   - **Combined Effects**: Alpha fade + scale + translateY
   - **Horizontal Lists**: Slide in from right with fade
   - **Pagination Indicator**: Animated loading at bottom

3. **Category Tabs**
   - **Gradient Pill Indicator**: Smooth sliding with VibrantBlue → VibrantPurple gradient
   - **Selection Animation**: Bouncy scale effect with FontWeight transition
   - **Color Transitions**: Animated text color changes

4. **Bottom Navigation**
   - **Icon Animations**: Bouncy spring scale (1.15x) with vertical offset
   - **Accent Colors**: Each tab has unique accent color (Blue/Purple/Pink)
   - **Radial Glow**: Selected item gets radial gradient background
   - **Rounded Corners**: Modern rounded top corners with shadow

5. **Loading Indicators**
   - **Multi-Ring Spinner**: Three animated rings with gradient colors
   - **Outer Ring**: Clockwise rotation (1.5s), gradient sweep
   - **Middle Ring**: Counter-clockwise rotation (1s)
   - **Center**: Pulsing gradient dot
   - **Animated Text**: "Loading..." with sequential dot animation
   - **Shimmer Placeholders**: Full-screen shimmer grids matching layout

6. **Detail Screen**
   - **Parallax Backdrop**: Backdrop moves at 0.3x scroll speed
   - **Section Entrances**: Staggered AnimatedVisibility with delays
   - **Cast Items**: Circular profile with horizontal slide entrance
   - **Favorite Button**: Rotation + scale animation on toggle
   - **Rating Chip**: Gradient background with star icon

7. **Empty & Error States**
   - **Bounce-In Animation**: Spring-based scale entrance
   - **Staggered Content**: Icon → Title → Message → Button
   - **Pulsing Icons**: Gradient background with animated icon
   - **Retry Button**: Scale animation on press

8. **Search Screen**
   - **Search Bar**: Focus scale animation (1.02x)
   - **Clear Button**: Animated fade in/out with scale
   - **Hint State**: Large icon with gradient background
   - **Results**: Same staggered grid animations

### Animation Specifications

```kotlin
object AnimationSpecs {
    // Duration constants
    const val INSTANT = 100
    const val SHORT_DURATION = 200
    const val MEDIUM_DURATION = 400
    const val LONG_DURATION = 600
    
    // Stagger delays
    const val STAGGER_DELAY_FAST = 30
    const val STAGGER_DELAY_MEDIUM = 50
    
    // Spring configurations
    val DefaultSpring = spring<Float>(dampingRatio = 0.6f, stiffness = 200f)
    val BouncySpring = spring<Float>(dampingRatio = 0.4f, stiffness = 150f)
    val SnappySpring = spring<Float>(dampingRatio = 0.7f, stiffness = 300f)
}
```

### Performance Optimizations

All animations are optimized for 60fps performance:

- **graphicsLayer**: All transforms use GPU-accelerated `graphicsLayer` modifier
- **Remembered Animations**: Animation states are properly remembered
- **Capped Delays**: Stagger delays capped at 400ms for large lists
- **Stable Keys**: All list items have stable keys for efficient diffing
- **Configuration Change Survival**: States preserved across rotations

## License

This project is for educational purposes.

---

Built with ❤️ using Kotlin and Jetpack Compose
