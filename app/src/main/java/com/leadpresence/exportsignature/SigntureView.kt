package com.leadpresence.exportsignature

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.RectF
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme

import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

// Data class encapsulating unique vector properties of each continuous path
data class StrokePath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignatureScreen() {
    val context = LocalContext.current
    val activity = context as Activity
    val colors = MaterialTheme.colorScheme

    // Canvas Draw States
    val strokes = remember { mutableStateListOf<StrokePath>() }
    val redoHistory = remember { mutableStateListOf<StrokePath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    var controlPanelExpanded = remember { mutableStateOf(false) }
    var pendingFormat by remember {
        mutableStateOf<Bitmap.CompressFormat?>(null)
    }
    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                if (granted) {
                    pendingFormat?.let {
                        exportSignatureNew(
                            context,
                            strokes,
                            it,
                        )
                    }
                }
            } else {
                Toast
                    .makeText(
                        context,
                        "Storage permission denied",
                        Toast.LENGTH_SHORT,
                    ).show()
            }
        }

    // Brush Configurations
    var selectedColor by remember { mutableStateOf(colors.primary) }
    var strokeWidth by remember { mutableStateOf(1f) }
    var showExportDialog by remember { mutableStateOf(false) }

    val availableColors =
        listOf(
            colors.primary, // Navy
            colors.secondary, // Red
            colors.tertiary, // Light Blue
            Color(0xFF4CAF50), // Green
            Color(0xFF2196F3), // Blue
            Color(0xFFFF9800), // Orange
            colors.onSurface, // Dark for white background
        )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Signature Pad",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.onSurface,
                        )
                        Text(
                            text = "Draw your signature",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.onSurfaceVariant,
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.surface,
                        titleContentColor = colors.onSurface,
                    ),
            )
        },
        containerColor = colors.background,
        contentColor = colors.onBackground,
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
        ) {
            // Canvas Section
            Box(
                modifier =
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(colors.surface)
                        .border(
                            width = 1.5.dp,
                            color = colors.outlineVariant,
                            shape = RoundedCornerShape(20.dp),
                        ),
            ) {
                Canvas(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .pointerInput(selectedColor, strokeWidth) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        redoHistory.clear()
                                        val path = Path().apply { moveTo(offset.x, offset.y) }
                                        currentPath = path
                                        strokes.add(StrokePath(path, selectedColor, strokeWidth))
                                    },
                                    onDrag = { change, _ ->
                                        currentPath?.lineTo(change.position.x, change.position.y)
                                        val last = strokes.removeAt(strokes.lastIndex)
                                        strokes.add(last)
                                    },
                                    onDragEnd = { currentPath = null },
                                )
                            },
                ) {
                    // Draw grid background for visual reference
                    drawRect(color = colors.surface)

                    strokes.forEach { strokePath ->
                        drawPath(
                            path = strokePath.path,
                            color = strokePath.color,
                            style =
                                Stroke(
                                    width = strokePath.strokeWidth,
                                    cap = StrokeCap.Round,
                                    join = StrokeJoin.Round,
                                ),
                        )
                    }
                }

                // Empty state message
                if (strokes.isEmpty()) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = null,
                            tint = colors.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier.size(56.dp),
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Draw your signature",
                            style = MaterialTheme.typography.bodyLarge,
                            color = colors.onSurfaceVariant.copy(alpha = 0.5f),
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Control Panel
            ExpandableControlPanel(
                colors = colors,
                strokes = strokes,
                redoHistory = redoHistory,
                availableColors = availableColors,
                selectedColor = selectedColor,
                onColorChange = { selectedColor = it },
                strokeWidth = strokeWidth,
                onStrokeWidthChange = { strokeWidth = it },
                onUndo = {
                    if (strokes.isNotEmpty()) {
                        redoHistory.add(strokes.removeAt(strokes.lastIndex))
                    }
                },
                onRedo = {
                    if (redoHistory.isNotEmpty()) {
                        strokes.add(redoHistory.removeAt(redoHistory.lastIndex))
                    }
                },
                onClear = {
                    strokes.clear()
                    redoHistory.clear()
                },
                onExport = { showExportDialog = true },
                context = context,
                isExpanded = controlPanelExpanded.value,
                onToggleExpand = {
                    controlPanelExpanded.value = !controlPanelExpanded.value
                },
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Export Dialog
    if (showExportDialog) {
        ExportDialog(
            onDismiss = { showExportDialog = false },
            onExportPNG = {
                val format = Bitmap.CompressFormat.PNG
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    exportSignatureNew(context, strokes, Bitmap.CompressFormat.PNG)
                } else {
                    if (StoragePermission.hasWritePermission(context)) {
                        exportSignatureNew(
                            context,
                            strokes,
                            format,
                        )
                    } else {
                        pendingFormat = format
                        StoragePermission.requestPermission(
                            activity,
                            permissionLauncher,
                        )
                    }
                }
//                exportSignatureNew(context, strokes, Bitmap.CompressFormat.PNG)
                showExportDialog = false
            },
            onExportJPEG = {
                val format = Bitmap.CompressFormat.JPEG
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    exportSignatureNew(context, strokes, Bitmap.CompressFormat.JPEG)
                } else {
                    if (StoragePermission.hasWritePermission(context)) {
                        exportSignatureNew(
                            context,
                            strokes,
                            format,
                        )
                    } else {
                        pendingFormat = format
                        StoragePermission.requestPermission(
                            activity = activity,
                            permissionLauncher,
                        )
                    }
                }
                showExportDialog = false
            },
            colors = colors,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpandableControlPanel(
    colors: ColorScheme,
    strokes: List<StrokePath>,
    redoHistory: List<StrokePath>,
    availableColors: List<Color>,
    selectedColor: Color,
    onColorChange: (Color) -> Unit,
    strokeWidth: Float,
    onStrokeWidthChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onClear: () -> Unit,
    onExport: () -> Unit,
    context: Context,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        color = colors.surface,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.5.dp, colors.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(enabled = true) { onToggleExpand() }
                        .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Brush Setting",
                    style = MaterialTheme.typography.labelLarge,
                    color = colors.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = colors.onSurfaceVariant,
                    modifier = Modifier.size(24.dp).rotate(if (isExpanded) 180f else 0f),
                )
            }
            AnimatedVisibility(visible = isExpanded, enter = expandVertically(), exit = shrinkVertically()) {
                // Color Selector Section
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "Color",
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        availableColors.forEach { color ->
                            val isSelected = selectedColor == color
                            Box(
                                modifier =
                                    Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) colors.onSurface else Color.Transparent,
                                            shape = CircleShape,
                                        ).clickable { onColorChange(color) },
                            )
                        }
                    }

                    HorizontalDivider(color = colors.outlineVariant, thickness = 1.dp)

                    // Stroke Width Section
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Finger size ",
                                style = MaterialTheme.typography.labelMedium,
                                color = colors.onSurfaceVariant,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Box(
                                modifier =
                                    Modifier
                                        .background(
                                            color = colors.primaryContainer,
                                            shape = RoundedCornerShape(8.dp),
                                        ).padding(horizontal = 12.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = "${strokeWidth.toInt()} px",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onPrimaryContainer,
                                    fontWeight = FontWeight.SemiBold,
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Visual stroke preview
                            Box(
                                modifier =
                                    Modifier
                                        .size(40.dp)
                                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center,
                            ) {
                                Box(
                                    modifier =
                                        Modifier
                                            .size((strokeWidth / 2).dp.coerceAtMost(24.dp))
                                            .clip(CircleShape)
                                            .background(selectedColor),
                                )
                            }

                            // Slider with custom styling
                            Slider(
                                value = strokeWidth,
                                onValueChange = onStrokeWidthChange,
                                valueRange = 1f..15f,
                                steps = 14,
                                modifier = Modifier.weight(1f),

                                thumb = {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(RectangleShape)
                                            .background(Color.Red)
                                    )

                                },
                                colors =
                                    SliderDefaults.colors(
                                        thumbColor = selectedColor,
                                        activeTrackColor = selectedColor,
                                        inactiveTrackColor = colors.outlineVariant,
                                    ),
                            )
                        }
                    }

                    HorizontalDivider(Modifier, 1.dp, colors.outlineVariant)

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        // Undo Button
                        ElevatedButton(
                            onClick = onUndo,
                            enabled = strokes.isNotEmpty(),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                ButtonDefaults.elevatedButtonColors(
                                    containerColor = colors.primaryContainer,
                                    contentColor = colors.onPrimaryContainer,
                                    disabledContainerColor = colors.surfaceVariant,
                                    disabledContentColor = colors.onSurfaceVariant.copy(alpha = 0.5f),
                                ),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Undo,
                                contentDescription = "Undo",
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        // Redo Button
                        ElevatedButton(
                            onClick = onRedo,
                            enabled = redoHistory.isNotEmpty(),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            colors =
                                ButtonDefaults.elevatedButtonColors(
                                    containerColor = colors.primaryContainer,
                                    contentColor = colors.onPrimaryContainer,
                                    disabledContainerColor = colors.surfaceVariant,
                                    disabledContentColor = colors.onSurfaceVariant.copy(alpha = 0.5f),
                                ),
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Redo,
                                contentDescription = "Redo",
                                modifier = Modifier.size(18.dp),
                            )
                        }

                        // Clear Button
                        OutlinedButton(
                            onClick = onClear,
                            enabled = strokes.isNotEmpty(),
                            modifier =
                                Modifier
                                    .weight(1f)
                                    .height(40.dp),
                            shape = RoundedCornerShape(10.dp),
                            border =
                                BorderStroke(
                                    1.5.dp,
                                    if (strokes.isNotEmpty()) colors.error else colors.outlineVariant,
                                ),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    contentColor = if (strokes.isNotEmpty()) colors.error else colors.outlineVariant,
                                    disabledContentColor = colors.outlineVariant,
                                ),
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }

                    // Export Button (Full Width)
                    Button(
                        onClick = onExport,
                        enabled = strokes.isNotEmpty(),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = colors.secondary,
                                contentColor = colors.onSecondary,
                                disabledContainerColor = colors.surfaceVariant,
                                disabledContentColor = colors.onSurfaceVariant.copy(alpha = 0.5f),
                            ),
                        elevation =
                            ButtonDefaults.elevatedButtonElevation(
                                defaultElevation = 6.dp,
                                pressedElevation = 8.dp,
                            ),
                    ) {
                        Icon(
                            Icons.Default.Download,
                            contentDescription = "Export",
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Save Signature",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExportDialog(
    onDismiss: () -> Unit,
    onExportPNG: () -> Unit,
    onExportJPEG: () -> Unit,
    colors: ColorScheme,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = colors.surface,
        titleContentColor = colors.onSurface,
        textContentColor = colors.onSurface,
        icon = {
            Icon(
                Icons.Default.Download,
                contentDescription = null,
                tint = colors.secondary,
                modifier = Modifier.size(32.dp),
            )
        },
        title = {
            Text(
                "Save Signature as?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
            )
        },
        text = {
            Text(
                "Choose a format to save your signature:",
                style = MaterialTheme.typography.bodyMedium,
                color = colors.onSurfaceVariant,
            )
        },
        confirmButton = {
            Button(
                onClick = onExportPNG,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colors.primary,
                        contentColor = colors.onPrimary,
                    ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("PNG", fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            Button(
                onClick = onExportJPEG,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = colors.secondary,
                        contentColor = colors.onSecondary,
                    ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("JPEG", fontWeight = FontWeight.SemiBold)
            }
        },
    )
}

@Composable
private fun rememberScrollState(): ScrollState = remember { ScrollState(0) }

/**
 * Robust offscreen graphics rendering. Replays vector elements inside an isolated
 * native canvas matching expected dimensional boundaries to ensure perfect clean exports.
 */

fun computeSignatureBounds(
    strokes: List<StrokePath>,
    padding: Float = 40f,
): RectF? {
    if (strokes.isEmpty()) return null

    val result = RectF()
    val temp = RectF()

    var first = true

    for (stroke in strokes) {
        stroke.path
            .asAndroidPath()
            .computeBounds(temp, true)

        if (first) {
            result.set(temp)
            first = false
        } else {
            result.union(temp)
        }
    }

    result.inset(-padding, -padding)

    return result
}

fun exportSignatureNew(
    context: Context,
    strokes: List<StrokePath>,
    format: Bitmap.CompressFormat,
): Uri? {
    if (strokes.isEmpty()) return null

    val bounds = computeSignatureBounds(strokes) ?: return null
    val targetWidth = 2048f
    val scale = targetWidth / bounds.width()

    val bitmap =
        Bitmap.createBitmap(
            targetWidth.toInt(),
            (bounds.height() * scale).roundToInt(),
            Bitmap.Config.ARGB_8888,
        )
    val canvas = android.graphics.Canvas(bitmap)

    canvas.scale(scale, scale)
    canvas.translate(-bounds.left, -bounds.top)
    if (format == Bitmap.CompressFormat.PNG) {
        canvas.drawColor(android.graphics.Color.TRANSPARENT, android.graphics.PorterDuff.Mode.CLEAR)
    } else {
        canvas.drawColor(android.graphics.Color.WHITE)
    }

    val paint =
        android.graphics.Paint().apply {
            isAntiAlias = true
            style = android.graphics.Paint.Style.STROKE
            strokeCap = android.graphics.Paint.Cap.ROUND
            strokeJoin = android.graphics.Paint.Join.ROUND
        }

    strokes.forEach {
        paint.color = it.color.toArgb()
        paint.strokeWidth = it.strokeWidth

        canvas.drawPath(
            it.path.asAndroidPath(),
            paint,
        )
    }

    val extension =
        if (format == Bitmap.CompressFormat.PNG) {
            "png"
        } else {
            "jpg"
        }

    val mime =
        if (format == Bitmap.CompressFormat.PNG) {
            "image/png"
        } else {
            "image/jpeg"
        }

    val fileName =
        "Signature_${System.currentTimeMillis()}.$extension"

    val resolver = context.contentResolver

    val values =
        ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)

            put(MediaStore.Images.Media.MIME_TYPE, mime)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/Signatures",
                )

                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

    val uri =
        resolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            values,
        ) ?: return null

    try {
        resolver.openOutputStream(uri)?.use {
            bitmap.compress(
                format,
                100,
                it,
            )

            it.flush()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)

            resolver.update(uri, values, null, null)
        }
//        savedUri = uri
        val sizeKb = (bitmap.byteCount / 1024)
        Toast.makeText(context, "✓ Saved successfully as $extension ($sizeKb KB)", Toast.LENGTH_LONG).show()
        return uri
    } catch (e: Exception) {
        resolver.delete(uri, null, null)

        return null
    } finally {
        bitmap.recycle()
    }
}
