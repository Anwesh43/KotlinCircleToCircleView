package ui.anwesome.com.circletocircleview

/**
 * Created by anweshmishra on 25/02/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
class CircleToCircleView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    val renderer = Renderer(this)
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
    data class CircleToCircle(var x : Float, var y : Float, var size : Float, var dir : Float = 0f) {
        val state = State()
        fun draw(canvas : Canvas, paint : Paint) {
            canvas.save()
            canvas.translate(x, y)
            paint.style = Paint.Style.FILL
            paint.color = Color.parseColor("#f44336")
            paint.strokeWidth = size/25
            canvas.drawCircle(this.dir * size * (state.scales[0] + state.scales[1]), 0f, size/4, paint)
            paint.style = Paint.Style.STROKE
            canvas.drawArc(RectF( -size/2, -size/2, size/2, size/2), 90 * (1 - dir), 360f * state.scales[0], false, paint)
            canvas.save()
            canvas.translate( 2 * size * this.dir, 0f)
            canvas.drawArc(RectF(-size/2, -size/2, size/2, size/2), 90 * (1 + dir), 360f * state.scales[1], false, paint)
            canvas.restore()
            canvas.restore()
        }
        fun update(stopcb : () -> Unit) {
            state.update(stopcb)
        }
        fun startUpdating(dir : Float, startcb : () -> Unit) {
            if(this.dir == 0f) {
                this.dir = dir
                state.startUpdating(startcb)
            }
        }
    }
    data class Renderer(var view : CircleToCircleView, var time : Int = 0) {
        val animator = Animator(view)
        var circleToCircle : CircleToCircle ?= null
        fun render(canvas : Canvas, paint : Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                circleToCircle = CircleToCircle(w/2, h/2, Math.min(w, h)/ 5)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            circleToCircle?.draw(canvas, paint)
            time++
            animator.animate {
                circleToCircle?.update {
                    animator.stop()
                }
            }
        }
        fun handleTap(x : Float) {
            val diff = x - (circleToCircle?.x?:0f)
            if(diff != 0f) {
                circleToCircle?.startUpdating(diff/Math.abs(diff)){
                    animator.start()
                }
            }
        }
    }
}