package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType
import okhttp3.ResponseBody
import retrofit2.Response

class ActivityViewModel : ViewModel() {

    private val getPostsSubject = PublishSubject.create<Unit>()
    val getNameObserver: io.reactivex.rxjava3.core.Observer<Unit> = getPostsSubject

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    init {
        getPostsSubject
            .switchMap {
                Repository.getPosts().toObservable().onErrorReturn { getFailureResponse() }
            }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { res ->
                    if (res.isSuccessful) {
                        res.body()?.let { _state.value = State.Loaded(it) }
                    } else {
                        _state.value = State.Loaded(emptyList())
                    }
                },
                {
                    _state.value = State.Loaded(emptyList())
                }
            )
    }

    private fun getFailureResponse(): Response<List<MainActivity.Adapter.Item>> {
        return Response.error<List<MainActivity.Adapter.Item>>(
            100,
            ResponseBody.create(MediaType.get("text"), "text")
        )
    }
}
