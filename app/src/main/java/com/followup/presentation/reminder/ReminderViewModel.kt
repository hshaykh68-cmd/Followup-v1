package com.followup.presentation.reminder

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.followup.domain.model.Reminder
import com.followup.domain.model.ReminderStatus
import com.followup.domain.usecase.CreateReminderUseCase
import com.followup.domain.usecase.DeleteReminderUseCase
import com.followup.domain.usecase.GetPendingCountUseCase
import com.followup.domain.usecase.GetRemindersUseCase
import com.followup.domain.usecase.MarkReminderDoneUseCase
import com.followup.domain.usecase.SnoozeReminderUseCase
import com.followup.domain.usecase.UpdateReminderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ReminderViewModel @Inject constructor(
    private val getRemindersUseCase: GetRemindersUseCase,
    private val createReminderUseCase: CreateReminderUseCase,
    private val updateReminderUseCase: UpdateReminderUseCase,
    private val deleteReminderUseCase: DeleteReminderUseCase,
    private val markReminderDoneUseCase: MarkReminderDoneUseCase,
    private val snoozeReminderUseCase: SnoozeReminderUseCase,
    private val getPendingCountUseCase: GetPendingCountUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReminderListUiState())
    val uiState: StateFlow<ReminderListUiState> = _uiState.asStateFlow()

    val pendingCount: StateFlow<Int> = getPendingCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            getRemindersUseCase()
                .collect { reminders ->
                    _uiState.update { state ->
                        state.copy(
                            reminders = reminders,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun setFilter(filter: ReminderFilter) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    fun addReminder(
        name: String,
        message: String? = null,
        reminderTime: Long,
        onSuccess: ((Long) -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            createReminderUseCase(name, message, reminderTime)
                .fold(
                    onSuccess = { reminderId ->
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess?.invoke(reminderId)
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message
                            )
                        }
                        onError?.invoke(error.message ?: "Failed to create reminder")
                    }
                )
        }
    }

    fun updateReminder(
        reminder: Reminder,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            updateReminderUseCase(reminder)
                .fold(
                    onSuccess = {
                        _uiState.update { it.copy(isLoading = false) }
                        onSuccess?.invoke()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = error.message
                            )
                        }
                        onError?.invoke(error.message ?: "Failed to update reminder")
                    }
                )
        }
    }

    fun deleteReminder(
        reminderId: Long,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            deleteReminderUseCase(reminderId)
                .fold(
                    onSuccess = {
                        onSuccess?.invoke()
                    },
                    onFailure = { error ->
                        _uiState.update {
                            it.copy(errorMessage = error.message)
                        }
                        onError?.invoke(error.message ?: "Failed to delete reminder")
                    }
                )
        }
    }

    fun markAsDone(
        reminderId: Long,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            markReminderDoneUseCase(reminderId)
                .fold(
                    onSuccess = {
                        onSuccess?.invoke()
                    },
                    onFailure = { error ->
                        onError?.invoke(error.message ?: "Failed to mark as done")
                    }
                )
        }
    }

    fun snoozeReminder(
        reminderId: Long,
        durationMillis: Long,
        onSuccess: (() -> Unit)? = null,
        onError: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            snoozeReminderUseCase(reminderId, durationMillis)
                .fold(
                    onSuccess = {
                        onSuccess?.invoke()
                    },
                    onFailure = { error ->
                        onError?.invoke(error.message ?: "Failed to snooze reminder")
                    }
                )
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
