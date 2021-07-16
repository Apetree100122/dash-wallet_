/*
 * Copyright 2021 Dash Core Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.schildbach.wallet.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.LinearLayout
import de.schildbach.wallet_test.R
import kotlinx.android.synthetic.main.please_wait_restoring_wallet_view.view.*


class PleaseWaitRestoringWalletView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    private val rotateAnimation = RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f).apply {
        duration = 2000
        repeatCount = Animation.INFINITE
        interpolator = LinearInterpolator()
    }

    init {
        inflate(context, R.layout.please_wait_restoring_wallet_view, this)
        orientation = VERTICAL
        gravity = Gravity.CENTER_VERTICAL
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        please_wait_arrows.startAnimation(rotateAnimation)
    }

    override fun onDetachedFromWindow() {
        please_wait_arrows.clearAnimation()
        super.onDetachedFromWindow()
    }
}
