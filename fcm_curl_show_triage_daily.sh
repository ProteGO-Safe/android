#command to send sample fcm notification with triage reminder
curl -X POST --header "Authorization: key=AAAAbK64j8I:APA91bHdFzSaBpAlzYDUvxQU_cgqFqd7U2lncyzCCZFs6_bKRcyt2OJuWn9RI1fA6S1KhbBfICof3AJLmfc2zv9HuOaRhZMYFNDtM9qyqHExcT-ahuAkMYEZSOVRh3OZJjDN-n4DXHdt"     --Header "Content-Type: application/json"     https://fcm.googleapis.com/fcm/send     -d '{"to":"/topics/daily",
  "data": {
    "title" : "Triage reminder",
    "content" : "Test notification content",
   }
}'