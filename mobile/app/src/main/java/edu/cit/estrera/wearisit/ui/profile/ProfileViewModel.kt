package edu.cit.estrera.wearisit.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.estrera.wearisit.core.Result
import edu.cit.estrera.wearisit.data.models.ProfileResponse
import edu.cit.estrera.wearisit.data.repository.AuthRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val repo: AuthRepository) : ViewModel() {
    val user = MutableLiveData<ProfileResponse?>()
    val error = MutableLiveData<String?>()

    fun loadProfile() {
        viewModelScope.launch {
            when (val result = repo.getProfile()) {
                is Result.Success -> {
                    user.value = result.data
                }
                is Result.Error -> {
                    error.value = result.message
                    user.value = null
                }
            }
        }
    }
}