package `in`.dimigo.dimigoin.data.usecase.timetable

import `in`.dimigo.dimigoin.data.service.DimigoinService
import `in`.dimigo.dimigoin.data.util.Result
import `in`.dimigo.dimigoin.data.util.UserDataStore
import `in`.dimigo.dimigoin.data.util.safeApiCall
import `in`.dimigo.dimigoin.ui.item.SubjectItem
import retrofit2.await
import java.time.DayOfWeek

class TimetableUseCaseImpl(
    private val service: DimigoinService,
    private val userDataStore: UserDataStore
) : TimetableUseCase {

    override suspend fun getWeeklyTimetable(): Result<List<SubjectItem?>> = safeApiCall {
        val grade = userDataStore.userData?.grade ?: 0
        val klass = userDataStore.userData?.klass ?: 0
        val timetables = service.getWeeklyTimetable(grade, klass)
            .await()
            .dailyTimetables
        val subjects = mutableListOf<SubjectItem?>()

        repeat(7) { i ->
            repeat(5) { j ->
                if (timetables[j].sequence.size > i)
                    subjects.add(SubjectItem(timetables[j].sequence[i], DayOfWeek.of(j + 1)))
                else
                    subjects.add(null)
            }
        }

        return@safeApiCall subjects
    }
}
