package com.pepcus.api.services.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileImportUtil {
	/**
	 * Finds if any required header is missing from given set of headers
	 * 
	 * @param String[] headersInFile
	 * @param String[] requiredHeaders
	 * @return String[] Array of missing headers 
	 */
	public static String[] getMissingHeaders(String[] presentHeaders, String[] requiredHeaders) {
		Set<String> headersInFileSet = new HashSet<String>(Arrays.asList(presentHeaders));
		Set<String> requiredHeadersSet = new HashSet<String>(Arrays.asList(requiredHeaders));
		requiredHeadersSet.removeAll(headersInFileSet);

		String[] missingHeaders = new String[requiredHeadersSet.size()];
		return requiredHeadersSet.toArray(missingHeaders);
	}

	public static boolean hasValidExtension(String fileName, String... validExtention) {
		return FilenameUtils.isExtension(fileName, validExtention);
	}

	public static List<String> readFileContent(MultipartFile file) throws IOException {
		BufferedReader br;
		List<String> result = new ArrayList<>();
		String line;
		InputStream is = file.getInputStream();
		br = new BufferedReader(new InputStreamReader(is));
		while ((line = br.readLine()) != null) {
			result.add(line);
		}
		return result;
	}

	public static Map<String, List<String>> getFieldsToHeaderMapForCompanyCSV() {
		Map<String, List<String>> fieldsToHeaderMap = new LinkedHashMap<String, List<String>>();

		//clientName
		fieldsToHeaderMap.put("companyName", Arrays.asList(new String[] { "CLIENT_NAME" }));

		//displayName
		fieldsToHeaderMap.put("displayName", Arrays.asList(new String[] { "DISPLAY_NAME" }));

		//companyPhone
		fieldsToHeaderMap.put("companyPhone", Arrays.asList(new String[] { "PHONE" }));

		///officeLocation
		fieldsToHeaderMap.put("officeLocation",
				Arrays.asList(new String[] { "ADDRESS", "ADDRESS2", "CITY", "STATE", "ZIP" }));

		//industry
		fieldsToHeaderMap.put("industry", Arrays.asList(new String[] { "INDUSTRY" }));

		//companySize
		fieldsToHeaderMap.put("companySize", Arrays.asList(new String[] { "COMPANY_SIZE" }));

		//producer
		fieldsToHeaderMap.put("producer", Arrays.asList(new String[] { "PRODUCER" }));

		//custom1
		fieldsToHeaderMap.put("custom1", Arrays.asList(new String[] { "BUSINESS_ID" }));

		//custom2
		fieldsToHeaderMap.put("custom2", Arrays.asList(new String[] { "BRANCH_ID" }));

		//custom3
		fieldsToHeaderMap.put("custom3", Arrays.asList(new String[] { "CLIENT_ID" }));

		//custom4
		fieldsToHeaderMap.put("custom4", Arrays.asList(new String[] { "CLIENT_TYPE" }));

		return fieldsToHeaderMap;
	}

}
