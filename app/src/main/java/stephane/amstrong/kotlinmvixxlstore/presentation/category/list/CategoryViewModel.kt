package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import stephane.amstrong.kotlinmvixxlstore.business.datasource.datastore.AppDataStore
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.ErrorHandling
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessage
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.UIComponentType
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.doesMessageAlreadyExistInQueue
import stephane.amstrong.kotlinmvixxlstore.business.interactors.category.GetOrderAndFilter
import stephane.amstrong.kotlinmvixxlstore.presentation.session.SessionManager
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys.Companion.CATEGORY_FILTER
import stephane.amstrong.kotlinmvixxlstore.presentation.util.DataStoreKeys.Companion.CATEGORY_ORDER
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import stephane.amstrong.kotlinmvixxlstore.business.interactors.category.SearchCategories
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel
@Inject
constructor(
    private val sessionManager: SessionManager,
    private val searchCategories: SearchCategories,
    private val getOrderAndFilter: GetOrderAndFilter,
    private val appDataStoreManager: AppDataStore,
) : ViewModel() {

    private val TAG: String = "AppDebug"

    val state: MutableLiveData<CategoryState> = MutableLiveData(CategoryState())

    init {
        onTriggerEvent(CategoryEvents.GetOrderAndFilter)
    }

    fun onTriggerEvent(event: CategoryEvents) {
        when (event) {
            is CategoryEvents.NewSearch -> {
                search()
            }
            is CategoryEvents.NextPage -> {
                nextPageNumber()
            }
            is CategoryEvents.UpdateFilter -> {
                onUpdateFilter(event.filter)
            }
            is CategoryEvents.UpdateSearchTerm -> {
                onUpdateQuery(event.searchTerm)
            }
            is CategoryEvents.UpdateOrder -> {
                onUpdateOrder(event.order)
            }
            is CategoryEvents.GetOrderAndFilter -> {
                getOrderAndFilter()
            }
            is CategoryEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is CategoryEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun getOrderAndFilter() {
        state.value?.let { state ->
            getOrderAndFilter.execute().onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { orderAndFilter ->
                    val order = orderAndFilter.order
                    val filter = orderAndFilter.filter
                    this.state.value = state.copy(
                        order = order,
                        filter = filter
                    )
                    onTriggerEvent(CategoryEvents.NewSearch)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun saveFilterOptions(filter: String, order: String) {
        viewModelScope.launch {
            appDataStoreManager.setValue(CATEGORY_FILTER, filter)
            appDataStoreManager.setValue(CATEGORY_ORDER, order)
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
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

    private fun onUpdateQueryExhausted(isExhausted: Boolean) {
        state.value?.let { state ->
            this.state.value = state.copy(isQueryExhausted = isExhausted)
        }
    }

    private fun clearList() {
        state.value?.let { state ->
            this.state.value = state.copy(categories = listOf())
        }
    }

    private fun resetPageNumber() {
        state.value = state.value?.copy(pageNumber = 1)
        onUpdateQueryExhausted(false)
    }

    private fun incrementPageNumberNumber() {
        state.value?.let { state ->
            this.state.value = state.copy(pageNumber = state.pageNumber + 1)
        }
    }

    private fun onUpdateQuery(searchTerm: String) {
        state.value = state.value?.copy(searchTerm = searchTerm)
    }

    private fun onUpdateFilter(filter: CategoryFilterOptions) {
        state.value?.let { state ->
            this.state.value = state.copy(filter = filter)
            saveFilterOptions(filter.value, state.order.value)
        }
    }

    private fun onUpdateOrder(order: CategoryOrderOptions) {
        state.value?.let { state ->
            this.state.value = state.copy(order = order)
            saveFilterOptions(state.filter.value, order.value)
        }
    }

    private fun search() {
        resetPageNumber()
        clearList()
        state.value?.let { state ->
            searchCategories.execute(
                authentication = sessionManager.state.value?.authentication,
                searchTerm = state.searchTerm,
                withTheName = state.withTheName,
                orderBy = state.orderBy,
                pageNumber = state.pageNumber,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(categories = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

    private fun nextPageNumber() {
        incrementPageNumberNumber()
        state.value?.let { state ->
            searchCategories.execute(
                authentication = sessionManager.state.value?.authentication,
                searchTerm = state.searchTerm,
                withTheName = state.withTheName,
                orderBy = state.orderBy,
                pageNumber = state.pageNumber,
            ).onEach { dataState ->
                this.state.value = state.copy(isLoading = dataState.isLoading)

                dataState.data?.let { list ->
                    this.state.value = state.copy(categories = list)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(true)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }

            }.launchIn(viewModelScope)
        }
    }

}