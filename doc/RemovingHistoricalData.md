# Removing historical data

User can clear historical data collected by Exposure Notification Framework and the app.

Steps:
- PWA requests **REQUEST_CLEAR_EXPOSURE_NOTIFICATIONS_DATA**.
- Clear local database calling **ExposureRepository.nukeDb()**.
- Redirect user to Exposure Notification Framework settings page, where can clear data collected by Exposure Notification Framework.