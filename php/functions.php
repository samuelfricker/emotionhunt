<?php

/**
 * Returns the current request action.
 * @return string
 */
function getAction() {
	return $_GET['action'] != null ? strtolower($_GET['action']) : '';
}

/**
 * Handles the request depending on the passed action and params.
 */
function handleRequest() {
	switch (getAction()) {
		case 'user/list' :
			getUsers();
			break;
		case 'experience/create' :
			createExperience();
			break;
		case 'experience/list' :
			getExperiences();
			break;
		case 'experience/public/create' :
			createPublicExperience();
			break;
		case 'experience/public/list' :
			getPublicExperiences();
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
 * Creates a new experience. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - sender (user id)
 * - recipients (, separated user ids)
 * - expectedEmotion JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createExperience() {
	printResult(dbCreateExperience());
}

/**
 * Creates a new public experience. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - sender (user id)
 * - expectedReaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createPublicExperience() {
	printResult(dbCreateExperience(true));
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
	printResult(dbGetExperiences());
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
	header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");
	header('Content-type: application/json');

	echo json_encode([
		'state'=>$state,
		'text'=>$stateText,
		'count'=>$data !== null ? count($data) : 0,
		'data'=>$data,
		'error'=>$errorTexts
	]);
}
