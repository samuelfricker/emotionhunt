#Emotion Hunt - Backend API
Dieses PHP-Projekt ist Bestandteil des Emotion-Hunt Projekts.
Hierbei handelt es sich um die API für die Emotion-Hunt-App. 

Zur Installation wird PHP 5.6+ und MySQL benötigt.

Autoren: Dimitri Suter, Benjamin Bur


##1. Installation
Lade das Projekt vom GIT-Repository via

```
git clone
```

* Initialisiere die Datenbank mit dem aktuellsten .sql-File unterhalb des /db-Ordners.
* Damit jeder Entwickler seine eigenen DB-Configs verwenden kann, wird die _config.php Datei pro Entwickler
selbst definiert. Kopiere hierfür die Datei `_config.php` und benenne die Kopie `_myconfig.php`.
Passe nun die neue Datei mit deinen eigenen Parametern an.

##2. Abfragen
Host: www.emotionhunt.com/api/?action=my/action
(Beispiel: www.emotionhunt.com/api/?action=avatar&id=1.....)

__WICHTIG:__ Für sämtliche Abfragen wird der `apiKey` entweder als POST oder GET Parameter mit folgendem Inhalt benötigt:
```
apiKey: bENFnP63CqNFDSucAFguwj7p685Z2eh3
```
Ein Aufruf könnte beispielsweise wiefolgt aussehen:
```
http://emotionhunt.com/api/?action=avatar&id=2&apiKey=bENFnP63CqNFDSucAFguwj7p685Z2eh3
```

##Avatar
###avatar/create
Erstellt einen neuen Avatar für einen User.

__WICHTIG:__ Content-Type: multipart/form-data
```
Params: 
media,       // File-Upload
androidId,   // android device id
```
###avatar
Gibt einen Avatar eines Users als Bild zurück.
```
Params: (required either 'user' or 'id')
user, // android device id
id    // user id
```

##User
###user/list
Liste aller User in der DB.

##Experience
###experience/create
Erstellt eine neue Experience.

__WICHTIG:__ Content-Type: multipart/form-data
```
Params: 

lat, //position
lon, //position
tex, //text
androidId, //android device id
recipients, //komma-getrennte user ids sämtlicher empfänger
expectedEmotions, //erwartete Emotion (JSON serialisiert)
media // File-Upload

Example-Call: experience/create
Post-Data (raw): lat=8.00&lon=43.00&text=blabla&visibilityDuration=24&recipients=2&sender=1&expectedEmotion={"anger":0.99,"contempt":0.0,"disgust":0.0,"fear":0.0,"happiness":0.0,"neutral":0.0,"sadness":0.0,"surprise":0.0}
```
###experience/public/create
Erstellt eine neue öffentliche Experience.

__WICHTIG:__ Content-Type: multipart/form-data
```
Params: 

lat, //position
lon, //position
visibilityDuration, //dauer der sichtbarkeit
tex, //text
androidId, //android device id
expectedEmotions, //erwartete Emotion (JSON serialisiert)
media // File-Upload
```
###experience/list
Liste aller Experiences in der DB.
```
Params: 

lat, //position
lon, //position
imei, //the user's device IMEI
```
###experience/media
Lädt ein Foto einer Experience.
```
Params: 

media //filename for download (e.g. media=e81732458bda9a61300cd63162686b5cdb80b2c6.png)
```
###experience/reactions
Liefert sämtliche Reaktionen zu einer Experience.
```
Params: 

id //experience id
```
###experience/reaction/create
Erstellt eine Reaktion und user experience auf eine öffentlichen Experience.
```
Params: 

androidId // device id
id        // experience id
emotion   // json serialized emotion
```
##Emotion
###emotion/create
Erstellt eine neue Emotion auf eine bestehende user-experience.
```
Params: 

userExperienceId, //User-Experience Id
emotion, //emotion beim betrachten der experience (JSON serialisiert)
```
