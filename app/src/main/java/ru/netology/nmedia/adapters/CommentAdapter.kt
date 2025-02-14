package ru.netology.nmedia.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import ru.netology.nmedia.dto.Comment
//import android.view.View
//import android.widget.PopupMenu
//import androidx.core.view.isVisible
//import ru.netology.nmedia.dto.AttachmentType
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nmedia.R
import ru.netology.nmedia.auxiliary.FloatingValue.renameUrl
import ru.netology.nmedia.auxiliary.NumberTranslator
import ru.netology.nmedia.databinding.FragmentCardCommentsBinding
import java.text.SimpleDateFormat
import java.util.*

interface OnInteractionListenerComment {
    fun onLike(comment: Comment) {}
}

class CommentAdapter(
    private val onInteractionListener: OnInteractionListenerComment
) : ListAdapter<Comment, CommentViewHolder>(CommentDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding =
            FragmentCardCommentsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding, onInteractionListener)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = getItem(position)
        holder.renderingPostStructure(comment)
    }
}

class CommentDiffCallback : DiffUtil.ItemCallback<Comment>() {
    override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
        return oldItem == newItem
    }
}

class CommentViewHolder(
    private val binding: FragmentCardCommentsBinding,
    private val onInteractionListener: OnInteractionListenerComment
) : RecyclerView.ViewHolder(binding.root) {

    fun renderingPostStructure(comment: Comment) {
        with(binding) {
            title.text = comment.author
            datePublished.text = SimpleDateFormat("HH:mm:ss dd.MM.yyyy", Locale.ROOT)
                .format(Date(comment.published.toLong() * 1000))
            content.text = comment.content
            like.text = NumberTranslator.translateNumber(comment.likes)
            like.isChecked = comment.likedByMe
            Glide.with(avatar)
                .load(renameUrl(comment.authorAvatar ?: "", "avatars"))
                .placeholder(R.drawable.ic_image_not_supported_24)
                .error(R.drawable.ic_not_avatars_24)
                .circleCrop()
                .timeout(10_000)
                .into(avatar)
            postListeners(comment)
        }
    }

    private fun postListeners(comment: Comment) {
        with(binding) {
            like.setOnClickListener {
                like.isChecked = !like.isChecked
                onInteractionListener.onLike(comment)
            }
        }
    }
}