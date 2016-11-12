<?php
include('db_base_functions.php');

/**
 * Returns all public emotions.
 * @return mixed
 */
function dbGetPublicEmotions() {
	$params = include('_params.php');

	$db = new Db();
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);

	$radius = $params['radius'];

	$query = "SELECT *," . dbDistanceFunction($lat,$lon) . " FROM emotion 
	WHERE is_public = 1 HAVING distance < $radius ORDER BY distance";

	$rows = $db->select($query);
	return $rows;
}

/**
 * Returns all private emotions.
 * @return mixed
 */
function dbGetEmotions() {
	$params = include('_params.php');

	$db = new Db();
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);

	$radius = $params['radius'];

	$query = "SELECT *," . dbDistanceFunction($lat,$lon) . " FROM emotion 
	WHERE is_public = 0 HAVING distance < $radius ORDER BY distance";
	$rows = $db->select($query);
	return $rows;
}

/**
 * Returns the emotion object by the passed id
 * @param integer $id emotion-id
 * @return mixed
 */
function dbGetEmotionById($id) {
	$db = new Db();
	$rows = $db->select('SELECT * FROM emotion where id = ' . $id);
	return $rows;
}

/**
 * Creates a new emotion in remote database and returns the inserted object.
 * @param bool $isPublic
 * @return bool validation
 */
function dbCreateEmotion($isPublic=false) {
	$db = new Db();
	$db->connect()->autocommit(false);

	$validation = true;
	$errorMessage = [];
	$errorTexts = [];

	//attributes from emotion object
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);

	$visibilityDuration = null;
	if ($db->escape($_POST['visibilityDuration'])) {
		$visibilityDuration = $db->escape($_POST['visibilityDuration']);
	}
	$text = $db->quote($_POST['text']);

	//attributes from related object (e.g. user)
	$sender = $db->escape($_POST['sender']);
	$recipients = $isPublic == 0 ? explode(',', $db->escape($_POST['recipients'])) : [];

	if ($isPublic == 0 && count($recipients) == 0) {
		$validation = false;
		$errorMessage[] = 'Recipient(s) missing. Please define at least one receipient per non-public emotion';
		$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
	}

	//reaction
	$expectedReactionValues = $_POST['expectedReaction'];

	//create emotion
	if (!$db->query("INSERT INTO `emotion` (`lat`,`lon`,`is_public`,`created_at`,`visibility_duration`,`text`) VALUES (" . $lat . "," . $lon . "," . $isPublic . ",NOW()," . $visibilityDuration . "," . $text . ")")) {
		$validation = false;
		$errorMessage[] = 'Could not insert new emotion';
		$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
	} else {
		$emotionId = $db->lastId();
	}

	//iterate all receipients
	if ($isPublic == 0) {
		foreach ($recipients as $recipient) {
			if (!dbCreateUserEmotion($recipient, $emotionId, false, $db)) {
				$validation = false;
				$errorMessage[] = 'Could not create new user-emotion with emotionId ' . $emotionId . ' userid ' . $recipient;
				$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
			}
		}
	}

	//create user and expected reaction
	if (!dbCreateUserEmotion($sender, $emotionId, true, $db)) {
		$validation = false;
		$errorMessage[] = 'Could not create new sender-user-emotion with emotionId ' . $emotionId . ' sender ' . $sender;
	} else {
		$senderUserEmotionId = $db->lastId();
		if (!dbCreateReaction($senderUserEmotionId, $expectedReactionValues, false, $db)) {
			$validation = false;
			$errorMessage[] = 'Could not create new reaction from sender';
			$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
		}
	}

	if ($validation) {
		$db->connect()->commit();
		return dbGetEmotionById($emotionId);
	} else {
		throwDbException(join('. ', $errorMessage), join('. ', $errorTexts));
		$db->connect()->rollback();
		$db->connect()->autocommit(true);
	}
}

/**
 * Returns all users.
 * @return mixed
 */
function dbGetUsers() {
	$db = new Db();
	$rows = $db->select('SELECT * FROM user');
	return $rows;
}

/**
 * Creates a user-emotion entry in the database.
 * @param integer $userId
 * @param integer $emotionId
 * @param bool $isSender
 * @param Db $db
 * @return mixed
 */
function dbCreateUserEmotion($userId, $emotionId, $isSender=false, $db) {
	if ($db == null) $db = new Db();
	$emotionId = $db->escape($emotionId);
	$userId = $db->escape($userId);
	$result = $db->query("INSERT INTO `user_emotion` (`user_id`,`emotion_id`,`is_sender`) VALUES (" . $userId . "," . $emotionId . "," . ($isSender ? 1 : 0) . ");");
	return $result;
}

/**
 * Creates a new reaction.
 * @param integer $userEmotionId
 * @param mixed $reactionValues
 * @param bool $isEmpty
 * @param Db $db
 * @return mixed
 */
function dbCreateReaction($userEmotionId, $reactionValues, $isEmpty = false, $db=null) {
	if ($db == null) $db = new Db();
	$reactionValues = json_decode($reactionValues);

	$query = "INSERT INTO `reaction` (`user_emotion_id`,`is_empty`,`anger`,`contempt`,`disgust`,`fear`,`happiness`,`neutral`,`sadness`,`surprise`) VALUES 
(" . $userEmotionId . "," . ($isEmpty ? 1 : 0) . "," . $reactionValues->anger . "," . $reactionValues->contempt . "," . $reactionValues->disgust . "," . $reactionValues->fear . ','. $reactionValues->happiness . ',' . $reactionValues->neutral .',' . $reactionValues->sadness . ','. $reactionValues->surprise . ");";

	return $db->query($query);
}

/**
 * Throws a db-exception.
 * @param string $message
 * @param string $errorTexts
 */
function throwDbException($message, $errorTexts) {
	printResult(null,500,$message, $errorTexts);
	die();
}
