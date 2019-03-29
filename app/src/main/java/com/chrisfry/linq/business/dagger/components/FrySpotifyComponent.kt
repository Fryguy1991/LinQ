package com.chrisfry.linq.business.dagger.components

import com.chrisfry.linq.business.dagger.modules.FrySpotifyModule
import com.chrisfry.linq.business.presenters.LinkDisplayPresenter
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FrySpotifyModule::class])
interface FrySpotifyComponent {
//    fun inject(linkDisplayPresenter: LinkDisplayPresenter)
}