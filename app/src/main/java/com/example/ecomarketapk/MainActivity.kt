package com.example.ecomarketapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ecomarketapk.ui.theme.EcoMarketApkTheme
import com.example.ecomarketapk.view.AgregarProductoScreen
import com.example.ecomarketapk.view.BackOfficeScreen
import com.example.ecomarketapk.view.CarritoScreen
import com.example.ecomarketapk.view.CatalogoScreen
import com.example.ecomarketapk.view.CompraExitosaScreen
import com.example.ecomarketapk.view.CompraRechazadaScreen
import com.example.ecomarketapk.view.DetalleProductoScreen
import com.example.ecomarketapk.view.LoginScreen
import com.example.ecomarketapk.view.PerfilScreen
import com.example.ecomarketapk.view.RegisterScreen
import com.example.ecomarketapk.viewmodel.AuthViewModel
import com.example.ecomarketapk.viewmodel.CarritoViewModel
import com.example.ecomarketapk.viewmodel.CatalogoViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EcoMarketApkTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel()
                    val catalogoViewModel: CatalogoViewModel = viewModel()
                    val carritoViewModel: CarritoViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("register") {
                            RegisterScreen(navController, authViewModel)
                        }
                        composable("login") {
                            LoginScreen(navController, authViewModel)
                        }
                        composable("catalogo") {
                            CatalogoScreen(
                                navController = navController,
                                viewModel = catalogoViewModel,
                                carritoViewModel = carritoViewModel,
                                authViewModel = authViewModel
                            )
                        }
                        composable("carrito") {
                            CarritoScreen(
                                navController = navController,
                                carritoViewModel = carritoViewModel
                            )
                        }
                        composable("perfil") {
                            PerfilScreen(navController, authViewModel)
                        }
                        composable("detalle/{id}") { backStackEntry ->
                            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
                            if (id != null) {
                                DetalleProductoScreen(
                                    productoId = id,
                                    viewModel = catalogoViewModel,
                                    carritoViewModel = carritoViewModel,
                                    navController = navController
                                )
                            }
                        }
                        composable("compraExitosa") {
                            CompraExitosaScreen(navController, carritoViewModel)
                        }
                        composable("compraRechazada") {
                            CompraRechazadaScreen(navController)
                        }
                        composable("backoffice") {
                            BackOfficeScreen(
                                navController = navController,
                                viewModel = catalogoViewModel,
                                authViewModel = authViewModel
                            )
                        }
                        composable("agregarProducto") {
                            AgregarProductoScreen(navController)
                        }
                    }
                }
            }
        }
    }
}
