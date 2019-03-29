package com.chrisfry.linq.business.presenters.interfaces

import com.chrisfry.linq.business.presenters.LoginPresenter

interface ILoginPresenter : IBasePresenter<LoginPresenter.ILoginView> {
    fun loginPressed()
}