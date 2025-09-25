package lib.chart.line

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lib.chart.component.ChartData
import lib.chart.component.LineChart
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .background(Color(0xFF000000))
                .safeContentPadding()
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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
                    ChartData(x = 10f, y = 12f),
                ),
                markerColor = Color.White
            )
        }
    }
}