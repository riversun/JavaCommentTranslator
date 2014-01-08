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

import java.io.File;
import java.io.IOException;

import org.riversun.comment_translator.CommentReplacer.CodeType;
import org.riversun.comment_translator.CommentReplacer.CommentListener;
import org.riversun.string_grabber.StringGrabber;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

/**
 * Translate comments in single java source file to your favorite language
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class SrcFileTranslator {

	public static interface ProgressListener {
		public void onProgress(int currentNumOfTranslation, int totalNumOfTargetTranslationLines);
	}

	private int mCurrentNumOfTargetTranslationLines = 0;
	private int mTotalNumOfTargetTranslationLines = 0;
	private ProgressListener mProgressListener = null;
	private String mSourceCodeOrg = null;
	private TranslationCondition mCondition = null;
	private final CommentReplacer mCommentReplacer = new CommentReplacer();

	private final CommentListener mTotalLinesToTranslationCounter = new CommentListener() {
		@Override
		public String onCommentFound(CodeType commentType, String comment) {

			final String commentCharRemovedText = removeCommentChars(comment);

			// if blank then return;
			if (isBlank(commentCharRemovedText)) {
				return null;
			}
			mTotalNumOfTargetTranslationLines++;
			return null;
		}
	};

	private final CommentListener mTranslationController = new CommentListener() {

		@Override
		public String onCommentFound(CodeType commentType, String comment) {

			final String commentCharRemovedText = removeCommentChars(comment);

			// if blank then return;
			if (isBlank(commentCharRemovedText)) {
				return null;
			}

			mCurrentNumOfTargetTranslationLines++;

			if (mProgressListener != null) {
				mProgressListener.onProgress(mCurrentNumOfTargetTranslationLines, mTotalNumOfTargetTranslationLines);
			}
			switch (commentType) {

			case JAVADOC_COMMENT:
				return CommentReplacer.JAVADOC_COMMENT_STARTED + doTranslate(mCondition.fromLang, commentCharRemovedText, mCondition.toLang) + CommentReplacer.JAVADOC_COMMENT_FINISHED;

			case BLOCK_COMMENT:
				return CommentReplacer.BLOCK_COMMENT_STARTED + doTranslate(mCondition.fromLang, commentCharRemovedText, mCondition.toLang) + CommentReplacer.BLOCK_COMMENT_FINISHED;

			case COMMENT:
				return CommentReplacer.COMMENT_STARTED + doTranslate(mCondition.fromLang, commentCharRemovedText, mCondition.toLang);

			default:

			}

			return null;

		}
	};

	/**
	 * Open and read a java source file to prepare translation
	 * 
	 * @param srcFile
	 * @param charset
	 * @return
	 */
	public boolean openFile(File srcFile, String charset) {

		final TextFileReader tfr = new TextFileReader();

		try {
			mSourceCodeOrg = tfr.readText(srcFile, charset);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;

	}

	/**
	 * Set the condition
	 * 
	 * @param condition
	 */
	public void setCondition(TranslationCondition condition) {
		mCondition = condition;

	}

	/**
	 * Set the progress listener<br>
	 * Progress listener callbacks the translation progress
	 * 
	 * @param listener
	 */
	public void setProgressListener(ProgressListener listener) {
		mProgressListener = listener;
	}

	/**
	 * Execute translation
	 * 
	 * @return
	 * @throws IOException
	 */
	public String translate() throws IOException {
		if (mCondition == null) {
			throw new RuntimeException("TranslationCondition may have not been specified.Please call setCondition() at first.");
		}
		if (mSourceCodeOrg == null || mSourceCodeOrg.isEmpty()) {
			throw new RuntimeException("Target source file may have not been specified.Please call openFile().");

		}
		getTotalNumOfTargetTranslationLines();

		mCommentReplacer.setCommentListener(mTranslationController);

		final String sourceCodeChanged = mCommentReplacer.replaceComment(mSourceCodeOrg);
		return sourceCodeChanged;

	}

	/**
	 * Returns the number of lines to translate in file
	 * 
	 * @param srcFile
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public int getTotalNumOfTargetTranslationLines() throws IOException {
		if (mSourceCodeOrg == null || mSourceCodeOrg.isEmpty()) {
			throw new RuntimeException("Target source file may have not been specified.Please call openFile() at first.");

		}
		mCurrentNumOfTargetTranslationLines = 0;
		mTotalNumOfTargetTranslationLines = 0;

		mCommentReplacer.setCommentListener(mTotalLinesToTranslationCounter);
		mCommentReplacer.replaceComment(mSourceCodeOrg);

		return mTotalNumOfTargetTranslationLines;
	}

	/**
	 * remove comment char like '//' from comment
	 * 
	 * @param comment
	 * @return
	 */
	private String removeCommentChars(String comment) {

		final String spaceRemoved = removeStartingSpaces(comment);
		final StringGrabber sg = new StringGrabber(spaceRemoved);

		// starts
		if (spaceRemoved.startsWith(CommentReplacer.JAVADOC_COMMENT_STARTED)) {
			sg.removeHead(CommentReplacer.JAVADOC_COMMENT_STARTED.length());
		} else if (spaceRemoved.startsWith(CommentReplacer.BLOCK_COMMENT_STARTED)) {
			sg.removeHead(CommentReplacer.BLOCK_COMMENT_STARTED.length());
		} else if (spaceRemoved.startsWith(CommentReplacer.BLOCK_COMMENT_MID)) {
			sg.removeHead(CommentReplacer.BLOCK_COMMENT_MID.length());
		} else if (spaceRemoved.startsWith(CommentReplacer.COMMENT_STARTED)) {
			sg.removeHead(CommentReplacer.COMMENT_STARTED.length());
		}

		// ends
		if (spaceRemoved.endsWith(CommentReplacer.JAVADOC_COMMENT_FINISHED)) {
			sg.removeTail(CommentReplacer.JAVADOC_COMMENT_FINISHED.length());
		}

		return sg.toString();
	}

	/**
	 * Remove consecutive space from the beginning of String
	 * 
	 * @param comment
	 * @return
	 */
	private String removeStartingSpaces(String comment) {
		return removeConsecutiveCharsFromBeginning(comment, " ");
	}

	/**
	 * Remove consecutive char from the beginning of String
	 * 
	 * @param text
	 * @param chars
	 * @return
	 */
	private String removeConsecutiveCharsFromBeginning(String text, String chars) {
		final StringGrabber sg = new StringGrabber(text);
		while (sg.toString().startsWith(chars)) {
			sg.removeHead(chars.length());
		}
		return sg.toString();
	}

	private boolean isBlank(String text) {
		return text.replace("\r", "").replace("\n", "").length() == 0;
	}

	private String doTranslate(Language from, String fromText, Language to) {

		if (to == null) {
			throw new RuntimeException("Language has not been specified. Please call setToLanguage to set language.");
		}

		// set proxy if you want to use.
		Translate.setProxy(mCondition.proxyHost, mCondition.proxyPort);

		// Set the Client ID / Client Secret once per JVM. It is set statically
		// and applies to all services
		Translate.setClientId(mCondition.clientID);
		Translate.setClientSecret(mCondition.clientSecret);

		String translatedText = fromText;

		try {
			if (from != null && !from.equals(Language.AUTO_DETECT)) {
				translatedText = Translate.execute(fromText, from, to);
			} else {
				translatedText = Translate.execute(fromText, to);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return translatedText;

	}
}
