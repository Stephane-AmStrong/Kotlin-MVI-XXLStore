package stephane.amstrong.kotlinmvixxlstore.presentation.category.list

import android.app.SearchManager
import android.content.Context.SEARCH_SERVICE
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import stephane.amstrong.kotlinmvixxlstore.R
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryQueryUtils.Companion.CATEGORY_FILTER_DATE_UPDATED
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryQueryUtils.Companion.CATEGORY_FILTER_USERNAME
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryQueryUtils.Companion.CATEGORY_ORDER_ASC
import stephane.amstrong.kotlinmvixxlstore.business.datasource.cache.category.CategoryQueryUtils.Companion.CATEGORY_ORDER_DESC
import stephane.amstrong.kotlinmvixxlstore.business.domain.models.Category
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.*
import stephane.amstrong.kotlinmvixxlstore.databinding.FragmentCategoryBinding
import stephane.amstrong.kotlinmvixxlstore.presentation.category.BaseCategoryFragment
import stephane.amstrong.kotlinmvixxlstore.presentation.util.TopSpacingItemDecoration
import stephane.amstrong.kotlinmvixxlstore.presentation.util.processQueue

class CategoryFragment : BaseCategoryFragment(),
    CategoryAdapter.Interaction,
    SwipeRefreshLayout.OnRefreshListener
{

    private lateinit var searchView: SearchView
    private var recyclerAdapter: CategoryAdapter? = null // can leak memory so need to null
    private val viewModel: CategoryViewModel by viewModels()
    private lateinit var menu: Menu

    private var _binding: FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        setHasOptionsMenu(true)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecyclerView()
        subscribeObservers()
    }

    private fun subscribeObservers(){
        viewModel.state.observe(viewLifecycleOwner) { state ->

            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(CategoryEvents.OnRemoveHeadFromQueue)
                    }
                })

            recyclerAdapter?.apply {
                submitList(categories = state.categories)
            }
        }
    }

    private fun initSearchView(){
        activity?.apply {
            val searchManager: SearchManager = getSystemService(SEARCH_SERVICE) as SearchManager
            searchView = menu.findItem(R.id.action_search).actionView as SearchView
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setIconifiedByDefault(true)
            searchView.isSubmitButtonEnabled = true
        }

        // ENTER ON COMPUTER KEYBOARD OR ARROW ON VIRTUAL KEYBOARD
        val searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_src_text) as EditText

        // set initial value of query text after rotation/navigation
        viewModel.state.value?.let { state ->
            if(state.searchTerm.isNotBlank()){
                searchPlate.setText(state.searchTerm)
                searchView.isIconified = false
                binding.focusableView.requestFocus()
            }
        }
        searchPlate.setOnEditorActionListener { v, actionId, event ->

            if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED
                || actionId == EditorInfo.IME_ACTION_SEARCH ) {
                val searchQuery = v.text.toString()
                Log.e(TAG, "SearchView: (keyboard or arrow) executing search...: ${searchQuery}")
                executeNewQuery(searchQuery)
            }
            true
        }

        // SEARCH BUTTON CLICKED (in toolbar)
        val searchButton = searchView.findViewById(androidx.appcompat.R.id.search_go_btn) as View
        searchButton.setOnClickListener {
            val searchQuery = searchPlate.text.toString()
            Log.e(TAG, "SearchView: (button) executing search...: ${searchQuery}")
            executeNewQuery(searchQuery)
        }

    }

    private fun executeNewQuery(query: String){
        viewModel.onTriggerEvent(CategoryEvents.UpdateSearchTerm(query))
        viewModel.onTriggerEvent(CategoryEvents.NewSearch)
        resetUI()
    }

    private  fun resetUI(){
        uiCommunicationListener.hideSoftKeyboard()
        binding.focusableView.requestFocus()
    }

    private fun initRecyclerView(){
        binding.categoryRecyclerview.apply {
            layoutManager = LinearLayoutManager(this@CategoryFragment.context)
            val topSpacingDecorator = TopSpacingItemDecoration(30)
            removeItemDecoration(topSpacingDecorator) // does nothing if not applied already
            addItemDecoration(topSpacingDecorator)

            recyclerAdapter = CategoryAdapter(this@CategoryFragment)
            addOnScrollListener(object: RecyclerView.OnScrollListener(){

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    Log.d(TAG, "onScrollStateChanged: exhausted? ${viewModel.state.value?.isQueryExhausted}")
                    if (
                        lastPosition == recyclerAdapter?.itemCount?.minus(1)
                        && viewModel.state.value?.isLoading == false
                        && viewModel.state.value?.isQueryExhausted == false
                    ) {
                        Log.d(TAG, "CategoryFragment: attempting to load next page...")
                        viewModel.onTriggerEvent(CategoryEvents.NextPage)
                    }
                }
            })
            adapter = recyclerAdapter
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        inflater.inflate(R.menu.search_menu, this.menu)
        initSearchView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.action_filter_settings -> {
                showFilterDialog()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onItemSelected(position: Int, category: Category) {
        try{
            viewModel.state.value?.let { state ->
                val bundle = bundleOf("categoryId" to category.id)
                findNavController().navigate(R.id.action_blogFragment_to_viewBlogFragment, bundle)
            }?: throw Exception("Null Category")
        }catch (e: Exception){
            e.printStackTrace()
            viewModel.onTriggerEvent(
                CategoryEvents.Error(
                    stateMessage = StateMessage(
                        response = Response(
                            message = e.message,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()
                        )
                    )
                ))
        }
    }

    override fun onRefresh() {
        viewModel.onTriggerEvent(CategoryEvents.NewSearch)
        binding.swipeRefresh.isRefreshing = false
    }

    private fun showFilterDialog(){
        activity?.let {
            viewModel.state.value?.let { state ->
                val filter = state.filter.value
                val order = state.order.value

                val dialog = MaterialDialog(it)
                    .noAutoDismiss()
                    .customView(R.layout.layout_category_filter)

                val view = dialog.getCustomView()

                view.findViewById<RadioGroup>(R.id.filter_group).apply {
                    when (filter) {
                        CATEGORY_FILTER_DATE_UPDATED -> check(R.id.filter_date)
                        CATEGORY_FILTER_USERNAME -> check(R.id.filter_author)
                    }
                }

                view.findViewById<RadioGroup>(R.id.order_group).apply {
                    when (order) {
                        CATEGORY_ORDER_ASC -> check(R.id.filter_asc)
                        CATEGORY_ORDER_DESC -> check(R.id.filter_desc)
                    }
                }

                view.findViewById<TextView>(R.id.positive_button).setOnClickListener {
                    val newFilter =
                        when (view.findViewById<RadioGroup>(R.id.filter_group).checkedRadioButtonId) {
                            R.id.filter_author -> CATEGORY_FILTER_USERNAME
                            R.id.filter_date -> CATEGORY_FILTER_DATE_UPDATED
                            else -> CATEGORY_FILTER_DATE_UPDATED
                        }

                    val newOrder =
                        when (view.findViewById<RadioGroup>(R.id.order_group).checkedRadioButtonId) {
                            R.id.filter_desc -> "-"
                            else -> ""
                        }

                    viewModel.apply {
                        onTriggerEvent(CategoryEvents.UpdateFilter(getFilterFromValue(newFilter)))
                        onTriggerEvent(CategoryEvents.UpdateOrder(getOrderFromValue(newOrder)))
                        onTriggerEvent(CategoryEvents.NewSearch)
                    }

                    dialog.dismiss()
                }

                view.findViewById<TextView>(R.id.negative_button).setOnClickListener {
                    Log.d(TAG, "FilterDialog: cancelling filter.")
                    dialog.dismiss()
                }

                dialog.show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        recyclerAdapter = null
        _binding = null
    }
}