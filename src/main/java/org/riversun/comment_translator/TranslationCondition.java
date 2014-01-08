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

/**
 * Translation condition(POJO)<br>
 * <br>
 * 
 * <pre>
 * <code>
 * {
 * 	
 * 	String proxyHost = [something if you need]
 * 	int proxyPort = 8080;
 * 
 * 	Language fromLang = Language.AUTO_DETECT;
 * 	Language toLang = Language.ENGLISH;
 * 
 * 	String clientID = [your cliend id]
 * 	String clientSecret = [your secret]
 * 
 * 	TranslationCondition condition = new TranslationCondition();
 * 	condition.setFromLang(fromLang);
 * 	condition.setToLang(toLang);
 * 	condition.setProxy(proxyHost, proxyPort);
 * 	condition.setClientID(clientID, clientSecret);
 * }
 * </code>
 * </pre>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class TranslationCondition {

	String clientID = null;
	String clientSecret = null;

	String proxyHost = null;
	int proxyPort = -1;
	Language fromLang = Language.AUTO_DETECT;
	Language toLang = Language.ENGLISH;

	/**
	 * Set from lang<br>
	 * 
	 * @param fromLang
	 * @return
	 */
	public TranslationCondition setFromLang(Language fromLang) {
		this.fromLang = fromLang;
		return TranslationCondition.this;
	}

	/**
	 * Set to lang<br>
	 * 
	 * @param toLang
	 * @return
	 */
	public TranslationCondition setToLang(Language toLang) {
		this.toLang = toLang;
		return TranslationCondition.this;
	}

	/**
	 * Set clientId and clientSecret provided from MS Translator API
	 * 
	 * @param clientID
	 * @param clientSecret
	 * @return
	 */
	public TranslationCondition setClientID(String clientID, String clientSecret) {
		this.clientID = clientID;
		this.clientSecret = clientSecret;
		return TranslationCondition.this;
	}

	/**
	 * Set proxy host and port if needed.
	 * 
	 * @param proxyHost
	 * @param proxyPort
	 * @return
	 */
	public TranslationCondition setProxy(String proxyHost, int proxyPort) {
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		return TranslationCondition.this;
	}

}
