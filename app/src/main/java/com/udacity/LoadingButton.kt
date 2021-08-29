package com.udacity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.admin.SystemUpdatePolicy
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import androidx.core.content.withStyledAttributes
import kotlinx.android.synthetic.main.content_main.view.*
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var downloadColorButton = 0
    private var downloadingColorButton = 0
    private var downloadingCircleColor = 0

    private var loadingWidth = 0f
    private var loadingAngle = 0f

    private var circleRadius = 0f

    private var buttonText = ""

    private var paintBarLoading = Paint()
    private val paintStatusCircle = Paint()

    private val paintBarText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.create( "", Typeface.NORMAL)
    }

    private var statusBarAnimator = ValueAnimator()
    private var circleAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
        when(new) {
            ButtonState.Clicked -> {
                buttonText = resources.getString(R.string.button_name)
                loadingWidth = 1f
                loadingAngle = 1f
            }

            ButtonState.Loading -> {
                buttonText = resources.getString(R.string.button_loading)
                statusBarAnimator = ValueAnimator.ofFloat(0f, measuredWidth.toFloat())
                        .apply {
                            duration = 2000L
                            repeatMode = ValueAnimator.RESTART
                            repeatCount = ValueAnimator.INFINITE
                            addUpdateListener {
                                loadingWidth = animatedValue as Float
                                this@LoadingButton.invalidate()
                            }
                            start()
                        }

                circleAnimator = ValueAnimator.ofFloat(0f, 360f)
                        .apply {
                            duration = 2000L
                            repeatMode =ValueAnimator.RESTART
                            repeatCount = ValueAnimator.INFINITE
                            interpolator = AccelerateInterpolator(1f)
                            addUpdateListener {
                                loadingAngle = animatedValue as Float
                                this@LoadingButton.invalidate()
                            }
                            start()
                        }

            }

            ButtonState.Completed -> {
                buttonText = resources.getString(R.string.button_name)
                loadingWidth = 1f
                loadingAngle = 1f
                statusBarAnimator.end()
                circleAnimator.end()
            }
        }
    }


    init {
        isClickable = true
        buttonText = resources.getString(R.string.button_name)
        buttonState = ButtonState.Clicked
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            downloadColorButton = getColor(R.styleable.LoadingButton_downloadColor, 0)
            downloadingColorButton = getColor(R.styleable.LoadingButton_downloadingColor, 0)
            downloadingCircleColor = getColor(R.styleable.LoadingButton_downloadingCircle, 0)
        }

        paintBarLoading.color = downloadingColorButton
        paintStatusCircle.color = downloadingCircleColor
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(downloadColorButton)

        if (buttonState == ButtonState.Loading){
            //loadingBar
            canvas.drawRect(0f, 0f,
                    loadingWidth, measuredHeight.toFloat(),
                    paintBarLoading)

            circleRadius = heightSize / 5f
            //loadingCircle
            canvas.drawArc((widthSize * 11 / 15) - circleRadius,
                    (heightSize / 2) - circleRadius,
                    (widthSize * 11 / 15) + circleRadius,
                    (heightSize / 2) + circleRadius,
                    0f, loadingAngle,
                    true, paintStatusCircle)
        }

        //textBar
        paintBarText.color = Color.WHITE
        canvas.drawText(buttonText,
                widthSize.toFloat()/2,
                heightSize.toFloat()/1.65f,
                paintBarText)


    }

    override fun performClick(): Boolean {
        super.performClick()
        when (buttonState) {
            ButtonState.Completed -> ButtonState.Clicked
            ButtonState.Clicked -> ButtonState.Loading
            else -> ButtonState.Completed
        }
        invalidate()
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

    fun setState(state: ButtonState) {
        buttonState = state
    }

}