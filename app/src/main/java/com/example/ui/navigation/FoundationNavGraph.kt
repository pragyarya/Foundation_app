package com.example.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.FoundationRepository
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RegistrationScreen

object FoundationRoutes {
  const val LOGIN = "login"
  const val REGISTRATION = "registration"
  const val DASHBOARD = "dashboard"
  const val PROFILE = "profile"
}

sealed class BottomNavItem(
  val route: String,
  val title: String,
  val icon: androidx.compose.ui.graphics.vector.ImageVector,
  val testTag: String
) {
  data object Dashboard : BottomNavItem(FoundationRoutes.DASHBOARD, "Dashboard", Icons.Default.Dashboard, "nav_dashboard")
  data object Profile : BottomNavItem(FoundationRoutes.PROFILE, "Digital ID Card", Icons.Default.Badge, "nav_profile")
}

@Composable
fun FoundationApp(
  navController: NavHostController = rememberNavController()
) {
  val navBackStackEntry by navController.currentBackStackEntryAsState()
  val currentRoute = navBackStackEntry?.destination?.route

  val currentMember by FoundationRepository.currentMember.collectAsState()

  val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Profile
  )

  // Show bottom navigation bar only when logged into member screens
  val showBottomBar = currentMember != null && currentRoute in listOf(
    FoundationRoutes.DASHBOARD,
    FoundationRoutes.PROFILE
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      if (showBottomBar) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          contentColor = MaterialTheme.colorScheme.onSurface,
          modifier = Modifier
            .navigationBarsPadding()
            .testTag("foundation_bottom_navigation")
        ) {
          bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
              selected = isSelected,
              onClick = {
                if (currentRoute != item.route) {
                  navController.navigate(item.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                      saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                  }
                }
              },
              icon = {
                Icon(imageVector = item.icon, contentDescription = item.title)
              },
              label = {
                Text(
                  text = item.title,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                  )
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer
              ),
              modifier = Modifier.testTag(item.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      NavHost(
        navController = navController,
        startDestination = if (FoundationRepository.currentMember.value != null) FoundationRoutes.DASHBOARD else FoundationRoutes.LOGIN
      ) {
        // 1. Member Login (Secure Entry Point)
        composable(FoundationRoutes.LOGIN) {
          LoginScreen(
            onLoginSuccess = {
              navController.navigate(FoundationRoutes.DASHBOARD) {
                popUpTo(FoundationRoutes.LOGIN) { inclusive = true }
              }
            },
            onNavigateToRegistration = {
              navController.navigate(FoundationRoutes.REGISTRATION)
            }
          )
        }

        // 2. Member Registration / ID Activation
        composable(FoundationRoutes.REGISTRATION) {
          RegistrationScreen(
            onRegistrationSuccess = {
              navController.navigate(FoundationRoutes.DASHBOARD) {
                popUpTo(FoundationRoutes.LOGIN) { inclusive = true }
              }
            },
            onNavigateToLogin = {
              navController.popBackStack()
            },
            onNavigateBack = {
              navController.popBackStack()
            }
          )
        }

        // 3. Member Dashboard (Shows only logged-in member's real information)
        composable(FoundationRoutes.DASHBOARD) {
          DashboardScreen(
            onNavigateToProfile = {
              navController.navigate(FoundationRoutes.PROFILE)
            },
            onLogout = {
              navController.navigate(FoundationRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
              }
            }
          )
        }

        // 4. Digital Member ID Card (Redesigned Profile)
        composable(FoundationRoutes.PROFILE) {
          ProfileScreen(
            onNavigateBack = {
              navController.popBackStack()
            },
            onLogout = {
              navController.navigate(FoundationRoutes.LOGIN) {
                popUpTo(0) { inclusive = true }
              }
            }
          )
        }
      }
    }
  }
}
