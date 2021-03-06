package `in`.dimigo.dimigoin.data.usecase.config

import `in`.dimigo.dimigoin.data.model.ConfigModel
import `in`.dimigo.dimigoin.data.model.MealTimesModel
import `in`.dimigo.dimigoin.data.model.UserModel
import `in`.dimigo.dimigoin.data.service.DimigoinService
import `in`.dimigo.dimigoin.data.util.Result
import `in`.dimigo.dimigoin.data.util.UserDataStore
import `in`.dimigo.dimigoin.data.util.safeApiCall
import retrofit2.await
import java.time.LocalDateTime

class ConfigUseCaseImpl(
    val service: DimigoinService,
    userDataStore: UserDataStore
) : ConfigUseCase {
    private val userData: UserModel = userDataStore.requireUserData()
    private var cachedConfig: ConfigModel? = null

    /**
     * AFSC: AfterSchool
     * NSS: NightSelfStudy
     * @return "AFSC${time}", "NSS1${time}"
     */
    override suspend fun getCurrentTimeCode(): Result<String> = safeApiCall {
        val grade =
            if (userData.isStudent()) userData.grade
            else 1
        val timeEndMinutes = getConfig()
            .SELF_STUDY_TIMES[grade]
            .mapValues {
                it.value.end.hour * 60 + it.value.end.minute
            }
            .toList()
            .sortedBy { (_, minutes) -> minutes }

        val now = LocalDateTime.now()
        val currentMinutes = now.hour * 60 + now.minute

        val currentTime = timeEndMinutes.find { (_, endMinutes) ->
            currentMinutes <= endMinutes
        } ?: timeEndMinutes.last()
        return@safeApiCall currentTime.first
    }

    override suspend fun getMealTimes(): Result<MealTimesModel> {
        return safeApiCall {
            getConfig().MEAL_TIMES[userData.grade]
        }
    }

    private suspend fun getConfig(): ConfigModel {
        return cachedConfig ?: run {
            val config = service.getConfigs().await().config
            cachedConfig = config
            return@run config
        }
    }
}
