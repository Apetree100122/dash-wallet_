package org.dash.wallet.common.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import org.dash.wallet.common.services.LockScreenBroadcaster
import javax.inject.Inject

@AndroidEntryPoint
@Deprecated("Use AdaptiveDialog to show dialogs that are auto-dismissible with lock screen")
open class BaseLockScreenFragment(@LayoutRes contentLayoutId: Int): Fragment(contentLayoutId) {
    @Inject lateinit var lockScreenBroadcaster: LockScreenBroadcaster
    protected lateinit var alertDialog: AlertDialog
    protected lateinit var dialogFragment: DialogFragment
    @Inject protected lateinit var baseAlertDialogBuilder: BaseAlertDialogBuilder

    constructor(): this(0)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lockScreenBroadcaster.activatingLockScreen.observe(this) {
            if (this::alertDialog.isInitialized) {
                alertDialog.dismissDialog()
            }
            if (this::dialogFragment.isInitialized){
                dialogFragment.dismiss()
            }
        }
    }
}