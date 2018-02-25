package ui.anwesome.com.kotlincircletocircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import ui.anwesome.com.circletocircleview.CircleToCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = CircleToCircleView.create(this)
        fullScreen()
        view.addCircleToCircleListener({ createToast("moved left") },{ createToast("moved right") },{ createToast("can't move to left") },{ createToast("can't move to right") })
    }
}
fun MainActivity.fullScreen() {
    supportActionBar?.hide()
    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}
fun MainActivity.createToast(text : String) {
    Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
}