package com.lions.wantitclient.data.model.chat

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lions.wantitclient.R
import com.lions.wantitclient.databinding.ItemChatBinding

class ChatAdapter(
    private val messageList: MutableList<Message>,
    private val listener: OnChatListener
) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private lateinit var context: Context

    fun add(message: Message) {
        if (!messageList.contains(message)) {
            messageList.add(message)
            notifyItemInserted(messageList.size - 1)
        }
    }

    fun update(message: Message) {
        val index = messageList.indexOf(message)
        if (index != -1) {
            messageList.set(index, message)
            notifyItemChanged(index)
        }
    }

    fun delete(message: Message) {
        val index = messageList.indexOf(message)
        if (index != -1) {
            messageList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemChatBinding.bind(view)

        fun setListener(message: Message) {
            binding.tvMessage.setOnLongClickListener {
                listener.deleteMessage(message)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messageList[position]
        holder.setListener(message)
        var gravity = Gravity.END
        var background = ContextCompat.getDrawable(context, R.drawable.background_chat_client)
        var textColor = ContextCompat.getColor(context, R.color.design_default_color_on_secondary)

        val marginHorizontal =
            context.resources.getDimensionPixelSize(R.dimen.chat_margin_horizontal)
        val params = holder.binding.tvMessage.layoutParams as ViewGroup.MarginLayoutParams
        params.marginStart = marginHorizontal
        params.marginEnd = 0
        params.topMargin = 0

        if (position > 0 && message.isSentByMe() != messageList[position - 1].isSentByMe()) {
            params.topMargin = context.resources.getDimensionPixelSize(R.dimen.common_padding_min)
        }

        if (!message.isSentByMe()) {  //si el mensaje NO es enviado por Mi !
            gravity = Gravity.START
            background = ContextCompat.getDrawable(context, R.drawable.background_chat_support)
            textColor = ContextCompat.getColor(context, R.color.design_default_color_on_primary)
            params.marginStart = 0
            params.marginEnd = marginHorizontal
        }
        holder.binding.root.gravity = gravity    // el root es nuestro Linear Layout
        holder.binding.tvMessage.layoutParams = params
        holder.binding.tvMessage.setBackground(background)
        holder.binding.tvMessage.setTextColor(textColor)
        holder.binding.tvMessage.text = message.message

    }

    override fun getItemCount(): Int = messageList.size
}