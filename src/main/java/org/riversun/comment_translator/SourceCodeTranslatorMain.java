/**
 Copyright 2006-2016 Tom Misawa(riversun.org@gmail.com)
 
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
import java.util.List;

import org.riversun.comment_translator.FileUtils.SearchFileInfo;
import org.riversun.comment_translator.SrcFileTranslator.SrcFileProgressListener;
import org.riversun.comment_translator.TranslationCondition.OpFlag;
import org.riversun.string_grabber.StringGrabber;

/**
 * Translate whole source code files in the source code directory
 * 
 * @author Tom Misawa (riversun.org@gmail.com)
 *
 */
public class SourceCodeTranslatorMain {

	public static interface SourceCodeTranslatorProgressListener {
		public void onProgress(int currentNumOfTranslation, int totalNumOfTargetTranslationLines, String targetFilePath);
	}

	public static final class ProgressInfo {
		public int totalLinesOfTranslation;
		public int currentProcessingLinesOfSrcFile;
		public int currentProcessingLinesOfProject;
	}

	private final FileUtils mFileUtil = new FileUtils();

	private SourceCodeTranslatorProgressListener mSrcFileProgressListener;

	public void setSrcFileProgressListener(SourceCodeTranslatorProgressListener listener) {
		mSrcFileProgressListener = listener;
	}

	/**
	 * Specify the directory of the source code and execute the translation
	 * according to the condition
	 * 
	 * @param srcDir
	 * @param destDir
	 * @param condition
	 */
	public void translateComments(File srcDir, File destDir, TranslationCondition condition) {

		final SearchFileInfo sourceCodeFileInfo = mFileUtil.searchFilesInDir(srcDir, condition.targetSourceFileExtensionList.toArray(new String[] {}));
		final SearchFileInfo documentFileInfo = mFileUtil.searchFilesInDir(srcDir, condition.targetDocumentFileExtensionList.toArray(new String[] {}));

		final List<String> allPathList = sourceCodeFileInfo.getAllFilePathList();

		// Source code file path list
		final List<String> sourceCodePathList = sourceCodeFileInfo.getFfilteredPathList();
		final List<String> documentPathList = documentFileInfo.getFfilteredPathList();

		final ProgressInfo pi = new ProgressInfo();

		final SrcFileTranslator srcTrans = new SrcFileTranslator();

		srcTrans.setCondition(condition);

		// STEP 1-1. Count total lines of translation
		for (String srcPath : sourceCodePathList) {

			allPathList.remove(srcPath);

			srcTrans.openFile(new File(srcPath), condition.sourceCodeCharset);

			final int totalNumOfTranslationLines = srcTrans.getTotalNumOfTargetTranslationLines();

			pi.totalLinesOfTranslation += totalNumOfTranslationLines;

		}

		// STEP 1-2. Count document files of translation
		for (String docPath : documentPathList) {

			if (condition.documentOperation == OpFlag.TRANSLATE || condition.documentOperation == OpFlag.REMOVE) {
				allPathList.remove(docPath);
				pi.totalLinesOfTranslation += 1;
			}

		}

		// STEP 2-1. Translate source codes
		for (String sourceCodePath : sourceCodePathList) {

			srcTrans.openFile(new File(sourceCodePath), condition.sourceCodeCharset);
			srcTrans.setProgressListener(new SrcFileProgressListener() {

				@Override
				public void onProgress(int currentNumOfTranslation, int totalNumOfTargetTranslationLines) {

					pi.currentProcessingLinesOfSrcFile = currentNumOfTranslation;

					// callback progress
					if (mSrcFileProgressListener != null) {
						mSrcFileProgressListener.onProgress((pi.currentProcessingLinesOfProject + currentNumOfTranslation), pi.totalLinesOfTranslation, sourceCodePath);
					}

				}
			});

			// execute translation for this source file
			final String resultText = srcTrans.translate();

			pi.currentProcessingLinesOfProject += pi.currentProcessingLinesOfSrcFile;

			writeTextToFile(srcDir, sourceCodePath, destDir, condition, resultText);

		}

		// STEP 2-2. Translate documents
		for (String docPath : documentPathList) {

			if (condition.documentOperation == OpFlag.TRANSLATE) {

				pi.currentProcessingLinesOfProject += 1;

				// callback progress
				if (mSrcFileProgressListener != null) {
					mSrcFileProgressListener.onProgress(pi.currentProcessingLinesOfProject, pi.totalLinesOfTranslation, docPath);
				}

				srcTrans.openFile(new File(docPath), condition.sourceCodeCharset);

				final String resultText = srcTrans.translateText();

				writeTextToFile(srcDir, docPath, destDir, condition, resultText);

			}
			else if (condition.documentOperation == OpFlag.REMOVE) {

				pi.currentProcessingLinesOfProject += 1;

				// callback progress
				if (mSrcFileProgressListener != null) {
					mSrcFileProgressListener.onProgress(pi.currentProcessingLinesOfProject, pi.totalLinesOfTranslation, docPath);
				}

				writeTextToFile(srcDir, docPath, destDir, condition, "");
			}

		}

		// STEP3. Copy the remainin files
		copyFiles(srcDir, destDir, condition, allPathList);

	}

	/**
	 * Write text to file
	 * 
	 * @param srcDir
	 * @param destDir
	 * @param condition
	 * @param sourceCodePath
	 * @param text
	 */
	private void writeTextToFile(File srcDir, String sourceCodePath, File destDir, TranslationCondition condition, String text) {

		final String createToPath = getCopyToPath(srcDir, destDir, sourceCodePath);

		final File createToFile = new File(createToPath);

		if (!createToFile.getParentFile().exists()) {
			createToFile.getParentFile().mkdirs();
		}

		final boolean append = false;
		final TextFileWriter tfw = new TextFileWriter();

		tfw.writeText(createToFile, text, condition.sourceCodeCharset, append);
	}

	private void copyFiles(File srcDir, File destDir, TranslationCondition condition, final List<String> filePathListToCopy) {

		// In case of non-source-code file
		if (condition.copyAllFiles) {

			for (String otherFilePath : filePathListToCopy) {

				final String copyToPath = getCopyToPath(srcDir, destDir, otherFilePath);

				final File f = new File(copyToPath);

				final File copyToDir = f.getParentFile();

				if (!copyToDir.exists()) {
					copyToDir.mkdirs();
				}

				mFileUtil.fileCopyToDir(new File(otherFilePath), copyToDir);
			}
		}
	}

	private String getCopyToPath(File srcDir, File destDir, String srcFilePath) {

		final StringGrabber sg = new StringGrabber(srcFilePath);
		sg.removeHead(srcDir.getAbsolutePath().length());

		final String copyToPath = destDir.getAbsolutePath() + sg.toString();

		return copyToPath;
	}
}
