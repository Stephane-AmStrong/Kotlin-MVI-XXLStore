package stephane.amstrong.kotlinmvixxlstore.presentation.account.authentication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import stephane.amstrong.kotlinmvixxlstore.business.interactors.account.Authenticate
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.doesMessageAlreadyExistInQueue
import stephane.amstrong.kotlinmvixxlstore.presentation.session.SessionEvents
import stephane.amstrong.kotlinmvixxlstore.presentation.session.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel
@Inject
constructor(
    private val authenticate: Authenticate,
    private val sessionManager: SessionManager,
): ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<AuthenticationState> = MutableLiveData(AuthenticationState())

    fun onTriggerEvent(event: AuthenticationEvents){
        when(event){
            is AuthenticationEvents.Authenticate -> {
                authenticate(email = event.email, password = event.password)
            }
            is AuthenticationEvents.OnUpdateEmail ->{
                onUpdateEmail(event.email)
            }
            is AuthenticationEvents.OnUpdatePassword ->{
                onUpdatePassword(event.password)
            }
            is AuthenticationEvents.OnForgotPassword -> {
                TODO()
            }
            is AuthenticationEvents.OnRefreshAccessToken -> {
                TODO()
            }
            is AuthenticationEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
            is AuthenticationEvents.OnResetPassword -> {
                TODO()
            }
            is AuthenticationEvents.RevokeAccessToken -> {
                TODO()
            }
        }
    }

    private fun removeHeadFromQueue(){
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            }catch (e: Exception){
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                if(!(stateMessage.response.uiComponentType is UIComponentType.None)){
                    queue.add(stateMessage)
                    this.state.value = state.copy(queue = queue)
                }
            }
        }
    }

    private fun onUpdateEmail(email: String){
        state.value?.let { state ->
            this.state.value = state.copy(email = email)
        }
    }

    private fun onUpdatePassword(password: String){
        state.value?.let { state ->
            this.state.value = state.copy(password = password)
        }
    }

    private fun authenticate(email: String, password: String){
        // TODO("Perform some simple form validation")
        state.value?.let { state ->
            authenticate.execute(
                email = email,
                password = password,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { authentication ->
                    sessionManager.onTriggerEvent(SessionEvents.Authenticate(authentication))
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }
}