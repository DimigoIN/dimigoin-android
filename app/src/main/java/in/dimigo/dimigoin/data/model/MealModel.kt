package `in`.dimigo.dimigoin.data.model

import `in`.dimigo.dimigoin.ui.item.MealItem
import com.squareup.moshi.JsonClass

private typealias MealList = List<String>

@JsonClass(generateAdapter = true)
data class WeeklyMealResponseModel(
    val meals: List<MealModel>
)

@JsonClass(generateAdapter = true)
data class MealResponseModel(
    val meal: MealModel
) {
    fun toMealItem(failedMealItem: MealItem) = meal.toMealItem(failedMealItem)
}

@JsonClass(generateAdapter = true)
data class MealModel(
    val breakfast: MealList,
    val lunch: MealList,
    val dinner: MealList,
    val date: String
) {
    fun toMealItem(failedMealItem: MealItem) = MealItem(
        this.breakfast.stringify(failedMealItem.breakfast),
        this.lunch.stringify(failedMealItem.lunch),
        this.dinner.stringify(failedMealItem.dinner)
    )
}

private fun MealList.stringify(failedMealString: String): String {
    return if (isNullOrEmpty()) failedMealString
    else joinToString(", ")
}
