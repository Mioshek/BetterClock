package com.mioshek.theclock.views

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.mioshek.theclock.extensions.permissions.PermissionManager
import com.mioshek.theclock.extensions.permissions.RuntimePermissions
import com.mioshek.theclock.services.NotificationsManager
import com.mioshek.theclock.views.stopwatch.StopwatchView
import com.mioshek.theclock.views.bars.BottomNavigationItem
import com.mioshek.theclock.views.clock.AlarmsListView
import com.mioshek.theclock.views.timer.TimerView

class ClockNavigation {
    companion object {
        @SuppressLint("NotConstructor")
        @Composable
        fun Navigate(navController: NavHostController) {
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

            NavHost(navController = navController, startDestination = "main") {
                navigation(
                    startDestination = BottomNavigationItem.Stopwatch.route,
                    route = "main"
                ) {
                    composable(
                        route = BottomNavigationItem.AlarmList.route,
                        enterTransition = { enterAnimation },
                        exitTransition = { exitAnimation }
                    ) {
                        var showView by remember{ mutableStateOf(true)}

                        BackToMain(context = LocalContext.current, navController = navController) {
                            showView = false
                        }
                        if (showView) AlarmsListView()
                    }

                    composable(
                        BottomNavigationItem.Stopwatch.route,
                        enterTransition = { enterAnimation },
                        exitTransition = { exitAnimation }
                    ) {
                        StopwatchView()
                    }

                    composable(
                        BottomNavigationItem.Timer.route,
                        enterTransition = { enterAnimation },
                        exitTransition = { exitAnimation }
                    ) {
                        var showView by remember{ mutableStateOf(true)}
                        BackToMain(context = LocalContext.current, navController = navController) {
                            showView = false
                        }

                        if (showView) TimerView()
                    }
                }
            }
        }

        @SuppressLint("RestrictedApi")
        private fun clearStack(navController: NavController) {
            navController.navigate(navController.currentBackStack.toString()) {
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

        @Composable
        private fun BackToMain(context: Context, navController: NavController, onCancel: () -> Unit){
            val activity = LocalContext.current as Activity
            var showDialog by remember{ mutableStateOf(true)}
            if (PermissionManager.checkPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) && showDialog
            ) {
                NotificationsAlertDialog(
                    onCancel = {
                        navController.popBackStack()
                        navController.navigate(BottomNavigationItem.Stopwatch.route)

                        showDialog = false
                        onCancel()
                    },
                    onOk = {
                        PermissionManager.requestPermission(activity, Manifest.permission.POST_NOTIFICATIONS)
                        showDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun NotificationsAlertDialog(onCancel: () -> Unit, onOk: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(text = "Notifications Required")
        },
        text = {
            Text(text = "Notifications must be enabled for the app to function properly.")
        },
        dismissButton = {
            Button(
                onClick = onCancel
            ) {
                Text(text = "Cancel")
            }
        },
        confirmButton = {
            Button(
                onClick = onOk
            ) {
                Text(text = "OK")
            }
        }
    )
}