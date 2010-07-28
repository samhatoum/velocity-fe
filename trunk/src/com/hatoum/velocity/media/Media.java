package com.hatoum.velocity.media;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.logging.Logger;

import org.farng.mp3.MP3File;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;

import com.hatoum.velocity.Resources;
import com.hatoum.velocity.Utilities;
import com.hatoum.velocity.filesystem.FileHelper;
import com.hatoum.velocity.framework.IAccessBean;
import com.hatoum.velocity.framework.IPlugin;

public class Media implements IPlugin {

	private static Logger logger = Logger.getLogger(Media.class.getName());

	private static Media instance;

	private MediaAccessBean accessBean;

	private boolean showOut = Resources.getBooleanProperty("debugMplayer");

	private boolean showErr = showOut;

	private static final String PAUSE = "pause";

	private static final String PAUSING = "pausing";

	private static final String FRAME_STEP = "frame_step";

	private static final String AUDIO_DELAY = "audio_delay";

	private static final String SEEK = "seek";

	private static final String SPEED_SET = "speed_set";

	private static final String QUIT = "quit";

	private static final String SUB_VISIBILITY = "sub_visibility";

	private static final String CONTRAST = "contrast";

	private static final String GAMMA = "gamma";

	private static final String BRIGHTNESS = "brightness";

	private static final String HUE = "hue";

	private static final String SATURATION = "saturation";

	private static final String MUTE = "mute";

	private static final String VOLUME = "volume";

	private static final String VO_FULLSCREEN = "vo_fullscreen";

	private static final String LOADFILE = "loadfile";

	private static final String SWITCH_AUDIO = "switch_audio";

	private static final String SEEK_CHAPTER = "seek_chapter";

	private static final String OSD = "osd";

	private static final String OSD_SHOW_TEXT = "osd_show_text";

	private static final String KEY_DOWN_EVENT = "key_down_event";

	private static final String SUB_SELECT = "sub_select";

	private static final String SWITCH_RATIO = "switch_ratio";

	private static final String GET_PERCENT_POS = "get_percent_pos";

	private final static String ANS_PERCENT_POSITION = "ANS_PERCENT_POSITION=";

	private static final String GET_TIME_POS = "get_time_pos";

	private final static String ANS_TIME_POSITION = "ANS_TIME_POSITION=";

	private static final String GET_TIME_LENGTH = "get_time_length";

	private final static String ANS_LENGTH = "ANS_LENGTH=";

	private static final String GET_FILE_NAME = "get_file_name";

	private final static String ANS_FILENAME = "ANS_FILENAME=";

	private static final String GET_VIDEO_CODEC = "get_video_codec";

	private final static String ANS_VIDEO_CODEC = "ANS_VIDEO_CODEC=";

	private static final String GET_VIDEO_BITRATE = "get_video_bitrate";

	private final static String ANS_VIDEO_BITRATE = "ANS_VIDEO_BITRATE=";

	private static final String GET_VIDEO_RESOLUTION = "get_video_resolution";

	private final static String ANS_VIDEO_RESOLUTION = "ANS_VIDEO_RESOLUTION=";

	private static final String GET_AUDIO_CODEC = "get_audio_codec";

	private final static String ANS_AUDIO_CODEC = "ANS_AUDIO_CODEC=";

	private static final String GET_AUDIO_BITRATE = "get_audio_bitrate";

	private final static String ANS_AUDIO_BITRATE = "ANS_AUDIO_BITRATE=";

	private static final String GET_AUDIO_SAMPLES = "get_audio_samples";

	private final static String ANS_AUDIO_SAMPLES = "ANS_AUDIO_SAMPLES=";

	private static final String GET_META_TITLE = "get_meta_title";

	private final static String ANS_META_TITLE = "ANS_META_TITLE=";

	private static final String GET_META_ARTIST = "get_meta_artist";

	private final static String ANS_META_ARTIST = "ANS_META_ARTIST=";

	private static final String GET_META_ALBUM = "get_meta_album";

	private final static String ANS_META_ALBUM = "ANS_META_ALBUM=";

	private static final String GET_META_YEAR = "get_meta_year";

	private final static String ANS_META_YEAR = "ANS_META_YEAR=";

	private static final String GET_META_COMMENT = "get_meta_comment";

	private final static String ANS_META_COMMENT = "ANS_META_COMMENT=";

	private static final String GET_META_TRACK = "get_meta_track";

	private final static String ANS_META_TRACK = "ANS_META_TRACK=";

	private static final String GET_META_GENRE = "get_meta_genre";

	private final static String ANS_META_GENRE = "ANS_META_GENRE=";

	private static final String GET_VO_FULLSCREEN = "get_vo_fullscreen";

	private final static String ANS_VO_FULLSCREEN = "ANS_VO_FULLSCREEN=";

	private static final String GET_SUB_VISIBILITY = "get_sub_visibility";

	private final static String ANS_SUB_VISIBILITY = "ANS_SUB_VISIBILITY=";

	private static final String DVD = "dvd://";

	private static final String SPACE = " ";

	private static final String NULL_STRING = "null";

	private static final String GEOMETRY_FLAG = "-geometry";

	private static final String NOBORDER_FLAG = "-noborder";

	private static final String SLAVE_FLAG = "-slave";

	private static final String IDLE_FLAG = "-idle";

	private static final String ON_TOP_FLAG = "-ontop";

	private static final String DVD_DEVICE = "-dvd-device";

	private static final String DVD_DRIVE = "dvdDrive";

	private static final String ADDITIONAL_MPLAYER_SWITCHES = "additionalMplayerSwitches";

	private static final String MPLAYER_PATH = "mplayerPath";

	private static final String STARTING_PLAYBACK = "Starting playback...";

	private static final String EMPTY = "";

	private static final String SKIN_RESOLUTION = "skinResolution";

	private static final String VIDEO_SIZE = "videoSize";

	private static final String VIDEO_POSITION = "videoPosition";

	private static final String X = "x";

	private static final String COMMA = ",";

	private PrintStream printStream;

	private Process playerPrc;

	private BufferedReader stdout;

	private BufferedReader stderr;

	// private Vector<String> outputLines;

	// private Vector<String> errorLines;

	private boolean isPaused;

	private boolean isPlaying;

	private boolean metaDataCollected;

	private Playlist playlist;

	private boolean mplayerKilled;

	private Media() {

		accessBean = new MediaAccessBean();
		playlist = new Playlist();
		// outputLines = new Vector<String>();
		// errorLines = new Vector<String>();

		accessBean.setPlaylist(playlist.getNames());

		startMplayer(NULL_STRING);

		logger.info("created a Media instance");
	}

	private void send(String command) {
		if (Resources.getBooleanProperty("debugMPlayer")) {
			logger.info("sending [" + command + "] to mplayer");
		}
		printStream.print(command + "\n");
		printStream.flush();
	}

	private void startMplayer(String filename) {
		String geometry = getGeometry();
		startMplayer(geometry, filename);
	}

	private String getGeometry() {
		// get the skin resolution
		Dimension skinResolution = getResolution(Resources
				.getProperty(SKIN_RESOLUTION));
		// Get the current screen size
		Dimension screenResolution = Toolkit.getDefaultToolkit()
				.getScreenSize();
		// calculate the ratios
		double xRatio = (double) screenResolution.width / skinResolution.width;
		double yRatio = (double) screenResolution.height
				/ skinResolution.height;

		// get the video resolution
		Dimension videoResolution = getResolution(Resources
				.getProperty(VIDEO_SIZE));
		// calcualte a new one
		int w = (int) (videoResolution.width * xRatio);
		int h = (int) (videoResolution.height * yRatio);

		// get the video position
		Point videoPosition = getVideoPosition(Resources
				.getProperty(VIDEO_POSITION));
		// calculate a new one
		int x = (int) (videoPosition.x * xRatio);
		int y = (int) (videoPosition.y * yRatio);

		// construct the geometry string
		String geometry = w + "x" + h + "+" + x + "+" + y;
		return geometry;
	}

	private Point getVideoPosition(String videoPosition) {
		String x = videoPosition.substring(0, videoPosition.indexOf(COMMA))
				.trim();
		String y = videoPosition.substring(videoPosition.indexOf(COMMA) + 1)
				.trim();
		return new Point(Integer.parseInt(x), Integer.parseInt(y));
	}

	private Dimension getResolution(String videoSize) {
		String x = videoSize.substring(0, videoSize.indexOf(X)).trim();
		String y = videoSize.substring(videoSize.indexOf(X) + 1).trim();
		return new Dimension(Integer.parseInt(x), Integer.parseInt(y));
	}

	private void startMplayer(String videoGeometry, String filename) {

		// construct the flags string
		String flagsString = new String();
		flagsString = SPACE + SLAVE_FLAG;
		flagsString += SPACE + IDLE_FLAG;
		flagsString += SPACE + ON_TOP_FLAG;
		flagsString += SPACE + NOBORDER_FLAG;
		flagsString += SPACE + GEOMETRY_FLAG + SPACE + videoGeometry;
		String dvdDrive = Resources.getProperty(DVD_DRIVE);
		if (!"".equals(dvdDrive)) {
			flagsString += SPACE + DVD_DEVICE + SPACE + dvdDrive;
		}

		String additionalMplayerSwitches = Resources
				.getProperty(ADDITIONAL_MPLAYER_SWITCHES);
		if (!"".equals(additionalMplayerSwitches)) {
			flagsString += SPACE + additionalMplayerSwitches;
		}

		logger.info("Starting mplayer with: "
				+ Resources.getProperty(MPLAYER_PATH) + flagsString + SPACE
				+ filename);

		// this will create an empty array space for the first command

		flagsString = "MPLAYER_PLACE_HOLDER" + SPACE + flagsString;
		String[] cmdArray = flagsString.split(" ");
		cmdArray[0] = Resources.getProperty(MPLAYER_PATH);

		if (isPlaying) {
			quitMPlayer();
		}

		playerPrc = Resources.exec(cmdArray, false);

		printStream = new PrintStream(playerPrc.getOutputStream());
		createStdOutMonitor();
		createStdErrMonitor();

	}

	private void handleMplayerCrash() {
		logger.info("handling mplayer crash");
		endMplayerProcess();
		startMplayer(NULL_STRING);
	}

	private void endMplayerProcess() {
		logger.info("waitfor called");
		try {
			playerPrc.waitFor();
		} catch (InterruptedException e) {
			logger.severe(Utilities.getLog(e));
		} finally {
			mplayerKilled = true;
			isPlaying = false;
			isPaused = false;
		}
		logger.info("waitfor Finished");
	}

	private void quitMPlayer() {
		logger.info("quitting mplayer");
		send(QUIT);
		endMplayerProcess();
	}

	private void createStdOutMonitor() {
		// creates a thread that monitors stdout and places entries in a
		// vector, for later consumption
		InputStream is = playerPrc.getInputStream();
		stdout = new BufferedReader(new InputStreamReader(is));
		Thread stdOutThread = new Thread() {
			public void run() {
				try {
					int l;
					String line;
					mplayerKilled = false;
					for (l = 0; (line = stdout.readLine()) != null;) {
						if (line.length() > 0) {
							l++;
							// outputLines.addElement(line);
							parseLine(line);
						}
						if (showOut) {
							logger.info("MPLAYER_STD_OUT: " + line + "\n");
						}
					}
					mplayerKilled = true;
					isPlaying = false;
					isPaused = false;
					logger.info("\nRead " + l + " lines from stdout.");
					stdout.close();
				} catch (IOException e) {
					logger.info("IO exception on stdout:\n"
							+ Utilities.getLog(e));
				}
			}
		};
		stdOutThread.start();
	}

	private void createStdErrMonitor() {
		// creates a thread that monitors stderr and places entries in a
		// vector, for later consumption
		InputStream is = playerPrc.getErrorStream();
		stderr = new BufferedReader(new InputStreamReader(is));
		Thread stdErrThread = new Thread() {
			public void run() {
				try {
					int l;
					String line;
					mplayerKilled = false;
					for (l = 0; (line = stderr.readLine()) != null;) {
						if (line.length() > 0) {
							l++;
							// errorLines.addElement(line);
							if (line.matches("File not found: 'null'")) {
								logger.info("Null file detected - Stopping");
								isPlaying = false;
								accessBean.setPlaying(false);
							} else if (line.indexOf("MPlayer crashed.") != -1) {
								logger.warning("mplayer has crashed");
								handleMplayerCrash();
							}
						}
						if (showErr) {
							logger.info("MPLAYER_STD_ERR: " + line + "\n");
						}
					}
					mplayerKilled = true;
					isPlaying = false;
					isPaused = false;
					logger.info("\nRead " + l + " lines from errout.");
					stderr.close();
				} catch (IOException e) {
					logger.info("IO exception on stdout:\n"
							+ Utilities.getLog(e));
				}
			}
		};
		stdErrThread.start();
	}

	private void parseLine(String line) {
		if (line.matches(STARTING_PLAYBACK)) {
			isPlaying = true;
			isPaused = false;
			accessBean.setPlaying(true);
		} else if (line.indexOf(ANS_PERCENT_POSITION) != -1) {
			int percentPosition = parseInt(line, ANS_PERCENT_POSITION);
			accessBean.setPercentPos(percentPosition);
		} else if (line.indexOf(ANS_TIME_POSITION) != -1) {
			int timePosition = parseInt(line, ANS_TIME_POSITION);
			accessBean.setTimePos(timePosition);
		} else if (line.indexOf(ANS_LENGTH) != -1) {
			int length = parseInt(line, ANS_LENGTH);
			accessBean.setLength(length);
		} else if (line.indexOf(ANS_FILENAME) != -1) {
			String filename = parseString(line, ANS_FILENAME);
			accessBean.setFileName(filename);
		} else if (line.indexOf(ANS_VIDEO_CODEC) != -1) {
			String videoCodec = parseString(line, ANS_VIDEO_CODEC);
			accessBean.setVideoCodec(videoCodec);
		} else if (line.indexOf(ANS_VIDEO_BITRATE) != -1) {
			String videoBitrate = parseString(line, ANS_VIDEO_BITRATE);
			accessBean.setVideoBitrate(videoBitrate);
		} else if (line.indexOf(ANS_VIDEO_RESOLUTION) != -1) {
			String videoResolution = parseString(line, ANS_VIDEO_RESOLUTION);
			parseVideoResolution(videoResolution);
		} else if (line.indexOf(ANS_AUDIO_CODEC) != -1) {
			String audioCodec = parseString(line, ANS_AUDIO_CODEC);
			accessBean.setAudioCodec(audioCodec);
		} else if (line.indexOf(ANS_AUDIO_BITRATE) != -1) {
			String audioBitrate = parseString(line, ANS_AUDIO_BITRATE);
			accessBean.setAudioBitrate(audioBitrate);
		} else if (line.indexOf(ANS_AUDIO_SAMPLES) != -1) {
			String audioSamplesString = parseString(line, ANS_AUDIO_SAMPLES);
			parseAudioSamples(audioSamplesString);
		} else if (line.indexOf(ANS_META_TITLE) != -1) {
			String metaTitle = parseString(line, ANS_META_TITLE);
			accessBean.setMetaTitle(metaTitle);
		} else if (line.indexOf(ANS_META_ARTIST) != -1) {
			String metaArtist = parseString(line, ANS_META_ARTIST);
			accessBean.setMetaArtist(metaArtist);
		} else if (line.indexOf(ANS_META_ALBUM) != -1) {
			String metaAlbum = parseString(line, ANS_META_ALBUM);
			accessBean.setMetaAlbum(metaAlbum);
		} else if (line.indexOf(ANS_META_YEAR) != -1) {
			String metaYear = parseString(line, ANS_META_YEAR);
			accessBean.setMetaYear(metaYear);
		} else if (line.indexOf(ANS_META_COMMENT) != -1) {
			String metaComment = parseString(line, ANS_META_YEAR);
			accessBean.setMetaComment(metaComment);
		} else if (line.indexOf(ANS_META_TRACK) != -1) {
			String metaTrack = parseString(line, ANS_META_TRACK);
			accessBean.setMetaTrack(metaTrack);
		} else if (line.indexOf(ANS_META_GENRE) != -1) {
			String metaGenre = parseString(line, ANS_META_GENRE);
			accessBean.setMetaGenre(metaGenre);
		} else if (line.indexOf(ANS_VO_FULLSCREEN) != -1) {
			boolean fullscreen = parseBoolean(line, ANS_VO_FULLSCREEN);
			accessBean.setVoFullscreen(fullscreen);
		} else if (line.indexOf(ANS_SUB_VISIBILITY) != -1) {
			boolean subVisibility = parseBoolean(line, ANS_SUB_VISIBILITY);
			accessBean.setVoFullscreen(subVisibility);
		}
	}

	private void parseAudioSamples(String audioSamplesString) {
		if (audioSamplesString.length() == 0) {
			return;
		}
		String frequencyString = audioSamplesString.substring(0,
				audioSamplesString.indexOf(SPACE));

		String channelsString = audioSamplesString.substring(
				audioSamplesString.indexOf(",") + 1,
				audioSamplesString.lastIndexOf(SPACE)).trim();

		int frequency = Integer.parseInt(frequencyString);
		int channels = Integer.parseInt(channelsString);

		accessBean.setAudioSamplesFrequency(frequency);
		accessBean.setAudioSamplesChannels(channels);
	}

	private void parseVideoResolution(String videoResolution) {
		if (videoResolution.indexOf("x") == -1)
			return;
		String xString = videoResolution.substring(0,
				videoResolution.indexOf("x")).trim();
		String yString = videoResolution.substring(
				videoResolution.indexOf("x") + 1).trim();
		int x = Integer.parseInt(xString);
		int y = Integer.parseInt(yString);
		accessBean.setVideoResolutionX(x);
		accessBean.setVideoResolutionY(y);
	}

	private String parseString(String line, final String prefix) {
		line = line.substring(prefix.length() + 1, line.length() - 1);
		return line;
	}

	private int parseInt(String line, final String prefix) {
		line = line.substring(prefix.length());
		int i = 0;
		if ((i = line.indexOf(".")) != -1) {
			line = line.substring(0, i);
		}
		return Integer.parseInt(line);
	}

	private boolean parseBoolean(String line, String prefix) {
		line = line.substring(prefix.length());
		return Boolean.parseBoolean(line);
	}

	static public Media getInstance() {
		if (instance == null) {
			instance = new Media();
		}
		return instance;
	}

	public IAccessBean getAccessBean() {
		return accessBean;
	}

	public void kill() {
		// TODO start this below as a thread, and if times out, kill it using
		// a process command
		quitMPlayer();
		instance = null;
		accessBean = null;
	}

	public void updateBean() {
		if (!isPaused && isPlaying) {
			getInfo();
		}
	}

	// TODO finish this method to create a link to the album art & id3 tag
	private void getMetaData() {

		// get the id3 tag 1 & 2 info
		MP3File mp3File = playlist.getSelectedMP3File();
		ID3v1 tag1 = mp3File.getID3v1Tag();
		AbstractID3v2 tag2 = mp3File.getID3v2Tag();

		// set all the id3v1 tag info in the bean
		accessBean.setV1Album(tag1.getAlbum());
		accessBean.setV1AlbumTitle(tag1.getAlbumTitle());
		accessBean.setV1Artist(tag1.getArtist());
		accessBean.setV1Comment(tag1.getComment());
		accessBean.setV1Genre(tag1.getGenre() + "");
		accessBean.setV1LeadArtist(tag1.getLeadArtist());
		accessBean.setV1Size(tag1.getSize() + "");
		accessBean.setV1SongComment(tag1.getSongComment());
		accessBean.setV1SongGenre(tag1.getSongGenre());
		accessBean.setV1SongTitle(tag1.getSongTitle());
		accessBean.setV1Title(tag1.getTitle());
		accessBean.setV1TrackNumberOnAlbum(tag1.getTrackNumberOnAlbum());
		accessBean.setV1Year(tag1.getYear());
		accessBean.setV1YearReleased(tag1.getYearReleased());

		// set all the id3v2 tag info in the bean
		accessBean.setV2AlbumTitle(tag2.getAlbumTitle());
		accessBean.setV2AuthorComposer(tag2.getAuthorComposer());
		accessBean.setV2LeadArtist(tag2.getLeadArtist());
		accessBean.setV2Size(tag2.getSize() + "");
		accessBean.setV2SongComment(tag2.getSongComment());
		accessBean.setV2SongGenre(tag2.getSongGenre());
		accessBean.setV2SongLyric(tag2.getSongLyric());
		accessBean.setV2SongTitle(tag2.getSongTitle());
		accessBean.setV2TrackNumberOnAlbum(tag2.getTrackNumberOnAlbum());
		accessBean.setV2YearReleased(tag2.getYearReleased());
	}

	private void getInfo() {

		// no point doing this if mplayer is dead
		if (mplayerKilled) {
			return;
		}

		// time info
		send(GET_PERCENT_POS);
		send(GET_TIME_POS);
		// stats and info
		send(GET_VIDEO_BITRATE);
		send(GET_AUDIO_BITRATE);

		// fullscreen
		send(GET_VO_FULLSCREEN);

		// subtitles
		send(GET_SUB_VISIBILITY);

		if (!metaDataCollected) {
			send(GET_FILE_NAME);

			// time info
			send(GET_TIME_LENGTH);

			// stats and info
			send(GET_VIDEO_CODEC);
			send(GET_VIDEO_RESOLUTION);
			send(GET_AUDIO_CODEC);
			send(GET_AUDIO_SAMPLES);

			// meta data
			send(GET_META_TITLE);
			send(GET_META_ARTIST);
			send(GET_META_ALBUM);
			send(GET_META_YEAR);
			send(GET_META_COMMENT);
			send(GET_META_TRACK);
			send(GET_META_GENRE);

			accessBean.setCurrentPosition(playlist.getCurrentPosition());

			metaDataCollected = true;
		}
	}

	private boolean nullOrEmpty(String fileOrIndex) {
		return (null == fileOrIndex || "".equals(fileOrIndex));
	}

	private void loadFile(String filePath) {
		if (!NULL_STRING.equals(filePath)) {
			metaDataCollected = false;
		}
		// what a nasty hack, but it's the way!
		filePath = "\"" + filePath.replace('\\', '/') + "\"";
		send(LOADFILE + SPACE + filePath);
		getMetaData();
	}

	private void play(boolean override) {
		if (override || !isPlaying) {
			String selected = playlist.getSelected();
			logger.info("playing " + selected);
			loadFile(selected);
		} else if (isPaused) {
			frameStep();
		}
	}

	private void addCollection(String aDirectory, final boolean recurse) {
		logger.info("adding media collection from " + aDirectory);
		File file = new File(aDirectory);
		// setup a filter
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File directoryCheck = new File(dir.getAbsoluteFile()
						+ File.separator + name);
				if (directoryCheck.isDirectory() && recurse) {
					addCollection(directoryCheck.getAbsolutePath(), true);
				}
				if (directoryCheck.isDirectory() && !recurse) {
					return false;
				}
				return FileHelper.isMediaFile(name);
			}
		};
		String[] files = file.list(filter);
		// this means we couldn't get into this directory, so ignore it
		if (files == null) {
			return;
		}
		for (int i = 0; i < files.length; i++) {
			// if the last charachter of the direcory is not seperator, then we
			// need a seperator
			String path = aDirectory;
			if (!File.separator.equals(aDirectory.substring(
					aDirectory.length() - 1, aDirectory.length()))) {
				path += File.separator;
			}
			path += files[i];
			add(path);
		}
	}

	public void arbitaryCommand(String string) {
		send(string);
	}

	public void osd(String value) {
		send(OSD + SPACE + value);
	}

	public void osdShowText(String text) {
		send(OSD_SHOW_TEXT + SPACE + text);
	}

	public void keyDownEvent(String key) {
		send(KEY_DOWN_EVENT + SPACE + key);
	}

	public void subSelect(String value) {
		send(SUB_SELECT + SPACE + value);
	}

	public void subVisibility(String subVisible) {
		send(SUB_VISIBILITY + SPACE + subVisible);
	}

	public void switchAudio(String audioTrack) {
		send(SWITCH_AUDIO + SPACE + audioTrack);
	}

	public void switchRatio(String ratio) {
		send(SWITCH_RATIO + SPACE + ratio);
	}

	public void frameStep() {
		send(FRAME_STEP);
	}

	public void seek(String position) {
		seek(position, "");
	}

	public void seek(String position, String relative) {
		send(SEEK + SPACE + position + SPACE + relative);
	}

	public void audioDelay(String delay, String relative) {
		send(AUDIO_DELAY + SPACE + delay + SPACE + relative);
	}

	public void pause() {
		send(PAUSE);
		isPaused = !isPaused;

	}

	public void mute() {
		if (isPaused) {
			printStream.print(PAUSING + SPACE + MUTE);
		} else {
			send(MUTE);
		}
	}

	public void clearList() {
		playlist.clear();
		accessBean.includePlaylistInNextTransmission();
	}

	public void play() {
		if (playlist.getLength() == 0) {
			return;
		} else if (playlist.getCurrentPosition() == -1) {
			playlist.setCurrentPosition(0);
		}
		play(true);
	}

	public void play(String fileOrIndex) {
		if (EMPTY.equals(fileOrIndex.trim())) {
			play(false);
		} else if (fileOrIndex.matches("[0-9]*")) {
			// we either have an index
			int index = Integer.parseInt(fileOrIndex);
			playlist.setCurrentPosition(index);
			play(true);
		} else {
			// or a file
			clearList();
			add(fileOrIndex);
			play(true);
		}
	}

	public void remove(String indexOrIndices) {
		int index = Integer.parseInt(indexOrIndices);
		playlist.remove(index);
		accessBean.includePlaylistInNextTransmission();
	}

	public void moveUp(String indexOrIndices) {
		int index = Integer.parseInt(indexOrIndices);
		playlist.moveUp(index);
		accessBean.includePlaylistInNextTransmission();
	}

	public void savePlaylist(String listName) {
		// TODO implement savePlaylist functionality
	}

	public void moveDown(String indexOrIndices) {
		if (nullOrEmpty(indexOrIndices)) {
			return;
		}
		int index = Integer.parseInt(indexOrIndices);
		playlist.moveDown(index);
		accessBean.includePlaylistInNextTransmission();
	}

	public void addRecursively(String aDirectory) {
		addCollection(aDirectory, true);
	}

	public void addAll(String aDirectory) {
		addCollection(aDirectory, false);
	}

	public void add(String aFile) {
		logger.info("adding " + aFile + " to playlist.");
		// check if the file is a playlist
		if (FileHelper.isPlaylistFile(aFile)) {
			playlist.loadPlaylist(aFile);
			accessBean.includePlaylistInNextTransmission();
		} else
		// or a directory
		if (new File(aFile).isDirectory()) {
			addCollection(aFile, true);
		}
		// or just a file
		else {
			playlist.add(aFile);
			accessBean.includePlaylistInNextTransmission();
		}
	}

	public void previous() {
		if (isPlaying && playlist.getSelected().equals(DVD)) {
			// next chapter
			send(SEEK_CHAPTER + SPACE + "-1");
		} else {
			// otherwise next in playlist
			playlist.previous();
			play(true);
		}
	}

	public void next() {
		if (isPlaying && playlist.getSelected().equals(DVD)) {
			// next chapter
			send(SEEK_CHAPTER + SPACE + "1");
		} else {
			// otherwise next in playlist
			playlist.next();
			play(true);
		}
	}

	public void stop() {
		loadFile(NULL_STRING);
	}

	public void volume(String volValue, String relative) {
		send(VOLUME + SPACE + volValue + SPACE + relative);
	}

	public void toggleFullScreen() {
		send(VO_FULLSCREEN);
	}

	public void speedSet(String speed) {
		send(SPEED_SET + SPACE + speed);
	}

	public void contrast(String contrast, String relative) {
		send(CONTRAST + SPACE + contrast + SPACE + relative);
	}

	public void gamma(String gamma, String relative) {
		send(GAMMA + SPACE + gamma + SPACE + relative);
	}

	public void brightness(String brightness, String relative) {
		send(BRIGHTNESS + SPACE + brightness + SPACE + relative);
	}

	public void hue(String hue, String relative) {
		send(HUE + SPACE + hue + SPACE + relative);
	}

	public void saturation(String saturation, String relative) {
		send(SATURATION + SPACE + saturation + SPACE + relative);
	}
}