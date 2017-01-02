<?php

/**
 * Returns the current request action.
 * @return string
 */
function getAction() {
	if (!validateApiKey()) throwDbException('Unauthorized','Invalid API key.','403');
	return $_GET['action'] != null ? strtolower($_GET['action']) : '';
}

function validateApiKey() {
	$params = include('_params.php');
	return strcmp($params['apiKey'],$_REQUEST['apiKey']) === 0;
}

/**
 * Handles the request depending on the passed action and params.
 */
function handleRequest() {
	switch (getAction()) {
		case 'reset' :
			resetDb();
			break;
		case 'avatar':
			avatar();
			break;
		case 'avatar/create':
			createAvatar();
			break;
		case 'user/login' :
			login();
			break;
		case 'user/register' :
			register();
			break;
		case 'user/list' :
			getUsers();
			break;
		case 'experience/media' :
			getMedia();
			break;
		case 'experience/reactions' :
			getReactions();
			break;
		case 'experience/create' :
			createExperience();
			break;
		case 'experience/reaction/create' :
			createExperienceReaction();
			break;
		case 'experience/list' :
			getExperiences();
			break;
		case 'experience/public/create' :
			createPublicExperience();
			break;
		case 'emotion/create' :
			createEmotion();
			break;
		default :
			printResult(null,404,'Action not found');
			break;
	}
}

/**
 * Removes all data from all tables
 */
function resetDb() {
	//TODO REMOVE THIS HACK!!!
	$db = new Db();
	$db->query("SET foreign_key_checks = 0;");
	$db->query("TRUNCATE user;");
	$db->query("TRUNCATE user_experience;");
	$db->query("TRUNCATE experience;");
	$db->query("TRUNCATE emotion;");
	$db->query("SET foreign_key_checks = 1;");
}

/**
 * Registers a new user. following params are required: androidId, name, phoneNumber
 * Either returns HTTP 201 on success or 403 on fail.
 */
function register() {
	$user = dbCreateUser();
	if (!empty($user)) {
		printResult($user, 201);
	} else {
		printResult(null, 403);
	}
}

/**
 * Tries to login a user with a given android id.
 * HTTP response will be either 200 or 403 if android id is not yet registered
 */
function login() {
	/** @var string[] $user */
	$user = dbGetUserByAndroidId();

	if (!empty($user)) {
		printResult($user, 200);
	} else {
		printResult(null, 403);
	}
}

/**
 * TODO describe header
 */
function avatar() {
	$user = $_GET['user'];
	$id = $_GET['id'];
	//test hash: ae205979be4cb7ceaa1978ab96a44b41

	if (empty($id)) {
		if (!preg_match('/^[a-f0-9]*$/i', $user)) {
			return printResult([],405,'','Not allowed call: Invalid request value for user: ' . $user);
		}
		$user = md5($user);
	} else {
		if (!is_numeric($id)) {
			return printResult([],405,'','Not allowed call: Invalid request value for id: ' . $id);
		}
		$user = md5(dbGetAndroidIdByUserId($_GET['id']));
	}

	$params = include('_params.php');
	$filename = './' .$params['avatarDir'] . '/small/' . $user . '.jpg';

	if (file_exists($filename)) {
		return printMedia($filename);
	} else {
		return printMedia('./' .$params['avatarDir'] . '/noavatar.jpg');
	}
}

function createAvatar() {
	validateAndMoveMediaFile(true);
	return printResult([],201,'');
}

/**
 * Creates a new experience. Following POST attributes are required:
 * - filename
 */
function getMedia() {
	$filename = $_POST['media'];

	if (!preg_match('/^[a-f0-9]+\.[a-z]{3,4}$/', $filename)) {
		return printResult([],405,'Not allowed call: Invalid request value for media: ' . $filename);
	}

	$params = include('_params.php');
	$filename = './' .$params['uploadDir'] . '/' . $filename;

	if (file_exists($filename)) {
		return printMedia($filename);
	} else {
		return printResult([],404,'Media not found');
	}
}

/**
 * Returns all reactions on a published experience.
 */
function getReactions() {
	$experienceId = $_POST['id'];

	if (!is_numeric($experienceId)) {
		return printResult([],405,'Not allowed call: Invalid request value for id: ' . $experienceId);
	}

	printResult(dbgetReactions($experienceId), 200);
}

/**
 * Creates a new experience. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - androidId (android device id)
 * - recipients (, separated user ids)
 * - expectedEmotion JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 * - media
 */
function createExperience() {
	printResult(dbCreateExperience(), 201);
}

function createExperienceReaction() {
	printResult(dbCreateExperienceReaction(), 201);
}

/**
 * Creates a new public experience. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - androidId (android device id)
 * - expectedReaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 * - media
 */
function createPublicExperience() {
	printResult(dbCreateExperience(true), 201);
}

/**
 * Fetches all public experiences within a radius around a given location (user's current location).
 * Following POST attributes are required:
 * - lat
 * - lon
 */
function getPublicExperiences() {
	printResult(dbGetPublicExperiences());
}

/**
 * Fetches all private experiences within a radius around a given location (user's current location).
 * Following POST attributes are required:
 * - lat
 * - lon
 */
function getExperiences() {
	printResult(array_merge(dbGetExperiences(), dbGetPublicExperiences()));
}

/**
 * Creates a reaction on a received emotion. Following POST attributes are required:
 * - userExperienceId
 * - emotion JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createEmotion() {
	printResult(dbCreateEmotion($_POST['userExperienceId'], $_POST['emotion']));
}

/**
 * Prints all users from the database.
 */
function getUsers() {
	printResult(dbGetUsers());
}

/**
 * Returns / prints out the result and the state
 * @param mixed $data
 * @param int $state
 */
function printResult($data, $state = 200, $stateText = 'SUCCESS', $errorTexts = null) {
	header('HTTP/1.0 ' . $state);
	header("Access-Control-Allow-Origin: *");
	header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");
	header('Content-type: application/json');

	if ($state >= 400 && $state < 500) {
		$stateText = "Client Error";
	}
	if ($state >= 500 && $state < 600) {
		$stateText = "Server Error";
	}

	echo json_encode([
		'state'=>$state,
		'text'=>$stateText,
		'count'=>$data !== null ? count($data) : 0,
		'data'=>$data,
		'error'=>$errorTexts
	]);
}

/**
 * Returns a media file.
 * @param $file
 * @param $mediaType
 */
function printMedia($file, $mediaType) {
	//TODO add support for multiple media types depending on the file located on the server
	header('HTTP/1.0 200');
	header("Access-Control-Allow-Origin: *");
	header('Content-type: image/png');
	readfile($file);
}
