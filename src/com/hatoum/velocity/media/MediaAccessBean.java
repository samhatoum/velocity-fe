package com.hatoum.velocity.media;

import java.util.List;

import com.hatoum.velocity.framework.AccessBeanBase;

public class MediaAccessBean extends AccessBeanBase {

	static {
		allProperties.add("playlist");
		allProperties.add("currentPosition");
		allProperties.add("fileName");
		allProperties.add("percentPos");
		allProperties.add("timePos");
		allProperties.add("length");
		allProperties.add("videoCodec");
		allProperties.add("videoBitrate");
		allProperties.add("videoResolutionX");
		allProperties.add("videoResolutionY");
		allProperties.add("audioCodec");
		allProperties.add("audioBitrate");
		allProperties.add("audioSamplesFrequency");
		allProperties.add("audioSamplesChannels");
		allProperties.add("metaTitle");
		allProperties.add("metaArtist");
		allProperties.add("metaAlbum");
		allProperties.add("metaYear");
		allProperties.add("metaComment");
		allProperties.add("metaTrack");
		allProperties.add("metaGenre");
		allProperties.add("voFullscreen");
		allProperties.add("subVisibility");
		allProperties.add("isPlaying");
		// id3v1
		allProperties.add("v1Album");
		allProperties.add("v1AlbumTitle");
		allProperties.add("v1Artist");
		allProperties.add("v1Comment");
		allProperties.add("v1Genre");
		allProperties.add("v1LeadArtist");
		allProperties.add("v1Size");
		allProperties.add("v1SongComment");
		allProperties.add("v1SongGenre");
		allProperties.add("v1SongTitle");
		allProperties.add("v1Title");
		allProperties.add("v1TrackNumberOnAlbum");
		allProperties.add("v1Year");
		allProperties.add("v1YearReleased");
		// id3v2
		allProperties.add("v1GetAlbumTitle");
		allProperties.add("v1GetAuthorComposer");
		allProperties.add("v1GetLeadArtist");
		allProperties.add("v1GetSize");
		allProperties.add("v1GetSongComment");
		allProperties.add("v1GetSongGenre");
		allProperties.add("v1GetSongLyric");
		allProperties.add("v1GetSongTitle");
		allProperties.add("v1GetTrackNumberOnAlbum");
		allProperties.add("v1GetYearReleased");
	}

	private List<String> playlist;

	private int currentPosition;

	private String fileName;

	private int percentPos;

	private int timePos;

	private int length;

	private String videoCodec;

	private String videoBitrate;

	private int videoResolutionX;

	private int videoResolutionY;

	private String audioCodec;

	private String audioBitrate;

	private int audioSamplesFrequency;

	private int audioSamplesChannels;

	private String metaTitle;

	private String metaArtist;

	private String metaAlbum;

	private String metaYear;

	private String metaComment;

	private String metaTrack;

	private String metaGenre;

	private boolean voFullscreen;

	private boolean subVisibility;

	private boolean isPlaying;

	// id3v1

	private String v1Album;

	private String v1AlbumTitle;

	private String v1Artist;

	private String v1Comment;

	private String v1Genre;

	private String v1LeadArtist;

	private String v1Size;

	private String v1SongComment;

	private String v1SongGenre;

	private String v1SongTitle;

	private String v1Title;

	private String v1TrackNumberOnAlbum;

	private String v1Year;

	private String v1YearReleased;

	// id3v2

	private String v2AlbumTitle;

	private String v2AuthorComposer;

	private String v2LeadArtist;

	private String v2Size;

	private String v2SongComment;

	private String v2SongGenre;

	private String v2SongLyric;

	private String v2SongTitle;

	private String v2TrackNumberOnAlbum;

	private String v2YearReleased;

	// end id3

	public MediaAccessBean() {
	}

	public String getAudioBitrate() {
		return audioBitrate;
	}

	public void setAudioBitrate(String audioBitrate) {
		include("audioBitrate");
		this.audioBitrate = audioBitrate;
	}

	public String getAudioCodec() {
		return audioCodec;
	}

	public void setAudioCodec(String audioCodec) {
		include("audioCodec");
		this.audioCodec = audioCodec;
	}

	public int getAudioSamplesChannels() {
		return audioSamplesChannels;
	}

	public void setAudioSamplesChannels(int audioSamplesChannels) {
		include("audioSamplesChannels");
		this.audioSamplesChannels = audioSamplesChannels;
	}

	public int getAudioSamplesFrequency() {
		return audioSamplesFrequency;
	}

	public void setAudioSamplesFrequency(int audioSamplesFrequency) {
		include("audioSamplesFrequency");
		this.audioSamplesFrequency = audioSamplesFrequency;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		include("fileName");
		this.fileName = fileName;
	}

	public String getMetaAlbum() {
		return metaAlbum;
	}

	public void setMetaAlbum(String metaAlbum) {
		include("metaAlbum");
		this.metaAlbum = metaAlbum;
	}

	public String getMetaArtist() {
		return metaArtist;
	}

	public void setMetaArtist(String metaArtist) {
		include("metaArtist");
		this.metaArtist = metaArtist;
	}

	public String getMetaComment() {
		return metaComment;
	}

	public void setMetaComment(String metaComment) {
		include("metaComment");
		this.metaComment = metaComment;
	}

	public String getMetaGenre() {
		return metaGenre;
	}

	public void setMetaGenre(String metaGenre) {
		include("metaGenre");
		this.metaGenre = metaGenre;
	}

	public String getMetaTitle() {
		return metaTitle;
	}

	public void setMetaTitle(String metaTitle) {
		include("metaTitle");
		this.metaTitle = metaTitle;
	}

	public String getMetaTrack() {
		return metaTrack;
	}

	public void setMetaTrack(String metaTrack) {
		include("metaTrack");
		this.metaTrack = metaTrack;
	}

	public String getMetaYear() {
		return metaYear;
	}

	public void setMetaYear(String metaYear) {
		include("metaYear");
		this.metaYear = metaYear;
	}

	public int getPercentPos() {
		return percentPos;
	}

	public void setPercentPos(int percentPos) {
		include("percentPos");
		this.percentPos = percentPos;
	}

	public boolean isSubVisibility() {
		return subVisibility;
	}

	public void setSubVisibility(boolean subVisibility) {
		include("subVisibility");
		this.subVisibility = subVisibility;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int timeLength) {
		include("length");
		this.length = timeLength;
	}

	public int getTimePos() {
		return timePos;
	}

	public void setTimePos(int timePos) {
		include("timePos");
		this.timePos = timePos;
	}

	public String getVideoBitrate() {
		return videoBitrate;
	}

	public void setVideoBitrate(String videoBitrate) {
		include("videoBitrate");
		this.videoBitrate = videoBitrate;
	}

	public String getVideoCodec() {
		return videoCodec;
	}

	public void setVideoCodec(String videoCodec) {
		include("videoCodec");
		this.videoCodec = videoCodec;
	}

	public int getVideoResolutionX() {
		return videoResolutionX;
	}

	public void setVideoResolutionX(int videoResolutionX) {
		include("videoResolutionX");
		this.videoResolutionX = videoResolutionX;
	}

	public int getVideoResolutionY() {
		return videoResolutionY;
	}

	public void setVideoResolutionY(int videoResolutionY) {
		include("videoResolutionY");
		this.videoResolutionY = videoResolutionY;
	}

	public boolean isVoFullscreen() {
		return voFullscreen;
	}

	public void setVoFullscreen(boolean voFullscreen) {
		include("voFullscreen");
		this.voFullscreen = voFullscreen;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		include("isPlaying");
		this.isPlaying = isPlaying;
	}

	public String toString() {
		return "[fileName = " + fileName + ", " + "percentPos = " + percentPos
				+ ", " + "timePos = " + timePos + ", " + "Length = " + length
				+ ", " + "videoCodec = " + videoCodec + ", "
				+ "videoBitrate = " + videoBitrate + ", "
				+ "videoResolutionX = " + videoResolutionX + ", "
				+ "videoResolutionY = " + videoResolutionY + ", "
				+ "audioCodec = " + audioCodec + ", " + "audioBitrate = "
				+ audioBitrate + ", " + "audioSamplesFrequency = "
				+ audioSamplesFrequency + ", " + "audioSamplesChannels = "
				+ audioSamplesChannels + ", " + "metaTitle = " + metaTitle
				+ ", " + "metaArtist = " + metaArtist + ", " + "metaAlbum = "
				+ metaAlbum + ", " + "metaYear = " + metaYear + ", "
				+ "metaComment = " + metaComment + ", " + "metaTrack = "
				+ metaTrack + ", " + "metaGenre = " + metaGenre + ", "
				+ "voFullscreen = " + voFullscreen + ", " + "subVisibility = "
				+ subVisibility + ", " + "isPlaying = " + isPlaying + "]";

	}

	public List getPlaylist() {
		return playlist;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		include("currentPosition");
		this.currentPosition = currentPosition;
	}

	public void setPlaylist(List<String> playlist) {
		this.playlist = playlist;
	}

	public void reset() {
		fileName = "";
		percentPos = -1;
		timePos = -1;
		length = -1;
		videoCodec = "";
		videoBitrate = "";
		videoResolutionX = -1;
		videoResolutionY = -1;
		audioCodec = "";
		audioBitrate = "";
		audioSamplesFrequency = -1;
		audioSamplesChannels = -1;
		metaTitle = "";
		metaArtist = "";
		metaAlbum = "";
		metaYear = "";
		metaComment = "";
		metaTrack = "";
		metaGenre = "";
		voFullscreen = false;
		subVisibility = false;
		isPlaying = false;
	}

	public void includePlaylistInNextTransmission() {
		include("playlist");
	}

	public String getV1Album() {
		return v1Album;
	}

	public void setV1Album(String album) {
		v1Album = album;
	}

	public String getV1AlbumTitle() {
		return v1AlbumTitle;
	}

	public void setV1AlbumTitle(String albumTitle) {
		v1AlbumTitle = albumTitle;
	}

	public String getV1Artist() {
		return v1Artist;
	}

	public void setV1Artist(String artist) {
		v1Artist = artist;
	}

	public String getV1Comment() {
		return v1Comment;
	}

	public void setV1Comment(String comment) {
		v1Comment = comment;
	}

	public String getV1Genre() {
		return v1Genre;
	}

	public void setV1Genre(String genre) {
		v1Genre = genre;
	}

	public String getV1LeadArtist() {
		return v1LeadArtist;
	}

	public void setV1LeadArtist(String leadArtist) {
		v1LeadArtist = leadArtist;
	}

	public String getV1Size() {
		return v1Size;
	}

	public void setV1Size(String size) {
		v1Size = size;
	}

	public String getV1SongComment() {
		return v1SongComment;
	}

	public void setV1SongComment(String songComment) {
		v1SongComment = songComment;
	}

	public String getV1SongGenre() {
		return v1SongGenre;
	}

	public void setV1SongGenre(String songGenre) {
		v1SongGenre = songGenre;
	}

	public String getV1SongTitle() {
		return v1SongTitle;
	}

	public void setV1SongTitle(String songTitle) {
		v1SongTitle = songTitle;
	}

	public String getV1Title() {
		return v1Title;
	}

	public void setV1Title(String title) {
		v1Title = title;
	}

	public String getV1TrackNumberOnAlbum() {
		return v1TrackNumberOnAlbum;
	}

	public void setV1TrackNumberOnAlbum(String trackNumberOnAlbum) {
		v1TrackNumberOnAlbum = trackNumberOnAlbum;
	}

	public String getV1Year() {
		return v1Year;
	}

	public void setV1Year(String year) {
		v1Year = year;
	}

	public String getV1YearReleased() {
		return v1YearReleased;
	}

	public void setV1YearReleased(String yearReleased) {
		v1YearReleased = yearReleased;
	}

	public String getV2AlbumTitle() {
		return v2AlbumTitle;
	}

	public void setV2AlbumTitle(String albumTitle) {
		v2AlbumTitle = albumTitle;
	}

	public String getV2AuthorComposer() {
		return v2AuthorComposer;
	}

	public void setV2AuthorComposer(String authorComposer) {
		v2AuthorComposer = authorComposer;
	}

	public String getV2LeadArtist() {
		return v2LeadArtist;
	}

	public void setV2LeadArtist(String leadArtist) {
		v2LeadArtist = leadArtist;
	}

	public String getV2Size() {
		return v2Size;
	}

	public void setV2Size(String size) {
		v2Size = size;
	}

	public String getV2SongComment() {
		return v2SongComment;
	}

	public void setV2SongComment(String songComment) {
		v2SongComment = songComment;
	}

	public String getV2SongGenre() {
		return v2SongGenre;
	}

	public void setV2SongGenre(String songGenre) {
		v2SongGenre = songGenre;
	}

	public String getV2SongLyric() {
		return v2SongLyric;
	}

	public void setV2SongLyric(String songLyric) {
		v2SongLyric = songLyric;
	}

	public String getV2SongTitle() {
		return v2SongTitle;
	}

	public void setV2SongTitle(String songTitle) {
		v2SongTitle = songTitle;
	}

	public String getV2TrackNumberOnAlbum() {
		return v2TrackNumberOnAlbum;
	}

	public void setV2TrackNumberOnAlbum(String trackNumberOnAlbum) {
		v2TrackNumberOnAlbum = trackNumberOnAlbum;
	}

	public String getV2YearReleased() {
		return v2YearReleased;
	}

	public void setV2YearReleased(String yearReleased) {
		v2YearReleased = yearReleased;
	}
}
