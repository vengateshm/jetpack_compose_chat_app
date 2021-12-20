package dev.vengateshm.ktorandroidchatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import dev.vengateshm.ktorandroidchatapp.presentation.chat.ChatScreen
import dev.vengateshm.ktorandroidchatapp.presentation.username.UsernameScreen
import dev.vengateshm.ktorandroidchatapp.ui.theme.KtorAndroidChatAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KtorAndroidChatAppTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "username_screen"
                ) {
                    composable("username_screen") {
                        UsernameScreen(onNavigate = navController::navigate)
                    }
                    composable(
                        route = "chat_screen/{username}",
                        arguments = listOf(
                            navArgument(name = "username") {
                                type = NavType.StringType
                                nullable = true
                            }
                        )
                    ) {
                        val username = it.arguments?.getString("username")
                        ChatScreen(username = username)
                    }
                }
            }
        }
    }
}