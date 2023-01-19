package com.moontv.application.roomdb

import androidx.lifecycle.*
import com.moontv.application.model.MoonTvMediaItem
import kotlinx.coroutines.launch

class MediaItemViewModel(private val foodItemRepository: MediaItemRepository) : ViewModel() {

    val allFoodItems : LiveData<MutableList<MoonTvMediaItem>> = foodItemRepository.getAllMediaFlow().asLiveData()

    // Launching a new coroutine to insert the data in a non-blocking way
    fun insert(mediaItem: MoonTvMediaItem) = viewModelScope.launch {
        foodItemRepository.insert(mediaItem)
    }

    fun delete(mediaItem: MoonTvMediaItem) = viewModelScope.launch {
        foodItemRepository.delete(mediaItem)
    }

}