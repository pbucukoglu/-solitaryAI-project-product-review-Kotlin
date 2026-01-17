package com.reportnami.claro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reportnami.claro.data.auth.AuthRepository
import com.reportnami.claro.ui.screens.addreview.AddReviewScreen
import com.reportnami.claro.ui.screens.auth.LoginScreen
import com.reportnami.claro.ui.screens.auth.RegisterScreen
import com.reportnami.claro.ui.screens.productdetail.ProductDetailScreen
import com.reportnami.claro.ui.screens.productlist.ProductListScreen
import com.reportnami.claro.ui.screens.reviews.ReviewsScreen
import com.reportnami.claro.ui.screens.settings.SettingsScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val ProductList = "product_list"
    const val ProductDetail = "product_detail/{productId}"
    const val Reviews = "reviews/{productId}"
    const val AddReview = "add_review/{productId}"
    const val Settings = "settings"

    fun productDetail(productId: Long): String = "product_detail/$productId"
    fun reviews(productId: Long): String = "reviews/$productId"
    fun addReview(productId: Long): String = "add_review/$productId"
}

@Composable
fun AppRoot(
    authRepository: AuthRepository
) {
    val navController = rememberNavController()
    val isLoggedIn by authRepository.isLoggedIn().collectAsState(initial = false)

    // Handle initial navigation based on auth state
    LaunchedEffect(isLoggedIn) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if (isLoggedIn && (currentRoute == Routes.Login || currentRoute == Routes.Register)) {
            // User is logged in but on auth screen, navigate to product list
            navController.navigate(Routes.ProductList) {
                popUpTo(Routes.Login) { inclusive = true }
                popUpTo(Routes.Register) { inclusive = true }
            }
        } else if (!isLoggedIn && currentRoute != Routes.Login && currentRoute != Routes.Register) {
            // User is not logged in but on protected screen, navigate to login
            navController.navigate(Routes.Login) {
                popUpTo(Routes.ProductList) { inclusive = true }
                popUpTo(Routes.Settings) { inclusive = true }
                popUpTo(Routes.ProductDetail) { inclusive = true }
                popUpTo(Routes.Reviews) { inclusive = true }
                popUpTo(Routes.AddReview) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) Routes.ProductList else Routes.Login,
    ) {
        // Authentication screens
        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    // Navigate to product list after successful login
                    navController.navigate(Routes.ProductList) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register)
                }
            )
        }
        
        composable(Routes.Register) {
            RegisterScreen(
                onRegisterSuccess = {
                    // Navigate to login after successful registration
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Register) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Protected screens (require authentication)
        composable(Routes.ProductList) {
            ProductListScreen(
                onOpenProduct = { id -> navController.navigate(Routes.productDetail(id)) },
                onOpenSettings = { navController.navigate(Routes.Settings) },
            )
        }
        
        composable(Routes.ProductDetail) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            ProductDetailScreen(
                productId = id,
                onBack = { navController.popBackStack() },
                onOpenSettings = { navController.navigate(Routes.Settings) },
                onOpenReviews = { pid -> navController.navigate(Routes.reviews(pid)) },
                onAddReview = { pid -> navController.navigate(Routes.addReview(pid)) },
            )
        }
        
        composable(Routes.Reviews) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            ReviewsScreen(
                productId = id,
                onBack = { navController.popBackStack() },
                onAddReview = { pid -> navController.navigate(Routes.addReview(pid)) },
            )
        }
        
        composable(Routes.AddReview) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
            AddReviewScreen(
                productId = id,
                onBack = { navController.popBackStack() },
                onSubmitted = { navController.popBackStack() },
            )
        }
        
        composable(Routes.Settings) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = { 
                    // Clear auth data and navigate to login
                    authRepository.logout()
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.ProductList) { inclusive = true }
                        popUpTo(Routes.Settings) { inclusive = true }
                        popUpTo(Routes.ProductDetail) { inclusive = true }
                        popUpTo(Routes.Reviews) { inclusive = true }
                        popUpTo(Routes.AddReview) { inclusive = true }
                    }
                }
            )
        }
    }
}
