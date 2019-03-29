package com.chrisfry.linq.business.dagger.components

import com.chrisfry.linq.business.dagger.modules.DatabaseModule
import com.chrisfry.linq.business.presenters.LinkDisplayPresenter
import com.chrisfry.linq.userinterface.activites.BaseActivity
import com.chrisfry.linq.userinterface.activites.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component (modules = [DatabaseModule::class])
interface AppDatabaseComponent {
//    fun inject(baseActivity: BaseActivity)
//    fun inject(linkDisplayPresenter: LinkDisplayPresenter)
}