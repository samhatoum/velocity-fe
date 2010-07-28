package com.hatoum.jaibus.io.vision;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HandGestureProcessor implements FrameListener {

	private static final int NUMBER_OF_THUMBS = 1;

	private static final int NUMBER_OF_FINGERS = 4;

	private static final int MAX_DIGIT_WIDTH = 20;

	private static final int MIN_DIGIT_LENGTH = 100;

	private static final int BACKGROUND = ImageAquisition.getRGBAInt(0, 0, 0, 255);

	private static final int FOREGROUND = ImageAquisition.getRGBAInt(255, 255, 255, 255);

	private static final int MAX_DIGIT_AREA = 400;

	private static final Point TERMINAL_POINT = new Point(-1, -1);

	private static final int STEP = 1;

	private static final int MIN_PALM_WIDTH = 50;

	private static final int MIN_PALM_LENGTH = 20;

	private static final int STEP_JUMP = 10;

	private int[] pixels;

	private final int width;

	private final int height;

	Point[] GF_KERNEL = { new Point(1, 0), new Point(-1, 0), new Point(0, 1),
			new Point(0, -1) };

	public HandGestureProcessor(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public void newPixels(int[] pixels) {
		this.pixels = pixels;
		// regionProcess();
	}

	private void process() {

		int digitCount = 0;
		int digitWidth = 0;
		int digitLength = 0;
		boolean allFingersFound = false;
		boolean digitEntered;

		// scan top down and check if 4 intersections are detected
		VERTICAL_SCAN: for (int y = 0; y < height; y++) {
			digitCount = 0;
			digitEntered = false;
			for (int x = 0; x < width; x++) {

				// scan for non zero values
				if (!digitEntered && pixels[y * width + x] == FOREGROUND) {
					digitEntered = true;
				}

				// keep track of how long we are in the region, in case we
				// reach a bigger blob than the threshold, in which case, abort
				if (digitEntered && pixels[y * width + x] == FOREGROUND) {
					digitWidth++;
					setPixel(x, y, 0, 0, 255);
					if (digitWidth > MAX_DIGIT_WIDTH) {
						break VERTICAL_SCAN;
					}
				}

				// wait until we exit the finger region
				if (digitEntered && pixels[y * width + x] == BACKGROUND) {
					// before incrementing the finger count
					digitCount++;
					digitEntered = false;
					digitWidth = 0;
				}
			}

			// check if we have reached the right number of fingers
			if (digitCount == NUMBER_OF_FINGERS) {
				digitLength++;
				for (int xr = 0; xr < 320; xr++) {
					setPixel(xr, y, 255, 0, 255);
				}
			}

			// check length of the fingers, to avoid nuckle misfires etc
			if (digitLength == MIN_DIGIT_LENGTH) {
				allFingersFound = true;
				break;
			}
		}

		// if we have all fingers
		if (!allFingersFound) {
			return;
		}

		// TODO refactor this as it's very similar to the above
		boolean thumbFound = false;
		digitWidth = 0;
		digitLength = 0;

		// check vertically for the thumb
		HORIZONTAL_SCAN: for (int x = 0; x < width; x++) {
			digitCount = 0;
			digitEntered = false;
			for (int y = 0; y < height; y++) {

				// scan for non zero values
				if (!digitEntered && pixels[y * width + x] == FOREGROUND) {
					digitEntered = true;
				}

				// keep track of how long we are in the region, in case we reach
				// a bigger blob than the threshold, in which case, abort
				if (digitEntered && pixels[y * width + x] == FOREGROUND) {
					digitWidth++;
					setPixel(x, y, 255, 0, 255);
					if (digitWidth > MAX_DIGIT_WIDTH) {
						break HORIZONTAL_SCAN;
					}
				}

				// wait until we exit the finger region
				if (digitEntered && pixels[y * width + x] == BACKGROUND) {
					// before incrementing the digit count
					digitCount++;
					digitWidth = 0;
					digitEntered = false;
				}
			}

			// check if we have one thumb
			if (digitCount == NUMBER_OF_THUMBS) {
				digitLength++;
				for (int yr = 0; yr < 240; yr++) {
					setPixel(x, yr, 255, 0, 255);
				}
			} else if (digitCount > NUMBER_OF_THUMBS) {
				break;
			}

			// check length of the thumb
			if (digitLength == MIN_DIGIT_LENGTH) {
				thumbFound = true;
				break;
			}
		}

		if (thumbFound) {
			System.err.print("X ");
		}
	}

	private void regionProcess() {
		int regionCount = 0;
		int previousRegionCount = -1;
		int regionWidth = 0;
		int regionLength = 0;
		boolean regionEntered = false;
		boolean atLeastOneRegionHasBeenSeen = false;

		for (int y = 0; y < height; y += STEP) {
			regionCount = 0;
			regionEntered = false;
			for (int x = 0; x < width; x += STEP) {

				// ENTER REGION
				// scan for non zero values
				if (!regionEntered && pixels[y * width + x] == FOREGROUND) {
					regionEntered = true;
					atLeastOneRegionHasBeenSeen = true;
				}

				// SCAN REGION
				// keep track of how long we are in the region
				if (regionEntered && pixels[y * width + x] == FOREGROUND) {
					regionWidth++;
					setPixel(x, y, 0, 0, 255);
				}

				// EXIT REGION
				// wait until we exit the finger region
				if (regionEntered && pixels[y * width + x] == BACKGROUND) {
					// before incrementing the finger count
					regionCount++;
					regionEntered = false;
					regionWidth = 0;
				}

				// check if we've reached the palm
				if (!regionEntered && regionWidth > MIN_PALM_WIDTH) {
					// TODO we've reached the palm/fist
				}
			}

			// process lengths only if we've started region scanning
			if (atLeastOneRegionHasBeenSeen) {
				if (regionCount == previousRegionCount) {
					regionLength++;
				} else {
					previousRegionCount = regionCount;
				}
			}
			if (regionLength == MIN_DIGIT_LENGTH) {
				break;
				// we've found all the digits, jump a few steps to speed up the
				// search for the palm
				// if (y + 10 < 240)
				// y += STEP_JUMP;
			}
		}
	}

	private void setPixel(int x, int y, int r, int g, int b) {
		if (ImageAquisition.inImage(x, y)) {
			pixels[y * width + x] = ImageAquisition.getRGBAInt(r, g, b, 255);
		}
	}

	private void gfProcess() {

		ArrayList<List> regions = new ArrayList<List>();
		ArrayList<Point> region = new ArrayList<Point>();
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (pixels[y * width + x] == FOREGROUND) {
					gfBurn(x, y, region);
				}
				if (region.size() != 0) {
					regions.add(region);
					region = new ArrayList<Point>();
				}
			}
		}

		System.err.println("number of regions = " + regions.size());

		for (List<Point> r : regions) {
			psuedoColourRegion(r);
		}
	}

	private void psuedoColourRegion(List<Point> region) {
		Random random = new Random();
		int r = random.nextInt(255);
		int g = random.nextInt(255);
		int b = random.nextInt(255);
		for (Point point : region) {
			setPixel(point.x, point.y, r, g, b);
		}
	}

	private void gfBurn(int x, int y, List<Point> region) {

		// stop grassfire if region is too big
		if (region.size() > MAX_DIGIT_AREA) {
			// System.out.println("Region Rejected, " + region.size() + "
			// pixels");
			return;
		}

		// burn that pixel
		setPixel(x, y, 0, 0, 0);
		region.add(new Point(x, y));

		// and the pixels around the kermel
		for (short i = 0; i < GF_KERNEL.length; i++) {
			short j = (short) (x + GF_KERNEL[i].x);
			short k = (short) (y + GF_KERNEL[i].y);
			if (ImageAquisition.inImage(j, k)
					&& pixels[k * width + j] == FOREGROUND) {
				region.add(new Point(j, k));
				gfBurn(j, k, region);
			}
		}
	}

}
