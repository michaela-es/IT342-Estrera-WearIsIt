package edu.cit.estrera.wearisit.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.estrera.wearisit.data.models.ProfileResponse
import edu.cit.estrera.wearisit.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: AuthRepository) : ViewModel() {
    val user = MutableLiveData<ProfileResponse>()

    fun loadProfile() {
        viewModelScope.launch {
            try {
                val result = repo.getProfile()
                user.value = result
            } catch (e: Exception) {
            }
        }
    }
}