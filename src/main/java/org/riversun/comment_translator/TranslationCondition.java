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

import java.util.ArrayList;
import java.util.List;

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

	public enum OpFlag {
		TRANSLATE, NOT_TRANSLATE, REMOVE
	}

	String clientID = null;
	String clientSecret = null;

	String proxyHost = null;
	int proxyPort = -1;

	Language fromLang = Language.AUTO_DETECT;
	Language toLang = Language.ENGLISH;

	boolean copyAllFiles = false;

	OpFlag javaDocCommentOperation = OpFlag.TRANSLATE;
	OpFlag commentOperation = OpFlag.TRANSLATE;
	OpFlag blockCommentOperation = OpFlag.TRANSLATE;

	// A 'document' is a text file such as readme.txt you specified by the
	// addDocument method
	OpFlag documentOperation = OpFlag.NOT_TRANSLATE;

	String sourceCodeCharset = "UTF-8";

	List<String> targetSourceFileExtensionList = new ArrayList<String>();
	List<String> targetDocumentFileExtensionList = new ArrayList<String>();

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
	 * Set charset of source code
	 * 
	 * @param sourceCodeCharset
	 *            (DEFAULT is UTF-8)
	 */
	public void setSourceCodeCharset(String sourceCodeCharset) {
		this.sourceCodeCharset = sourceCodeCharset;
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

	/**
	 * To enable coping all files included non-source-code files
	 * 
	 * @param copyAllFiles
	 * @return
	 */
	public TranslationCondition setCopyAllFiles(boolean copyAllFiles) {
		this.copyAllFiles = copyAllFiles;
		return TranslationCondition.this;
	}

	/**
	 * Add extension that comment part is to be translated like ".java"
	 * 
	 * @param extension
	 * @return
	 */
	public TranslationCondition addSourceFileExtension(String extension) {
		this.targetSourceFileExtensionList.add(extension);
		return TranslationCondition.this;
	}

	/**
	 * Add extension to be translated like ".txt"
	 * 
	 * @param extension
	 * @return
	 */
	public TranslationCondition addDocumentFileExtension(String extension) {
		this.targetDocumentFileExtensionList.add(extension);
		return TranslationCondition.this;
	}

	/**
	 * Set operation for JavaDoc comments
	 * 
	 * @param op
	 * @return
	 */
	public TranslationCondition setJavaDocCommentOp(OpFlag op) {
		this.javaDocCommentOperation = op;
		return TranslationCondition.this;
	}

	/**
	 * Set operation for normal comments
	 * 
	 * @param op
	 * @return
	 */
	public TranslationCondition setNormalCommentOp(OpFlag op) {
		this.commentOperation = op;
		return TranslationCondition.this;
	}

	/**
	 * Set operation for Block comments
	 * 
	 * @param op
	 * @return
	 */
	public TranslationCondition setBlockCommentOp(OpFlag op) {
		this.blockCommentOperation = op;
		return TranslationCondition.this;
	}

	/**
	 * Set operation for Documents
	 * 
	 * @param op
	 * @return
	 */
	public TranslationCondition setDocumentOp(OpFlag op) {
		this.documentOperation = op;
		return TranslationCondition.this;
	}

}
