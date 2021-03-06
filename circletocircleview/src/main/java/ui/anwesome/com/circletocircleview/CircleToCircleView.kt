package ui.anwesome.com.circletocircleview

/**
 * Created by anweshmishra on 25/02/18.
 */
import android.app.Activity
import android.view.*
import android.content.*
import android.content.pm.ActivityInfo
import android.graphics.*
class CircleToCircleView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
    var circleToCircleListener : CircleToCircleListener ?= null
    fun addCircleToCircleListener(onLeftMoveListener : () -> Unit, onRightMoveListener : () -> Unit, onCantMoveLeftListener: () -> Unit, onCantMoveRightListener : () -> Unit) {
        circleToCircleListener = CircleToCircleListener(onLeftMoveListener, onRightMoveListener, onCantMoveLeftListener, onCantMoveRightListener)
    }
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas, paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.handleTap(event.x)
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
    data class Animator(var view: View, var animated : Boolean = false) {
        fun animate(updatecb : () -> Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex : Exception) {

                }
            }
        }
        fun start() {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    data class CircleToCircle(var x : Float, var y : Float, var size : Float, var w:Float, var dir : Float = 0f) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            canvas.save()
            canvas.translate(x, y)
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#f44336")
            paint.strokeWidth = size/25
            canvas.drawCircle(this.dir * (size / 2) * (state.scales[0] + state.scales[1]), 0f, size/4, paint)
            paint.style = Paint.Style.STROKE
            canvas.drawArc(RectF( -size/2, -size/2, size/2, size/2), 90 * (1 - dir), 360f * (1 - state.scales[0]), false, paint)
            canvas.save()
            canvas.translate(  size * this.dir, 0f)
            canvas.drawArc(RectF(-size/2, -size/2, size/2, size/2), 90 * (1 + dir), 360f * state.scales[1], false, paint)
            canvas.restore()
            canvas.restore()
        }
        fun update(stopcb : (Float) -> Unit) {
            state.update({
                this.x += this.dir * size
                stopcb(this.dir)
                this.dir = 0f
            })
        }
        fun startUpdating(dir : Float, startcb : () -> Unit) {
            if(this.dir == 0f) {
                this.dir = dir
                state.startUpdating(startcb)
            }
        }
        fun insideBound(dir : Float):Boolean {
            when(dir) {
                1f -> {
                    return x <= w - 3 * size / 2
                }
                -1f -> {
                    return x >= 3 * size / 2
                }
            }
            return false
        }
    }
    data class Renderer(var view : CircleToCircleView, var time : Int = 0) {
        val animator = Animator(view)
        var circleToCircle : CircleToCircle ?= null
        fun render(canvas : Canvas, paint : Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                circleToCircle = CircleToCircle(w/2, h/2, Math.min(w, h)/ 5, w)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            circleToCircle?.draw(canvas, paint)
            time++
            animator.animate {
                circleToCircle?.update {
                    animator.stop()
                    when(it) {
                        1f -> view.circleToCircleListener?.onRightMoveListener?.invoke()
                        -1f -> view.circleToCircleListener?.onLeftMoveListener?.invoke()
                    }
                }
            }
        }
        fun handleTap(x : Float) {
            val circleX = circleToCircle?.x?:0f
            val diff = Math.floor((x - circleX).toDouble()).toFloat()
            if(diff != 0f && circleToCircle?.insideBound((diff)/Math.abs(diff))?:false) {
                circleToCircle?.startUpdating(diff/Math.abs(diff)){
                    animator.start()
                }
            }
            else if(diff != 0f) {
                when(diff/Math.abs(diff)) {
                    1f -> view.circleToCircleListener?.onCantMoveRightListener?.invoke()
                    -1f -> view.circleToCircleListener?.onCantMoveLeftListener?.invoke()
                }
            }
        }
    }
    companion object {
        fun create(activity : Activity):CircleToCircleView {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            val view = CircleToCircleView(activity)
            activity.setContentView(view)
            return view
        }
    }
    data class CircleToCircleListener(val onLeftMoveListener : () -> Unit, val onRightMoveListener : () -> Unit, val onCantMoveLeftListener: () -> Unit, val onCantMoveRightListener : () -> Unit)
}