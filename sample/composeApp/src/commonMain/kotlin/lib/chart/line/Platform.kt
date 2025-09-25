package lib.chart.line

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform