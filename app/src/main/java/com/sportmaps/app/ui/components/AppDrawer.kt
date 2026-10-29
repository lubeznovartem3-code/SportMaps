package com.sportmaps.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaps.app.data.User
import com.sportmaps.app.ui.navigation.Routes

/** Пункты бокового меню. */
enum class DrawerItem(val label: String, val route: String, val icon: ImageVector) {
    MAP("Карта площадок", Routes.MAP, Icons.Default.Place),
    MESSAGES("Сообщения", Routes.friends(false), Icons.AutoMirrored.Filled.Chat),
    FRIENDS("Друзья", Routes.friends(false), Icons.Default.Group),
    PROFILE("Профиль", Routes.PROFILE, Icons.Default.Person)
}

/** Боковое меню: аватар, имя, разделы и «Настройки» внизу. */
@Composable
fun AppDrawer(
    drawerState: DrawerState,
    selected: DrawerItem?,
    user: User?,
    onNavigate: (String) -> Unit,
    gesturesEnabled: Boolean = true,
    content: @Composable () -> Unit
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.background) {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .statusBarsPadding()
                        .navigationBarsPadding()
                ) {
                    Row(
                        Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Avatar(
                            name = user?.displayName.orEmpty(),
                            size = 48.dp,
                            avatarPath = user?.avatarPath
                        )
                        Spacer(Modifier.width(14.dp))
                        Column {
                            Text(user?.displayName.orEmpty(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                user?.handle.orEmpty(),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(8.dp))
                    DrawerItem.entries.forEach { item ->
                        NavigationDrawerItem(
                            label = { Text(item.label, fontSize = 14.sp) },
                            icon = { Icon(item.icon, contentDescription = null) },
                            selected = item == selected,
                            onClick = { onNavigate(item.route) },
                            modifier = Modifier.padding(horizontal = 8.dp),
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    NavigationDrawerItem(
                        label = { Text("Настройки", fontSize = 14.sp) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        selected = false,
                        onClick = { onNavigate(Routes.SETTINGS) },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
            }
        },
        content = content
    )
}
