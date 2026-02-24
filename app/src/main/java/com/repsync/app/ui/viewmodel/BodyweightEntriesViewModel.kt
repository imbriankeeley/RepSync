package com.repsync.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.repsync.app.data.RepSyncDatabase
import com.repsync.app.data.entity.BodyweightEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

data class BodyweightEntriesUiState(
    val entries: List<BodyweightEntryEntity> = emptyList(),
    val filteredEntries: List<BodyweightEntryEntity> = emptyList(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val editingEntry: BodyweightEntryEntity? = null,
    val showEditDialog: Boolean = false,
    val showDatePicker: Boolean = false,
)

class BodyweightEntriesViewModel(application: Application) : AndroidViewModel(application) {

    private val bodyweightDao = RepSyncDatabase.getDatabase(application).bodyweightDao()

    private val _uiState = MutableStateFlow(BodyweightEntriesUiState())
    val uiState: StateFlow<BodyweightEntriesUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            bodyweightDao.getAllEntriesChronological().collect { entries ->
                val reversed = entries.reversed()
                _uiState.value = _uiState.value.copy(
                    entries = reversed,
                    filteredEntries = applyFilter(reversed, _uiState.value.startDate, _uiState.value.endDate),
                )
            }
        }
    }

    fun setDateRange(startDate: LocalDate, endDate: LocalDate) {
        _uiState.value = _uiState.value.copy(
            startDate = startDate,
            endDate = endDate,
            filteredEntries = applyFilter(_uiState.value.entries, startDate, endDate),
            showDatePicker = false,
        )
    }

    fun clearDateRange() {
        _uiState.value = _uiState.value.copy(
            startDate = null,
            endDate = null,
            filteredEntries = _uiState.value.entries,
        )
    }

    fun showDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = true)
    }

    fun dismissDatePicker() {
        _uiState.value = _uiState.value.copy(showDatePicker = false)
    }

    fun showEditDialog(entry: BodyweightEntryEntity) {
        _uiState.value = _uiState.value.copy(
            editingEntry = entry,
            showEditDialog = true,
        )
    }

    fun dismissEditDialog() {
        _uiState.value = _uiState.value.copy(
            editingEntry = null,
            showEditDialog = false,
        )
    }

    fun updateWeight(id: Long, newWeight: Double) {
        viewModelScope.launch {
            bodyweightDao.updateWeight(id, newWeight)
            dismissEditDialog()
        }
    }

    fun deleteEntry(entry: BodyweightEntryEntity) {
        viewModelScope.launch {
            bodyweightDao.delete(entry)
        }
    }

    private fun applyFilter(
        entries: List<BodyweightEntryEntity>,
        startDate: LocalDate?,
        endDate: LocalDate?,
    ): List<BodyweightEntryEntity> {
        if (startDate == null || endDate == null) return entries
        return entries.filter { entry ->
            val entryDate = runCatching { LocalDate.parse(entry.date) }.getOrNull()
            entryDate != null && !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate)
        }
    }
}
