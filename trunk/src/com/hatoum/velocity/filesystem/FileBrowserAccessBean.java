package com.hatoum.velocity.filesystem;

import com.hatoum.velocity.framework.AccessBeanBase;

public class FileBrowserAccessBean extends AccessBeanBase {

	static {
		allProperties.add("roots");
		allProperties.add("currentDirectory");
		allProperties.add("currentFile");
		allProperties.add("directoriesView");
		allProperties.add("filesView");
		allProperties.add("mixedView");
		allProperties.add("currentFileStale");
	}

	protected String[] roots;

	protected String currentDirectory;

	protected String currentFile;

	protected String[] directoriesView;

	protected String[] filesView;

	protected String[] mixedView;

	protected boolean currentFileStale;

	public FileBrowserAccessBean() {
		currentFileStale = true;
	}

	public void setRoots(String[] roots) {
		include("roots");
		this.roots = roots;
	}

	public String[] getRoots() {
		return roots;
	}

	public String getCurrentDirectory() {
		return currentDirectory;
	}

	public void setCurrentDirectory(String currentDirectory) {
		include("currentDirectory");
		this.currentDirectory = currentDirectory;
	}

	public String getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(String currentFile) {
		include("currentFile");
		this.currentFile = currentFile;
	}

	public String[] getDirectoriesView() {
		return directoriesView;
	}

	public void setDirectoriesView(String[] currentDirectoriesView) {
		include("currentDirectoriesView");
		this.directoriesView = currentDirectoriesView;
	}

	public String[] getFilesView() {
		return filesView;
	}

	public void setFilesView(String[] currentFilesView) {
		include("currentFilesView");
		this.filesView = currentFilesView;
	}

	public String[] getMixedView() {
		return mixedView;
	}

	public void setMixedView(String[] mixedView) {
		include("mixedView");
		this.mixedView = mixedView;
	}

	public void setCurrentFileStale(boolean currentFileStale) {
		include("currentFileStale");
		this.currentFileStale = currentFileStale;
	}

	public boolean getCurrentFileStale() {
		return currentFileStale;
	}

	public void reset() {
		roots = null;
		directoriesView = null;
		filesView = null;
		mixedView = null;

		currentDirectory = null;
		currentFile = null;
		currentFileStale = false;
	}
}
