package com.studyorganizer

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            // ── Tags ──────────────────────────────────────────────────────────
            if (res.tags.isNotBlank()) {
                b.tvTags.text       = res.tags.split(",").joinToString("  ") { "#${it.trim()}" }
                b.tvTags.visibility = View.VISIBLE
            } else {
                b.tvTags.visibility = View.GONE
            }

            // ── Star icon ────────────────────────────────────────────────────
            b.btnStar.setImageResource(
                if (res.isImportant) R.drawable.ic_star_filled else R.drawable.ic_star_outline
            )

            // ── Favicon via Google's public favicon service ──────────────────
            val host = try { Uri.parse(res.link).host ?: "" } catch (e: Exception) { "" }
            if (host.isNotEmpty()) {
                Glide.with(b.root.context)
                    .load("https://www.google.com/s2/favicons?domain=$host&sz=64")
                    .placeholder(R.drawable.ic_link)
                    .error(R.drawable.ic_link)
                    .into(b.imgFavicon)
            } else {
                b.imgFavicon.setImageResource(R.drawable.ic_link)
            }

            // ── Click whole card → open link ─────────────────────────────────
            b.root.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(res.link))
                b.root.context.startActivity(intent)
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
