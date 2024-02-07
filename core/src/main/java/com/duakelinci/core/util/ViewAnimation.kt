package com.duakelinci.core.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.animation.Animation
import android.view.animation.Transformation

fun View.expand(onFinishListener: (() -> Unit)? = null) {
    val a = expandAction(this)
    a.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(p0: Animation?) {

        }

        override fun onAnimationEnd(p0: Animation?) {
            onFinishListener?.invoke()
        }

        override fun onAnimationRepeat(p0: Animation?) {
        }
    })
    this.startAnimation(a)
}

private fun expandAction(v: View): Animation {
    v.measure(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
    val targtetHeight = v.measuredHeight
    v.layoutParams.height = 0
    v.visibility = View.VISIBLE
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            v.layoutParams.height =
                if (interpolatedTime == 1f) LayoutParams.WRAP_CONTENT else (targtetHeight * interpolatedTime).toInt()
            v.requestLayout()
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = (targtetHeight / v.context.resources.displayMetrics.density).toLong()
    v.startAnimation(a)
    return a
}

fun View.collapse() {
    val initialHeight = this.measuredHeight
    val a: Animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            if (interpolatedTime == 1f) {
                this@collapse.gone()
            } else {
                this@collapse.layoutParams.height =
                    initialHeight - (initialHeight * interpolatedTime).toInt()
                this@collapse.requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean {
            return true
        }
    }
    a.duration = ((initialHeight / this.context.resources.displayMetrics.density).toLong())
    this.startAnimation(a)
}

fun View.flyInDown(onFinishListener: (() -> Unit)? = null) {
    this.visible()
    this.alpha = 0.0f
    this.translationY = 0f
    this.translationY = -this.height.toFloat()
    // Prepare the View for the animation
    this.animate()
        .setDuration(200)
        .translationY(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .alpha(1.0f)
        .start()
}

fun View.flyOutDown(onFinishListener: (() -> Unit)? = null) {
    this.visibility = View.VISIBLE
    this.alpha = 1.0f
    this.translationY = 0f
    // Prepare the View for the animation
    this.animate()
        .setDuration(200)
        .translationY(this.height.toFloat())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .alpha(0.0f)
        .start()
}

fun View.fadeIn(onFinishListener: (() -> Unit)? = null) {
    this.gone()
    this.alpha = 0.0f
    // Prepare the View for the animation
    this.animate()
        .setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeIn.visible()
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .alpha(1.0f)
}

fun View.fadeOut(onFinishListener: (() -> Unit)? = null) {
    this.alpha = 1.0f
    // Prepare the View for the animation
    this.animate()
        .setDuration(500)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@fadeOut.gone()
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .alpha(0.0f)
}

fun View.showIn() {
    this.visible()
    this.alpha = 0f
    this.translationY = this.height.toFloat()
    this.animate()
        .setDuration(200)
        .translationY(0f)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@showIn.visible()
                super.onAnimationEnd(animation)
            }
        })
        .alpha(1f)
        .start()
}

fun View.initShowOut() {
    this.gone()
    this.translationY = this.height.toFloat()
    this.alpha = 0f
}

fun View.showOut() {
    this.visible()
    this.alpha = 1f
    this.translationY = 0f
    this.animate()
        .setDuration(200)
        .translationY(this.height.toFloat())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@showOut.gone()
                super.onAnimationEnd(animation)
            }
        }).alpha(0f)
        .start()
}

fun View.rotateFab(rotate: Boolean): Boolean {
    this.animate().setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
            }
        })
        .rotation(if (rotate) 135f else 0f)
    return rotate
}

fun View.fadeOutIn() {
    this.alpha = 0f
    val animatorSet = AnimatorSet()
    val animatorAlpha: ObjectAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 0.5f, 1f)
    ObjectAnimator.ofFloat(this, "alpha", 0f).start()
    animatorAlpha.duration = (500)
    animatorSet.play(animatorAlpha)
    animatorSet.start()
}

fun View.showScale(onFinishListener: (() -> Unit)? = null) {
    this.animate()
        .scaleY(1f)
        .scaleX(1f)
        .setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .start()
}

fun View.hideScale(onFinishListener: (() -> Unit)? = null) {
    this.animate()
        .scaleY(0f)
        .scaleX(0f)
        .setDuration(200)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                onFinishListener?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .start()
}

fun View.hideFab() {
    val moveY = 2 * this.height
    this.animate()
        .translationY(moveY.toFloat())
        .setDuration(300)
        .start()
}

fun View.showFab() {
    this.animate()
        .translationY(0f)
        .setDuration(300)
        .start()
}
