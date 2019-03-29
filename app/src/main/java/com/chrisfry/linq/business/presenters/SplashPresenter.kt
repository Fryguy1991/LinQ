package com.chrisfry.linq.business.presenters

import android.util.Log
import com.chrisfry.linq.business.models.AccessModel
import com.chrisfry.linq.business.presenters.interfaces.ISplashPresenter

class SplashPresenter: ISplashPresenter{
    companion object {
        private val TAG = this::class.java.name
    }

    private var view: ISplashView? = null

    override fun attach(view: ISplashView) {
        Log.d(TAG, "Attaching SplashPresenter to view")
        this.view = view
    }

    override fun animationComplete() {
        Log.d(TAG, "Splash animation complete")
        if (AccessModel.getRefreshToken().isEmpty()) {
            Log.d(TAG, "Navigating to login screen")
            view?.navigateToLogin()
        } else {
            Log.d(TAG, "Attempting to refresh access and go to link list screen")
            view?.refreshAccessAndNavigateToLinkList()
        }
    }

    override fun detach() {
        Log.d(TAG, "Detaching SplashPresenter from view")
        view = null
    }

    interface ISplashView {
        fun navigateToLogin()

        fun refreshAccessAndNavigateToLinkList()
    }
}