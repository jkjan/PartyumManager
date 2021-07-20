package com.partyum.partyummanager.di

import com.partyum.partyummanager.document.DocumentViewModel
import com.partyum.partyummanager.main.MainModel
import com.partyum.partyummanager.main.MainRepository
import com.partyum.partyummanager.main.MainViewModel
import com.partyum.partyummanager.main.SearchFragment
import com.partyum.partyummanager.reservation.ReservationViewModel
import org.koin.androidx.fragment.dsl.fragment
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { MainRepository() }
    single { MainModel() }

    viewModel { MainViewModel(get(), get()) }
    viewModel { DocumentViewModel() }
    viewModel { ReservationViewModel() }

    fragment { SearchFragment() }
}