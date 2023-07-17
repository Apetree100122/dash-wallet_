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

package de.schildbach.wallet.ui.invite

import android.view.LayoutInflater
import android.view.ViewGroup
import de.schildbach.wallet.database.entity.Invitation
import de.schildbach.wallet.util.WalletUtils
import de.schildbach.wallet_test.R
import kotlinx.android.synthetic.main.invite_history_row.view.*

open class InviteViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        InvitesHistoryViewHolder(R.layout.invite_history_row, inflater, parent) {

    fun bind(invitation: Invitation?, vararg args: Any?) {
        itemView.apply {
            val id = args[0] as Int
            memo.text = if (invitation!!.memo.isNotEmpty()) {
                invitation.memo
            } else {
                itemView.context.getString(R.string.invitation_created_title) + " " + id // is this a good way or should it be "Invitation %d"
            }
            state_icon.setImageResource(if (invitation.acceptedAt != 0L) R.drawable.ic_claimed_invite else R.drawable.ic_pending_invite)
            date.text = WalletUtils.formatDate(invitation.sentAt)
        }
    }
}