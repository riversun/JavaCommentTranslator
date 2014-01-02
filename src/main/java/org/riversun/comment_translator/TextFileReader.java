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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Read text from file/inputStream easily
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class TextFileReader {

	/**
	 * Read whole text char by char
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String readTextCharByChar(File file) throws IOException {
		return readTextCharByChar(file, null);
	}

	/**
	 * Read whole text from file char by char
	 * 
	 * @param file
	 * @param charset
	 *            specify character set like 'UTF-8'
	 * @return
	 * @throws IOException
	 */
	public String readTextCharByChar(File file, String charset) throws IOException {

		FileInputStream fis = null;

		fis = new FileInputStream(file);

		return readTextCharByChar(fis, charset);

	}

	/**
	 * Read whole text from input stream char by char
	 * 
	 * @param is
	 * @param charset
	 *            specify character set like 'UTF-8'
	 * @return
	 * @throws IOException
	 */
	public String readTextCharByChar(InputStream is, String charset) throws IOException {

		final StringBuilder sb = new StringBuilder();

		InputStreamReader isr = null;
		BufferedReader br = null;

		try {

			if (isNotBlank(charset)) {
				isr = new InputStreamReader(is, charset);
			} else {
				isr = new InputStreamReader(is);
			}

			br = new BufferedReader(isr);

			int iChar = br.read();

			while (iChar != -1) {
				sb.append((char) iChar);
				iChar = br.read();
			}

		} finally {

			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return sb.toString();

	}

	/**
	 * Read whole text from file line by line
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String readText(File file) throws IOException {
		return readText(file, null);
	}

	/**
	 * Read whole text from file line by line
	 * 
	 * @param file
	 * @param charset
	 *            specify character set like 'UTF-8'
	 * @return
	 * @throws IOException
	 */
	public String readText(File file, String charset) throws IOException {
		return readTextCharByChar(file, charset);
	}

	/**
	 * Read whole text as list from file line by line
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public List<String> readTextAsList(File file) throws IOException {
		return readTextAsList(file, null);
	}

	/**
	 * Read whole text as list from file line by line
	 * 
	 * @param file
	 * @param charset
	 *            specify character set like 'UTF-8'
	 * @return
	 * @throws IOException
	 */
	public List<String> readTextAsList(File file, String charset) throws IOException {

		FileInputStream fis = new FileInputStream(file);

		return readTextAsList(fis, charset);

	}

	/**
	 * Read whole text as list from inputStream line by line
	 * 
	 * @param is
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public List<String> readTextAsList(InputStream is, String charset) throws IOException {

		final List<String> lineList = new ArrayList<String>();

		InputStreamReader isr = null;
		BufferedReader br = null;

		try {

			if (isNotBlank(charset)) {
				isr = new InputStreamReader(is, charset);
			} else {
				isr = new InputStreamReader(is);
			}

			br = new BufferedReader(isr);

			String line;

			while ((line = br.readLine()) != null) {
				lineList.add(line);
			}

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
			if (isr != null) {
				try {
					isr.close();
				} catch (IOException e) {
				}
			}
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return lineList;
	}

	/**
	 * Convert List<String> into String<br>
	 * Returns blank text if stringList is null.
	 * 
	 * @param stringList
	 * @return
	 */
	private String getStringFromStringList(List<String> stringList) {

		final StringBuilder sb = new StringBuilder();

		if (stringList != null) {

			final int numOfLines = stringList.size();

			for (int i = 0; i < numOfLines; i++) {
				sb.append(stringList.get(i));
			}

		}
		return sb.toString();
	}

	/**
	 * Returns true if string is not NULL and if length greater than 0.
	 * 
	 * @param str
	 * @return
	 */
	private boolean isNotBlank(String str) {

		if (str != null && !str.isEmpty()) {
			return true;
		}
		return false;
	}
}