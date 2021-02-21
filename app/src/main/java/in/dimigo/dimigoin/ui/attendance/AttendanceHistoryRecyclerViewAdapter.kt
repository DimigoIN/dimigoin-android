package `in`.dimigo.dimigoin.ui.attendance

import `in`.dimigo.dimigoin.R
import `in`.dimigo.dimigoin.data.model.AttendanceLogModel
import `in`.dimigo.dimigoin.databinding.ItemHistoryBinding
import `in`.dimigo.dimigoin.ui.custom.CustomAccentSpan
import `in`.dimigo.dimigoin.ui.main.fragment.main.AttendanceLocation
import android.text.Spannable
import android.text.Spanned
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.toSpannable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView

class AttendanceHistoryRecyclerViewAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<AttendanceLogModel> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemHistoryBinding =
            DataBindingUtil.inflate(inflater, R.layout.item_history, parent, false)

        return AttendanceHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as AttendanceHistoryViewHolder).bind(items[position])
    }

    override fun getItemCount() = items.size

    fun setItem(items: List<AttendanceLogModel>) {
        this.items = items
        notifyDataSetChanged()
    }
}

private class AttendanceHistoryViewHolder(private val binding: ItemHistoryBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: AttendanceLogModel) {
        val context = binding.root.context
        val location = AttendanceLocation.fromPlace(item.place).label
        val updatedBy = item.updatedBy?.name ?: item.student.name
        val updatedPerson = if (item.updatedBy == null) "자신" else item.student.name + "님"
        val format = context.getString(R.string.attendance_log_format)
        val spannable = format.format(item.date, updatedBy, updatedPerson, location).toSpannable()

        spannable[location] = CustomAccentSpan(
            ResourcesCompat.getFont(context, R.font.bold),
            R.color.pink_400
        )

        binding.messageText.text = spannable
    }
}

operator fun Spannable.set(text: String, span: Any) {
    val first = this.indexOf(text)
    val last = first + text.length

    setSpan(span, first, last, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
}