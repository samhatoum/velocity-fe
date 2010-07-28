package com.hatoum.velocity.filesystem;

import java.util.HashMap;
import java.util.Map;

import com.hatoum.velocity.Resources;

public class FileHelper {

	private static Map<String, Boolean> audioFiltersMap;

	private static Map<String, Boolean> videoFiltersMap;

	private static Map<String, Boolean> playlistFiltersMap;

	static {
		audioFiltersMap = new HashMap<String, Boolean>();
		videoFiltersMap = new HashMap<String, Boolean>();
		playlistFiltersMap = new HashMap<String, Boolean>();

		String audioFilters = Resources.getProperty("audioFilters");
		String videoFilters = Resources.getProperty("videoFilters");
		String playlistFilters = Resources.getProperty("playlistFilters");

		String[] audioFiltersTokens = audioFilters.split(" ");
		String[] videoFiltersTokens = videoFilters.split(" ");
		String[] playlistFiltersTokens = playlistFilters.split(" ");

		for (String eachAudioFilter : audioFiltersTokens) {
			audioFiltersMap.put(eachAudioFilter, Boolean.TRUE);
		}
		for (String eachVideoFilter : videoFiltersTokens) {
			videoFiltersMap.put(eachVideoFilter, Boolean.TRUE);
		}
		for (String eachPlaylistFilter : playlistFiltersTokens) {
			playlistFiltersMap.put(eachPlaylistFilter, Boolean.TRUE);
		}
	}

	private FileHelper() {
	}

	public static String getExtension(String name) {
		String extension = name.substring(name.lastIndexOf(".") + 1);
		return extension;
	}

	public static boolean isMediaFile(String name) {
		return isAudioFile(name) || isMediaFile(name);
	}

	public static boolean isAudioFile(String name) {
		String extension = getExtension(name);
		return audioFiltersMap.get(extension) == null ? false : audioFiltersMap
				.get(extension);
	}

	public static boolean isVideoFile(String name) {
		String extension = getExtension(name);
		return videoFiltersMap.get(extension) == null ? false : videoFiltersMap
				.get(extension);
	}
	
	public static boolean isPlaylistFile(String name) {
		String extension = getExtension(name);
		return playlistFiltersMap.get(extension) == null ? false
				: playlistFiltersMap.get(extension);
	}

	public static boolean isCompatibleFile(String name) {
		String extension = getExtension(name);

		boolean isAudio = audioFiltersMap.get(extension) == null ? false
				: audioFiltersMap.get(extension);

		boolean isVideo = videoFiltersMap.get(extension) == null ? false
				: videoFiltersMap.get(extension);

		boolean isPlaylist = playlistFiltersMap.get(extension) == null ? false
				: playlistFiltersMap.get(extension);

		return isAudio || isVideo || isPlaylist;
	}
}
