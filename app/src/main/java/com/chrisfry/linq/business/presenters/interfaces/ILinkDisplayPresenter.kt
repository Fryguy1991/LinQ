package com.chrisfry.linq.business.presenters.interfaces

import com.chrisfry.linq.business.presenters.LinkDisplayPresenter

interface ILinkDisplayPresenter : IBasePresenter<LinkDisplayPresenter.ILinkDisplayView> {

    fun createNewLink()

    fun editExistingLink(trackGroupId: Int)

    fun deleteLink(trackGroupId: Int)

    fun settingsPressed()
}