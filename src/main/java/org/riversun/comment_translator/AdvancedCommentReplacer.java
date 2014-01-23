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

/**
 * AdvancedCommentReplacer To replace comments as you like<br>
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class AdvancedCommentReplacer extends CommentReplacer {

	private static String COMMENT_SPACER = " ";
	private boolean mIsMergeConsequitiveComments = true;
	private boolean mIsRemoveUnnecessaryNewline = true;

	@Override
	public List<CodeBlock> getCodeBlock(String sourceCode) {

		final List<CodeBlock> resList = super.getCodeBlock(sourceCode);

		final List<CodeBlock> retCodeBlockList = new ArrayList<CodeBlock>();

		for (int i = 0; i < resList.size(); i++) {

			final CodeBlock codeBlock = resList.get(i);

			CodeBlock nextCodeBlock = null;

			if (i + 1 < resList.size() - 1) {
				nextCodeBlock = resList.get(i + 1);
			}

			if (mIsMergeConsequitiveComments && codeBlock.tagType == CodeType.COMMENT && nextCodeBlock != null && nextCodeBlock.tagType == CodeType.COMMENT) {

				// if tag is COMMENT and next tag will also COMMENT tag.
				nextCodeBlock.value = codeBlock.value + COMMENT_SPACER + nextCodeBlock.value;

			} else {

				if (mIsRemoveUnnecessaryNewline) {
					codeBlock.value = removeUnnecessaryNewline(codeBlock.value);
				}

				if (codeBlock.value.length() > 0) {
					retCodeBlockList.add(codeBlock);
				}
			}

		}

		return retCodeBlockList;

	}

	/**
	 * Enables handling multi line normal comments as "ONE LINE" comment
	 * 
	 * @param mergeConsequitiveComments
	 */
	public void setMergeConsequitiveComments(boolean mergeConsequitiveComments) {
		this.mIsMergeConsequitiveComments = mergeConsequitiveComments;
	}

	public void setRemoveUnnecessaryNewline(boolean removeUnnecessaryNewline) {
		this.mIsRemoveUnnecessaryNewline = removeUnnecessaryNewline;
	}

	String removeUnnecessaryNewline(String text) {
		if (text == null || text.isEmpty()) {
			return text;
		}

		final String[] separatedText = text.replace("\r", "").split("\n");

		final StringBuilder sb = new StringBuilder();
		for (int i = 0; i < separatedText.length; i++) {

			String line = separatedText[i];

			if (line.length() > 0) {

				sb.append(line);

				if (i + 1 < separatedText.length) {
					// if not final part,add newline to tail
					sb.append("\n");
				}

			} else {
				// line contains blank char or newline char only.
			}
		}
		return sb.toString();
	}

}
