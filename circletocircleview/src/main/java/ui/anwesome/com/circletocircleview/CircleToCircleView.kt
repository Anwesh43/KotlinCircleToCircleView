package ui.anwesome.com.circletocircleview

/**
 * Created by anweshmishra on 25/02/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
class CircleToCircleView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class State(var j:Int = 0, var dir:Float = 0f) {
        var scales:Array<Float> = arrayOf(0f, 0f)
        fun update(stopcb : () -> Unit) {
            scales[j] += 0.1f * dir
            if(scales[j] > 1) {
                scales[j] = 1f
                j++
                if(j == scales.size) {
                    dir = 0f
                    j = 0
                    stopcb()
                }
            }
        }
        fun startUpdating(startcb : () -> Unit) {
            if(dir == 0f) {
                dir = 1f
                scales = arrayOf(0f, 0f)
                startcb()
            }
        }
    }
}