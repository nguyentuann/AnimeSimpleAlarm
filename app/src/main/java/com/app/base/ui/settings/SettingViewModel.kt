package com.app.base.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.base.data.model.SettingModel
import com.app.base.local.db.AppPreferences
import com.brally.mobile.base.viewmodel.BaseViewModel
import com.app.base.R


class SettingViewModel(
) : BaseViewModel() {

    private val _settingList = MutableLiveData<List<SettingModel>>()
    val settingList: LiveData<List<SettingModel>> get() = _settingList

    val prefs: AppPreferences by lazy {
        AppPreferences(context)
    }

    fun loadSettings() {
        val list = listOf(
            SettingModel(
                title = getString(R.string.language),
                iconRes = R.drawable.ic_language,
                action = ::onLanguageClicked
            ),
            SettingModel(
                title = getString(R.string.feed_back),
                iconRes = R.drawable.ic_feedback,
                action = ::onFeedbackClicked
            ),
            SettingModel(
                title = getString(R.string.policy),
                iconRes = R.drawable.ic_policy,
                action = ::onPolicyClicked
            ),
            SettingModel(
                title = getString(R.string.share_app),
                iconRes = R.drawable.ic_share,
                action = ::onShareClicked
            ),
        )
        _settingList.value = list
    }

    private fun onLanguageClicked() {
        _events.value = Event(SettingEvent.ShowLanguageDialog)
    }

    private fun onFeedbackClicked() {
        _events.value = Event(SettingEvent.ShowFeedbackDialog)
    }

    private fun onPolicyClicked() {
        _events.value = Event(SettingEvent.ShowDevelopingToast)
    }

    private fun onShareClicked() {
        _events.value = Event(SettingEvent.ShowDevelopingToast)
    }

    // Event LiveData để Fragment quan sát
    private val _events = MutableLiveData<Event<SettingEvent>>()
    val events: LiveData<Event<SettingEvent>> get() = _events

    sealed class SettingEvent {
        object ShowLanguageDialog : SettingEvent()
        object ShowFeedbackDialog : SettingEvent()
        object ShowDevelopingToast : SettingEvent()
    }
}

class Event<out T>(private val content: T) {
    private var hasBeenHandled = false
    fun getContentIfNotHandled(): T? =
        if (hasBeenHandled) null else {
            hasBeenHandled = true
            content
        }
}

