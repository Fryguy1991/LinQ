package com.chrisfry.linq.business.dagger.components

import com.chrisfry.linq.business.dagger.modules.AppModule
import com.chrisfry.linq.business.dagger.modules.DatabaseModule
import com.chrisfry.linq.business.dagger.modules.FrySpotifyModule
import com.chrisfry.linq.business.presenters.LinkDisplayPresenter
import com.chrisfry.linq.business.presenters.SettingsPresenter
import com.chrisfry.linq.services.AccessService
import com.chrisfry.linq.userinterface.App
import com.chrisfry.linq.userinterface.activites.BaseActivity
import com.chrisfry.linq.userinterface.activites.MainActivity
import com.chrisfry.linq.userinterface.fragments.LoginFragment
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AppModule::class, FrySpotifyModule::class, DatabaseModule::class])
interface AppComponent {
    fun inject(app: App)
    fun inject(baseActivity: BaseActivity)
    // TODO: Will be removing below activity
    fun inject(mainActivity: MainActivity)
    fun inject(accessService: AccessService)
    fun inject(loginFragment: LoginFragment)
    fun inject(linkDisplayPresenter: LinkDisplayPresenter)
    fun inject(settingsPresenter: SettingsPresenter)
}