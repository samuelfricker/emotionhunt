<?php
return [
	//api key which is required for each API call either as POST or GET param
	'apiKey' => 'bENFnP63CqNFDSucAFguwj7p685Z2eh3',
	//radius within experiences can be fetched
	'radius'=>100,
	//upload directory for uploaded media files
	'uploadDir'=>'uploads',
	//max file size which is a limit for a media file upload in this API (in bytes, e.g. 8388608 = 8MB)
	'maxFileSize'=>8388608,
	//supported MIME types for uploaded media file
	'supportedMimes'=>[
		'jpg'=>'image/jpeg',
		'png'=>'image/png',
		'gif'=>'image/gif',
	],
];
