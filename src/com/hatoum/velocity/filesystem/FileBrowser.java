package com.hatoum.velocity.filesystem;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import com.hatoum.velocity.framework.IAccessBean;
import com.hatoum.velocity.framework.IPlugin;

public class FileBrowser implements IPlugin {

	private static final String BACK_DIR = "..";

	private static final Logger logger = Logger.getLogger(FileBrowser.class
			.getName());

	private static FileBrowser instance;

	FileBrowserAccessBean accessBean;

	private List<String> filters;

	private String[] rootsStrings;

	private FileBrowser() {
		accessBean = new FileBrowserAccessBean();
		filters = new ArrayList<String>();
		refreshRoots();
	}

	public IAccessBean getAccessBean() {
		return accessBean;
	}

	public void kill() {
		accessBean = null;
		instance = null;
	}

	static public FileBrowser getInstance() {
		if (instance == null) {
			instance = new FileBrowser();
		}
		return instance;
	}

	private String[] filesToStrings(File[] directories) {
		String[] directoriesStrings = new String[directories.length];
		for (int i = 0; i < directories.length; i++) {
			directoriesStrings[i] = directories[i].toString();
		}
		return directoriesStrings;
	}

	// FIXME refactor this horribly long method
	private void select(String input, boolean directorySelection) {

		String copyCurrentDirectory = new String(accessBean
				.getCurrentDirectory() == null ? "" : accessBean
				.getCurrentDirectory());

		// if we have a top level root, then we need not do anything to the path
		if (!File.separator.equals(input)) {

			// check if we have a referential link, then convert it to an
			// absoloute path
			int firstSeperator = input.indexOf(File.separator);
			int lastSeperator = input.lastIndexOf(File.separator);

			// if there is only one seperator and it's at the begining of the
			// string this this is a referential link, and the parameter is a
			// directory
			if (firstSeperator == 0 && firstSeperator == lastSeperator) {
				if (input.equals(File.separator + BACK_DIR)) {
					String cd = accessBean.getCurrentDirectory();
					for (String eachRoot : rootsStrings) {
						if (eachRoot.equals(cd)) {
							accessBean.reset();
							refreshRoots();
							accessBean.setCurrentFileStale(true);
							return;
						}
					}
					cd = cd.substring(0, cd.lastIndexOf(File.separator) + 1);
					accessBean.setCurrentDirectory(cd);
					input = accessBean.getCurrentDirectory();
				} else {
					input = accessBean.getCurrentDirectory() + input;
					if (directorySelection) {
						accessBean.setCurrentFile(input);
						accessBean.setCurrentFileStale(true);
						return;
					}
				}
			}

			// this is a referential link, and the parameter is a file
			if (firstSeperator == -1) {
				String currentDirectory = accessBean.getCurrentDirectory();
				// add a file seperator to the end if, unless it's a root
				if (!File.separator.equals(currentDirectory
						.substring(currentDirectory.length() - 1))) {
					input = currentDirectory + File.separator + input;
				} else {
					input = currentDirectory + input;
				}
			}
		}

		// get the directories for the given string and set the access bean with
		// the results
		File requestedInput = new File(input);
		if (!requestedInput.exists()) {
			logger.warning("file or directory " + input
					+ "does not exist, reverting to previous directory");
			resetAccessBean();
			input = copyCurrentDirectory;
		}
		
		// now we have either a valid file or a directory
		if (!requestedInput.isDirectory()) {
			accessBean.setCurrentFile(input);
			accessBean.setCurrentFileStale(false);
			requestedInput = requestedInput.getParentFile();
		} else {
			accessBean.setCurrentFileStale(true);
		}

		File[] files = requestedInput.listFiles(new FileFilter() {
			public boolean accept(File file) {
				if (file.isDirectory()) {
					return false;
				}
				if (!FileHelper.isCompatibleFile(file.getName())) {
					return false;
				}
				if (filters.size() == 0) {
					return true;
				}
				for (String eachFilter : filters) {
					String fileName = file.getName();
					int lastIndex = fileName.lastIndexOf(eachFilter);
					if (lastIndex == fileName.length() - eachFilter.length()) {
						return true;
					}
				}
				return false;
			}
		});
		File[] directories = requestedInput.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});

		String[] directoriesView = filesToStrings(directories);
		String[] filesView = filesToStrings(files);

		Arrays.sort(directoriesView);
		Arrays.sort(filesView);

		String[] mixedView = makeMixedView(directoriesView, filesView);

		accessBean.setCurrentDirectory(requestedInput.getPath());
		accessBean.setDirectoriesView(directoriesView);
		accessBean.setFilesView(filesView);
		accessBean.setMixedView(mixedView);
	}

	private String[] makeMixedView(String[] directories, String[] files) {
		String[] mixedView = new String[directories.length + files.length + 1];
		int dstIndex = 0;
		mixedView[dstIndex++] = File.separator + BACK_DIR;
		for (int i = 0; i < directories.length; i++) {
			int seperatorIndexPlusOne = directories[i]
					.lastIndexOf(File.separator) + 1;
			mixedView[dstIndex++] = File.separator
					+ directories[i].substring(seperatorIndexPlusOne);
		}
		for (int i = 0; i < files.length; i++) {
			int seperatorIndexPlusOne = files[i].lastIndexOf(File.separator) + 1;
			mixedView[dstIndex++] = files[i].substring(seperatorIndexPlusOne);
		}
		return mixedView;
	}

	private void resetAccessBean() {
		accessBean.setCurrentDirectory(null);
		accessBean.setCurrentFile(null);
		accessBean.setDirectoriesView(null);
		accessBean.setFilesView(null);
		accessBean.setMixedView(null);
		accessBean.setCurrentFileStale(true);
		accessBean.setRoots(null);
	}

	public void refreshRoots() {
		File[] roots = File.listRoots();
		rootsStrings = filesToStrings(roots);
		accessBean.setRoots(rootsStrings);
		accessBean.setMixedView(rootsStrings);
	}

	public void selectDirectory(String input) {
		select(input, true);
	}

	public void select(String input) {
		select(input, false);
	}

	public void filter(String filter) {
		filters.add(filter);
	}

	public void clearFilters() {
		filters.clear();
	}

	public void refresh() {
		String currentDirectory = accessBean.getCurrentDirectory();
		// null can only happen if the filter method is called with the
		// refresh flag, before any directory is selected
		if (currentDirectory != null) {
			select(currentDirectory);
		}
	}

	public void updateBean() {
		// TODO implement updateBean method
	}
}