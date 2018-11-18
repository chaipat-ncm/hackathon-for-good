<?php

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);


$minArea = 200;

if (isset($_POST['submit'])) {
	
	$companyName = $_POST['company'];
	$targetCountry = $_POST['target_country'];
	
	if ($companyName != '' && $targetCountry != '') {
		
		$result = get_company_risk_for_country($companyName, $targetCountry);
		
	} else {
		
		$result = 'Please enter a company name and a country';
		
	}
	
} else {
	
	$result = false;
	
}

function find_company_in_list($companyName, $companies) {
	
	$foundIndex = false;
	
	foreach($companies as $index => $company) {
		
		if (strtolower($company['name']) == strtolower($companyName)) {
			$foundIndex = $index;
		}
	}
	
	return $foundIndex;
}

function is_foreign_company($targetCountry, $companyCountry) {
	
	return strtolower($targetCountry) != strtolower($companyCountry);
}

function has_min_area($areaSize) {
	
	global $minArea;
	
	return $areaSize >= $minArea;
	
}

function get_all_companies() {
	
	$companies = array();
	
	// loop through grain db
	
	if (($handle = fopen('land_grab_deals.csv', 'r')) !== FALSE) {
		
	    while (($data = fgetcsv($handle, 1000, ';')) !== FALSE) {
		    
		    $targetCountry = $data[0];
		    $companyName = $data[1];
		    $companyCountry = $data[3];
		    $areaSize = $data[4];
	
			// only consider the deal a land grab if:
			// - it's a foreign company
			// - the area has at least a certain size
			    
		    if (is_foreign_company($targetCountry, $companyCountry) && has_min_area($areaSize)) {
			    
			    $companyIndex = find_company_in_list($companyName, $companies);
			    
			    if ($companyIndex) {
				    
				    $companies[$companyIndex]['totalArea'] += $areaSize;
				    $companies[$companyIndex]['numberOfGrabs']++;
				    
			    } else {
	
				    $companies[] = array(
					    'name' => $companyName,
					    'country' => $companyCountry,
					    'totalArea' => $areaSize,
					    'numberOfGrabs' => 1,
					    'numberOfDeals' => 0
					    
				    );
				    
			    }
		    }
	    }
	    fclose($handle);
	}
	
	// loop through grain db
	
	if (($handle = fopen('land_deals.csv', 'r')) !== FALSE) {
		
	    while (($data = fgetcsv($handle, 1000, ';')) !== FALSE) {
		    
		    $targetCountry = $data[0];
		    $companyName = $data[1];
		    $companyCountry = $data[2];
		    $areaSize = $data[5];
	
			// only consider the deal a land grab if:
			// - it's a foreign company
			// - the area has at least a certain size
			    
		    if (is_foreign_company($targetCountry, $companyCountry) && has_min_area($areaSize)) {
			    
			    $companyIndex = find_company_in_list($companyName, $companies);
			    
			    if ($companyIndex) {
				    
				    $companies[$companyIndex]['numberOfDeals']++;
				    
			    } else {
	
				    $companies[] = array(
					    'name' => $companyName,
					    'country' => $companyCountry,
					    'numberOfGrabs' => 0,
					    'numberOfDeals' => 1,
					    'totalArea' => 0
				    );
				    
			    }
		    }
	    }
	    fclose($handle);
	}
	
	return $companies;
}

function sort_by_total_area($a, $b) {
    return $b['totalArea'] - $a['totalArea'];
}


function rank_companies_by_area($companies) {
	
	foreach ($companies as $index => &$company) {
		$company['rank'] = $index + 1;
	}
	
	return $companies;
}

function get_company_by_name($companyName, $companies) {
	
	$foundCompany = false;
	
	foreach ($companies as $company) {
		
		if (strtolower($company['name']) == strtolower($companyName)) {
			$foundCompany = $company;
		}
	}
	
	return $foundCompany;
	
}

function get_company_risk_for_country($companyName, $targetCountry) {
	
	$companies = get_all_companies();

	usort($companies, 'sort_by_total_area');
	
	$companies = rank_companies_by_area($companies);
	
	$thisCompany = get_company_by_name($companyName, $companies);
	
	if ($thisCompany) { 
		
		// if the company is from the same country, it's usually not considered land grabbing
		
		if ($thisCompany['country'] == $targetCountry) {
			
			$risk = '<em>' . $companyName . '</em> is from the target country (' . $targetCountry . '), so this is probably not a land grabbing issue.';
			
		} else {
			
			// company rating = ( frequency in grain DB / frequency in land matrix DB ) x ( total number of companies / area based rank from grain DB)
			
			// if the frequency in the grain DB is 0 we don't want the risk to be 0, because the company still occurs in the land matrix DB
			// so make it at least 1
			
			$numberOfGrabs = $thisCompany['numberOfGrabs'] == 0 ? 1 : $thisCompany['numberOfGrabs'];
			
			// if the frequency in land matrix DB is zero, make it the same as the frequency in the grain DB so the result is 1
			
			$numberOfDeals = $thisCompany['numberOfDeals'] == 0 ? $thisCompany['numberOfGrabs'] : $thisCompany['numberOfDeals'];
			
			// if the frequency in the grain DB is higher than in in land matrix, make it the same as the frequencyso the result is 1
			
			if ($numberOfGrabs > $numberOfDeals) {
				$numberOfGrabs = $numberOfDeals;
			}
			
			$risk = round(( $numberOfGrabs / $numberOfDeals ) * ( count($companies) / $thisCompany['rank'] ), 2);
			
			$risk = 'The land grabbing risk for <em>' . $companyName . '</em> in ' . $targetCountry . ' is ' . $risk;
			
		}
		
	} else {
		
		$risk = '<em>' . $companyName . '</em> is not in the database. You might want to try a different spelling';	
	}
	
	return $risk;
}

$countries = array("Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla", "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria", "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium", "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegowina", "Botswana", "Bouvet Island", "Brazil", "British Indian Ocean Territory", "Brunei Darussalam", "Bulgaria", "Burkina Faso", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde", "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island", "Cocos (Keeling) Islands", "Colombia", "Comoros", "Congo", "Congo, the Democratic Republic of the", "Cook Islands", "Costa Rica", "Cote d'Ivoire", "Croatia (Hrvatska)", "Cuba", "Cyprus", "Czech Republic", "Denmark", "Djibouti", "Dominica", "Dominican Republic", "East Timor", "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia", "Ethiopia", "Falkland Islands (Malvinas)", "Faroe Islands", "Fiji", "Finland", "France", "France Metropolitan", "French Guiana", "French Polynesia", "French Southern Territories", "Gabon", "Gambia", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece", "Greenland", "Grenada", "Guadeloupe", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana", "Haiti", "Heard and Mc Donald Islands", "Holy See (Vatican City State)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India", "Indonesia", "Iran (Islamic Republic of)", "Iraq", "Ireland", "Israel", "Italy", "Jamaica", "Japan", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Korea, Democratic People's Republic of", "Korea, Republic of", "Kuwait", "Kyrgyzstan", "Lao, People's Democratic Republic", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libyan Arab Jamahiriya", "Liechtenstein", "Lithuania", "Luxembourg", "Macau", "Macedonia, The Former Yugoslav Republic of", "Madagascar", "Malawi", "Malaysia", "Maldives", "Mali", "Malta", "Marshall Islands", "Martinique", "Mauritania", "Mauritius", "Mayotte", "Mexico", "Micronesia, Federated States of", "Moldova, Republic of", "Monaco", "Mongolia", "Montserrat", "Morocco", "Mozambique", "Myanmar", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia", "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama", "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn", "Poland", "Portugal", "Puerto Rico", "Qatar", "Reunion", "Romania", "Russian Federation", "Rwanda", "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Samoa", "San Marino", "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Seychelles", "Sierra Leone", "Singapore", "Slovakia (Slovak Republic)", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Georgia and the South Sandwich Islands", "Spain", "Sri Lanka", "St. Helena", "St. Pierre and Miquelon", "Sudan", "Suriname", "Svalbard and Jan Mayen Islands", "Swaziland", "Sweden", "Switzerland", "Syrian Arab Republic", "Taiwan, Province of China", "Tajikistan", "Tanzania, United Republic of", "Thailand", "Togo", "Tokelau", "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands", "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "United States Minor Outlying Islands", "Uruguay", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam", "Virgin Islands (British)", "Virgin Islands (U.S.)", "Wallis and Futuna Islands", "Western Sahara", "Yemen", "Yugoslavia", "Zambia", "Zimbabwe");


?>

<html>
	
<body>
	
	<form method="post" action="<?= $_SERVER['PHP_SELF'] ?>">
		
		<label for="company">Company name</label>
		<input type="text" name="company" value="<?= isset($_POST['company']) ? $_POST['company'] : '' ?>" />
		
		<label for="target_country">Which country are you in?</label>
		<select name="target_country">
			<option value="">Choose a country</option>
			<?php foreach($countries as $country): ?>
				<option<?= isset($_POST['target_country']) && $_POST['target_country'] == $country ? ' selected' : '' ?>><?= $country ?></option>
			<?php endforeach; ?>
		</select>
		
		<input type="submit" name="submit" value="find" />
		
	</form>
	
	<?php if (isset($result)): ?>
		
		<strong><?= $result; ?></strong>
		
	<?php endif; ?>
	
</body>
	
</html>