package com.example.ceubfinder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ceubfinder.data.database.CeubDatabaseHelper
import com.example.ceubfinder.ui.screens.admin.*
import com.example.ceubfinder.ui.screens.publico.*
import com.example.ceubfinder.ui.theme.CEUBFinderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CEUBFinderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CeubFinderAppNavHost()
                }
            }
        }
    }
}

@Composable
fun CeubFinderAppNavHost() {
    val navController = rememberNavController()
    val context = androidx.compose.ui.platform.LocalContext.current
    val dbHelper = remember { CeubDatabaseHelper(context) }

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            IncioScreen(
                dbHelper = dbHelper,
                onNavigateToSearch = { query, categoryId ->
                    val qParam = if (!query.isNullOrBlank()) "query=$query" else ""
                    val cParam = if (categoryId != null && categoryId > 0L) "categoryId=$categoryId" else ""
                    val params = listOf(qParam, cParam).filter { it.isNotBlank() }.joinToString("&")
                    val route = if (params.isNotBlank()) "buscar?$params" else "buscar"
                    navController.navigate(route)
                },
                onNavigateToDetail = { objetoId ->
                    navController.navigate("detalhe/$objetoId")
                },
                onNavigateToInfo = {
                    navController.navigate("informacoes")
                },
                onNavigateToLogin = {
                    navController.navigate("login_adm")
                }
            )
        }

        composable(
            route = "buscar?query={query}&categoryId={categoryId}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument("categoryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query")
            val catIdArg = backStackEntry.arguments?.getLong("categoryId") ?: -1L
            val categoryId = if (catIdArg != -1L) catIdArg else null
            BuscarScreen(
                dbHelper = dbHelper,
                initialQuery = query,
                initialCategoryId = categoryId,
                onNavigateToDetail = { objetoId ->
                    navController.navigate("detalhe/$objetoId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "detalhe/{objetoId}",
            arguments = listOf(navArgument("objetoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val objetoId = backStackEntry.arguments?.getLong("objetoId") ?: 0L
            DetalheObjetoScreen(
                dbHelper = dbHelper,
                objetoId = objetoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable("informacoes") {
            InformacoesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("login_adm") {
            LoginAdmScreen(
                onLoginSuccess = {
                    navController.navigate("dashboard_adm") {
                        popUpTo("inicio")
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("dashboard_adm") {
            DashboardAdmScreen(
                dbHelper = dbHelper,
                onNavigateToCadastro = {
                    navController.navigate("cadastro_adm")
                },
                onNavigateToGerenciamento = {
                    navController.navigate("gerenciamento_adm")
                },
                onNavigateToHistorico = {
                    navController.navigate("historico_adm")
                },
                onLogout = {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "cadastro_adm?objetoId={objetoId}",
            arguments = listOf(navArgument("objetoId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val objetoId = backStackEntry.arguments?.getLong("objetoId") ?: -1L
            CadastroObjetoScreen(
                dbHelper = dbHelper,
                objetoIdToEdit = if (objetoId != -1L) objetoId else null,
                onSaveFinished = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }

        composable("gerenciamento_adm") {
            GerenciamentoScreen(
                dbHelper = dbHelper,
                onNavigateToEdit = { objetoId ->
                    navController.navigate("cadastro_adm?objetoId=$objetoId")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("historico_adm") {
            HistoricoScreen(
                dbHelper = dbHelper,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
