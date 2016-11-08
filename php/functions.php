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
			sendUserList();
			break;
		case 'emotion/create' :
			createEmotion();
			break;
		case 'emotion/list' :
			sendEmotionList();
			break;
		default :
			sendResult(null,404,'Action not found');
			break;
	}
}

/**
 * Following POST attributes are requested:
 * - lat
 * - lon
 * - isPublic
 * - visibilityDuration (optional)
 * - text
 * - sender (user id)
 * - recipients (, separated user ids)
 * - expectedReaction JSON-serialized Reaction Object {"anger":1.0, "fear":0.0, ...}
 *
 * Example
 * Call: localhost/emotionhunt/?action=emotion/create
 * POST DATA: lat=8.00&lon=43.00&isPublic=0&text=mein text&visibilityDuration=24&recipients=2&sender=1&expectedReaction={"anger":0.99, "contempt":0.0, "disgust":0.0, "fear":0.0, "happiness":0.0, "neutral":0.0, "sadness": 0.0, "surprise":0.0}
 */
function createEmotion() {
	sendResult(dbCreateEmotion());
}

function sendUserList() {
	sendResult(dbGetUsers());
}

function sendEmotionList() {
	sendResult(dbGetEmotions());
}

/**
 * Returns / prints out the result and the state
 * @param mixed $data
 * @param int $state
 */
function sendResult($data, $state = 200, $stateText = 'SUCCESS', $errorTexts = null) {
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
