<?php
/*
APJP, A PHP/JAVA PROXY
Copyright (C) 2009-2011 Jeroen Van Steirteghem

This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/

error_reporting(0);

require('HTTPS.properties.php');

$td = null;

$td = mcrypt_module_open(MCRYPT_ARCFOUR, '', MCRYPT_MODE_STREAM, '');

if(!$td)
{
	header('HTTP/1.0 500 Internal Server Error');
	
	die();
}

// INPUT

$handle1 = null;

$handle1 = fopen('php://input', 'r');

if(!$handle1)
{
	header('HTTP/1.0 500 Internal Server Error');
	
	die();
}

mcrypt_generic_init($td, $APJP_KEY, '');

$buffer1 = null;
$buffer2 = null;
$buffer3 = null;

$i = 0;

while(!feof($handle1))
{
	$buffer1 = fread($handle1, 1);
	
	if($buffer1 != null)
	{
		$buffer2 = $buffer2 . mdecrypt_generic($td, $buffer1);
		
		$i = $i + 1;
		
		if($i >= 4)
		{
			if
			(
				$buffer2[$i - 4] == "\r" && 
				$buffer2[$i - 3] == "\n" && 
				$buffer2[$i - 2] == "\r" && 
				$buffer2[$i - 1] == "\n"
			)
			{
				break;
			}
		}
	}
}

$handle2 = null;

if(preg_match('/\r\nHost: ([0-9a-z\.\-]+)(?:\:([0-9]+))?\r\n/i', $buffer2, $buffer3) != 0)
{
	if(count($buffer3) == 2)
	{
		$handle2 = fsockopen('ssl://'.$buffer3[1], 443);
	}
	else
	{
		$handle2 = fsockopen('ssl://'.$buffer3[1], $buffer3[2]);
	}
}

if(!$handle2)
{
	header('HTTP/1.0 500 Internal Server Error');
	
	die();
}

for($i = 0; $i < count($APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY); $i = $i + 1)
{
	if($APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[$i] != '')
	{
		header($APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[$i] . ': ' . $APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE[$i]);
	}
}

fwrite($handle2, $buffer2);

$buffer1 = null;
$buffer2 = null;
$buffer3 = null;

while(!feof($handle1))
{
	$buffer1 = fread($handle1, 5120);
	
	if($buffer1 != null)
	{
		fwrite($handle2, mdecrypt_generic($td, $buffer1));
	}
}

mcrypt_generic_deinit($td);

fclose($handle1);

// OUTPUT

$handle3 = null;

$handle3 = fopen('php://output', 'w');

if(!$handle3)
{
	header('HTTP/1.0 500 Internal Server Error');
	
	die();
}

mcrypt_generic_init($td, $APJP_KEY, '');

$buffer1 = null;
$buffer2 = null;
$buffer3 = null;

while(!feof($handle2))
{
	$buffer1 = fread($handle2, 5120);
	
	if($buffer1 != null)
	{
		fwrite($handle3, mcrypt_generic($td, $buffer1));
	}
}

fclose($handle2);

mcrypt_generic_deinit($td);

fclose($handle3);

mcrypt_module_close($td);
?>