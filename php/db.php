<?php
$config = include('_config.php');

//connect to database
$db = new mysqli($config['host'],$config['username'],$config['password'],$config['database']);
if(mysqli_connect_errno()) printf("Connect failed: %s\n", $db->connect_error);

//close connection
$db->close();
