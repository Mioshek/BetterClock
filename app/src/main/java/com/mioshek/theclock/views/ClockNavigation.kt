package com.mioshek.theclock.views

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
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
            NavHost(navController = navController, startDestination = "main"){
                navigation(
                    startDestination = BottomNavigationItem.AlarmList.route,
                    route = "main"
                ){
                    composable(BottomNavigationItem.AlarmList.route){
                        AlarmsListView()
                    }

                    composable(BottomNavigationItem.Stopwatch.route){
                        StopwatchView()
                    }

                    composable(BottomNavigationItem.Timer.route){
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