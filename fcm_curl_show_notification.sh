#command to send sample fcm notification and show notification with title and content
curl -X POST --header "Authorization: key=AAAAbK64j8I:APA91bHdFzSaBpAlzYDUvxQU_cgqFqd7U2lncyzCCZFs6_bKRcyt2OJuWn9RI1fA6S1KhbBfICof3AJLmfc2zv9HuOaRhZMYFNDtM9qyqHExcT-ahuAkMYEZSOVRh3OZJjDN-n4DXHdt"     --Header "Content-Type: application/json"     https://fcm.googleapis.com/fcm/send     -d '{"to":"/topics/general",
  "data": {
    "title" : "Test notification title",
    "content" : "Test notification content"
   }
}'
