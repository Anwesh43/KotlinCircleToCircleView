package ui.anwesome.com.kotlincircletocircleview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.circletocircleview.CircleToCircleView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CircleToCircleView.create(this)
    }
}
