package com.mioshek.theclock.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mioshek.theclock.views.bars.BottomNavigationItem

class ClockNavigation {
    companion object{
        @SuppressLint("NotConstructor")
        @Composable
        fun Navigate(navController: NavHostController){
            val width = LocalConfiguration.current.screenWidthDp
            val enterAnimation = fadeIn(
                tween(
                    durationMillis = 250,
                    delayMillis = 50,
                    easing = LinearOutSlowInEasing
                ),
            )

            val exitAnimation = fadeOut(
                tween(
                    durationMillis = 250,
                    delayMillis = 50,
                    easing = LinearOutSlowInEasing
                ),
            )

            NavHost(navController = navController, startDestination = "main"){
                navigation(
                    startDestination = BottomNavigationItem.AlarmList.route,
                    route = "main"
                ){
                    composable(
                        route = BottomNavigationItem.AlarmList.route,
                        enterTransition = {enterAnimation},
                        exitTransition = { exitAnimation }
                    ){
                        AlarmsListView()
                    }

                    composable(
                        BottomNavigationItem.Stopwatch.route,
                        enterTransition = {enterAnimation},
                        exitTransition = { exitAnimation }
                    ){
                        StopwatchView()
                    }

                    composable(
                        BottomNavigationItem.Timer.route,
                        enterTransition = {enterAnimation},
                        exitTransition = { exitAnimation }
                    ){
                        TimerView()
                    }
                }
            }
        }

        @SuppressLint("RestrictedApi")
        private fun clearStack(navController: NavController){
            navController.navigate(navController.currentBackStack.toString()){
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                // Avoid multiple copies of the same destination when
                // reselecting the same item
                launchSingleTop = true
                // Restore state when reselecting a previously selected item
                restoreState = true
            }
        }
    }
}