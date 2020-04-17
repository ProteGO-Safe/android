package pl.gov.mc.protegosafe.domain.model

enum class PushNotificationTopic(val topicName: String) {
    UNKNOWN(""),
    MAIN("/topics/main"),
    DAILY("/topics/daily");

    companion object {
        fun of(value: String): PushNotificationTopic = PushNotificationTopic.values().find { it.topicName == value } ?: throw IllegalAccessException()
    }
}