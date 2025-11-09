package com.app.base.ui.alarm.character

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.utils.AppConstants
import com.brally.mobile.base.viewmodel.BaseViewModel
import com.app.base.R

class CharacterViewModel: BaseViewModel() {
    private val _characters = MutableLiveData<List<Int>>()
    val characters: LiveData<List<Int>> = _characters

    private val _selectedCharacter = MutableLiveData<Int> ()
    val selectedCharacter: LiveData<Int> = _selectedCharacter

    fun loadCharacters(initialSelected: Int? = null) {
        _characters.value = AppConstants.getAllCharacters()
        _selectedCharacter.value = initialSelected ?: R.drawable.img_naruto
    }

    fun updateSelectedCharacter(characterId: Int) {
        _selectedCharacter.value = characterId
    }
}