package io.vertigo.chatbot.commons;

public class FileDescriptor {

	private String fileName;

	private String fileContent;

	private String fileType;

	public FileDescriptor() {

	}

	public FileDescriptor(final String fileName, final String fileContent, final String fileType) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(final String fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(final String fileType) {
		this.fileType = fileType;
	}
}
