package com.sportmaps.app.ui.components

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sportmaps.app.ui.theme.OfflineGray
import com.sportmaps.app.ui.theme.OnlineGreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Поле ввода с подписью сверху, как на макетах. */
@Composable
fun SportTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isPassword) KeyboardType.Password else keyboardType
            ),
            colors = sportFieldColors()
        )
    }
}

@Composable
fun sportFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
)

/** Тёмная главная кнопка. */
@Composable
fun PrimaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

/** Светлая кнопка с рамкой. */
@Composable
fun SecondaryButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

/** Верхняя панель с разделителем снизу. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenTopBar(
    title: @Composable () -> Unit,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    Column {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.onBackground
            )
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun ScreenTopBar(
    title: String,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    ScreenTopBar(
        title = { Text(title, fontWeight = FontWeight.SemiBold, fontSize = 18.sp) },
        navigationIcon = navigationIcon,
        actions = actions
    )
}

@Composable
fun BackButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
    }
}

@Composable
fun MenuButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(Icons.Default.Menu, contentDescription = "Меню")
    }
}

/** Небольшая плашка (вид спорта и т.п.). */
@Composable
fun SportChip(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/** Блок с пунктирной рамкой. */
@Composable
fun DashedBox(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    val line = MaterialTheme.colorScheme.outline
    val fill = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(fill)
            .drawBehind {
                drawRoundRect(
                    color = line,
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 10f))
                    ),
                    cornerRadius = CornerRadius(12.dp.toPx())
                )
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}

/** Аватар: фото (если есть) или первая буква имени; опционально точка статуса. */
@Composable
fun Avatar(
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    avatarPath: String? = null,
    online: Boolean? = null
) {
    val bitmap by produceState<ImageBitmap?>(initialValue = null, avatarPath) {
        value = if (avatarPath == null) {
            null
        } else {
            withContext(Dispatchers.IO) {
                try {
                    val options = BitmapFactory.Options().apply { inSampleSize = 4 }
                    BitmapFactory.decodeFile(avatarPath, options)?.asImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }
    val image = bitmap
    Box(modifier.size(size)) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (image != null) {
                Image(
                    bitmap = image,
                    contentDescription = "Фото профиля",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    name.take(1).uppercase(),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (size.value * 0.4f).sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (online != null) {
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(size * 0.3f)
                    .background(if (online) OnlineGreen else OfflineGray, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.background, CircleShape)
            )
        }
    }
}
