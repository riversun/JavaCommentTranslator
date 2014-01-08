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

import org.junit.After;
import org.junit.Before;

public class TestBase {
	protected static final String PROJECT_PATH = new File(".").getAbsoluteFile().getParent();
	protected static final String SAMPLE_CODE_PATH = PROJECT_PATH + "/src/test/resources/" + "SampleCode.txt";
	protected static final String SAMPLE_PROJECT_PATH = PROJECT_PATH + "/src/test/resources/example_project";
	protected static final String SAMPLE_PROJECT_OUT_PATH = PROJECT_PATH + "/src/test/resources/generated_project";
	protected static final String SOURCE_CODE_CHARSET = "UTF-8";
	protected String mSourceCode;

	@Before
	public void setUp() throws Exception {
		TextFileReader tr = new TextFileReader();
		mSourceCode = tr.readText(new File(SAMPLE_CODE_PATH), "UTF-8");
	}

	@After
	public void tearDown() throws Exception {
	}
}
