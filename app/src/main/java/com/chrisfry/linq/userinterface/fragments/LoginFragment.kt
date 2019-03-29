package com.chrisfry.linq.userinterface.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.chrisfry.linq.AppConstants
import com.chrisfry.linq.R
import com.chrisfry.linq.business.presenters.LoginPresenter
import com.chrisfry.linq.business.presenters.interfaces.ILoginPresenter

/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment(), LoginPresenter.ILoginView {

    private var presenter: ILoginPresenter? = null
    private var listener: LoginFragmentListener? = null

    // UI ELEMENTS
    // Button pressed to login (when not automatically logged in by presenter)
    private lateinit var loginButton: View

    private val accessBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && context != null) {
                if (intent.action == AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED) {
                    // If we received indication that access token is updated that means we successfully logged in
                    LocalBroadcastManager.getInstance(context).unregisterReceiver(this)

                    this@LoginFragment.findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToLinkDisplayFragment())
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is LoginFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement LoginFragmentListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = LoginPresenter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.attach(this)

        loginButton = view.findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            presenter?.loginPressed()
        }
    }

    override fun onDestroy() {
        presenter?.detach()
        presenter = null

        super.onDestroy()
    }

    override fun onDetach() {
        listener = null

        super.onDetach()
    }

    override fun requestAuthorization() {
        val currentContext = context
        if (currentContext != null) {
            LocalBroadcastManager.getInstance(currentContext)
                .registerReceiver(accessBroadcastReceiver, IntentFilter(AppConstants.BR_INTENT_ACCESS_TOKEN_UPDATED))
            listener?.requestAuthorization()
        }
    }

    interface LoginFragmentListener {
        fun requestAuthorization()
    }
}
