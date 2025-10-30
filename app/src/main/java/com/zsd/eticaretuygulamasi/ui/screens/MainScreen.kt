package com.zsd.eticaretuygulamasi.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.zsd.eticaretuygulamasi.navigation.BottomNavItem
import com.zsd.eticaretuygulamasi.navigation.NavigationGraph
import com.zsd.eticaretuygulamasi.ui.screens.sepet_sayfa.SepetSayfaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    sepetViewModel: SepetSayfaViewModel = hiltViewModel()
) {
    val navController = rememberNavController()

    LaunchedEffect(Unit) {
        sepetViewModel.sepetiYukle()
    }

    val items = BottomNavItem.items

    Scaffold(
        bottomBar = {
            BottomNavigation(
                navController = navController,
                items = items,
                sepetViewModel = sepetViewModel
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavigationGraph(navController = navController, sepetViewModel = sepetViewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavigation(
    navController: NavHostController,
    items: List<BottomNavItem>,
    sepetViewModel: SepetSayfaViewModel
) {
    val toplamUrunAdedi by sepetViewModel.toplamUrunAdedi.collectAsState()
    val ICON_SIZE = 28.dp

    NavigationBar(
        modifier = Modifier.height(72.dp),
        containerColor = MaterialTheme.colorScheme.background,
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val selected = currentRoute == item.route

            NavigationBarItem(
                icon = {
                    val icon = if (selected) item.selectedIcon else item.unselectedIcon

                    if (item.route == BottomNavItem.Sepet.route) {
                        BadgedBox(
                            badge = {
                                if (toplamUrunAdedi > 0) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(text = "$toplamUrunAdedi")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                modifier = Modifier.size(ICON_SIZE)
                            )
                        }
                    } else {
                        Icon(
                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title,
                            modifier = Modifier.size(ICON_SIZE)
                        )
                    }
                },
                label = null,
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    indicatorColor = MaterialTheme.colorScheme.background
                ),
                alwaysShowLabel = false
            )
        }
    }
}