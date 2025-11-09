package com.app.base.ui.alarm.sound

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.R
import com.app.base.utils.AppConstants
import com.brally.mobile.base.viewmodel.BaseViewModel

class SoundViewModel : BaseViewModel() {

    private val _sounds = MutableLiveData<List<Int>>()
    val sounds: LiveData<List<Int>> = _sounds

    private val _selectedSoundId = MutableLiveData<Int>()

    val selectedSoundId: LiveData<Int> = _selectedSoundId

    fun loadSounds(initialSelected: Int?) {
        _sounds.value = AppConstants.getAllSoundIds()
        _selectedSoundId.value = initialSelected ?: R.raw.base
    }

    fun selectSound(soundId: Int) {
        _selectedSoundId.value = soundId
    }
}