package `in`.dimigo.dimigoin.ui.login

import `in`.dimigo.dimigoin.data.model.LoginRequestModel
import `in`.dimigo.dimigoin.data.usecase.auth.AuthUseCase
import `in`.dimigo.dimigoin.data.usecase.user.UserUseCase
import `in`.dimigo.dimigoin.data.util.UserDataStore
import `in`.dimigo.dimigoin.ui.util.EventWrapper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class LoginViewModel(private val useCase: AuthUseCase, private val userUseCase: UserUseCase) : ViewModel() {
    private val _event = MutableLiveData<EventWrapper<LoginActivity.Event>>()
    private val _isLoginRequested = MutableLiveData<Boolean>()

    val id = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val event: LiveData<EventWrapper<LoginActivity.Event>> = _event
    val isLoginRequested: LiveData<Boolean> = _isLoginRequested

    fun onLoginButtonClick() {
        val loginRequestModel = LoginRequestModel(id.value ?: "", password.value ?: "")
        viewModelScope.launch {
            _isLoginRequested.value = true
            login(loginRequestModel)
            _isLoginRequested.value = false
        }
    }

    private suspend fun login(loginRequestModel: LoginRequestModel) {
        val loginSucceeded = useCase.tryLogin(loginRequestModel)
        if (!loginSucceeded) {
            _event.value = EventWrapper(LoginActivity.Event.LoginFail)
            return
        }

        try {
            UserDataStore.userData = userUseCase.getMyInfo()
            _event.value = EventWrapper(LoginActivity.Event.LoginSuccess)
        } catch (e: Exception) {
            e.printStackTrace()
            _event.value = EventWrapper(LoginActivity.Event.LoginFail)
        }
    }
}
