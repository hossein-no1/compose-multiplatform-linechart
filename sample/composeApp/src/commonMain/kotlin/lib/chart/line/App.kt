package lib.chart.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lib.chart.component.ChartData
import lib.chart.component.LineChart
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(platform: Platform) {
    MaterialTheme {

        val data = listOf(
            ChartData(x = 1f, y = 8f),
            ChartData(x = 2f, y = 9f),
            ChartData(x = 3f, y = 7f),
            ChartData(x = 4f, y = 3f),
            ChartData(x = 5f, y = 11f),
            ChartData(x = 6f, y = 14f),
            ChartData(x = 7f, y = 6f),
            ChartData(x = 8f, y = 3f),
            ChartData(x = 9f, y = 12f),
            when (platform) {
                Platform.ANDROID -> ChartData(x = 10f, y = 12f)
                Platform.IOS -> ChartData(x = 10f, y = 22f)
                Platform.DESKTOP -> ChartData(x = 10f, y = 6f)
            },
        )

        Box(
            modifier = Modifier
                .background(Color(0xFF030621))
                .fillMaxSize(),
        ) {

            Column(
                modifier = Modifier.align(alignment = Alignment.TopStart)
                    .offset(x = 32.dp, y = 128.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.Start,
            ) {
                Text(
                    text = "+ 5.45%",
                    color = when (platform) {
                        Platform.ANDROID -> Color(0xFFD1D11C)
                        Platform.IOS -> Color(0xFF1CD1A1)
                        Platform.DESKTOP -> Color(0xFFD11D1D)
                    },
                    fontSize = 32.sp,
                )

                Text(
                    text = "Gains all time",
                    color = Color.Gray,
                    fontSize = 14.sp,
                )
            }

            LineChart(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(fraction = .6f)
                    .align(alignment = Alignment.BottomCenter),
                data = data,
                markerColor = Color.White
            )
        }
    }
}

enum class Platform {
    ANDROID,
    IOS,
    DESKTOP
}