<?php
//return own config if exists
if (file_exists('php/_myconfig.php')) {
	$config = include('_myconfig.php');
	return $config;
}

return [
	'host' => 'localhost',
	'database' => 'emotion_hunt',
	'username' => 'root',
	'password' => '',
];
