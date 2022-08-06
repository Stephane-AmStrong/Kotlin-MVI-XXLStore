package stephane.amstrong.kotlinmvixxlstore.presentation.account.authentication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessageCallback
import stephane.amstrong.kotlinmvixxlstore.databinding.FragmentAuthenticationBinding
import stephane.amstrong.kotlinmvixxlstore.presentation.BaseFragment
import stephane.amstrong.kotlinmvixxlstore.presentation.util.processQueue

class AuthenticationFragment : BaseFragment() {

    private val viewModel : AuthenticationViewModel by viewModels ()

    private var _binding: FragmentAuthenticationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAuthenticationBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeObservers()
        binding.btnLogin.setOnClickListener{
            cacheState()
            authenticate()
        }
    }
    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner){
            uiCommunicationListener.displayProgressBar(it.isLoading)
            processQueue(
                context = context,
                queue = it.queue,
                stateMessageCallback = object : StateMessageCallback{
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(AuthenticationEvents.OnRemoveHeadFromQueue)
                    }
                }
            )

        }
    }

    private fun authenticate() {
        viewModel.onTriggerEvent(AuthenticationEvents.Authenticate(
            email = binding.txtEmail.text.toString(),
            password = binding.txtPwd.text.toString(),
        ))
    }

    private fun cacheState() {
        viewModel.onTriggerEvent(AuthenticationEvents.OnUpdateEmail(binding.txtEmail.text.toString()))
        viewModel.onTriggerEvent(AuthenticationEvents.OnUpdatePassword(binding.txtPwd.text.toString()))
    }

    override fun onPause() {
        super.onPause()
        cacheState()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}