package com.hatoum.velocity.media;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import com.hatoum.velocity.filesystem.FileHelper;

public class Playlist {

	private static Logger logger = Logger.getLogger(Playlist.class.getName());

	private enum PlaylistType {
		PLS, M3U, ASX, WPL
	}

	private static final String MP3 = "mp3";

	private static final String EMPTY_STRING = "";

	private static final String EMPTY = "";

	private List<String> playlist;

	private List<String> names;

	private Map<String, String> pathPrepend;

	private int currentPosition;

	private Map<String, MP3File> mp3Objects;

	public Playlist() {

		playlist = Collections.synchronizedList(new ArrayList<String>());
		names = Collections.synchronizedList(new ArrayList<String>());
		mp3Objects = Collections
				.synchronizedMap(new HashMap<String, MP3File>());
		pathPrepend = Collections
				.synchronizedMap(new HashMap<String, String>());
		currentPosition = -1;
	}

	public void remove(int index) {
		playlist.remove(index);
		String name = names.remove(index);
		mp3Objects.remove(name);
		if (playlist.size() <= currentPosition) {
			currentPosition = playlist.size() - 1;
		}
	}

	public void moveUp(int index) {
		if (index == 0) {
			return;
		}
		String removedItem = playlist.remove(index);
		playlist.add(index - 1, removedItem);

		String removedName = names.remove(index);
		names.add(index - 1, removedName);
	}

	public void moveDown(int index) {
		if (index == playlist.size() - 1) {
			return;
		}
		String removedItem = playlist.remove(index);
		playlist.add(index + 1, removedItem);

		String removedName = names.remove(index);
		names.add(index + 1, removedName);
	}

	public void next() {
		if (currentPosition < playlist.size() - 1) {
			++currentPosition;
		}
	}

	public void previous() {
		if (currentPosition > 0) {
			--currentPosition;
		}
	}

	public String getSelected() {
		if (currentPosition == -1) {
			return EMPTY_STRING;
		}
		// get the current filename and path
		String currentPositionPath = playlist.get(currentPosition);

		// if it's a relative one, then we should have its path in the
		// pathPrepend map
		String pathPrefix = pathPrepend.get(currentPositionPath);
		if (pathPrefix != null) {
			return pathPrefix + File.separator + currentPositionPath;
		}
		// otherwise this is not a relative path, just return it as it is
		return currentPositionPath;
	}

	public void clear() {
		playlist.clear();
		names.clear();
		currentPosition = -1;
	}

	public boolean isEmpty() {
		return playlist.isEmpty();
	}

	public void loadPlaylist(String filepath) {
		List<String> content = readFile(filepath);

		// find out what type of playlist we've got
		PlaylistType playlistType = getPlayListType(content);

		appendPlaylist(playlistType, content, filepath);
	}

	private void appendPlaylist(PlaylistType playlistType,
			List<String> content, String filepath) {
		if (playlistType == PlaylistType.WPL) {
			parseWPL(content, filepath);
		} else if (playlistType == PlaylistType.ASX) {
			parseASX(content, filepath);
		} else if (playlistType == PlaylistType.M3U) {
			parseM3U(content, filepath);
		} else if (playlistType == PlaylistType.PLS) {
			parsePLS(content, filepath);
		} else {
			logger.severe("did not recognise the playlist type " + filepath);
		}
	}

	private void parseWPL(List<String> content, String filepath) {
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (line.indexOf("<media src=\"") == 0) {
				line = line.substring(line.indexOf("\"") + 1);
				line = line.substring(0, line.indexOf("\""));
				handleRelative(filepath, line);
			}
		}
	}

	private void parseASX(List<String> content, String filepath) {
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (line.indexOf("<Ref href") == 0) {
				line = line.substring(line.indexOf("\"") + 1);
				line = line.substring(0, line.indexOf("\""));
				handleRelative(filepath, line);
			}
		}
	}

	private void parseM3U(List<String> content, String filepath) {
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (line.indexOf("#EXTINF") == -1 && line.indexOf("#EXTM3U") == -1) {
				handleRelative(filepath, line);
			}
		}
	}

	private void parsePLS(List<String> content, String filepath) {
		int numberOfEntries = -1;
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (line.toUpperCase().indexOf("NUMBEROFENTRIES") == 0) {
				line = line.substring(line.indexOf("=") + 1);
				numberOfEntries = Integer.parseInt(line);
			}
		}
		if (numberOfEntries == -1) {
			logger.severe("Found a pls file but could not find the "
					+ "NumberOfEntries field");
			return;
		}
		String[] entries = new String[numberOfEntries];
		for (int i = 0; i < content.size(); i++) {
			String line = content.get(i);
			if (line.toUpperCase().indexOf("FILE") == 0) {
				int entryIndex = Integer.parseInt(line.substring(4, line
						.indexOf("="))) - 1;
				entries[entryIndex] = line.substring(line.indexOf("=") + 1);
			}
		}
		for (int i = 0; i < entries.length; i++) {
			handleRelative(filepath, entries[i]);
		}

	}

	private void handleRelative(String filepath, String line) {
		File playlistPath = new File(filepath).getParentFile();
		if (isRelative(line)) {
			try {
				add(line, playlistPath.getCanonicalPath());
			} catch (IOException e) {
				logger.severe("Error getting relative link canonical path from"
						+ " playlist. " + e.getMessage());
			}
		} else {
			add(line);
		}
	}

	public void savePlaylist() {
		// TODO
	}

	private boolean isRelative(String line) {
		// ..
		if (line.indexOf("..") == 0) {
			return false;
		}
		// / or \ as first char
		if (line.indexOf("\\") == 0 || line.indexOf("/") == 0) {
			return false;
		}
		// : as the second char
		if (line.indexOf(":") == 1) {
			return false;
		}
		// http or ftp
		if (line.indexOf("http") == 0 || line.indexOf("ftp") == 0) {
			return false;
		}
		// \\
		if (line.indexOf("\\\\") == 1) {
			return false;
		}
		return true;
	}

	private List<String> readFile(String filepath) {
		List<String> content = new ArrayList<String>();
		try {
			RandomAccessFile reader = new RandomAccessFile(filepath, "r");
			String line = null;
			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (EMPTY.equals(line)) {
					continue;
				}
				content.add(line);
			}
			return content;
		} catch (FileNotFoundException e) {
			logger.severe(e.getMessage());
		} catch (IOException e) {
			logger.severe(e.getMessage());
		}
		return null;
	}

	private PlaylistType getPlayListType(List<String> content) {
		// we're only interested in the first line
		String firstLine = content.get(0).toUpperCase();
		// pls - first line should contain [playlist]
		if (firstLine.indexOf("[PLAYLIST]") != -1) {
			return PlaylistType.PLS;
		} else
		// asx - first line should begin with <Asx
		if (firstLine.indexOf("<ASX") != -1) {
			return PlaylistType.ASX;
		} else
		// wpl - first line should begin with <?wpl
		if (firstLine.indexOf("<?WPL") != -1) {
			return PlaylistType.WPL;
		} else
		// m3u - first line should begin with #EXTM3U
		if (firstLine.indexOf("#EXTM3U") != -1) {
			return PlaylistType.M3U;
		} else {
			return null;
		}
	}

	public int getLength() {
		return playlist.size();
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public List<String> getPlaylist() {
		return playlist;
	}

	public List<String> getNames() {
		return names;
	}

	public void setCurrentPosition(int currentPosition) {
		if (currentPosition >= 0 && currentPosition < playlist.size()) {
			this.currentPosition = currentPosition;
		}
	}

	public void add(String mrl) {
		add(mrl, EMPTY);
	}

	private void add(String mrl, String prepend) {
		playlist.add(mrl);
		String name = getName(mrl);
		names.add(name);
		String extension = FileHelper.getExtension(mrl).toLowerCase();
		if (MP3.equals(extension)) {
			try {
				MP3File file = new MP3File(mrl);
				mp3Objects.put(name, file);
			} catch (IOException e) {
				logger.severe(e.getMessage());
			} catch (TagException e) {
				logger.severe(e.getMessage());
			}
		}
		if (!EMPTY.equals(prepend)) {
			pathPrepend.put(mrl, prepend);
		}
		if (currentPosition == -1) {
			currentPosition = 0;
		}
	}

	private String getName(String mrl) {
		return mrl.substring(mrl.lastIndexOf(File.separator) + 1, mrl
				.lastIndexOf("."));
	}

	public MP3File getSelectedMP3File() {
		if (currentPosition == -1) {
			return null;
		}
		return mp3Objects.get(names.get(currentPosition));
	}
}
