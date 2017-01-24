<?php
include('db_base_functions.php');

/**
 * Returns the user row from db by a given android id
 * @return string[]
 */
function dbCreateUser() {
	$db = new Db();
	if (empty($_POST['androidId']) || empty($_POST['name']) || empty($_POST['phoneNumber'])) return [];

	$phoneNumber = $db->quote($_POST['phoneNumber']);
	$androidId = $db->quote($_POST['androidId']);
	$name = $db->quote($_POST['name']);

	$userId = null;
	if (!$db->query("INSERT INTO `user` (`phone_number`,`android_id`,`name`) VALUES (" . $phoneNumber . "," . $androidId . "," . $name . ")")) {
		$errorTexts[] = sprintf("Error message: %s", $db->connect()->error);
		throwDbException('Could not insert new user', join('. ', $errorTexts));
	}

	return dbGetUserByAndroidId();
}

/**
 * Returns the user row from db by a given android id
 * @return string[]
 */
function dbGetUserByAndroidId($androidId = null) {
	$db = new Db();

	if (isset($_POST['androidId'])) {
		$androidId = $db->quote($_POST['androidId']);
	} else {
		if (empty($androidId)) return [];
		$androidId = $db->quote($androidId);
	}

	$query = "SELECT * FROM `user` WHERE android_id = " . $androidId . " LIMIT 1";
	$rows = $db->select($query);

	return $rows;
}

/**
 * Returns the android id by a given user id
 * @param $id
 * @return null|string
 */
function dbGetAndroidIdByUserId($id) {
	$rows = dbGetUserById($id);
	$androidId = '';
	if (empty($rows)) {
		return null;
	} else {
		$androidId = $rows[0]['android_id'];
	}
	return $androidId;
}

/**
 * Returns the user row from db by a given android id
 * @return string[]
 */
function dbGetUserById($id) {
	$db = new Db();

	$query = "SELECT * FROM `user` WHERE id = " . $id . " LIMIT 1";
	$rows = $db->select($query);

	return $rows;
}

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

	$query = "SELECT DISTINCT(e.id), e.lat, e.lon, e.is_public, e.is_location_based, e.created_at, e.visibility_duration, e.text, e.filename," . dbDistanceFunction($lat,$lon) . ",
	(SELECT us.name FROM user_experience usex LEFT JOIN user us on us.id = usex.user_id WHERE usex.experience_id = e.id AND usex.is_sender = 1) as sender_name,
	(SELECT us.id FROM user_experience usex LEFT JOIN user us on us.id = usex.user_id WHERE usex.experience_id = e.id AND usex.is_sender = 1) as sender_id
	FROM experience e
	LEFT JOIN user_experience ue ON ue.experience_id = e.id
	LEFT JOIN user u ON u.id = ue.user_id
	WHERE e.is_public = 1 HAVING distance < $radius ORDER BY distance";

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
	if (empty($_POST['lat'])) throwDbException("Invalid Call","Missing lat param.");
	$lat = $db->escape($_POST['lat']);
	if (empty($_POST['lon'])) throwDbException("Invalid Call","Missing lon param.");
	$lon = $db->escape($_POST['lon']);
	if (empty($_POST['imei'])) throwDbException("Invalid Call","Missing imei param.");
	$imei = $db->escape($_POST['imei']);

	$radius = $params['radius'];

	$query = "SELECT DISTINCT(e.id), e.lat, e.lon, e.is_public, e.is_location_based, e.created_at, e.visibility_duration, e.text, e.filename," . dbDistanceFunction($lat,$lon) . ",
	(SELECT us.name FROM user_experience usex LEFT JOIN user us on us.id = usex.user_id WHERE usex.experience_id = e.id AND usex.is_sender = 1) as sender_name,
	(SELECT us.id FROM user_experience usex LEFT JOIN user us on us.id = usex.user_id WHERE usex.experience_id = e.id AND usex.is_sender = 1) as sender_id
	FROM experience e
	LEFT JOIN user_experience ue ON ue.experience_id = e.id
	LEFT JOIN user u ON u.id = ue.user_id
	WHERE e.is_public = 0 AND u.android_id = '" . $imei . "' AND ue.is_sender = 0" .
		" HAVING distance < $radius ORDER BY distance";
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
	$rows = $db->select('SELECT * FROM experience WHERE id = ' . $id);
	return $rows;
}

/**
 * Returns all reactions on an experience.
 * @param $id experience id
 * @return mixed
 */
function dbGetReactions($id) {
	$db = new Db();
	$query = 'SELECT e.*, ue.user_id, ue.is_sender, u.name, u.profile_picture FROM emotion e 
LEFT JOIN user_experience ue ON ue.id = e.user_experience_id
LEFT JOIN user u ON u.id = ue.user_id 
WHERE ue.experience_id = ' . $id;
	$rows = $db->select($query);
	return $rows;
}

/**
 * Creates a new experience in remote database and returns the inserted object.
 * @param bool $isPublic
 * @return bool validation
 */
function dbCreateExperience($isPublic=false) {
	//ensure that the media file is sent and is not corrupt and move file
	$filename = validateAndMoveMediaFile();

	/** @var Db $db */
	$db = new Db();
	$db->connect()->autocommit(false);

	$validation = true;
	$errorMessage = [];
	$errorTexts = [];

	//check if this experience is location based or not
	$isLocationBased = isset($_POST['isLocationBased']) && $_POST['isLocationBased'] == 1;

	//attributes from emotion object
	$lat = $db->escape($_POST['lat']);
	$lon = $db->escape($_POST['lon']);
	$filename = $db->escape($filename);

	$visibilityDuration = null;
	if ($db->escape($_POST['visibilityDuration'])) {
		$visibilityDuration = $db->escape($_POST['visibilityDuration']);
	}
	$text = $db->quote($_POST['text']);

	//attributes from related object (e.g. user)
	$sender = $db->escape($_POST['androidId']);
	$recipients = !$isPublic ? explode(',', $db->escape($_POST['recipients'])) : [];

	if (!$isPublic && count($recipients) == 0) {
		$validation = false;
		$errorMessage[] = 'Recipient(s) missing. Please define at least one recipient for a private experience';
		$errorTexts[] = sprintf("Error 1 message: %s", $db->connect()->error);
	}

	//reaction
	$expectedEmotionValues = $_POST['expectedEmotion'];

	$timestamp = time();

	//create emotion
	if (!$db->query("INSERT INTO `experience` (`lat`,`lon`,`is_public`,`is_location_based`,`created_at`,`visibility_duration`,`text`, `filename`) VALUES (" . $lat . "," . $lon . "," . ($isPublic ? 1 : 0) . ", " . ($isLocationBased ? 1 : 0) . ", " . $timestamp . "," . ($isPublic ? $visibilityDuration : "null"). "," . $text . ",'". $filename ."')")) {
		$validation = false;
		$errorMessage[] = 'Could not insert new experience';
		$errorTexts[] = sprintf("Error 2 message: %s", $db->connect()->error);
	} else {
		$experienceId = $db->lastId();
	}

	//iterate all receipients
	if (!$isPublic && count($recipients) > 0) {
		foreach ($recipients as $recipient) {
			if (!dbCreateUserExperience($recipient, $experienceId, false, $db)) {
				$validation = false;
				$errorMessage[] = 'Could not create new user-emotion with experienceId ' . $experienceId . ' userid ' . $recipient;
				$errorTexts[] = sprintf("Error 3 message: %s", $db->connect()->error);
			}
		}
	}

	$rows = dbGetUserByAndroidId($sender);
	if (empty($rows)) {
		$validation = false;
		$errorMessage[] = 'Sender not found.';
		$errorTexts[] = sprintf("Sender not found by android id: %s", $sender);
	} else {
		$sender = $rows[0]['id'];
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
			$errorTexts[] = sprintf("Error 4 message: %s", $db->connect()->error);
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
 * Create new emotion and user-experience entry if the passed experience is a public experience.
 * @return array|string
 */
function dbCreateExperienceReaction() {

	/** @var Db $db */
	$db = new Db();
	$db->connect()->autocommit(false);

	$validation = true;
	$errorMessage = [];
	$errorTexts = [];

	if (empty($_POST['androidId']) || empty($_POST['id'])) return [];

	$emotion = $_POST['emotion'];
	$sender = $db->quote($_POST['androidId']);
	$experienceId = $_POST['id'];

	//get user id
	$rows = dbGetUserByAndroidId($sender);
	$userId = null;
	if (empty($rows)) {
		$validation = false;
		$errorMessage[] = sprintf("User not found by android id: %s", $sender);
		$errorTexts[] = sprintf("Error 1 message: %s", $db->connect()->error);
	} else {
		$userId = $rows[0]['id'];
	}

	//check if experience is public and create user-experience if necessary
	$rows = dbGetExperienceById($experienceId);
	$isPublic = false;
	if (empty($rows)) {
		$validation = false;
		$errorMessage[] = sprintf("Experience not found by id: %s", $experienceId);
		$errorTexts[] = sprintf("Error 2 message: %s", $db->connect()->error);
	} else {
		$isPublic = $rows[0]['is_public'] == 1;
	}
	$userExperienceId = null;
	if ($isPublic) {
		if (!dbCreateUserExperience($userId, $experienceId, false, $db)) {
			$validation = false;
			$errorMessage[] = 'Could not create new user-emotion with experienceId ' . $experienceId . ' userid ' . $userId;
			$errorTexts[] = sprintf("Error 4 message: %s", $db->connect()->error);
		} else {
			$userExperienceId = $db->lastId();
		}
	} else {
		$rows = dbGetUserExperienceByUserAndExperience($userId, $experienceId);
		$userExperienceId = $rows[0]['id'];
	}

	//create emotion
	if (!dbCreateEmotion($userExperienceId,$emotion, false, $db)) {
		$validation = false;
		$errorMessage[] = 'Could not create new emotion from sender';
		$errorTexts[] = sprintf("Error 5 message: %s", $db->connect()->error);
	}

	if ($validation) {
		$db->connect()->commit();
		$db->connect()->autocommit(true);
		return dbGetReactions($experienceId);
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
	$rows = $db->select('SELECT id, android_id, name, profile_picture FROM user ORDER BY `name`');
	return $rows;
}

/**
 * Returns a user experience by user and experience.
 * @return mixed
 */
function dbGetUserExperienceByUserAndExperience($userId, $experienceId) {
	$db = new Db();
	$rows = $db->select('SELECT * FROM user_experience WHERE user_id = ' . $userId . ' AND experience_id = ' . $experienceId);
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

	if (!empty($emotionValues)) {
		$emotionValues = json_decode($emotionValues);

		$query = "INSERT INTO `emotion` (`user_experience_id`,`is_empty`,`anger`,`contempt`,`disgust`,`fear`,`happiness`,`neutral`,`sadness`,`surprise`, `strength`) VALUES (" . $userExperienceId . "," . 0 . "," . $emotionValues->anger . "," . $emotionValues->contempt . "," .
			$emotionValues->disgust . "," . $emotionValues->fear . ','. $emotionValues->happiness . ',' .
			$emotionValues->neutral .',' . $emotionValues->sadness . ','. $emotionValues->surprise . ", " . (isset($_POST['strength']) ? $_POST['strength'] : "NULL") . ");";
	} else {
		$query = "INSERT INTO `emotion` (`user_experience_id`,`is_empty`) VALUES (" . $userExperienceId . "," . 1  . ");";
	}


	return $db->query($query);
}

/**
 * Validates an uploaded media file.
 * @param bool $isAvatar
 * @return string
 */
function validateAndMoveMediaFile($isAvatar = false) {
	$params = include('_params.php');
	$errorMessage = [];

	if (!isset($_FILES['media']['error']) || is_array($_FILES['media']['error'])) {
		$errorMessage[] = 'Corrupt request.';
	}

	switch ($_FILES['media']['error']) {
		case UPLOAD_ERR_OK:
			break;
		case UPLOAD_ERR_NO_FILE:
			$errorMessage[] = 'Missing file.';
			break;
		case UPLOAD_ERR_INI_SIZE:
		case UPLOAD_ERR_FORM_SIZE:
			$errorMessage[] = 'File exceeded filesize limit.';
			break;
		default:
			$errorMessage[] = 'Unknown error.';
	}

	if ($_FILES['media']['size'] > $params['maxFileSize']) {
		$errorMessage[] = 'Exceeded filesize limit.';
	}

	$imageInfo = getimagesize($_FILES['media']['tmp_name']);
	if (false === $ext = array_search($imageInfo['mime'], $params['supportedMimes'], true)) {
		$errorMessage[] = 'Invalid file format. Supported formats: ' . join(', ', $params['supportedMimes']);
	}

	if (count($errorMessage) > 0) {
		throwDbException('Media file validation error:', join('. ', $errorMessage), 415);
	}

	return moveMediaFile($ext,$isAvatar);
}

/**
 * @param $ext
 * @param bool $isAvatar
 * @return string filename of moved file
 */
function moveMediaFile($ext, $isAvatar = false) {
	$params = include('_params.php');
	//TODO resize picture for performance reasons
	$filename = sprintf('%s.%s', sha1($_FILES['media']['tmp_name'] . time()), $ext);
	$uploadDir =  $params['uploadDir'];

	if ($isAvatar) {
		$filename = sprintf('%s.%s', md5($_POST['androidId']), 'jpg');
		$uploadDir = $params['avatarDir'];
	}

	if (!move_uploaded_file($_FILES['media']['tmp_name'], sprintf('./' . $uploadDir . '/%s', $filename))) {
		throwDbException('Media Upload.','Failed to move uploaded file.');
	}

	if ($isAvatar) {
		//resize
		// *** Include the class
		include("resize-class.php");

		// *** 1) Initialise / load image
		$resizeObj = new resize(sprintf('./' . $uploadDir . '/%s', $filename));
		// *** 2) Resize image (options: exact, portrait, landscape, auto, crop)
		$resizeObj -> resizeImage(150, 150, 'crop');
		// *** 3) Save image
		$resizeObj -> saveImage(sprintf('./' . $uploadDir . '/small/%s', $filename), 1000);
	}
	return $filename;
}

/**
 * Throws a db-exception.
 * @param string $message
 * @param string $errorTexts
 */
function throwDbException($message, $errorTexts, $errCode=500) {
	printResult(null,$errCode,$message, $errorTexts);
	die();
}
