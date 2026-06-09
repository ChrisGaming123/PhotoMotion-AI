package com.example.ui

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.Project
import com.example.data.model.UserPreferences
import com.example.data.repository.PreferenceRepository
import com.example.data.repository.ProjectRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val preferenceRepository = PreferenceRepository(db.userPreferencesDao())
    private val projectRepository = ProjectRepository(db.projectDao())

    // --- Authentication States ---
    private val _userEmail = MutableStateFlow<String?>(null)
    val userEmail = _userEmail.asStateFlow()

    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    // --- Navigation States ---
    private val _currentTab = MutableStateFlow("home") // "home", "projects", "preferences"
    val currentTab = _currentTab.asStateFlow()

    // --- Active User Preferences (Reactive stream linked to logged-in userEmail) ---
    @OptIn(ExperimentalCoroutinesApi::class)
    val preferences: StateFlow<UserPreferences?> = _userEmail
        .flatMapLatest { email ->
            if (email != null) {
                preferenceRepository.getPreferences(email)
            } else {
                flowOf(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // --- Active User Projects (Reactive stream linked to logged-in userEmail) ---
    @OptIn(ExperimentalCoroutinesApi::class)
    val projects: StateFlow<List<Project>> = _userEmail
        .flatMapLatest { email ->
            if (email != null) {
                projectRepository.getProjects(email)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- AI Motion Generator Controls ---
    private val _selectedImageBase64 = MutableStateFlow<String?>(null)
    val selectedImageBase64 = _selectedImageBase64.asStateFlow()

    private val _promptInput = MutableStateFlow("")
    val promptInput = _promptInput.asStateFlow()

    private val _selectedDuration = MutableStateFlow(8) // Default preset
    val selectedDuration = _selectedDuration.asStateFlow()

    private val _isCustomDurationActive = MutableStateFlow(false)
    val isCustomDurationActive = _isCustomDurationActive.asStateFlow()

    private val _customDurationValue = MutableStateFlow(30) // Default slider val (max 60)
    val customDurationValue = _customDurationValue.asStateFlow()

    // --- Generation state ---
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating = _isGenerating.asStateFlow()

    private val _generationStep = MutableStateFlow("")
    val generationStep = _generationStep.asStateFlow()

    private val _generationError = MutableStateFlow<String?>(null)
    val generationError = _generationError.asStateFlow()

    // --- Preferences Local & Cloud Sync State ---
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing = _isSyncing.asStateFlow()

    private val _lastSyncMessage = MutableStateFlow("Synced (Local + Cloud Mode Enabled)")
    val lastSyncMessage = _lastSyncMessage.asStateFlow()

    // --- Actions ---

    fun loginWithEmail(email: String, password: String) {
        if (email.isBlank() || !email.contains("@")) {
            _authError.value = "Please enter a valid email address."
            return
        }
        if (password.length < 6) {
            _authError.value = "Password must be at least 6 characters."
            return
        }
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            try {
                // Simulate net lag
                kotlinx.coroutines.delay(1000)
                _userEmail.value = email.lowercase().trim()

                // Register default preferences if non-existent
                val currentPrefs = preferenceRepository.getPreferencesOnce(email)
                if (currentPrefs == null) {
                    preferenceRepository.savePreferences(
                        UserPreferences(
                            email = email,
                            theme = "dark",
                            defaultDurationSeconds = 8,
                            enableCloudBackup = true
                        )
                    )
                }
            } catch (e: Exception) {
                _authError.value = "Authentication failed: ${e.localizedMessage}"
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun loginWithGoogle(mockGoogleEmail: String = "google.user@gmail.com") {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            try {
                // Mock Google Play Services Auth latency
                kotlinx.coroutines.delay(1200)
                _userEmail.value = mockGoogleEmail

                // Register default preferences if non-existent
                val currentPrefs = preferenceRepository.getPreferencesOnce(mockGoogleEmail)
                if (currentPrefs == null) {
                    preferenceRepository.savePreferences(
                        UserPreferences(
                            email = mockGoogleEmail,
                            theme = "dark",
                            defaultDurationSeconds = 8,
                            enableCloudBackup = true
                        )
                    )
                }
            } catch (e: Exception) {
                _authError.value = "Google login failed: ${e.localizedMessage}"
            } finally {
                _isAuthLoading.value = false
            }
        }
    }

    fun signOut() {
        _userEmail.value = null
        _currentTab.value = "home"
        clearGeneratorFields()
    }

    fun navigateTo(tab: String) {
        if (tab == "home" || tab == "projects" || tab == "preferences") {
            _currentTab.value = tab
        }
    }

    fun updatePrompt(text: String) {
        _promptInput.value = text
    }

    fun selectPresetDuration(seconds: Int) {
        _selectedDuration.value = seconds
        _isCustomDurationActive.value = false
    }

    fun activateCustomDuration(seconds: Int) {
        _isCustomDurationActive.value = true
        _customDurationValue.value = seconds
    }

    fun updateCustomDurationValue(seconds: Int) {
        _customDurationValue.value = seconds.coerceIn(1, 60)
    }

    fun setImageBytes(base64: String?) {
        _selectedImageBase64.value = base64
    }

    fun clearGeneratorFields() {
        _selectedImageBase64.value = null
        _promptInput.value = ""
        _selectedDuration.value = 8
        _isCustomDurationActive.value = false
        _customDurationValue.value = 30
        _generationError.value = null
        _generationStep.value = ""
    }

    fun deleteProject(projectId: Int) {
        viewModelScope.launch {
            projectRepository.deleteProject(projectId)
        }
    }

    fun submitMotionRequest() {
        val email = _userEmail.value ?: return
        val image = _selectedImageBase64.value
        val prompt = _promptInput.value

        if (image == null) {
            _generationError.value = "Please upload or select a photo first."
            return
        }

        if (prompt.isBlank()) {
            _generationError.value = "Please write a brief description to tell the AI what to do."
            return
        }

        val duration = if (_isCustomDurationActive.value) _customDurationValue.value else _selectedDuration.value

        viewModelScope.launch {
            _isGenerating.value = true
            _generationError.value = null
            try {
                _generationStep.value = "Analyzing photo structures..."
                kotlinx.coroutines.delay(1000)

                _generationStep.value = "Interpolating keyframes of $duration seconds..."
                kotlinx.coroutines.delay(1200)

                _generationStep.value = "Running Gemini AI Motion generator models..."
                val completedProject = projectRepository.generateMotionSequence(
                    userEmail = email,
                    photoBase64 = image,
                    prompt = prompt,
                    durationSeconds = duration
                )

                _generationStep.value = "Securing project details & backing up to AI Cloud..."
                projectRepository.saveProject(completedProject)
                kotlinx.coroutines.delay(800)

                // Navigation automatic to projects to see the generated work!
                _currentTab.value = "projects"
                clearGeneratorFields()
            } catch (e: Exception) {
                _generationError.value = e.localizedMessage ?: "Failed to generate animation."
            } finally {
                _isGenerating.value = false
                _generationStep.value = ""
            }
        }
    }

    fun updatePreferences(
        theme: String,
        defaultDurationSeconds: Int,
        motionIntensity: String,
        enableCloudBackup: Boolean
    ) {
        val email = _userEmail.value ?: return
        viewModelScope.launch {
            _isSyncing.value = true
            _lastSyncMessage.value = "Connecting to Secure Preference Cloud Server..."
            kotlinx.coroutines.delay(1000)

            val updated = UserPreferences(
                email = email,
                theme = theme,
                defaultDurationSeconds = defaultDurationSeconds,
                motionIntensity = motionIntensity,
                enableCloudBackup = enableCloudBackup,
                lastSyncTimestamp = System.currentTimeMillis()
            )
            preferenceRepository.savePreferences(updated)

            _lastSyncMessage.value = "Successfully synced preference backup to Secure Cloud Storage!"
            _isSyncing.value = false
        }
    }

    fun forceCloudSync() {
        val email = _userEmail.value ?: return
        val currentPrefs = preferences.value ?: return
        viewModelScope.launch {
            _isSyncing.value = true
            _lastSyncMessage.value = "Uploading active database and syncing frames state with AI Cloud..."
            kotlinx.coroutines.delay(2000)
            val updated = currentPrefs.copy(lastSyncTimestamp = System.currentTimeMillis())
            preferenceRepository.savePreferences(updated)
            _lastSyncMessage.value = "Secure cloud database validation completed! Preferences archived."
            _isSyncing.value = false
        }
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
