package io.minepkg.companion.common;

import java.util.Objects;

public final class Modpack {
	private final String name;
	private final String version;
	private final String platform;

	public Modpack(String name, String version, String platform) {
		this.name = name;
		this.version = version;
		this.platform = platform;
	}

	public String name() {
		return name;
	}

	public String version() {
		return version;
	}

	public String platform() {
		return platform;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		Modpack that = (Modpack) obj;
		return Objects.equals(this.name, that.name) &&
			Objects.equals(this.version, that.version) &&
			Objects.equals(this.platform, that.platform);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, version, platform);
	}

	@Override
	public String toString() {
		return "Modpack[" +
			"name=" + name + ", " +
			"version=" + version + ", " +
			"platform=" + platform + ']';
	}
}
