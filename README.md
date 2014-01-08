# Overview
'comment_translator' is a java library that can translate comments in the source code.
You can translate comments written in national languages into your familiar language.

- You can translate entire files in the specified directory.
- You can also REMOVE comments in source code.

It is licensed under [Apache License ver. 2.0](https://www.apache.org/licenses/LICENSE-2.0).


# Preparation
Since this library uses Microsoft Translator API internally, in order to actually use it, it is necessary to obtain clientId/clientSecret from below.

- Microsoft Translator API<br>
https://datamarket.azure.com/dataset/bing/microsofttranslator<br>

- For the use of Microsoft Translator API, follow Microsoft's terms of service.

# Quick Example
```java


public class TranslationTest {

	public static void main(String[] args) {

		// - proxy information if needed
		String proxyHost = "[YOUR_PROXY_IF_NEEDED]";
		int proxyPort = 8080;

		// - translate-from language, and you can specify the language or
		// choose auto_detect
		Language fromLang = Language.AUTO_DETECT;

		// - translate-to language
		Language toLang = Language.FRENCH;

		// - clientID,clientSecret of Translation API
		// (Not required when dryrun)
		final String clientID = "[YOUR_CLIENT_ID]";
		final String clientSecret = "[YOUR_CLIENT_SECRET]";

		TranslationCondition condition = new TranslationCondition();

		// - Add extension(s) for source code
		condition.addSourceFileExtension(".java");

		// - Add extension(s) for document file(text format)
    //if you want to translate with your source code files
		condition.addDocumentFileExtension(".txt");

		condition.setFromLang(fromLang);
		condition.setToLang(toLang);
		condition.setProxy(proxyHost, proxyPort);

		condition.setClientID(clientID, clientSecret);

		// - Sets whether to copy all files (except source code) or not
		condition.setCopyAllFiles(true);

		// - Set translation operation for JavaDoc Comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setJavaDocCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for Normal comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setNormalCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for Block comments from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setBlockCommentOp(OpFlag.TRANSLATE);

		// - Set translation operation for document in text file format from
		// TRANSLATE/REMOVE(COMMENTS)/NOT_TRANSLATE
		condition.setDocumentOp(OpFlag.TRANSLATE);

		// - Set charset(It is also used for document translation)
		condition.setSourceCodeCharset("UTF-8");

		//Source Directory(Folder)
		final File srcDirFile = new File("c:/temp/SrcProject");

		//Translation destination folder (created after translation)
		final File destDirFile = new File("c:/temp/TranslatedProject");

		SourceCodeTranslatorMain obj = new SourceCodeTranslatorMain();

    obj.setSrcFileProgressListener(new SourceCodeTranslatorProgressListener() {
			@Override
			public void onProgress(int crr, int ttl, String targetFilePath) {
        //Show translation progress if you want
				System.out.println(crr + "/" + ttl + " " + targetFilePath);
			}
		});

    //Execute translation
		obj.translateComments(srcDirFile, destDirFile, condition);
  }
}

```
## Translate only comments in java source code?
It corresponds to translation of the following three comment types

- Normal comment
```java
// comment
```
- Block comment
```java
/* comment */
```
- JavaDoc comment
```java
/** comment */
```

If you have a <strong>C++ project</strong>, you can add extensions like followings.
```java
	condition.addSourceFileExtension(".cpp");
	condition.addSourceFileExtension(".c");
	condition.addSourceFileExtension(".h");
```

## Want to remove comments?
When you want to remove comments, edit condition for translation operation like followings.

```java
condition.setJavaDocCommentOp(OpFlag.REMOVE);
condition.setNormalCommentOp(OpFlag.REMOVE);
condition.setBlockCommentOp(OpFlag.REMOVE);
```


## How to specify language
### Auto detect(default)
```java
condition.setFromLang(Language.AUTO_DETECT);
condition.setToLang(Language.FRENCH);
```

### Specify language
```java
condition.setFromLang(Language.ENGLISH);
condition.setToLang(Language.FRENCH);
```
