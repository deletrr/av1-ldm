import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.fatec.av1.ui.MainApp
import com.fatec.av1.ui.theme.AppTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(canvasElementId = "ComposeTarget", title = "LDM AV1") {
        AppTheme(darkTheme = true){
            MainApp()
        }
    }
}
