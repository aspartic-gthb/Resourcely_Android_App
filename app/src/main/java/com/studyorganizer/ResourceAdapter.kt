package com.studyorganizer

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.studyorganizer.databinding.ItemResourceBinding

class ResourceAdapter(
    private val onEdit:           (Resource) -> Unit,
    private val onDelete:         (Resource) -> Unit,
    private val onToggleImportant:(Resource) -> Unit
) : ListAdapter<Resource, ResourceAdapter.VH>(Diff()) {

    inner class VH(private val b: ItemResourceBinding) : RecyclerView.ViewHolder(b.root) {

        fun bind(res: Resource) {
            b.tvTitle.text    = res.title
            b.tvCategory.text = res.category
            b.tvLink.text     = res.link

            // ── Icon and Background Color ────────────────────────────────────
            val context = b.root.context
            val (iconRes, bgColor) = when (res.type) {
                ResourceType.DRIVE -> R.drawable.ic_drive to R.color.maths_bg
                ResourceType.LOCAL -> R.drawable.ic_file to R.color.physics_bg
                else -> R.drawable.ic_link to R.color.chemistry_bg
            }
            
            b.cardIconBg.setCardBackgroundColor(ContextCompat.getColor(context, bgColor))
            
            if (res.type == ResourceType.LINK) {
                val host = try { Uri.parse(res.link).host ?: "" } catch (e: Exception) { "" }
                if (host.isNotEmpty()) {
                    Glide.with(context)
                        .load("https://www.google.com/s2/favicons?domain=$host&sz=64")
                        .placeholder(iconRes)
                        .error(iconRes)
                        .into(b.imgFavicon)
                } else {
                    b.imgFavicon.setImageResource(iconRes)
                }
            } else {
                b.imgFavicon.setImageResource(iconRes)
                b.imgFavicon.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary))
            }

            // ── Star icon ────────────────────────────────────────────────────
            b.btnStar.setImageResource(
                if (res.isImportant) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            // ── Interaction ──────────────────────────────────────────────────
            b.root.setOnClickListener {
                try {
                    val intent = if (res.type == ResourceType.LOCAL) {
                        // For local files, we use a simple view intent
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(Uri.parse(res.link), "*/*")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                    } else {
                        Intent(Intent.ACTION_VIEW, Uri.parse(res.link))
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    // Handle error
                }
            }

            b.btnStar.setOnClickListener   { onToggleImportant(res) }
            b.btnEdit.setOnClickListener   { onEdit(res) }
            b.btnDelete.setOnClickListener { onDelete(res) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemResourceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class Diff : DiffUtil.ItemCallback<Resource>() {
        override fun areItemsTheSame(old: Resource, new: Resource) = old.id == new.id
        override fun areContentsTheSame(old: Resource, new: Resource) = old == new
    }
}
