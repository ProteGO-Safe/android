package pl.gov.mc.protegosafe.domain.usecase

import io.reactivex.Single
import pl.gov.mc.protegosafe.domain.repository.NotificationRepository

class GetNotificationDataAndClearUseCase(
    private val notificationRepository: NotificationRepository
) {

    fun execute(): Single<String> {
        return Single.fromCallable {
            val data = notificationRepository.getLatestNotificationData()
            notificationRepository.saveNotificationData("")
            data
        }
    }
}
