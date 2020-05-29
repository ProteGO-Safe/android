# Reporting Risk Level

Pass the highest risk level of user's exposure from last 14 days detected by Exposure Notification Framework to PWA.

Steps:

- PWA request reporting risk level
- Get all data about exposures from local database
- Filter exposure with the highest risk score from exposures
- Calc risk level from risk score(max **riskScore** = 4096):
  - **riskScore** < 1500 -> **NO_RISK**
  - **riskScore** < 3000 -> **MIDDLE_RISK**
  - else -> **HIGH_RISK**
- Pass calculated risk level to PWA