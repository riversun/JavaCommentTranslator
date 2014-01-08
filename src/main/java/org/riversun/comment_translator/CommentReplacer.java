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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JavaCommentRemover To remove comments from java source code<br>
 * To replace comments as you like<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class CommentReplacer {

	/**
	 * 
	 * Tag that is enclosed by some strings
	 *
	 */
	private static class TagType {
		CodeType codeType;
		String tagBegin;
		String tagEnd;
	}

	/**
	 * Code Type
	 */
	public enum CodeType {
		EXECUTABLE_CODE, // Normal Code.Not a comment
		COMMENT, // One line comment
		BLOCK_COMMENT, // BlockComemnt Code
		JAVADOC_COMMENT// JavaDoc Code
	}

	/**
	 * Callback interface When found the comment
	 */
	public static interface CommentListener {
		/**
		 * When comment found
		 * 
		 * @param commentType
		 * @param comment
		 * @return returns comment to replace.If null,the comment will be
		 *         removed from the source code<br>
		 */
		public String onCommentFound(CodeType commentType, String comment);
	}

	/**
	 * Specify NEWLINE according to your code environment
	 */
	public static String NEWLINE = "\r\n";

	public static final String JAVADOC_COMMENT_STARTED = "/**";
	public static final String JAVADOC_COMMENT_FINISHED = "*/";
	public static final String BLOCK_COMMENT_STARTED = "/*";
	public static final String BLOCK_COMMENT_FINISHED = "*/";
	public static final String BLOCK_COMMENT_MID = "*";

	public static final String COMMENT_STARTED = "//";
	public static final String COMMENT_FINISHED = NEWLINE;
	private CodeType mScanMode = CodeType.EXECUTABLE_CODE;
	private String mCurrentSrcCode = "";
	private int mCurrentSrcCodeLen = 0;
	private CommentListener mCommentListener = null;
	private List<TagType> mTagTypeList = new ArrayList<TagType>();
	private Map<CodeType, StringBuilder> mBufferMap = new HashMap<CodeType, StringBuilder>();

	public CommentReplacer() {
		initialize();
	}

	public void setCommentListener(CommentListener commentListener) {
		mCommentListener = commentListener;
	}

	/**
	 * Get comment removed/comment replaced source code
	 * 
	 * @param sourceCode
	 * @return edited sourceCode
	 */
	public String replaceComment(String sourceCode) {

		mBufferMap.clear();

		mCurrentSrcCode = sourceCode;
		mCurrentSrcCodeLen = sourceCode.length();

		int index = 0;

		charScanLoop: while (index < mCurrentSrcCodeLen) {

			for (TagType tagType : mTagTypeList) {

				// if [tagBegin] found
				if (isStartsWith(index, tagType.tagBegin)) {

					if (CodeType.EXECUTABLE_CODE == mScanMode) {
						index += lenthOf(tagType.tagBegin);
						mScanMode = tagType.codeType;
						continue charScanLoop;
					}
				}
				// if [tagEnd] found
				else if (isStartsWith(index, tagType.tagEnd)) {

					if (tagType.codeType == mScanMode) {
						index += lenthOf(tagType.tagEnd);
						mScanMode = CodeType.EXECUTABLE_CODE;

						final String blockComment = tagType.tagBegin + getCommentBuffer(tagType.codeType).toString() + tagType.tagEnd;
						final String commentToReplace = onCommentFound(tagType.codeType, blockComment);

						if (commentToReplace != null) {
							getCommentBuffer(CodeType.EXECUTABLE_CODE).append(commentToReplace);
						}

						// clear buffer
						getCommentBuffer(tagType.codeType).setLength(0);

						continue charScanLoop;
					}
				}

			}

			final String currentChar = stringAt(index);

			getCommentBuffer(mScanMode).append(currentChar);

			index++;

		}

		return getCommentBuffer(CodeType.EXECUTABLE_CODE).toString();
	}

	/**
	 * Register comment tags<br>
	 * You can add the original tags
	 */
	private void initialize() {

		// Register JavaDoc Comment Tag
		final TagType tagTypeJavaDocComment = new TagType();
		tagTypeJavaDocComment.codeType = CodeType.JAVADOC_COMMENT;
		tagTypeJavaDocComment.tagBegin = JAVADOC_COMMENT_STARTED;
		tagTypeJavaDocComment.tagEnd = JAVADOC_COMMENT_FINISHED;
		mTagTypeList.add(tagTypeJavaDocComment);

		// Register Block Comment Tag
		final TagType tagTypeBlockComment = new TagType();
		tagTypeBlockComment.codeType = CodeType.BLOCK_COMMENT;
		tagTypeBlockComment.tagBegin = BLOCK_COMMENT_STARTED;
		tagTypeBlockComment.tagEnd = BLOCK_COMMENT_FINISHED;
		mTagTypeList.add(tagTypeBlockComment);

		// Register Comment Tag

		final TagType tagTypeNormalComment = new TagType();
		tagTypeNormalComment.codeType = CodeType.COMMENT;
		tagTypeNormalComment.tagBegin = COMMENT_STARTED;
		tagTypeNormalComment.tagEnd = COMMENT_FINISHED;
		mTagTypeList.add(tagTypeNormalComment);
	}

	private String onCommentFound(CodeType commentType, String comment) {

		if (mCommentListener != null) {
			return mCommentListener.onCommentFound(commentType, comment);
		}
		return null;
	}

	private StringBuilder getCommentBuffer(CodeType mode) {

		StringBuilder buffer = mBufferMap.get(mode);

		if (buffer == null) {
			buffer = new StringBuilder();
			mBufferMap.put(mode, buffer);
		}
		return buffer;

	}

	/**
	 * Get string at specified position
	 * 
	 * @param index
	 * @return
	 */
	private String stringAt(int index) {

		final char charAt = mCurrentSrcCode.charAt(index);
		return String.valueOf(charAt);

	}

	private boolean isStartsWith(int fromIndex, String text) {

		final int textLen = text.length();

		final StringBuilder target = new StringBuilder();

		final int fromPos = fromIndex;
		final int toPos = fromIndex + textLen;

		if (toPos >= mCurrentSrcCodeLen) {
			return false;
		}

		for (int i = fromPos; i < toPos; i++) {
			target.append(stringAt(i));
		}

		if (target.toString().equals(text)) {
			return true;
		} else {
			return false;
		}
	}

	private int lenthOf(String str) {
		return str.length();
	}

}