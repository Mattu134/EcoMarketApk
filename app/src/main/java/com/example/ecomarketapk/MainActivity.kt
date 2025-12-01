package com.example.ecomarketapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ecomarketapk.ui.theme.EcoMarketApkTheme
import com.example.ecomarketapk.view.AgregarProductoScreen
import com.example.ecomarketapk.view.BackOfficeScreen
import com.example.ecomarketapk.view.CarritoScreen
import com.example.ecomarketapk.view.CatalogoScreen
import com.example.ecomarketapk.view.CompraExitosaScreen
import com.example.ecomarketapk.view.CompraRechazadaScreen
import com.example.ecomarketapk.view.DetalleProductoScreen
import com.example.ecomarketapk.view.EditarProductoScreen
import com.example.ecomarketapk.view.LoginScreen
import com.example.ecomarketapk.view.PerfilScreen
import com.example.ecomarketapk.view.RegisterScreen
import com.example.ecomarketapk.viewmodel.AuthViewModel
import com.example.ecomarketapk.viewmodel.BackOfficeViewModel
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import com.example.ecomarketapk.viewmodel.CatalogoViewModel
import com.example.ecomarketapk.viewmodel.MonedaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoMarketApkTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val catalogoViewModel: CatalogoViewModel = viewModel()
                    val carritoViewModel: CarritoViewModel = viewModel()
                    val backOfficeViewModel: BackOfficeViewModel = viewModel()
                    val monedaViewModel: MonedaViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("register") {
                            RegisterScreen(
                                navController = navController,
                                viewModel = authViewModel
                            )
                        }

                        composable("login") {
                            LoginScreen(
                                navController = navController,
                                viewModel = authViewModel
                            )
                        }

                        composable("catalogo") {
                            CatalogoScreen(
                                navController = navController,
                                viewModel = catalogoViewModel,
                                carritoViewModel = carritoViewModel,
                                authViewModel = authViewModel,
                                monedaViewModel = monedaViewModel
                            )
                        }

                        composable("carrito") {
                            CarritoScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                carritoViewModel = carritoViewModel
                            )
                        }

                        composable("perfil") {
                            PerfilScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                carritoViewModel = carritoViewModel
                            )
                        }

                        composable(
                            route = "detalle/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getInt("id") ?: return@composable

                            DetalleProductoScreen(
                                productoId = id,
                                viewModel = catalogoViewModel,
                                carritoViewModel = carritoViewModel,
                                navController = navController
                            )
                        }

                        composable("compraExitosa") {
                            CompraExitosaScreen(
                                navController = navController,
                                carritoViewModel = carritoViewModel,
                                authViewModel = authViewModel,
                                monedaViewModel = monedaViewModel
                            )
                        }

                        composable("compraRechazada") {
                            CompraRechazadaScreen(
                                navController = navController
                            )
                        }

                        composable("backoffice") {
                            BackOfficeScreen(
                                navController = navController,
                                viewModel = backOfficeViewModel,
                                authViewModel = authViewModel
                            )
                        }

                        composable("agregarProducto") {
                            AgregarProductoScreen(
                                navController = navController,
                                viewModel = backOfficeViewModel,
                                catalogoViewModel = catalogoViewModel
                            )
                        }

                        composable(
                            route = "editarProducto/{id}",
                            arguments = listOf(
                                navArgument("id") { type = NavType.LongType }
                            )
                        ) { backStackEntry ->
                            val id = backStackEntry.arguments?.getLong("id") ?: 0L

                            EditarProductoScreen(
                                navController = navController,
                                productoId = id,
                                viewModel = backOfficeViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}
