/**
 * Copyright 2006-2016 Tom Misawa(riversun.org@gmail.com)
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 http://www.apache.org/licenses/LICENSE-2.0
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package org.riversun.comment_translator;

import com.memetix.mst.language.Language;

public class TestParamCondition {

	private static final String proxyHost = "[PROXY_IF_NEEDED]";
	private static final int proxyPort = 0;

	private static final String clientID = "[YOUR_CLIENT_ID]";
	private static final String clientSecret = "[YOUR_SECRET]";

	private static final Language fromLang = Language.AUTO_DETECT;
	private static final Language toLang = Language.ENGLISH;

	public static TranslationCondition getCondition() {

		TranslationCondition condition = new TranslationCondition();
		condition.setFromLang(fromLang);
		condition.setToLang(toLang);
		condition.setProxy(proxyHost, proxyPort);
		condition.setClientID(clientID, clientSecret);

		return condition;
	}
}
