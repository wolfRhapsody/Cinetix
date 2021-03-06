package alex.guerra.cinetix.ui.main

import alex.guerra.cinetix.ui.common.Event
import alex.guerra.cinetix.ui.common.ScopeViewModel
import alex.guerra.domain.Movie
import alex.guerra.usecases.GetPopularMovies
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class MainViewModel(
    private val getPopularMovies: GetPopularMovies,
    uiDispatcher: CoroutineDispatcher
) : ScopeViewModel(uiDispatcher) {

    init {
        initScope()
    }

    sealed class UiModel {
        object Loading : UiModel()
        data class Content(val movies: List<Movie>) : UiModel()
        object RequestLocationPermission : UiModel()
    }

    private val _model = MutableLiveData<UiModel>()
    val model: LiveData<UiModel>
        get() {
            if (_model.value == null) refresh()
            return _model
        }

    private val _navigation = MutableLiveData<Event<Movie>>()
    val navigation: LiveData<Event<Movie>> get() = _navigation

    private fun refresh() {
        _model.value =
            UiModel.RequestLocationPermission
    }

    fun getRemotePopularMovies() {
        launch {
            _model.value = UiModel.Loading
            _model.value = UiModel.Content(getPopularMovies())
        }
    }

    fun onMovieClicked(movie: Movie) {
        _navigation.value = Event(movie)
    }

    override fun onCleared() {
        destroyScope()
        super.onCleared()
    }
}
