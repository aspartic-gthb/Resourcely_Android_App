package com.studyorganizer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studyorganizer.databinding.ItemTimetableBinding

class TimetableAdapter(
    private var list: List<Timetable>,
    private val onDelete: (Timetable) -> Unit
) : RecyclerView.Adapter<TimetableAdapter.ViewHolder>() {

    fun updateData(newList: List<Timetable>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimetableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class ViewHolder(private val binding: ItemTimetableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Timetable) {
            binding.tvStartTime.text = item.startTime
            binding.tvEndTime.text = item.endTime
            binding.tvSubject.text = item.subject
            binding.tvType.text = item.type
            binding.btnDelete.setOnClickListener { onDelete(item) }
        }
    }
}
