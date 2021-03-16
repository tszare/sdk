package de.onvif.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.onvif.ver10.media.wsdl.GetOSD;
import org.onvif.ver10.media.wsdl.GetOSDResponse;
import org.onvif.ver10.media.wsdl.Media;
import org.onvif.ver10.media.wsdl.SetOSD;
import org.onvif.ver10.schema.OSDConfiguration;
import org.onvif.ver10.schema.PTZPreset;
import org.onvif.ver10.schema.PTZStatus;
import org.onvif.ver10.schema.PTZVector;
import org.onvif.ver10.schema.Profile;
import org.onvif.ver10.schema.Vector2D;
import org.onvif.ver20.ptz.wsdl.Capabilities;
import org.onvif.ver20.ptz.wsdl.PTZ;

import com.google.common.collect.Lists;

import de.onvif.soap.OnvifDevice;
import lombok.extern.log4j.Log4j;
import regis.http.client.RegisUtil;

@Log4j
public final class OnvifUtils {
	private final static String sep = "\n";

	public static String format(PTZVector vector) {
		String out = "";
		if (vector != null) {
			out += "[" + vector.getPanTilt().getX() + "," + vector.getPanTilt().getY();
			if (vector.getZoom() != null) {
				out += "," + vector.getZoom().getX();
			}
			out += "]";
		}
		return out;
	}

	public static String format(PTZPreset preset) {
		String out = "";
		if (preset != null) {
			out += preset.getToken() + "/" + preset.getName() + ":" + format(preset.getPTZPosition());
		}
		return out;
	}

	public static String format(PTZStatus status) {
		String out = "";
		if (status != null) {
			out += "moveStatus=" + format(status.getMoveStatus()) + " position=" + format(status.getPosition())
					+ " time=" + status.getUtcTime();
		}
		return out;
	}

	public static String format(Object o) {
		String out = "";
		if (o != null) {
			out = o.toString();
			for (;;) {
				int ch = out.indexOf("org.onvif.ver");
				if (ch == -1) {
					break;
				}
				int end = out.indexOf("[", ch);
				if (end == -1) {
					assert (false);
					break;
				} //
				int at = out.indexOf("@", ch);
				if (at == -1 || at > end) {
					assert (false);
					break;
				}

				out = out.substring(0, ch) + out.substring(end);
			}

			out = out.replaceAll("<null>", "");
		}
		return out;
	}

	public static List<String> getResource(String resourceName) {
		InputStream in = OnvifUtils.class.getResourceAsStream("/" + resourceName);
		List<String> lineList = Lists.newArrayList();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				if (RegisUtil.isBlank(line)) {
					continue;
				}
				lineList.add(line);
			}
		} catch (UnsupportedEncodingException e) {
			log.error("fatal error", e);
		} catch (IOException e) {
			log.error("fatal error", e);
		}
		return lineList;
	}

	public static void printAndSetOSD(Media media) {
		java.util.List<org.onvif.ver10.schema.OSDConfiguration> osdList = media.getOSDs(null);

		log.info(RegisUtil.toJson(osdList));
		GetOSD param = new GetOSD();
		param.setOSDToken("TextOSD001");
		GetOSDResponse response = media.getOSD(param);
		OSDConfiguration configuration = response.getOSD();
		configuration.getTextString().setPlainText("测试@N415大华球机");
		SetOSD setOSD = new SetOSD();
		setOSD.setOSD(configuration);
		media.setOSD(setOSD);
		log.info(RegisUtil.toJson(response));
	}

	public static String formatMediaProfiles(OnvifDevice device) {
		StringBuilder builder = new StringBuilder();
		Media media = device.getMedia();
		List<Profile> profiles = media.getProfiles();
		builder.append("Media Profiles: ").append(profiles.size()).append(sep);
		final String tab = " ";

		for (Profile profile : profiles) {
			String profileToken = profile.getToken();
			String rtsp = device.getStreamUri(profileToken);
			builder.append(tab).append("Profile: ").append(profile.getName()).append(" token=")
					.append(profile.getToken()).append(sep);
			builder.append(tab).append(tab).append("stream: ").append(rtsp).append(profile.getToken()).append(sep);
			builder.append(tab).append(tab).append("snapshot: ").append(device.getSnapshotUri(profileToken))
					.append(sep);
			builder.append(tab).append(tab).append("details:").append(OnvifUtils.format(profile)).append(sep);
		}
		return builder.toString();
	}

	public static String formatPtzList(OnvifDevice device) {
		StringBuilder builder = new StringBuilder();
		Media media = device.getMedia();
		List<Profile> profiles = media.getProfiles();
		PTZ ptz = device.getPtz();
		final String tab = " ";

		for (Profile profile : profiles) {
			log.info(profile.getToken());
		}

		if (ptz != null) {
			String profileToken = profiles.get(0).getToken();
			try {

				Capabilities ptz_caps = ptz.getServiceCapabilities();
				builder.append("PTZ:").append(sep);
				builder.append(tab).append("getServiceCapabilities=").append(format(ptz_caps)).append(sep);
				PTZStatus s = ptz.getStatus(profileToken);
				builder.append(tab).append("getStatus=").append(format(s)).append(sep);
				// out += "ptz.getConfiguration=" +
				// ptz.getConfiguration(profileToken) + sep;
				log.info("start ptz control ......................");
				PTZVector ptzSpeed = new PTZVector();
				Vector2D vector2 = new Vector2D();
				vector2.setX(1.0f);
				ptzSpeed.setPanTilt(vector2);
				ptz.relativeMove(profileToken, ptzSpeed, null);

				log.info("end ptz control ......................");
				List<PTZPreset> presets = ptz.getPresets(profileToken);
				if (presets != null && !presets.isEmpty()) {
					builder.append(tab).append("Presets:").append(presets.size()).append(sep);
					for (PTZPreset p : presets) {
						builder.append(tab).append(tab).append(format(p)).append(sep);
					}
				}
			} catch (Throwable th) {
				log.error("PTZ: Unavailable" + th.getMessage());
			}
		}
		return builder.toString();
	}

}
