#Emotion Hunt - Backend API
Dieses PHP-Projekt ist Bestandteil des Emotion-Hunt Projekts.
Hierbei handelt es sich um die API für die Emotion-Hunt-App. 

Zur Installation wird PHP 5.6+ und MySQL benötigt.

Autoren:
+ Dimitri Suter
+ Benjamin Bur


##1. Installation
+ Projekt laden
Lade das Projekt vom GIT-Repository via
<code>git clone</code>
+ Initialisiere die Datenbank mit dem aktuellsten .sql-File unterhalb des /db-Ordners.
+ Damit jeder Entwickler seine eigenen DB-Configs verwenden kann, wird die _config.php Datei pro Entwickler
selbst definiert. Kopiere hierfür die Datei <code>_config.php</code> und benenne die Kopie <code>_myconfig.php</code>.
Passe nun die neue Datei mit deinen eigenen Parametern an.

##2. Abfragen
###User
####user/list
Liste aller User in der DB.

###Experience
####experience/create
Erstellt eine neue Experience.<br/>
<b>WICHTIG:</b> Content-Type: multipart/form-data

	Params: 
	
	lat, //position
	lon, //position
	tex, //text
	sender, //user id des senders
	recipients, //komma-getrennte user ids sämtlicher empfänger
	expectedEmotions, //erwartete Emotion (JSON serialisiert)
	media // File-Upload
	
	Example-Call: experience/create
	Post-Data (raw): lat=8.00&lon=43.00&text=blabla&visibilityDuration=24&recipients=2&sender=1&expectedEmotion={"anger":0.99,"contempt":0.0,"disgust":0.0,"fear":0.0,"happiness":0.0,"neutral":0.0,"sadness":0.0,"surprise":0.0}

####experience/public/create
Erstellt eine neue öffentliche Experience.<br/>
<b>WICHTIG:</b> Content-Type: multipart/form-data

	Params: 
	
	lat, //position
	lon, //position
	visibilityDuration, //dauer der sichtbarkeit
	tex, //text
	sender, //user id des senders
	expectedEmotions, //erwartete Emotion (JSON serialisiert)
	media // File-Upload

####experience/list
Liste aller Experiences in der DB.

	Params: 
	
	lat, //position
	lon, //position

####experience/media
Lädt ein Foto einer Experience.

	Params: 
	
	media //filename for download (e.g. media=e81732458bda9a61300cd63162686b5cdb80b2c6.png)

####experience/public/list
Liste aller öffentlichen Experiences in der DB.

	Params: 
	
	lat, //position
	lon, //position

###Emotion
####emotion/create
Erstellt eine neue Emotion als eine Art Reaktion bzw. "gelesen" einer Experience.

	Params: 
	
	userExperienceId, //User-Experience Id
	emotion, //emotion beim betrachten der experience (JSON serialisiert)
