package com.partyum.partyummanager.di

import com.partyum.partyummanager.document.DocumentRepository
import com.partyum.partyummanager.document.DocumentViewModel
import com.partyum.partyummanager.model.MainModel
import com.partyum.partyummanager.main.MainRepository
import com.partyum.partyummanager.main.MainViewModel
import com.partyum.partyummanager.reservation.ReservationRepository
import com.partyum.partyummanager.reservation.ReservationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { MainRepository() }
    single { ReservationRepository() }
    single { MainModel() }
    single { DocumentRepository() }

    viewModel { MainViewModel(get(), get()) }
    viewModel { DocumentViewModel(get(), get()) }
    viewModel { ReservationViewModel(get()) }
}