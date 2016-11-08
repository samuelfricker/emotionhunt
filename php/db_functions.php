<?php

function dbGetEmotions() {
	$db = new Db();
	$rows = $db->select('SELECT * FROM emotion');
	return $rows;
}

function dbGetUsers() {
	$db = new Db();
	$rows = $db->select('SELECT * FROM user');
	return $rows;
}
