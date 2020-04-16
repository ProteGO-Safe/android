package se.sigmaconnectivity.blescanner.domain.model

enum class PushNotificationTopic(val topicName: String) {
    UNKNOWN(""),
    GENERAL("/topics/general"),
    DAILY("/topics/daily");

    companion object {
        fun of(value: String): PushNotificationTopic = PushNotificationTopic.values().find { it.topicName == value } ?: throw IllegalAccessException()
    }
}