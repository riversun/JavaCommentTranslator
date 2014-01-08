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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;
import org.riversun.comment_translator.SourceCodeTranslatorMain.SourceCodeTranslatorProgressListener;
import org.riversun.comment_translator.TranslationCondition.OpFlag;

import com.memetix.mst.language.Language;

/**
 * UT for SourceCodeTranslatorMain
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class TestSourceCodeTranslatorMain extends TestBase {

	protected SourceCodeTranslatorMain get() {

		SourceCodeTranslatorMain obj = new SourceCodeTranslatorMain();

		// NO web service client_id/secret needed on DryRun mode

		return obj;
	}

	@Test
	public void test_translate_entire_contents_of_the_dir() {

		// The entire contents of the directory

		final boolean DRY_RUN_MODE = true;

		// - proxy information if needed
		String proxyHost = "[YOUR_PROXY_IF_NEEDED]";
		int proxyPort = 8080;

		// - translate-from language, and you can specify the language or
		// choose auto_detect
		Language fromLang = Language.AUTO_DETECT;

		// - translate-to language
		Language toLang = Language.FRENCH;

		// - clientID,clientSecret of Translation API
		// (Not required when dryrun)
		final String clientID = "[YOUR_CLIENT_ID]";
		final String clientSecret = "[YOUR_CLIENT_SECRET]";

		TranslationCondition condition = new TranslationCondition();

		// - Add extension(s) for source code
		// For the sake of the sample, use "jav" for convenience
		condition.addSourceFileExtension(".jav");

		// - Add extension(s) for document file(text format)
		condition.addDocumentFileExtension(".txt");

		condition.setFromLang(fromLang);
		condition.setToLang(toLang);
		condition.setProxy(proxyHost, proxyPort);

		condition.setClientID(clientID, clientSecret);

		// - Sets whether to copy all files (except source code) or not
		condition.setCopyAllFiles(true);

		// - When you want to execute translation without actually translating
		condition.setDryRunModeEnabled(DRY_RUN_MODE);

		// - Set translation operation for JavaDoc Comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setJavaDocCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for Normal comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setNormalCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for Block comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setBlockCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for document in text file format from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setDocumentOp(OpFlag.TRANSLATE);

		// - Set charset(It is also used for document translation)
		condition.setSourceCodeCharset("UTF-8");

		final File srcDirFile = new File(SAMPLE_PROJECT_PATH);
		final File destDirFile = new File(SAMPLE_PROJECT_OUT_PATH);

		final SourceCodeTranslatorMain sourceCodeTranslatorMain = get();

		final class Holder {
			int currentNumOfTranslation;
			int totalNumOfTargetTranslationLines;
		}

		final Holder holder = new Holder();

		sourceCodeTranslatorMain.setSrcFileProgressListener(new SourceCodeTranslatorProgressListener() {

			@Override
			public void onProgress(int currentNumOfTranslation, int totalNumOfTargetTranslationLines, String targetFilePath) {

				// Show progress
				System.out.println(currentNumOfTranslation + "/" + totalNumOfTargetTranslationLines + " " + targetFilePath);

				holder.currentNumOfTranslation = currentNumOfTranslation;
				holder.totalNumOfTargetTranslationLines = totalNumOfTargetTranslationLines;

			}
		});

		sourceCodeTranslatorMain.translateComments(srcDirFile, destDirFile, condition);

		assertEquals(37, holder.currentNumOfTranslation);
		assertEquals(37, holder.totalNumOfTargetTranslationLines);
	}
}
