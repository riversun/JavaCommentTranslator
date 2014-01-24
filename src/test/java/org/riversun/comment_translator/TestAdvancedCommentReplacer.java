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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.riversun.comment_translator.CommentReplacer.CodeBlock;
import org.riversun.comment_translator.CommentReplacer.CodeType;

/**
 * 
 * UT for AdvancedCommentReplacer
 *
 */
public class TestAdvancedCommentReplacer extends TestBase {

	protected AdvancedCommentReplacer get() {
		final AdvancedCommentReplacer acr = new AdvancedCommentReplacer();
		acr.setCommentCallbackWithTag(false);
		return acr;
	}

	@Test
	public void test_getCodeBlock_can_get_JAVADOC_COMMENT() {
		final AdvancedCommentReplacer acr = get();

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);

		CodeBlock cb;
		cb = cbList.get(0);
		assertTrue(cb.tagType == CodeType.JAVADOC_COMMENT);
	}

	@Test
	public void test_getCodeBlock_can_get_NORMAL_COMMENT() {

		final AdvancedCommentReplacer acr = get();

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);

		CodeBlock cb;
		cb = cbList.get(2);
		assertTrue(cb.tagType == CodeType.COMMENT);

	}

	@Test
	public void test_getCodeBlock_can_get_BLOCK_COMMENT() {
		final AdvancedCommentReplacer acr = get();

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);

		CodeBlock cb;
		cb = cbList.get(8);
		assertTrue(cb.tagType == CodeType.BLOCK_COMMENT);
	}

	@Test
	public void test_getCodeBlock_last_block_is_executable_code() {
		final AdvancedCommentReplacer acr = get();

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);

		CodeBlock cb;
		cb = cbList.get(cbList.size() - 1);
		assertTrue(cb.tagType == CodeType.EXECUTABLE_CODE);
	}

	@Test
	public void test_getCodeBlock_handle_multiple_normal_comemnts_as_one_line() {

		final AdvancedCommentReplacer acr = get();

		// To enable explicitly(default is enabled)
		acr.setMergeConsequitiveComments(true);

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);

		CodeBlock cb;

		cb = cbList.get(6);

		/**
		 * Handle like below comment as 'Tag that is enclosed by some strings.<br>
		 * It's important for usage of translation.<br>
		 * 
		 * <code>
		 * // Tag that is<br>
		 * // enclosed by<br>
		 * // some strings<br>
		 * </code>
		 */

		assertEquals("Tag that is enclosed by some strings", cb.value);
	}

	@Test
	public void test_setCommentCallbackWithTag() {

		final AdvancedCommentReplacer acr = get();
		acr.setCommentCallbackWithTag(true);

		List<CodeBlock> cbList = acr.getCodeBlock(mSourceCode);
		CodeBlock cb;
		cb = cbList.get(2);

		// check if return with comment tag
		assertEquals("// Imports", cb.value);

	}

	@Test
	public void testRemoveUnnecessaryNewline() {

		assertEquals("A\nB", get().removeUnnecessaryNewline("A\nB\n\n"));
		assertEquals("A", get().removeUnnecessaryNewline("\n\nA"));

	}

}
