package com.chrisfry.linq.business.presenters.interfaces

import com.chrisfry.linq.business.presenters.SettingsPresenter

interface ISettingsPresenter : IBasePresenter<SettingsPresenter.ISettingsView> {

    fun enforceLinksChanged(shouldBeActive: Boolean)

    fun requestRemoveAllLinks()

    fun logoutPressed()
}