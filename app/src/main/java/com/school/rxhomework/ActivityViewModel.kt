package com.school.rxhomework

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject

class ActivityViewModel : ViewModel() {

    private val getPostsSubject = PublishSubject.create<List<MainActivity.Adapter.Item>>()

    private val _state = MutableLiveData<State>(State.Loading)
    val state: LiveData<State>
        get() = _state

    init {
        refreshData()
        getPostsSubject.subscribe { _state.value = State.Loaded(it) }
    }

    private fun refreshData() {
        Repository.getPosts()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                { res ->
                    if (res.isSuccessful) {
                        res.body()?.let { getPostsSubject.onNext(it) }
                    }
                },
                {
                    getPostsSubject.onNext(emptyList())
                }
            )
    }

    fun processAction(action: Action) {
        when (action) {
            Action.RefreshData -> refreshData()
        }
    }
}
