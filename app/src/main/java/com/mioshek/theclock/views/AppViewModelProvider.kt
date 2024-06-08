//package com.mioshek.theclock.views
//
//import androidx.lifecycle.viewmodel.CreationExtras
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import com.mioshek.theclock.controllers.StopwatchViewModel
//
//object AppViewModelProvider {
//    val factory = viewModelFactory {
//        initializer {
//            StopwatchViewModel()
//        }
//    }
//}
//
//fun CreationExtras.habitApplication(): HabitApplication =
//    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitApplication)