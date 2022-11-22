package za.co.ishmaelt.examplecompe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import za.co.ishmaelt.examplecompe.ui.theme.ComposeTheme

class MainActivity : ComponentActivity() {

    private val remoteConfigService = Injector.remoteConfigService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        remoteConfigService.getDebugToken()

        remoteConfigService.fetchAndActiveRemoteConfig()

        setContent {
            ComposeTheme {
                Surface(color = MaterialTheme.colors.background) {
                    ShowButton("")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeTheme {
        ShowButton("")
    }
}

@Composable
fun ShowButton(color: String) {
    ConstraintLayout(
        modifier = Modifier
            .absolutePadding(left = 16.dp, right = 16.dp)
            .fillMaxSize()
    ) {

        var colorChange = remember { mutableStateOf("#0000ff")}

        val button = createRef()
        Button(
            colors = ButtonDefaults.buttonColors(backgroundColor = colorChange.value.color),
            onClick = {
                val analyticsService = Injector.analyticsService
                analyticsService.logEvent("button_click", mapOf("name" to "change color"))
                val remoteConfigService = Injector.remoteConfigService
                colorChange.value = remoteConfigService.getString("button_color")
            },
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(button) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        ) {
            Text(
                text = "Show button colored: $color",
                color = Color.White
            )
        }
    }
}

private val String.color
    get() = Color(android.graphics.Color.parseColor(this))