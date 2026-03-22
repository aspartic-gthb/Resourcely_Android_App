package com.studyorganizer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.studyorganizer.databinding.ItemAttendanceBinding

class AttendanceAdapter(
    private var list: List<Attendance>,
    private val onUpdate: (Attendance) -> Unit,
    private val onDelete: (Attendance) -> Unit
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    fun updateData(newList: List<Attendance>) {
        list = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int = list.size

    inner class AttendanceViewHolder(private val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            binding.tvSubject.text = attendance.subject
            binding.tvPercentage.text = String.format("%.1f%%", attendance.percentage)
            binding.tvStats.text = "${attendance.attendedClasses} / ${attendance.totalClasses}"
            binding.tvStatus.text = attendance.getStatusMessage()

            binding.progressAttendance.progress = attendance.percentage.toInt()

            if (attendance.percentage < 75f) {
                binding.tvStatus.setBackgroundColor(0xFFFFEBEE.toInt())
                binding.tvStatus.setTextColor(0xFFD32F2F.toInt())
                binding.progressAttendance.setIndicatorColor(0xFFD32F2F.toInt())
            } else {
                binding.tvStatus.setBackgroundColor(0xFFE8F5E9.toInt())
                binding.tvStatus.setTextColor(0xFF388E3C.toInt())
                binding.progressAttendance.setIndicatorColor(0xFF388E3C.toInt())
            }

            binding.btnPlus.setOnClickListener {
                val updated = attendance.copy(
                    totalClasses = attendance.totalClasses + 1,
                    attendedClasses = attendance.attendedClasses + 1
                )
                onUpdate(updated)
            }

            binding.btnMinus.setOnClickListener {
                val updated = attendance.copy(
                    totalClasses = attendance.totalClasses + 1,
                    attendedClasses = attendance.attendedClasses
                )
                onUpdate(updated)
            }

            binding.btnDelete.setOnClickListener {
                onDelete(attendance)
            }
        }
    }
}
