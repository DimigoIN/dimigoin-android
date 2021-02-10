package `in`.dimigo.dimigoin.ui.item

import `in`.dimigo.dimigoin.data.model.IngangApplicationModel
import `in`.dimigo.dimigoin.data.model.IngangTime

data class IngangStatusItem(
    val weeklyTicketCount: Int,
    val weeklyUsedTicket: Int,
    val weeklyRemainTicket: Int,
    val ingangMaxApplier: Int,
    private val time1Applied: Boolean,
    private val time2Applied: Boolean,
    private val time1Applications: List<IngangApplicationModel>,
    private val time2Applications: List<IngangApplicationModel>
) {
    fun isApplied(time: IngangTime) =
        if (time == IngangTime.NSS1) time1Applied
        else time2Applied

    fun getApplications(time: IngangTime) =
        if (time == IngangTime.NSS1) time1Applications
        else time2Applications
}
