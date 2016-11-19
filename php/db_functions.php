<?php
include('db_base_functions.php');

/**
 * Returns all public experiences.
 * @return mixed
 */
function dbGetPublicExperiences() {
	$params = include('_params.php');

	$db = new Db();
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);

	$radius = $params['radius'];

	$query = "SELECT *," . dbDistanceFunction($lat,$lon) . " FROM experience 
	WHERE is_public = 1 HAVING distance < $radius ORDER BY distance";

	$rows = $db->select($query);
	return $rows;
}

/**
 * Returns all private experiences.
 * @return mixed
 */
function dbGetExperiences() {
	$params = include('_params.php');

	$db = new Db();
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);

	$radius = $params['radius'];

	$query = "SELECT *," . dbDistanceFunction($lat,$lon) . " FROM experience 
	WHERE is_public = 0 HAVING distance < $radius ORDER BY distance";
	$rows = $db->select($query);
	return $rows;
}

/**
 * Returns the experience object by the passed id
 * @param integer $id experience-id
 * @return mixed
 */
function dbGetExperienceById($id) {
	$db = new Db();
	$rows = $db->select('SELECT * FROM experience where id = ' . $id);
	return $rows;
}

/**
 * Creates a new experience in remote database and returns the inserted object.
 * @param bool $isPublic
 * @return bool validation
 */
function dbCreateExperience($isPublic=false) {
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
		$errorMessage[] = 'Recipient(s) missing. Please define at least one receipient per non-public experience';
		$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
	}

	//reaction
	$expectedEmotionValues = $_POST['expectedEmotion'];

	//create emotion
	if (!$db->query("INSERT INTO `experience` (`lat`,`lon`,`is_public`,`created_at`,`visibility_duration`,`text`) VALUES (" . $lat . "," . $lon . "," . $isPublic . ",NOW()," . $visibilityDuration . "," . $text . ")")) {
		$validation = false;
		$errorMessage[] = 'Could not insert new experience';
		$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
	} else {
		$experienceId = $db->lastId();
	}

	//iterate all receipients
	if ($isPublic == 0) {
		foreach ($recipients as $recipient) {
			if (!dbCreateUserExperience($recipient, $experienceId, false, $db)) {
				$validation = false;
				$errorMessage[] = 'Could not create new user-emotion with experienceId ' . $experienceId . ' userid ' . $recipient;
				$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
			}
		}
	}

	//create user and expected reaction
	if (!dbCreateUserExperience($sender, $experienceId, true, $db)) {
		$validation = false;
		$errorMessage[] = 'Could not create new sender-user-experience with experienceId ' . $experienceId . ' sender ' . $sender;
	} else {
		$senderUserExperienceId = $db->lastId();
		if (!dbCreateEmotion($senderUserExperienceId, $expectedEmotionValues, false, $db)) {
			$validation = false;
			$errorMessage[] = 'Could not create new emotion from sender';
			$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
		}
	}

	if ($validation) {
		$db->connect()->commit();
		return dbGetExperienceById($experienceId);
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
 * Creates a user-experience entry in the database.
 * @param integer $userId
 * @param integer $experienceId
 * @param bool $isSender
 * @param Db $db
 * @return mixed
 */
function dbCreateUserExperience($userId, $experienceId, $isSender=false, $db) {
	if ($db == null) $db = new Db();
	$experienceId = $db->escape($experienceId);
	$userId = $db->escape($userId);
	$result = $db->query("INSERT INTO `user_experience` (`user_id`,`experience_id`,`is_sender`) VALUES (" . $userId . "," . $experienceId . "," . ($isSender ? 1 : 0) . ");");
	return $result;
}

/**
 * Creates a new emotion.
 * @param integer $userExperienceId
 * @param mixed $emotionValues
 * @param bool $isEmpty
 * @param Db $db
 * @return mixed
 */
function dbCreateEmotion($userExperienceId, $emotionValues, $isEmpty = false, $db=null) {
	if ($db == null) $db = new Db();
	$emotionValues = json_decode($emotionValues);

	$query = "INSERT INTO `emotion` (`user_experience_id`,`is_empty`,`anger`,`contempt`,`disgust`,`fear`,`happiness`,`neutral`,`sadness`,`surprise`) VALUES 
		(" . $userExperienceId . "," . ($isEmpty ? 1 : 0) . "," . $emotionValues->anger . "," . $emotionValues->contempt . "," .
		$emotionValues->disgust . "," . $emotionValues->fear . ','. $emotionValues->happiness . ',' .
		$emotionValues->neutral .',' . $emotionValues->sadness . ','. $emotionValues->surprise . ");";

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
