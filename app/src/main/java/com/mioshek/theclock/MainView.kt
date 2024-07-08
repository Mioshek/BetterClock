package com.mioshek.theclock

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mioshek.theclock.views.ClockNavigation
import com.mioshek.theclock.views.bars.BottomNavigationBar

@Composable
fun MainView(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        modifier.fillMaxSize(),

//        topBar = {
//            appBars.TopAppBar()
//        },

        content = { padding -> // We have to pass the scaffold inner padding to our content. That's why we use Box.
            Box(modifier = Modifier.padding(padding)) {
                ClockNavigation.Navigate(navController = navController)
            }
        },

        bottomBar = {
            BottomNavigationBar(navController)
        },

        containerColor = MaterialTheme.colorScheme.surface
    )
}
