package com.pagasa.microfinance.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Analytics
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pagasa.microfinance.data.model.MemberProfile
import com.pagasa.microfinance.data.repository.DashboardSnapshot
import com.pagasa.microfinance.data.repository.PagasaRepository
import com.pagasa.microfinance.ui.screens.*

sealed class Route(val value: String) {
    data object Auth : Route("auth")
    data object Home : Route("home")
    data object Loans : Route("loans")
    data object LoanApply : Route("loan_apply")
    data object LoanDetail : Route("loan_detail")
    data object Savings : Route("savings")
    data object Transactions : Route("transactions")
    data object Insights : Route("insights")
    data object Profile : Route("profile")
    data object Notifications : Route("notifications")
    data object Support : Route("support")
    data object BranchAdmin : Route("branch_admin")
}

private data class BottomItem(val route: Route, val label: String, val icon: ImageVector)

private val bottomItems = listOf(
    BottomItem(Route.Home, "Home", Icons.Outlined.Home),
    BottomItem(Route.Loans, "Loans", Icons.Outlined.Payments),
    BottomItem(Route.Savings, "Savings", Icons.Outlined.AccountBalance),
    BottomItem(Route.Transactions, "History", Icons.Outlined.ReceiptLong),
    BottomItem(Route.Insights, "Insights", Icons.Outlined.Analytics),
    BottomItem(Route.Profile, "Profile", Icons.Outlined.Person)
)

@Composable
fun PagasaApp(repository: PagasaRepository) {
    var profile by remember { mutableStateOf<MemberProfile?>(null) }
    var refreshKey by remember { mutableIntStateOf(0) }

    if (profile == null) {
        AuthScreen(
            repository = repository,
            onAuthenticated = { profile = it },
            onForgotPassword = { /* TODO: connect Firebase password reset */ }
        )
    } else {
        val navController = rememberNavController()
        val currentProfile = profile!!
        val dashboardState by produceState<DashboardSnapshot?>(initialValue = null, currentProfile, refreshKey) {
            value = repository.getDashboard(currentProfile.branchId, currentProfile.uid)
        }
        val backStackEntry by navController.currentBackStackEntryAsState()
        val destination = backStackEntry?.destination
        val showBottom = bottomItems.any { item -> destination?.hierarchy?.any { it.route == item.route.value } == true }

        Scaffold(
            bottomBar = {
                if (showBottom) {
                    NavigationBar {
                        bottomItems.forEach { item ->
                            val selected = destination?.hierarchy?.any { it.route == item.route.value } == true
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route.value) {
                                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = { Icon(item.icon, contentDescription = item.label) },
                                label = { Text(item.label) }
                            )
                        }
                    }
                }
            }
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.value,
                modifier = Modifier.padding(padding)
            ) {
                composable(Route.Home.value) {
                    DashboardScreen(
                        snapshot = dashboardState,
                        onOpenNotifications = { navController.navigate(Route.Notifications.value) },
                        onApplyLoan = { navController.navigate(Route.LoanApply.value) },
                        onLoanDetail = { navController.navigate(Route.LoanDetail.value) },
                        onRefresh = { refreshKey++ }
                    )
                }
                composable(Route.Loans.value) {
                    LoansScreen(
                        repository = repository,
                        profile = currentProfile,
                        activeLoan = dashboardState?.activeLoan,
                        onApply = { navController.navigate(Route.LoanApply.value) },
                        onLoanDetail = { navController.navigate(Route.LoanDetail.value) }
                    )
                }
                composable(Route.LoanApply.value) {
                    LoanApplicationScreen(repository = repository, profile = currentProfile, onDone = { navController.popBackStack() })
                }
                composable(Route.LoanDetail.value) {
                    LoanDetailScreen(repository = repository, loan = dashboardState?.activeLoan, onBack = { navController.popBackStack() })
                }
                composable(Route.Savings.value) { SavingsScreen(savings = dashboardState?.savings, transactions = dashboardState?.recentTransactions.orEmpty()) }
                composable(Route.Transactions.value) { TransactionHistoryScreen(repository = repository, profile = currentProfile) }
                composable(Route.Insights.value) { InsightsScreen(snapshot = dashboardState) }
                composable(Route.Profile.value) {
                    ProfileScreen(
                        profile = currentProfile,
                        onLogout = { profile = null },
                        onSupport = { navController.navigate(Route.Support.value) },
                        onBranchAdmin = { navController.navigate(Route.BranchAdmin.value) }
                    )
                }
                composable(Route.Notifications.value) { NotificationsScreen(repository = repository, profile = currentProfile, onBack = { navController.popBackStack() }) }
                composable(Route.Support.value) { SupportScreen(profile = currentProfile, onBack = { navController.popBackStack() }) }
                composable(Route.BranchAdmin.value) { BranchAdminScreen(profile = currentProfile, onBack = { navController.popBackStack() }) }
            }
        }
    }
}
