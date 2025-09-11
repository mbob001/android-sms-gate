# android-sms-gate
SMS gateway using Android phone and REST API

The project includes two parts:
* Android project - application + service for sending and receiving SMS message
* Web API .Net Core project - REST API for push or get SMS

## How it's works
Use REST API method **SendSms** to push SMS message to server repository. SMS is stored to repository. Android application take SMS from repository and send to phone number. To gets received list SMS messages use **GetReceivedSms** periodically. This method returns list SMS stored in server repository. You can use swagger.

Next methods **GetSmsToSend** and **SaveReceivedSms** is only for Android application.

## Initial setup
Base REST API address is set for all available IP on current PC and port is 7279. Communaction between Android application and REST API is over https. To change port modify file [launchSettings.json](https://github.com/mbob001/android-sms-gate/blob/main/Server/SMS.WebAPI/Properties/launchSettings.json). After the change, don't forget to also change the address in BASE_URL in a file [APIComm.java](https://github.com/mbob001/android-sms-gate/blob/main/Android/app/src/main/java/cz/soft4you/smsgate/APIComm.java)

## Run projects
Run REST API, after run Android application. Try to use **SendSMS** by swagger to send message. If SMS message arrives on mobile phone, you can try call Web API method **GetReceivedSms**.

## Missing parts
* no authorization between Android application and REST API
* no authorization for sending and receiving SMS messages by REST API
* repository is only LocalRepository (in memory) - consider using a database (repository interface is prepared)
