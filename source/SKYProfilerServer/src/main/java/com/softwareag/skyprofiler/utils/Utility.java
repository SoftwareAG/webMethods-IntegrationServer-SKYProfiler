/*
* Copyright © 2013 - 2018 Software AG, Darmstadt, Germany and/or its licensors
*
* SPDX-License-Identifier: Apache-2.0
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.                                                            
*
*/

package com.softwareag.skyprofiler.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Base64;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.core.io.ClassPathResource;

import com.softwareag.skyprofiler.model.ServerData;

public class Utility {
	public static String getReportFileContent() throws Exception {
		ClassPathResource classPathResource = new ClassPathResource("static/report/report.html");
		InputStream inputStream = classPathResource.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		String line = "";
		StringBuilder result = new StringBuilder();
		while ((line = reader.readLine()) != null) {
			result.append(line).append("\n");
		}
		reader.close();
		return result.toString();
	}

	public static HttpResponse sendConfigurationToServer(ServerData serverData, String includedPackages,
			String kafkaBootstrapUrl, String kafkaTopicName, String externalHostname) throws Exception {
		String targetURL = "http://" + serverData.getHostName() + ":" + serverData.getPort()
				+ "/invoke/SKYProfiler.svc:updateSettings";
		String encoding = Base64.getEncoder()
				.encodeToString((serverData.getUserName() + ":" + serverData.getPassword()).getBytes());

		ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		postParameters.add(new BasicNameValuePair("includedPackages", includedPackages));
		postParameters.add(new BasicNameValuePair("kafkaBootstrapUrl", kafkaBootstrapUrl));
		postParameters.add(new BasicNameValuePair("kafkaTopicName", kafkaTopicName));
		postParameters.add(new BasicNameValuePair("externalHostname", externalHostname));

		HttpPost httppost = new HttpPost(targetURL);
		httppost.setHeader("Authorization", "Basic " + encoding);
		httppost.setHeader("Accept", "application/json");
		httppost.setEntity(new UrlEncodedFormEntity(postParameters));

		HttpClient client = HttpClientBuilder.create().build();
		HttpResponse response = client.execute(httppost);

		return response;
	}
}
