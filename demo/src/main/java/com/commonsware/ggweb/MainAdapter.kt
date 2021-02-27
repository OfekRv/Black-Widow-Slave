/*
 * Copyright (c) 2021 CommonsWare, LLC
 * All rights reserved.
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this
 * software and associated documentation files (the "Software"), to deal in the Software
 * without restriction, including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
 * to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED *AS IS*, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package com.commonsware.ggweb

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.commonsware.ggweb.databinding.RowReceivedBinding
import com.commonsware.ggweb.databinding.RowSentBinding

class MainAdapter(private val inflater: LayoutInflater) :
  ListAdapter<Message, MainAdapter.AbstractMessageHolder>(MESSAGE_DIFFER) {
  override fun getItemViewType(position: Int) =
    if (getItem(position).wasSent) R.layout.row_sent else R.layout.row_received

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    if (viewType == R.layout.row_sent) {
      SentMessageHolder(RowSentBinding.inflate(inflater, parent, false))
    } else {
      ReceivedMessageHolder(RowReceivedBinding.inflate(inflater, parent, false))
    }

  override fun onBindViewHolder(holder: AbstractMessageHolder, position: Int) {
    holder.bind(getItem(position))
  }

  abstract class AbstractMessageHolder(root: View) : RecyclerView.ViewHolder(root) {
    abstract fun bind(message: Message)
  }

  class SentMessageHolder(private val binding: RowSentBinding) : AbstractMessageHolder(binding.root) {
    override fun bind(message: Message) {
      binding.message.text = message.text
    }
  }

  class ReceivedMessageHolder(private val binding: RowReceivedBinding) : AbstractMessageHolder(binding.root) {
    override fun bind(message: Message) {
      binding.message.text = message.text
    }
  }
}

private val MESSAGE_DIFFER = object : DiffUtil.ItemCallback<Message>() {
  override fun areItemsTheSame(oldItem: Message, newItem: Message) = areContentsTheSame(oldItem, newItem)

  override fun areContentsTheSame(oldItem: Message, newItem: Message) = oldItem == newItem
}
