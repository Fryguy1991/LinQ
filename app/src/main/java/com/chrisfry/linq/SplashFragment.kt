package com.chrisfry.linq


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.chrisfry.linq.business.presenters.SplashPresenter
import com.chrisfry.linq.business.presenters.interfaces.ISplashPresenter

/**
 * A simple [Fragment] subclass.
 *
 */
class SplashFragment : Fragment(), SplashPresenter.ISplashView {
    companion object {
        private val TAG = this::class.java.name
    }

    private val accessBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && context != null) {
                if (intent.action == AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED) {
                    // If we received indication that access token is updated that means we successfully logged in
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(this)

                    this@SplashFragment.findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLinkDisplayFragment())
                }
            }
        }
    }

    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message?) {
            presenter?.animationComplete()
        }
    }

    private var presenter: ISplashPresenter? = null
    private var listener: SplashFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is SplashFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement LoginFragmentListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = SplashPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        presenter?.attach(this)
    }

    override fun onResume() {
        super.onResume()

        // TODO: Add some sort of animation, this 2 second wait simulates an animation time
        handler.sendEmptyMessageDelayed(0, 2000)
    }

    override fun onDestroy() {
        presenter?.detach()
        presenter = null

        listener = null

        super.onDestroy()
    }


    override fun navigateToLogin() {
        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
    }

    override fun refreshAccessAndNavigateToLinkList() {
        val currentContext = context
        if (currentContext != null) {
            LocalBroadcastManager.getInstance(currentContext)
                .registerReceiver(accessBroadcastReceiver, IntentFilter(AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED))
            listener?.requestAccessRefresh()
        }
    }

    interface SplashFragmentListener {
        fun requestAccessRefresh()
    }
}
