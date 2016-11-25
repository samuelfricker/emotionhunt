<?php
return [
	//radius within experiences can be fetched
	'radius'=>1000,
	//upload directory for uploaded media files
	'uploadDir'=>'uploads',
	//max file size which is a limit for a media file upload in this API (in bytes, e.g. 5242880 = 5MB)
	'maxFileSize'=>5242880,
	//supported MIME types for uploaded media file
	'supportedMimes'=>[
		'jpg'=>'image/jpeg',
		'png'=>'image/png',
		'gif'=>'image/gif',
	],
];
