<?php

function dbDistanceFunction($lat, $lon, $latAtt='lat', $lonAtt='lon') {
	return "COALESCE(
        6371 *
        acos(
            cos(radians($lat)) *
            cos(radians($latAtt)) *
            cos(
                radians($lonAtt) - radians($lon)
            ) +
            sin(radians($lat)) *
            sin(radians($latAtt))
        )
    , 0.0) as distance";
}
