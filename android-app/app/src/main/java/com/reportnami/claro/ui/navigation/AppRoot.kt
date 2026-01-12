package com.reportnami.claro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.reportnami.claro.ui.screens.addreview.AddReviewScreen
import com.reportnami.claro.ui.screens.productdetail.ProductDetailScreen
import com.reportnami.claro.ui.screens.productlist.ProductListScreen
import com.reportnami.claro.ui.screens.reviews.ReviewsScreen
import com.reportnami.claro.ui.screens.settings.SettingsScreen

object Routes {
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
fun AppRoot() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.ProductList,
    ) {
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
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
