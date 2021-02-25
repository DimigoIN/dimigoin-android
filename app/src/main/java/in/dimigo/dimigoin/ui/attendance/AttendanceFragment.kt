package `in`.dimigo.dimigoin.ui.attendance

import `in`.dimigo.dimigoin.R
import `in`.dimigo.dimigoin.data.model.UserType
import `in`.dimigo.dimigoin.data.util.DateUtil
import `in`.dimigo.dimigoin.data.util.DateUtil.from
import `in`.dimigo.dimigoin.data.util.UserDataStore
import `in`.dimigo.dimigoin.databinding.DialogAttendanceDetailBinding
import `in`.dimigo.dimigoin.databinding.DialogHistoryBinding
import `in`.dimigo.dimigoin.databinding.FragmentAttendanceBinding
import `in`.dimigo.dimigoin.ui.custom.DimigoinDialog
import `in`.dimigo.dimigoin.ui.main.fragment.main.AttendanceLocation
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import org.koin.android.viewmodel.ext.android.viewModel

class AttendanceFragment : Fragment() {
    private val isTeacher = UserDataStore.userData.userType == UserType.TEACHER
    private val viewModel: AttendanceViewModel by viewModel()
    private val historyAdapter = AttendanceHistoryRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val attendanceAdapter = AttendanceRecyclerViewAdapter(if (isTeacher) viewModel else null)
        val binding = FragmentAttendanceBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerView.adapter = attendanceAdapter
            vm = viewModel
            attendanceTableLayout.attendanceTableRoot.clipToOutline = true
        }

        if (isTeacher) enterTeacherMode(binding)

        viewModel.attendanceData.observe(viewLifecycleOwner) {
            attendanceAdapter.setItem(it)
        }

        viewModel.query.observe(viewLifecycleOwner) {
            attendanceAdapter.filter(it)
        }

        return binding.root
    }

    private fun enterTeacherMode(binding: FragmentAttendanceBinding) {
        with(binding) {
            //init tab
            repeat(3) { gradeTap.addTab(gradeTap.newTab().setText("${it + 1}학년")) }
            repeat(6) { classTap.addTab(classTap.newTab().setText("${it + 1}반")) }
            gradeTap.addOnTabSelected { onTabSelected(this) }
            classTap.addOnTabSelected { onTabSelected(this) }

            //history dialog
            attendanceHistoryButton.setOnClickListener {
                viewModel.attendanceLogs.value?.let { data -> historyAdapter.setItem(data) }

                val dialogBinding = DialogHistoryBinding.inflate(layoutInflater).apply {
                    historyRecyclerView.adapter = historyAdapter
                }

                DimigoinDialog(requireContext(), useNarrowDialog = true).CustomView(dialogBinding.root).show()
            }
        }

        //attendance detail dialog
        viewModel.attendanceDetail.observe(viewLifecycleOwner) {
            it.logs?.let { logs -> historyAdapter.setItem(logs) }

            val updatedAt: String =
                if (!it.logs.isNullOrEmpty()) DateUtil.timeFormatter.from(it.logs[0].time)
                else requireContext().getString(R.string.no_info)
            val location: AttendanceLocation =
                it.logs?.let { logs -> AttendanceLocation.fromPlace(logs[0].place) }
                    ?: AttendanceLocation.Class
            val placeName: String =
                it.logs?.let { logs -> logs[0].place.name } ?: it.student.getDefaultClassName(requireContext())
            val dialogBinding = DialogAttendanceDetailBinding.inflate(layoutInflater).apply {
                historyRecyclerView.adapter = historyAdapter
                this.location = location
                this.student = it.student
                this.updatedAt = updatedAt
                this.placeName = placeName
                this.studentInfo = requireContext().getString(R.string.format_student_info)
                    .format(it.student.grade, it.student.klass, it.student.number)
            }

            DimigoinDialog(requireContext(), useNarrowDialog = true).CustomView(dialogBinding.root).show()
        }
    }

    private fun onTabSelected(binding: FragmentAttendanceBinding) {
        viewModel.grade.value = binding.gradeTap.selectedTabPosition + 1
        viewModel.klass.value = binding.classTap.selectedTabPosition + 1
        viewModel.refresh()
    }

    private fun TabLayout.addOnTabSelected(onSelected: (tab: TabLayout.Tab?) -> Unit): TabLayout.OnTabSelectedListener {
        val listener = object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                onSelected(tab)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        }
        this.addOnTabSelectedListener(listener)
        return listener
    }
}
