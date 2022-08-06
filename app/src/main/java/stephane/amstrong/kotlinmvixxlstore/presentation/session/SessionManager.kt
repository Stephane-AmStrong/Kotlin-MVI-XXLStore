package stephane.amstrong.kotlinmvixxlstore.presentation.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Authentication
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.SuccessHandling.Companion.SUCCESS_LOGOUT
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.doesMessageAlreadyExistInQueue
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.CheckPreviousAuthUser
import stephane.amstrong.kotlinmvixxlstore.business.interactors.session.Logout
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Think of this class as an "application viewmodel"
 * It keeps the authentication state of the user.
 */
@Singleton
class SessionManager
@Inject
constructor(
    private val checkPreviousAuthUser: CheckPreviousAuthUser,
    private val logout: Logout,
    private val appDataStoreManager: AppDataStore,
) {

    private val TAG: String = "AppDebug"

    private val sessionScope = CoroutineScope(Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        // Check if a user was authenticated in a previous session
        sessionScope.launch {
            appDataStoreManager.readValue(DataStoreKeys.PREVIOUS_AUTH_USER)?.let { email ->
                onTriggerEvent(SessionEvents.CheckPreviousAuthUser(email))
            }?: onFinishCheckingPrevAuthUser()
        }
    }

    fun onTriggerEvent(event: SessionEvents){
        when(event){
            is SessionEvents.Login -> {
                login(event.authentication)
            }
            is SessionEvents.Logout -> {
                logout()
            }
            is SessionEvents.CheckPreviousAuthUser -> {
                checkPreviousAuthUser(email = event.email)
            }
            is SessionEvents.OnRemoveHeadFromQueue ->{
                removeHeadFromQueue()
            }
            is SessionEvents.Authenticate -> {
                authenticate(event.authentication)
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

    private fun checkPreviousAuthUser(email: String){
        state.value?.let { state ->
            checkPreviousAuthUser.execute(email).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { authentication ->
                    this.state.value = state.copy(authentication = authentication)
                    onTriggerEvent(SessionEvents.Login(authentication))
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message.equals(RESPONSE_CHECK_PREVIOUS_AUTH_USER_DONE)){
                        onFinishCheckingPrevAuthUser()
                    }
                    else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(sessionScope)
        }
    }

    private fun authenticate(authentication: Authentication){
        state.value?.let { state ->
            this.state.value = state.copy(authentication = authentication)
        }
    }

    private fun login(authentication: Authentication){
        state.value?.let { state ->
            this.state.value = state.copy(authentication = authentication)
        }
    }

    private fun logout(){
        state.value?.let { state ->
            logout.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)
                dataState.data?.let { response ->
                    if(response.message.equals(SUCCESS_LOGOUT)){
                        this.state.value = state.copy(authentication = null)
                        clearAuthUser()
                        onFinishCheckingPrevAuthUser()
                    }
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(sessionScope)
        }
    }

    private fun onFinishCheckingPrevAuthUser(){
        state.value?.let { state ->
            this.state.value = state.copy(didCheckForPreviousAuthUser = true)
        }
    }

    private fun clearAuthUser() {
        sessionScope.launch {
            appDataStoreManager.setValue(DataStoreKeys.PREVIOUS_AUTH_USER, "")
        }
    }

}