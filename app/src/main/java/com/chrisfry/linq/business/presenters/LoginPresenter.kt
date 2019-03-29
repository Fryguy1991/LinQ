package com.chrisfry.linq.business.presenters

import android.util.Log
import com.chrisfry.linq.business.presenters.interfaces.ILoginPresenter


class LoginPresenter : ILoginPresenter {
    companion object {
        private val TAG = this::class.java.name
    }
    private var view: ILoginView? = null

    override fun attach(view: ILoginView) {
        Log.d(TAG, "Attaching LoginPresenter to view")

        this.view = view
    }

    override fun loginPressed() {
        Log.d(TAG, "Login pressed. Requesting new authorization")

        view?.requestAuthorization()
    }

    override fun detach() {
        Log.d(TAG, "Detaching LoginPresenter from view")

        view = null
    }

    interface ILoginView {
        fun requestAuthorization()
    }
}