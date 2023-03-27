package com.example.hieke_oliver_MAD

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.annotation.ExperimentalCoilApi
import com.example.hieke_oliver_MAD.screens.DetailScreen
import com.example.hieke_oliver_MAD.screens.FavouritesScreen
import com.example.hieke_oliver_MAD.screens.HomeScreen

const val ARG_DETAIL_MOV_ID = "movieID"

sealed class Navigation(val route: String) {

    object HomeScreen : Navigation(route = "homeScreen")
    object FavouriteScreen : Navigation(route = "favouriteScreen")
    object DetailScreen : Navigation(route = "detailScreen/{$ARG_DETAIL_MOV_ID}") {
        fun setID(movieID: String): String {
            return this.route.replace(oldValue = "{$ARG_DETAIL_MOV_ID}",
                newValue = movieID)
        }
    }
}

@ExperimentalCoilApi
@Composable
fun NavigationController() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Navigation.HomeScreen.route
    ) {
        composable(
            route = Navigation.HomeScreen.route
        ) { HomeScreen(navController = navController) }
        composable(
            Navigation.DetailScreen.route,
            arguments = listOf(navArgument(ARG_DETAIL_MOV_ID) {
                type = NavType.StringType
            })
        ) { navBackStackEntry ->
            DetailScreen(
                navController = navController,
                movieID = navBackStackEntry.arguments?.getString(ARG_DETAIL_MOV_ID)
            )
        }
        composable(
            Navigation.FavouriteScreen.route
        ) { FavouritesScreen(navController = navController) }
    }
}