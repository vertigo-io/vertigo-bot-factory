package io.vertigo.chatbot.executor.mail;

public class FileDescriptor {

	private String fileName;

	private String fileContent;

	private String fileType;

	public FileDescriptor() {

	}

	public FileDescriptor(String fileName, String fileContent, String fileType) {
		this.fileName = fileName;
		this.fileContent = fileContent;
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileContent() {
		return fileContent;
	}

	public void setFileContent(String fileContent) {
		this.fileContent = fileContent;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
}
