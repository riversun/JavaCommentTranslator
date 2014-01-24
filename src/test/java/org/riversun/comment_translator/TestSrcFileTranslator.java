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

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.riversun.comment_translator.CommentReplacer.CodeType;
import org.riversun.comment_translator.SrcFileTranslator.SrcFileProgressListener;

/**
 * 
 * UT for SrcFileTranslator
 *
 */
public class TestSrcFileTranslator extends TestBase {

	// To set this false,you should put client_id/secret and something into
	// TestParamCondition.java.
	public static final boolean DRY_RUN_MODE_ENALBLED = true;

	protected SrcFileTranslator get() {

		SrcFileTranslator sft = new SrcFileTranslator();

		// NO web service client_id/secret needed on DryRun mode
		sft.setDryRun(DRY_RUN_MODE_ENALBLED);

		return sft;
	}

	@Test
	public void test_getTotalNumOfTargetTranslationLines() {

		final SrcFileTranslator sft = get();

		sft.setCondition(TestParamCondition.getCondition());

		sft.openFile(new File(SAMPLE_CODE_PATH), SOURCE_CODE_CHARSET);

		final int totalNumOfTargetTranslationLines = sft.getTotalNumOfTargetTranslationLines();

		// make sure num of comments is 29.
		assertEquals(29, totalNumOfTargetTranslationLines);
	}

	@Test
	public void test_getCommentEnclosedWithCommentChars() {

		final SrcFileTranslator sft = get();

		assertEquals("//" + "DUMMY_COMMENT", sft.getCommentEnclosedWithCommentChars(CodeType.COMMENT, "DUMMY_COMMENT"));
		assertEquals("/*" + "DUMMY_COMMENT" + "*/", sft.getCommentEnclosedWithCommentChars(CodeType.BLOCK_COMMENT, "DUMMY_COMMENT"));
		assertEquals("/**" + "DUMMY_COMMENT" + "*/", sft.getCommentEnclosedWithCommentChars(CodeType.JAVADOC_COMMENT, "DUMMY_COMMENT"));

	}

	@Test
	public void test_Translate() {

		final SrcFileTranslator sft = get();

		sft.setCondition(TestParamCondition.getCondition());
		sft.openFile(new File(SAMPLE_CODE_PATH), SOURCE_CODE_CHARSET);

		class Holder {
			int currentNumOfTranslation;
			int totalNumOfTargetTranslationLines;
		}

		final Holder holder = new Holder();

		sft.setProgressListener(new SrcFileProgressListener() {
			@Override
			public void onProgress(int currentNumOfTranslation, int totalNumOfTargetTranslationLines) {
				holder.currentNumOfTranslation = currentNumOfTranslation;
				holder.totalNumOfTargetTranslationLines = totalNumOfTargetTranslationLines;
			}
		});

		sft.translate();

		assertEquals(29, holder.currentNumOfTranslation);
		assertEquals(29, holder.totalNumOfTargetTranslationLines);

	}

}
