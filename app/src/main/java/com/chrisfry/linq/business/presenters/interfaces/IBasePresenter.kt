package com.chrisfry.linq.business.presenters.interfaces

interface IBasePresenter<T> {
    abstract fun attach(view: T)

    abstract fun detach()
}