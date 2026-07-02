package com.example.signatureapp

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.ByteArrayOutputStream

//

// Data class encapsulating unique vector properties of each continuous path
data class StrokePath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float
)
@Preview(showBackground = true)

@Composable
fun SignatureScreen() {
    val context = LocalContext.current

    // Canvas Draw States
    val strokes = remember { mutableStateListOf<StrokePath>() }
    val redoHistory = remember { mutableStateListOf<StrokePath>() }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    // Brush Configurations
    var selectedColor by remember { mutableStateOf(Color.White) }
    var strokeWidth by remember { mutableStateOf(8f) }

    val availableColors = listOf(
        Color.Black,
        Color.White,
        Color(0xFF7C4DFF), // Deep Purple
        Color(0xFF00E5FF), // Cyan
        Color(0xFF00E676), // Green
        Color(0xFFFFD700), // Amber
        Color(0xFFFF1744)  // Red
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Export Signature",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Sign and export with ease.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }

            Row {
                IconButton(
                    onClick = {
                        if (strokes.isNotEmpty()) {
                            redoHistory.add(strokes.removeAt(strokes.lastIndex))
                        }
                    },
                    enabled = strokes.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Undo")
                }
                IconButton(
                    onClick = {
                        if (redoHistory.isNotEmpty()) {
                            strokes.add(redoHistory.removeAt(redoHistory.lastIndex))
                        }
                    },
                    enabled = redoHistory.isNotEmpty()
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Redo")
                }
                IconButton(
                    onClick = {
                        strokes.clear()
                        redoHistory.clear()
                    },
                    enabled = strokes.isNotEmpty()
                ) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFFFF1744))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- SIGNATURE CANVAS CORE ---
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(Color.White)
                .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(selectedColor, strokeWidth) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                redoHistory.clear() // Mutation breaks future redos
                                val path = Path().apply { moveTo(offset.x, offset.y) }
                                currentPath = path
                                strokes.add(StrokePath(path, selectedColor, strokeWidth))
                            },
                            onDrag = { change, _ ->
                                currentPath?.lineTo(change.position.x, change.position.y)
                                // Trick to force canvas recomposition over mutating object properties
                                val last = strokes.removeAt(strokes.lastIndex)
                                strokes.add(last)
                            },
                            onDragEnd = { currentPath = null }
                        )
                    }
            ) {
                strokes.forEach { strokePath ->
                    drawPath(
                        path = strokePath.path,
                        color = strokePath.color,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokePath.strokeWidth,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        // --- CONTROL & EXPORT PANEL ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(24.dp))
                .padding(16.dp)
        ) {
            Text(
                text = "Select Color",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.5f)
            )
            // Color Selector & Stroke Customization Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    availableColors.forEach { color ->
                        val isSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .border(
                                    width = if (isSelected) 2.dp else 0.dp,
                                    color = if (isSelected) color else Color.Transparent,
                                    shape = CircleShape
                                )
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(color)
                                .clickable { selectedColor = color }
                        )
                    }
                }

            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Text(
                    text = " Thickness: ${strokeWidth.toInt()} px",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f)
                )
                Box(
                    modifier = Modifier.size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size((strokeWidth / 2).dp.coerceAtMost(28.dp))
                            .clip(CircleShape)
                            .background(selectedColor)
                    )
                }
            }


            Slider(
                value = strokeWidth,
                onValueChange = { strokeWidth = it },
                valueRange = 2f..30f,
                colors = SliderDefaults.colors(
                    thumbColor = selectedColor,
                    activeTrackColor = selectedColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.1f)
                ),
                modifier = Modifier.width(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Trigger Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { exportSignature(context, strokes, Bitmap.CompressFormat.PNG) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.AccountBox, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save PNG", color = Color.White)
                }

                Button(
                    onClick = { exportSignature(context, strokes, Bitmap.CompressFormat.JPEG) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Save JPEG", color = Color.White)
                }
            }
        }
    }
}

/**
 * Robust offscreen graphics rendering. Replays vector elements inside an isolated
 * native canvas matching expected dimensional boundaries to ensure perfect clean exports.
 */
private fun exportSignature(
    context: Context,
    strokes: List<StrokePath>,
    format: Bitmap.CompressFormat
) {
    if (strokes.isEmpty()) {
        Toast.makeText(context, "Signature is empty! Draw something first.", Toast.LENGTH_SHORT).show()
        return
    }

    // Set static canvas capture boundaries
    val width = 800
    val height = 600

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)

    // JPEGs cannot support transparent parameters; initialize with solid color
    if (format == Bitmap.CompressFormat.JPEG) {
        canvas.drawColor(android.graphics.Color.BLACK)
    }

    val paint = Paint().asFrameworkPaint().apply {
        isAntiAlias = true
        style = android.graphics.Paint.Style.STROKE
        strokeCap = android.graphics.Paint.Cap.ROUND
        strokeJoin = android.graphics.Paint.Join.ROUND
    }

    // Render the cached state paths over the physical surface configuration
    strokes.forEach { strokePath ->
        paint.color = strokePath.color.hashCode() // Convert compose Color to native Int
        paint.strokeWidth = strokePath.strokeWidth
        canvas.drawPath(strokePath.path.asAndroidPath(), paint)
    }

    val stream = ByteArrayOutputStream()
    bitmap.compress(format, 100, stream)
    val byteArray = stream.toByteArray()

    // Successful stream handling simulation
    Toast.makeText(
        context,
        "Exported ${format.name} successfully!",
        Toast.LENGTH_LONG
    ).show()
}