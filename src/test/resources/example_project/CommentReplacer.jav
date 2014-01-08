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
package example_project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.riversun.string_grabber.StringGrabber;

/**
 * CommentReplacer To remove comments from java source code<br>
 * To replace comments as you like<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 */
public class CommentReplacer {

	private static char BYTE_ORDER_MARK = 0xFEFF;

	/**
	 * 
	 * Tag that is enclosed by some strings
	 *
	 */
	private static class TagType {
		CodeType codeType;
		String tagBegin;
		String tagEnd;

		@Override
		public String toString() {
			return "TagType [codeType=" + codeType + "]";
		}

	}

	/**
	 * Flag to determine whether callback with comment-tag like '//' or not
	 */
	private boolean commentCallbackWithTag = true;

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
	private CodeType mCurrentScanMode = CodeType.EXECUTABLE_CODE;

	private String mCurrentSrcCode = "";
	private int mCurrentSrcCodeLen = 0;

	private List<TagType> mTagTypeList = new ArrayList<TagType>();
	private Map<CodeType, StringBuilder> mBufferMap = new HashMap<CodeType, StringBuilder>();

	public CommentReplacer() {
		initialize();
	}

	private void setCurrentScanMode(CodeType scanMode) {

		mCurrentScanMode = scanMode;

	}

	/**
	 * Get comment removed/comment replaced source code
	 * 
	 * @param sourceCode
	 * @return edited sourceCode
	 */
	public List<CodeBlock> getCodeBlock(String sourceCode) {

		final List<CodeBlock> codeBlockList = new ArrayList<CodeBlock>();

		mBufferMap.clear();

		mCurrentSrcCode = sourceCode;
		mCurrentSrcCodeLen = sourceCode.length();

		int index = 0;

		charScanLoop: while (index < mCurrentSrcCodeLen) {

			for (TagType tagType : mTagTypeList) {

				// if [tagBegin] found
				if (isStartsWith(index, tagType.tagBegin)) {

					if (mCurrentScanMode == CodeType.EXECUTABLE_CODE) {

						String crrExecutableCode = getCommentBuffer(mCurrentScanMode).toString();
						
						if (crrExecutableCode.length() > 0) {

							CodeBlock cb = createCodeBlock(mCurrentScanMode, crrExecutableCode);
							if (cb != null) {
								codeBlockList.add(cb);
							}

							// clear buffer
							getCommentBuffer(mCurrentScanMode).setLength(0);
						}

						// move the index forward by adding index
						index += lenthOf(tagType.tagBegin);

						setCurrentScanMode(tagType.codeType);

						continue charScanLoop;
					}

				}
				// if [tagEnd] found
				else if (isStartsWith(index, tagType.tagEnd)) {

					if (mCurrentScanMode == tagType.codeType) {

						// move the index forward by adding index
						index += lenthOf(tagType.tagEnd);

						// reset mode to executable code scanning mode
						setCurrentScanMode(CodeType.EXECUTABLE_CODE);

						final String anyComment;

						if (commentCallbackWithTag) {
							anyComment = tagType.tagBegin + getCommentBuffer(tagType.codeType).toString() + tagType.tagEnd;
						} else {
							anyComment = getCommentBuffer(tagType.codeType).toString();
						}

						CodeBlock cb = createCodeBlock(tagType.codeType, anyComment);
						if (cb != null) {
							codeBlockList.add(cb);
						}
						// clear buffer
						getCommentBuffer(tagType.codeType).setLength(0);

						continue charScanLoop;
					}
				} else {

				}

			}

			final String currentChar = stringAt(index);

			getCommentBuffer(mCurrentScanMode).append(currentChar);

			index++;

		}

		// Add executable code at last
		if (getCommentBuffer(mCurrentScanMode).length() > 0) {
			String crrExecutableCode = getCommentBuffer(mCurrentScanMode).toString();
			if (crrExecutableCode.length() > 0) {
				final CodeBlock cb = new CodeBlock();
				cb.tagType = mCurrentScanMode;
				cb.value = crrExecutableCode;
				codeBlockList.add(cb);
			}
		}

		return codeBlockList;
	}

	private CodeBlock createCodeBlock(CodeType codeType, final String comment) {

		final CodeBlock cb = new CodeBlock();

		StringGrabber sg = new StringGrabber(comment);
		sg.removeHeadAndTailChars(' ');

		cb.tagType = codeType;
		cb.value = sg.toString();

		// when BOM(byte order mark) found,skip processing
		if (cb.value.length() == 1 && cb.value.charAt(0) == BYTE_ORDER_MARK) {
			return null;
		}

		// make sure this value is not space or tab only string.
		if (cb.value.replace(" ", "").replace("\t", "").replace("\n", "").replace("\r", "").length() > 0) {
			return cb;
		} else {
			return null;
		}
	}

	public class CodeBlock {
		public CodeType tagType;
		public String value;

		@Override
		public String toString() {
			return "Tags [tagType=" + tagType + ", value=" + value + "]";
		}

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

	/**
	 * Set callback commented code with comment tag or not
	 * 
	 * @param commentCallbackWithTag
	 */
	public void setCommentCallbackWithTag(boolean commentCallbackWithTag) {

		this.commentCallbackWithTag = commentCallbackWithTag;
	}

	private int lenthOf(String str) {
		return str.length();
	}

}