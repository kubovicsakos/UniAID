package com.kakos.uniAID.core.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kakos.uniAID.R
import kotlinx.coroutines.launch

/**
 * Represents the navigation drawer for the application.
 *
 * This component provides a side navigation menu with options to navigate
 * to different screens within the app.
 *
 * @param modifier Optional [Modifier] to be applied to the drawer.
 * @param navController The [NavController] used for navigation.
 * @param drawerState The [DrawerState] representing the current state of the drawer.
 */
@Composable
fun NavDrawer(
    modifier: Modifier = Modifier,
    navController: NavController,
    drawerState: DrawerState
) {
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentBackStackEntry?.destination?.route


    NavigationDrawerItem(
        label = { Text(text = "Notes") },
        icon = {
            Icon(
                //imageVector = Icons.Default.Create,
                painter = painterResource(R.drawable.notes),
                contentDescription = "Navigate to Notes"
            )
        },
        selected = currentRoute == Screen.NotesScreen::class.java.canonicalName,
        onClick = {
            scope.launch {
                if (currentRoute != Screen.NotesScreen::class.java.canonicalName) {
                    drawerState.close()
                    navController.navigate(Screen.NotesScreen)
                } else {
                    drawerState.close()
                }
            }
        },
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
    Spacer(modifier = Modifier.height(8.dp))
    NavigationDrawerItem(
        label = { Text(text = "Calendar") },
        icon = {
            Icon(
                //imageVector = Icons.Default.DateRange,
                painter = painterResource(R.drawable.calendar),
                contentDescription = "Navigate to Calendar"
            )
        },
        selected = currentRoute == Screen.CalendarScreen::class.java.canonicalName,
        onClick = {
            scope.launch {
                if (currentRoute != Screen.CalendarScreen::class.java.canonicalName) {
                    drawerState.close()
                    navController.navigate(Screen.CalendarScreen)
                } else {
                    drawerState.close()
                }
            }
        },
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp))
    NavigationDrawerItem(
        label = { Text(text = "Subjects") },
        icon = {
            Icon(
                //imageVector = Icons.Default.Face,
                painter = painterResource(R.drawable.subjects),
                contentDescription = "Navigate to Subjects"
            )
        },
        selected = currentRoute == Screen.SubjectScreen::class.java.canonicalName,
        onClick = {
            scope.launch {
                if (currentRoute != Screen.SubjectScreen::class.java.canonicalName) {
                    drawerState.close()
                    navController.navigate(Screen.SubjectScreen)
                } else {
                    drawerState.close()
                }
            }
        },
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
    Spacer(modifier = Modifier.height(8.dp))
    NavigationDrawerItem(
        label = { Text(text = "Statistics") },
        icon = {
            Icon(
                //imageVector = Icons.Default.Info,
                painter = painterResource(R.drawable.stats),
                contentDescription = "Navigate to Statistics"
            )
        },
        selected = currentRoute == Screen.StatisticsScreen::class.java.canonicalName,
        onClick = {
            scope.launch {
                if (currentRoute != Screen.StatisticsScreen::class.java.canonicalName) {
                    drawerState.close()
                    navController.navigate(Screen.StatisticsScreen)
                } else {
                    drawerState.close()
                }
            }
        },
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp))
    NavigationDrawerItem(
        label = { Text(text = "Settings") },
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Navigate to Settings"
            )
        },
        selected = currentRoute == Screen.SettingsScreen::class.java.canonicalName,
        onClick = {
            scope.launch {
                if (currentRoute != Screen.SettingsScreen::class.java.canonicalName) {
                    drawerState.close()
                    navController.navigate(Screen.SettingsScreen)
                } else {
                    drawerState.close()
                }
            }
        },
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}
