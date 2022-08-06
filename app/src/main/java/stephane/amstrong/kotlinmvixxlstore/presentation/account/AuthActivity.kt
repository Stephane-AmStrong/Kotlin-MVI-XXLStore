package stephane.amstrong.kotlinmvixxlstore.presentation.account

import android.content.Intent
import android.os.Bundle
import android.view.View
import stephane.amstrong.kotlinmvixxlstore.business.domain.util.StateMessageCallback
import stephane.amstrong.kotlinmvixxlstore.databinding.ActivityAuthBinding
import stephane.amstrong.kotlinmvixxlstore.presentation.BaseActivity
import stephane.amstrong.kotlinmvixxlstore.presentation.main.MainActivity
import stephane.amstrong.kotlinmvixxlstore.presentation.session.SessionEvents
import stephane.amstrong.kotlinmvixxlstore.presentation.util.processQueue
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class AuthActivity : BaseActivity()
{

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        subscribeObservers()
    }

    private fun subscribeObservers(){
        sessionManager.state.observe(this) { state ->
            displayProgressBar(state.isLoading)
            processQueue(
                context = this,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        sessionManager.onTriggerEvent(SessionEvents.OnRemoveHeadFromQueue)
                    }
                }
            )
            if (state.didCheckForPreviousAuthUser) {
                onFinishCheckPreviousAuthUser()
            }
            if (state.authentication?.accessToken != null || state.authentication != null && state.authentication.accessToken.expiryDate> Date() ) {
                navMainActivity()
            }
        }
    }

    private fun onFinishCheckPreviousAuthUser(){
        binding.fragmentContainer.visibility = View.VISIBLE
        binding.splashLogo.visibility = View.INVISIBLE
    }

    private fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun displayProgressBar(isLoading: Boolean){
        if(isLoading){
            binding.progressBar.visibility = View.VISIBLE
        }
        else{
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun expandAppBar() {
        // ignore
    }

}










