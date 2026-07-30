package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.CheatSheetScreen
import com.example.ui.screens.RegexTesterScreen
import com.example.ui.screens.SavedPatternsScreen
import com.example.ui.screens.TutorialScreen
import com.example.ui.theme.RegexLabTheme
import com.example.ui.theme.Slate200
import com.example.ui.theme.Slate600
import com.example.ui.theme.Slate900
import com.example.ui.theme.Teal100
import com.example.ui.theme.Teal600
import com.example.viewmodel.MainViewModel

import com.example.model.AppThemeData

class MainActivity : ComponentActivity() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settings by mainViewModel.appSettings.collectAsState()
            RegexLabTheme(settings = settings) {
                RegexLabApp(viewModel = mainViewModel)
            }
        }
    }
}

@Composable
fun RegexLabApp(viewModel: MainViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val settings by viewModel.appSettings.collectAsState()
    val activeTheme = AppThemeData.getThemeById(settings.themeId)
    val isImeVisible = WindowInsets.ime.asPaddingValues().calculateBottomPadding() > 0.dp

    val navItems = mutableListOf<Triple<String, androidx.compose.ui.graphics.vector.ImageVector, Int>>()
    navItems.add(Triple("Tester", Icons.Default.Code, 0))
    if (!settings.hideCheatSheet && !settings.hideAllBottomBar) {
        navItems.add(Triple("Cheat Sheet", Icons.Default.MenuBook, 1))
    }
    if (!settings.hideTutorials && !settings.hideAllBottomBar) {
        navItems.add(Triple("Tutorials", Icons.Default.School, 2))
    }
    if (!settings.hideSaved && !settings.hideAllBottomBar) {
        navItems.add(Triple("Saved", Icons.Default.Bookmark, 3))
    }

    val showBottomBar = !isImeVisible && !settings.hideAllBottomBar && navItems.size > 1

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.statusBars,
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically { it } + fadeIn(),
                exit = slideOutVertically { it } + fadeOut()
            ) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    navItems.forEach { (title, icon, index) ->
                        val isSelected = selectedTab == index
                        NavigationBarItem(
                            selected = isSelected,
                            onClick = { viewModel.setTab(index) },
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title
                                )
                            },
                            label = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = activeTheme.primaryColor,
                                selectedTextColor = activeTheme.primaryColor,
                                indicatorColor = activeTheme.primaryContainer,
                                unselectedIconColor = Slate600,
                                unselectedTextColor = Slate600
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else 0.dp
                )
                .windowInsetsPadding(
                    if (!showBottomBar) WindowInsets.navigationBars else WindowInsets(0, 0, 0, 0)
                )
        ) {
            when (selectedTab) {
                0 -> RegexTesterScreen(viewModel = viewModel)
                1 -> CheatSheetScreen(viewModel = viewModel)
                2 -> TutorialScreen(viewModel = viewModel)
                3 -> SavedPatternsScreen(viewModel = viewModel)
            }
        }
    }
}
