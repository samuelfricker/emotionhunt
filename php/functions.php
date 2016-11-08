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
		case 'emotion/list' :
			sendEmotionList();
			break;
		default :
			sendResult(null,404,'Action not found');
			break;
	}
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
function sendResult($data, $state = 200, $stateText = 'SUCCESS') {
	header("Cache-Control: no-store, no-cache, must-revalidate, max-age=0");
	header("Cache-Control: post-check=0, pre-check=0", false);
	header("Pragma: no-cache");
	header('Content-type: application/json');

	echo json_encode([
		'state'=>$state,
		'text'=>$stateText,
		'count'=>$data !== null ? count($data) : 0,
		'data'=>$data
	]);
}
