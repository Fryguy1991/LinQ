package com.chrisfry.linq.business.presenters.interfaces

import com.chrisfry.linq.business.presenters.SplashPresenter

interface ISplashPresenter : IBasePresenter<SplashPresenter.ISplashView> {
    fun animationComplete()
}