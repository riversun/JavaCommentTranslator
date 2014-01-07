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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * FileUtils for Searching a file and coping a file
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class FileUtils {

	public static class SearchFileInfo {

		private List<String> allFilePathList = new ArrayList<String>();

		private List<String> filteredPathList = new ArrayList<String>();

		public List<String> getAllFilePathList() {
			return allFilePathList;
		}

		public List<String> getFfilteredPathList() {
			return filteredPathList;
		}
	}

	/**
	 * Search files with the specified extension in the directory
	 * 
	 * @param dir
	 * @param extensions
	 * @return
	 */
	public SearchFileInfo searchFilesInDir(File dir, String... extensions) {

		SearchFileInfo allFilesInDir = getAllFilesInDir(dir, null);
		List<String> allFilePathList = allFilesInDir.getAllFilePathList();

		for (String path : allFilePathList) {

			for (String extension : extensions) {

				if (path.endsWith(extension)) {
					allFilesInDir.filteredPathList.add(path);
				}

			}

		}
		return allFilesInDir;
	}

	/**
	 * Search files that have specified extension in dir
	 * 
	 * @param dir
	 * @param extension
	 * @param out
	 * @return
	 */
	private SearchFileInfo getAllFilesInDir(File dir, SearchFileInfo out) {

		if (out == null) {
			out = new SearchFileInfo();
		}

		final File[] childDirs = dir.listFiles();

		for (File file : childDirs) {

			if (file.exists() == false) {

				continue;

			} else if (file.isDirectory()) {

				getAllFilesInDir(file, out);

			} else if (file.isFile()) {

				out.allFilePathList.add(file.getAbsolutePath());

			}

		}
		return out;
	}

	/**
	 * exec File Copy
	 * 
	 * @param fromFile
	 * @param toDir
	 * @return
	 */
	public boolean fileCopyToDir(File fromFile, File toDir) {

		final File copyToFile = new File(toDir.getPath() + File.separator + fromFile.getName());

		FileChannel channelFrom = null;
		FileChannel channelTo = null;

		if (copyToFile.exists()) {
			// copytofile already exists
			return false;
		}

		try {

			copyToFile.createNewFile();

			channelFrom = new FileInputStream(fromFile).getChannel();
			channelTo = new FileOutputStream(copyToFile).getChannel();
			channelFrom.transferTo(0, channelFrom.size(), channelTo);

			return true;

		} catch (IOException e) {

			e.printStackTrace();
			return false;

		} finally {

			try {
				if (channelFrom != null) {
					channelFrom.close();
				}
				if (channelTo != null) {
					channelTo.close();
				}

				// copy with updated date
				copyToFile.setLastModified(fromFile.lastModified());

			} catch (IOException e) {

				e.printStackTrace();

				return false;
			}
		}
	}
}
