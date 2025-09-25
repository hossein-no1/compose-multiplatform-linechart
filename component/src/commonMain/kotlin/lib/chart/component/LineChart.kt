package lib.chart.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

@Composable
fun LineChart(
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    data: List<ChartData>,
    strokeColor: Color? = null,
    markerColor: Color = Color.Black,
) {

    val thresholdValue = 5f
    val strokeColor by remember(data.lastOrNull()) {
        mutableStateOf(
            when {
                strokeColor != null -> strokeColor
                ((data.lastOrNull()?.y ?: 0f) - (data.firstOrNull()?.y
                    ?: 0f)) in (0f..thresholdValue) -> Color(0xFFD1D11C)

                ((data.lastOrNull()?.y ?: 0f) - (data.firstOrNull()?.y
                    ?: 0f)) < thresholdValue -> Color(0xFFD11D1D)

                ((data.lastOrNull()?.y ?: 0f) - (data.firstOrNull()?.y ?: 0f)) > 0 -> Color(
                    0xFF1CD1A1
                )

                else -> Color.Yellow
            }
        )
    }

    val markerTextMeasurer = rememberTextMeasurer()

    val upperValue = remember(key1 = data) {
        (data.maxOfOrNull { it.y }?.let { it + it * 0.05f } ?: 0f)
    }
    val lowerValue = remember(key1 = data) {
        (data.minOfOrNull { it.y }?.let { it - it * 0.02f } ?: 0f)
    }

    val animationProgress = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = data) {
        animationProgress.animateTo(1f, tween(3000))
    }

    var selectedX by remember { mutableStateOf<Float?>(null) }

    Box(modifier = modifier, contentAlignment = alignment) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(data) {
                    detectDragGestures(
                        onDragEnd = { selectedX = null },
                        onDragStart = {
                            selectedX = it.x
                        },
                        onDragCancel = { selectedX = null },
                        onDrag = { change, _ ->
                            selectedX = change.position.x
                        })
                }
        ) {
            val leftPadding = 0f
            val rightPadding = 16.dp.toPx()
            val topPadding = 16.dp.toPx()
            val bottomPadding = 32.dp.toPx()

            val axisLeft = leftPadding
            val axisRight = size.width - rightPadding
            val axisTop = topPadding
            val axisBottom = size.height - bottomPadding

            val chartWidth = (axisRight - axisLeft).coerceAtLeast(0f)
            val chartHeight = (axisBottom - axisTop).coerceAtLeast(0f)

            val pointsCount = (data.size - 1).coerceAtLeast(1)
            val spacePerPoint = chartWidth / pointsCount

            val px = FloatArray(data.size)
            val py = FloatArray(data.size)
            data.indices.forEach { i ->
                val ratio = ((data[i].y - lowerValue) / (upperValue - lowerValue)).coerceIn(0f, 1f)
                px[i] = axisLeft + i * spacePerPoint
                py[i] = axisBottom - ratio * chartHeight
            }

            val midCount = (data.size - 1).coerceAtLeast(0)
            val midXArr = FloatArray(midCount)
            val midYArr = FloatArray(midCount)
            for (i in 0 until midCount) {
                midXArr[i] = (px[i] + px[i + 1]) / 2f
                midYArr[i] = (py[i] + py[i + 1]) / 2f
            }

            val strokePath = Path().apply {
                if (data.isNotEmpty()) {
                    moveTo(px[0], py[0])
                    if (data.size != 1) {
                        quadraticTo(px[0], py[0], midXArr[0], midYArr[0])
                        for (i in 1 until data.lastIndex) {
                            val nextMedX = midXArr[i]
                            val nextMedY = midYArr[i]
                            quadraticTo(px[i], py[i], nextMedX, nextMedY)
                        }
                        quadraticTo(px.last(), py.last(), px.last(), py.last())
                    }
                }
            }
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(axisLeft + chartWidth, axisBottom)
                lineTo(axisLeft, axisBottom)
                close()
            }
            clipRect(right = size.width * animationProgress.value) {
                drawPath(
                    path = strokePath,
                    color = strokeColor,
                    style = Stroke(
                        width = 2.dp.toPx(),
                        cap = StrokeCap.Round,
                    ),
                )

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            strokeColor.copy(alpha = .3f),
                            Color.Transparent,
                        ),
                        endY = axisBottom,
                    ),
                )
            }

            selectedX?.let { touchXRaw ->
                if (data.isNotEmpty() && chartWidth > 0f) {
                    val touchX = touchXRaw.coerceIn(axisLeft, axisLeft + chartWidth)

                    data.size
                    var segIndex = -1
                    if (data.size == 1) {
                        segIndex = 0
                    } else {
                        if (touchX <= midXArr[0]) {
                            segIndex = 0
                        } else {
                            for (i in 1 until data.lastIndex) {
                                val startX = midXArr[i - 1]
                                val endX = if (i < midXArr.size) midXArr[i] else px.last()
                                if (touchX in startX..endX) {
                                    segIndex = i
                                    break
                                }
                            }
                            if (segIndex == -1) {
                                segIndex = data.lastIndex
                            }
                        }
                    }

                    val p0x: Float
                    val p0y: Float
                    val p1x: Float
                    val p1y: Float
                    val p2x: Float
                    val p2y: Float
                    if (data.size == 1) {
                        p0x = px[0]; p0y = py[0]
                        p1x = px[0]; p1y = py[0]
                        p2x = px[0]; p2y = py[0]
                    } else if (segIndex == 0) {
                        p0x = px[0]; p0y = py[0]
                        p1x = px[0]; p1y = py[0]
                        p2x = midXArr[0]; p2y = midYArr[0]
                    } else if (segIndex in 1 until data.lastIndex) {
                        p0x = midXArr[segIndex - 1]; p0y = midYArr[segIndex - 1]
                        p1x = px[segIndex]; p1y = py[segIndex]
                        p2x = midXArr[segIndex]; p2y = midYArr[segIndex]
                    } else {
                        p0x = midXArr.last(); p0y = midYArr.last()
                        p1x = px.last(); p1y = py.last()
                        p2x = px.last(); p2y = py.last()
                    }

                    val tx = touchX

                    val ax = p0x - 2f * p1x + p2x
                    val bx = -2f * p0x + 2f * p1x
                    val cxq = p0x - tx

                    val t = if (kotlin.math.abs(ax) < 1e-6f) {
                        if (kotlin.math.abs(bx) < 1e-6f) 0f else (-cxq / bx).coerceIn(0f, 1f)
                    } else {
                        val disc = bx * bx - 4f * ax * cxq
                        if (disc < 0f) {
                            0f
                        } else {
                            val sqrtD = kotlin.math.sqrt(disc)
                            val t1 = (-bx + sqrtD) / (2f * ax)
                            val t2 = (-bx - sqrtD) / (2f * ax)
                            val mid = listOf(t1, t2).filter { it in 0f..1f }
                            when {
                                mid.isEmpty() -> 0f
                                mid.size == 1 -> mid.first()
                                else -> {
                                    val x1 =
                                        (1 - t1) * (1 - t1) * p0x + 2 * (1 - t1) * t1 * p1x + t1 * t1 * p2x
                                    val x2 =
                                        (1 - t2) * (1 - t2) * p0x + 2 * (1 - t2) * t2 * p1x + t2 * t2 * p2x
                                    if (kotlin.math.abs(x1 - tx) <= kotlin.math.abs(x2 - tx)) t1 else t2
                                }
                            }
                        }
                    }

                    val oneMinusT = 1f - t
                    val cx = oneMinusT * oneMinusT * p0x + 2f * oneMinusT * t * p1x + t * t * p2x
                    val cy = oneMinusT * oneMinusT * p0y + 2f * oneMinusT * t * p1y + t * t * p2y

                    val outerRadius = 4.dp.toPx()
                    val ringStroke = 1.dp.toPx()
                    drawCircle(
                        color = markerColor,
                        radius = outerRadius,
                        center = Offset(cx, cy),
                        style = Stroke(width = ringStroke)
                    )
                    drawCircle(
                        color = markerColor,
                        radius = outerRadius - 2.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = markerColor,
                        radius = outerRadius - 2.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    val value =
                        (lowerValue + ((axisBottom - cy) / chartHeight) * (upperValue - lowerValue)).roundToInt()

                    val layout = markerTextMeasurer.measure(
                        text = AnnotatedString(text = value.toString()),
                        style = TextStyle(
                            color = markerColor,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                        ),
                    )
                    val textLeft = cx - layout.size.width / 2f
                    val textTop = cy - outerRadius - 6.dp.toPx() - layout.size.height

                    drawText(
                        textLayoutResult = layout,
                        topLeft = Offset(
                            x = textLeft.coerceIn(0f, size.width - layout.size.width.toFloat()),
                            y = textTop.coerceAtLeast(0f)
                        )
                    )
                }
            }
        }
    }

}


@Preview(showBackground = true)
@Composable
fun LineChartPreview() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        LineChart(
            modifier = Modifier.fillMaxSize(),
            data = listOf(
                ChartData(x = 1f, y = 2f),
                ChartData(x = 2f, y = 3f),
                ChartData(x = 3f, y = 5f),
                ChartData(x = 4f, y = 4f),
                ChartData(x = 5f, y = 1f),
                ChartData(x = 6f, y = 2f),
                ChartData(x = 7f, y = 3f),
                ChartData(x = 8f, y = 5f),
                ChartData(x = 9f, y = 4f),
                ChartData(x = 10f, y = 1f),
            ),
            markerColor = Color.White
        )
    }
}