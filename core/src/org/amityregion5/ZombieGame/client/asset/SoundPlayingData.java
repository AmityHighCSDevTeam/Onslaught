package org.amityregion5.ZombieGame.client.asset;

public class SoundPlayingData {
	private String sound;
	private double volume;
	private double pitch;
	
	/**
	 * @param sound
	 * @param volume
	 * @param pitch
	 */
	public SoundPlayingData(String sound, double volume, double pitch) {
		this.sound = sound;
		this.volume = volume;
		this.pitch = pitch;
	}
	/**
	 * @return the sound
	 */
	public String getSound() {
		return sound;
	}
	/**
	 * @param sound the sound to set
	 */
	public void setSound(String sound) {
		this.sound = sound;
	}
	/**
	 * @return the volume
	 */
	public double getVolume() {
		return volume;
	}
	/**
	 * @param volume the volume to set
	 */
	public void setVolume(double volume) {
		this.volume = volume;
	}
	/**
	 * @return the pitch
	 */
	public double getPitch() {
		return pitch;
	}
	/**
	 * @param pitch the pitch to set
	 */
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
}
