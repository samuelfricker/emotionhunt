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
		case 'emotion/create' :
			createEmotion();
			break;
		case 'emotion/list' :
			getEmotions();
			break;
		case 'reaction/create' :
			createReaction();
			break;
		case 'emotion/public/create' :
			createPublicEmotion();
			break;
		case 'emotion/public/list' :
			getPublicEmotions();
			break;
		default :
			printResult(null,404,'Action not found');
			break;
	}
}

/**
 * Creates a new emotion. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - sender (user id)
 * - recipients (, separated user ids)
 * - expectedReaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createEmotion() {
	printResult(dbCreateEmotion());
}

/**
 * Creates a new public emotion. Following POST attributes are required:
 * - lat
 * - lon
 * - visibilityDuration (optional)
 * - text
 * - sender (user id)
 * - expectedReaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createPublicEmotion() {
	printResult(dbCreateEmotion(true));
}

/**
 * Creates a reaction on a received emotion. Following POST attributes are required:
 * - userEmotionId
 * - reaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 */
function createReaction() {
	printResult(dbCreateReaction($_POST['userEmotionId'], $_POST['reaction']));
}

function getUsers() {
	printResult(dbGetUsers());
}

/**
 * Fetches all public emotions within a radius around a given location (user's current location).
 * Following POST attributes are required:
 * - lat
 * - lon
 */
function getPublicEmotions() {
	printResult(dbGetPublicEmotions());
}

function getEmotions() {
	printResult(dbGetEmotions());
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
