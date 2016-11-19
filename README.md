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
<code>GET</code>
<code>keine Parameter notwendig</code>

###Experience
####experience/create
Erstellt eine neue Experience.
<code>POST</code>

<code>	
	Params: 
	
	lat, //position
	lon, //position
	tex, //text
	sender, //user id des senders
	recipients, //komma-getrennte user ids sämtlicher empfänger
	expectedEmotions //erwartete Emotion (JSON serialisiert)
</code>

####experience/public/create
Erstellt eine neue öffentliche Experience.
<code>POST</code>

<code>	
	Params: 
	
	lat, //position
	lon, //position
	visibilityDuration, //dauer der sichtbarkeit
	tex, //text
	sender, //user id des senders
	expectedEmotions //erwartete Emotion (JSON serialisiert)
</code>

###experience/list
Liste aller Experiences in der DB.
<code>POST</code>

<code>	
	Params: 
	
	lat, //position
	lon, //position
</code>

###experience/public/list
Liste aller öffentlichen Experiences in der DB.
<code>POST</code>

<code>	
	Params: 
	
	lat, //position
	lon, //position
</code>

##Emotion
###emotion/create
Erstellt eine neue Emotion als eine Art Reaktion bzw. "gelesen" einer Experience.
<code>POST</code>

<code>	
	Params: 
	
	userExperienceId, //User-Experience Id
	emotion, //emotion beim betrachten der experience (JSON serialisiert)
</code>
