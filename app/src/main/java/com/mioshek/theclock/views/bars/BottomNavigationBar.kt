package com.mioshek.theclock.views.bars

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mioshek.theclock.R
import com.mioshek.theclock.data.Storage

enum class BottomNavigationItem(var route: String, var icon: Int, val index: Int, val title: Int) {
    AlarmList("alarm-list", R.drawable.alarm, 1, R.string.alarms),
    Stopwatch("stopwatch", R.drawable.stopwatch, 2, R.string.stopwatch),
    Timer("timer", R.drawable.hourglass, 3, R.string.timer)
}

@Composable
fun BottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        BottomNavigationItem.AlarmList,
        BottomNavigationItem.Stopwatch,
        BottomNavigationItem.Timer
    )

    NavigationBar(
        modifier = modifier.clip(BottomNavCurve()),
//        containerColor = MaterialTheme.colorScheme.background,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
        tonalElevation = 5.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach{ item ->

            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = item.icon),
                        contentDescription = stringResource(id = item.title),
                    )
                },
                label = { Text(text = stringResource(id = item.title)) },
                alwaysShowLabel = true,
                selected = currentRoute == item.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.secondary,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                    indicatorColor = MaterialTheme.colorScheme.background,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(0.8f),
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledIconColor = MaterialTheme.colorScheme.onBackground.copy(0.1f),
                    disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(0.1f)
                ),
                onClick = {
                    Storage.put("BottomNavBarItemIndex", item.index)
                    navController.navigate(item.route) {
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
                },
            )
        }
    }
}