package com.hatoum.jaibus.io.ibus;

import gnu.trove.TByteHashSet;
import gnu.trove.TByteObjectHashMap;

import com.hatoum.jaibus.io.Device;

@SuppressWarnings( { "unchecked", "unchecked" })
public enum IBusDevice implements Device {

	CD_PLAYER((byte) 0x18),

	NAVIGATION_VIDEO_MODULE((byte) 0x3B),

	MENUSCREEN((byte) 0x43),

	STEERING_WHEEL_BUTTONS((byte) 0x50),

	PARK_DISTANCE_CONTROL((byte) 0x60),

	RAD_RADIO((byte) 0x68),

	DIGITAL_SOUND_PROCESSOR((byte) 0x6A),

	INSTRUMENT_KOMBI_ELECTRONICS((byte) 0x80),

	TV_MODULE((byte) 0xBB),

	LIGHT_CONTROL_MODULE((byte) 0xBF),

	MULTI_INFORMATION_DISPLAY_BUTTONS((byte) 0xC0),

	TEL_TELEPHONE((byte) 0xC8),

	NAVIGATION_LOCATION((byte) 0xD0),

	OBC_TEXTBAR((byte) 0xE7),

	LIGHTS_WIPERS_SEAT_MEMORY((byte) 0xED),

	BOARD_MONITOR_BUTTONS((byte) 0xF0),

	BROADCAST_00((byte) 0x00),

	BROADCAST_FF((byte) 0xFF),

	UKNONWN1((byte) 0x30),

	UNKNOWN2((byte) 0x3F),

	UNKNOWN3((byte) 0x44),

	UNKNOWN4((byte) 0x7F),

	UNKNOWN5((byte) 0xA8),

	UNKNOWN6((byte) 0xE8);

	private static TByteHashSet devices;
	static {
		devices = new TByteHashSet();
		devices.add(IBusDevice.CD_PLAYER.id);
		devices.add(IBusDevice.NAVIGATION_VIDEO_MODULE.id);
		devices.add(IBusDevice.MENUSCREEN.id);
		devices.add(IBusDevice.STEERING_WHEEL_BUTTONS.id);
		devices.add(IBusDevice.PARK_DISTANCE_CONTROL.id);
		devices.add(IBusDevice.RAD_RADIO.id);
		devices.add(IBusDevice.DIGITAL_SOUND_PROCESSOR.id);
		devices.add(IBusDevice.INSTRUMENT_KOMBI_ELECTRONICS.id);
		devices.add(IBusDevice.TV_MODULE.id);
		devices.add(IBusDevice.LIGHT_CONTROL_MODULE.id);
		devices.add(IBusDevice.MULTI_INFORMATION_DISPLAY_BUTTONS.id);
		devices.add(IBusDevice.TEL_TELEPHONE.id);
		devices.add(IBusDevice.NAVIGATION_LOCATION.id);
		devices.add(IBusDevice.OBC_TEXTBAR.id);
		devices.add(IBusDevice.LIGHTS_WIPERS_SEAT_MEMORY.id);
		devices.add(IBusDevice.BOARD_MONITOR_BUTTONS.id);
		devices.add(IBusDevice.BROADCAST_00.id);
		devices.add(IBusDevice.BROADCAST_FF.id);
		devices.add(IBusDevice.UKNONWN1.id);
		devices.add(IBusDevice.UNKNOWN2.id);
		devices.add(IBusDevice.UNKNOWN3.id);
		devices.add(IBusDevice.UNKNOWN4.id);
		devices.add(IBusDevice.UNKNOWN5.id);
		devices.add(IBusDevice.UNKNOWN6.id);
	}

	private static TByteObjectHashMap devicesByID;
	static {
		devicesByID = new TByteObjectHashMap();
		devicesByID.put(IBusDevice.CD_PLAYER.id, IBusDevice.CD_PLAYER);
		devicesByID.put(IBusDevice.NAVIGATION_VIDEO_MODULE.id,
				IBusDevice.NAVIGATION_VIDEO_MODULE);
		devicesByID.put(IBusDevice.MENUSCREEN.id, IBusDevice.MENUSCREEN);
		devicesByID.put(IBusDevice.STEERING_WHEEL_BUTTONS.id,
				IBusDevice.STEERING_WHEEL_BUTTONS);
		devicesByID.put(IBusDevice.PARK_DISTANCE_CONTROL.id,
				IBusDevice.PARK_DISTANCE_CONTROL);
		devicesByID.put(IBusDevice.RAD_RADIO.id, IBusDevice.RAD_RADIO);
		devicesByID.put(IBusDevice.DIGITAL_SOUND_PROCESSOR.id,
				IBusDevice.DIGITAL_SOUND_PROCESSOR);
		devicesByID.put(IBusDevice.INSTRUMENT_KOMBI_ELECTRONICS.id,
				IBusDevice.INSTRUMENT_KOMBI_ELECTRONICS);
		devicesByID.put(IBusDevice.TV_MODULE.id, IBusDevice.TV_MODULE);
		devicesByID.put(IBusDevice.LIGHT_CONTROL_MODULE.id,
				IBusDevice.LIGHT_CONTROL_MODULE);
		devicesByID.put(IBusDevice.MULTI_INFORMATION_DISPLAY_BUTTONS.id,
				IBusDevice.MULTI_INFORMATION_DISPLAY_BUTTONS);
		devicesByID.put(IBusDevice.TEL_TELEPHONE.id, IBusDevice.TEL_TELEPHONE);
		devicesByID.put(IBusDevice.NAVIGATION_LOCATION.id,
				IBusDevice.NAVIGATION_LOCATION);
		devicesByID.put(IBusDevice.OBC_TEXTBAR.id, IBusDevice.OBC_TEXTBAR);
		devicesByID.put(IBusDevice.LIGHTS_WIPERS_SEAT_MEMORY.id,
				IBusDevice.LIGHTS_WIPERS_SEAT_MEMORY);
		devicesByID.put(IBusDevice.BOARD_MONITOR_BUTTONS.id,
				IBusDevice.BOARD_MONITOR_BUTTONS);
		devicesByID.put(IBusDevice.BROADCAST_00.id, IBusDevice.BROADCAST_00);
		devicesByID.put(IBusDevice.BROADCAST_FF.id, IBusDevice.BROADCAST_FF);
		devicesByID.put(IBusDevice.UKNONWN1.id, IBusDevice.UKNONWN1);
		devicesByID.put(IBusDevice.UNKNOWN2.id, IBusDevice.UNKNOWN2);
		devicesByID.put(IBusDevice.UNKNOWN3.id, IBusDevice.UNKNOWN3);
		devicesByID.put(IBusDevice.UNKNOWN4.id, IBusDevice.UNKNOWN4);
		devicesByID.put(IBusDevice.UNKNOWN5.id, IBusDevice.UNKNOWN5);
		devicesByID.put(IBusDevice.UNKNOWN6.id, IBusDevice.UNKNOWN6);
	}

	public static Device getDeviceByID(Number id) {
		return (Device) devicesByID.get(id.byteValue());
	}

	private final byte id;

	private IBusDevice(byte id) {
		this.id = id;
	}

	public boolean validate(Number id) {
		return devices.contains(id.byteValue());
	}

	public Number getID() {
		return id;
	}

	public String getName() {
		return this.toString();
	}
}