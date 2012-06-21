<?php
# APJP, A PHP/JAVA PROXY
# Copyright (C) 2009-2012 Jeroen Van Steirteghem
# 
# This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.
# 
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
# 
# You should have received a copy of the GNU General Public License along with this program; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

require_once('APJP/APJP.php');

class APJP_HTTPS_Application
{
	public function execute()
	{
		$td = mcrypt_module_open(MCRYPT_ARCFOUR, '', MCRYPT_MODE_STREAM, '');
		
		if(!$td)
		{
			header('HTTP/1.0 500 Internal Server Error');
			
			return;
		}
		
		mcrypt_generic_init($td, APJP_APJP::$APJP_KEY, '');
		
		$handle1 = fopen('php://input', 'r');
		
		if(!$handle1)
		{
			mcrypt_generic_deinit($td);
			
			mcrypt_module_close($td);
			
			header('HTTP/1.0 500 Internal Server Error');
			
			return;
		}
		
		$http_request_header = '';
		$http_request_header_length = 0;
		
		while(!feof($handle1))
		{
			$buffer = fread($handle1, 1);
			$buffer_length = strlen($buffer);
			
			if($buffer_length > 0)
			{
				$http_request_header = $http_request_header . mdecrypt_generic($td, $buffer);
				$http_request_header_length = strlen($http_request_header);
				
				if($http_request_header_length >= 4)
				{
					if
					(
						$http_request_header[$http_request_header_length - 4] == "\r" && 
						$http_request_header[$http_request_header_length - 3] == "\n" && 
						$http_request_header[$http_request_header_length - 2] == "\r" && 
						$http_request_header[$http_request_header_length - 1] == "\n"
					)
					{
						break;
					}
				}
			}
		}
		
		$http_request_header_values = explode("\r\n", $http_request_header);
		$http_request_header_values_length = count($http_request_header_values);
		
		$http_request_address = '';
		$http_request_port = 0;
		
		$i = 1;
		while($i < $http_request_header_values_length - 2)
		{
			$http_request_header_value1 = $http_request_header_values[$i];
			$http_request_header_values1 = explode(': ', $http_request_header_value1);
			$http_request_header_values1_length = count($http_request_header_values1);
			if($http_request_header_values1_length == 2)
			{
				$http_request_header_value2 = $http_request_header_values1[0];
				$http_request_header_value3 = $http_request_header_values1[1];
				if(strtoupper($http_request_header_value2) == strtoupper('Host'))
				{
					$http_request_header1_values3 = explode(':', $http_request_header_value3);
					$http_request_header1_values3_length = count($http_request_header1_values3);
					if($http_request_header1_values3_length == 1)
					{
						$http_request_address = $http_request_header1_values3[0];
						$http_request_port = 443;
					}
					else
					{
						if($http_request_header1_values3_length == 2)
						{
							$http_request_address = $http_request_header1_values3[0];
							$http_request_port = $http_request_header1_values3[1];
						}
					}
				}
			}
			$i = $i + 1;
		}
		
		$handle2 = fsockopen('ssl://' . $http_request_address, $http_request_port);
		
		if(!$handle2)
		{
			fclose($handle1);
			
			mcrypt_generic_deinit($td);
			
			mcrypt_module_close($td);
			
			header('HTTP/1.0 500 Internal Server Error');
			
			return;
		}
		
		fwrite($handle2, $http_request_header);
		
		while(!feof($handle1))
		{
			$buffer = fread($handle1, 5120);
			$buffer_length = strlen($buffer);
			
			if($buffer_length > 0)
			{
				fwrite($handle2, mdecrypt_generic($td, $buffer));
			}
		}
		
		fclose($handle1);
		
		mcrypt_generic_deinit($td);
		
		mcrypt_generic_init($td, APJP_APJP::$APJP_KEY, '');
		
		$handle3 = fopen('php://output', 'w');
		
		if(!$handle3)
		{
			fclose($handle2);
			
			mcrypt_generic_deinit($td);
			
			mcrypt_module_close($td);
			
			header('HTTP/1.0 500 Internal Server Error');
			
			return;
		}
		
		for($i = 0; $i < count(APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY); $i = $i + 1)
		{
			if(APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[$i] != '')
			{
				header(APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_KEY[$i] . ': ' . APJP_APJP::$APJP_REMOTE_HTTPS_SERVER_RESPONSE_PROPERTY_VALUE[$i]);
			}
		}
		
		while(!feof($handle2))
		{
			$buffer = fread($handle2, 5120);
			$buffer_length = strlen($buffer);
			
			if($buffer_length > 0)
			{
				fwrite($handle3, mcrypt_generic($td, $buffer));
			}
		}
		
		fclose($handle2);
		
		fclose($handle3);
		
		mcrypt_generic_deinit($td);
		
		mcrypt_module_close($td);
		
		return true;
	}
}
?>