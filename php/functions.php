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
		case 'emotion/list' :
			sendResult(['asdasd'=>1],201);
			break;
		default :
			break;
	}
}

function getEmotionList() {

}

/**
 * Returns / prints out the result and the state
 * @param mixed $data
 * @param int $state
 */
function sendResult($data, $state = 200) {
	header('Content-type: application/json');
	echo json_encode(['state'=>$state, 'data'=>$data]);
}
