package dev.havlicektomas.shaderbrushexample

import android.graphics.RuntimeShader
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.havlicektomas.shaderbrushexample.ui.theme.ShaderBrushExampleTheme
import org.intellij.lang.annotations.Language

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShaderBrushExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ShaderBrushExample(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Language("AGSL")
val CUSTOM_SHADER = """
    uniform float time;
    uniform float2 resolution;
    layout(color) uniform half4 color;
    layout(color) uniform half4 color2;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord/resolution.xy;

        //float mixValue = distance(uv, vec2(0, 1));
        float mixValue = distance(uv, vec2(0, 1)) + abs(sin(time* 0.5));
        return mix(color, color2, mixValue);
    }
""".trimIndent()

val Coral = Color(0xFFF3A397)
val LightYellow = Color(0xFFF8EE94)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
@Preview
fun ShaderBrushExample(modifier: Modifier = Modifier) {
    val time by produceState(0f) {
        while (true) {
            withInfiniteAnimationFrameMillis {
                value = it / 1000f
            }
        }
    }
    Box(
        modifier = Modifier
            .drawWithCache {
                val shader = RuntimeShader(CUSTOM_SHADER)
                val shaderBrush = ShaderBrush(shader)
                shader.setFloatUniform("time", time)
                shader.setFloatUniform("resolution", size.width, size.height)
                onDrawBehind {
                    shader.setColorUniform(
                        "color",
                        android.graphics.Color.valueOf(
                            LightYellow.red, LightYellow.green,
                            LightYellow
                                .blue,
                            LightYellow.alpha
                        )
                    )
                    shader.setColorUniform(
                        "color2",
                        android.graphics.Color.valueOf(
                            Coral.red,
                            Coral.green,
                            Coral.blue,
                            Coral.alpha
                        )
                    )
                    drawRect(shaderBrush)
                }
            }
            .fillMaxWidth()
            .height(200.dp)
    )
}