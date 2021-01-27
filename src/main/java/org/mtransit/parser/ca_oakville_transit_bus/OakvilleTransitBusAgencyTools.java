package org.mtransit.parser.ca_oakville_transit_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.ColorUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.StringUtils;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mtransit.parser.StringUtils.EMPTY;

// https://portal-exploreoakville.opendata.arcgis.com/
// https://portal-exploreoakville.opendata.arcgis.com/datasets/oakville-transit-route-and-schedule-information
// https://www.arcgis.com/sharing/rest/content/items/d78a1c1ad6a940009de8b68839a8f606/data
public class OakvilleTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@Nullable String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-oakville-transit-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new OakvilleTransitBusAgencyTools().start(args);
	}

	@Nullable
	private HashSet<Integer> serviceIdInts;

	@Override
	public void start(@NotNull String[] args) {
		MTLog.log("Generating Oakville Transit bus data...");
		long start = System.currentTimeMillis();
		this.serviceIdInts = extractUsefulServiceIdInts(args, this, true);
		super.start(args);
		MTLog.log("Generating Oakville Transit bus data... DONE in %s.", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIdInts != null && this.serviceIdInts.isEmpty();
	}

	@Override
	public boolean excludeCalendar(@NotNull GCalendar gCalendar) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarInt(gCalendar, this.serviceIdInts);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(@NotNull GCalendarDate gCalendarDates) {
		if (this.serviceIdInts != null) {
			return excludeUselessCalendarDateInt(gCalendarDates, this.serviceIdInts);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if (this.serviceIdInts != null) {
			return excludeUselessTripInt(gTrip, this.serviceIdInts);
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@NotNull
	@Override
	public String getRouteLongName(@NotNull GRoute gRoute) {
		return CleanUtils.cleanLabel(gRoute.getRouteLongNameOrDefault().toLowerCase(Locale.ENGLISH));
	}

	private static final String AGENCY_COLOR = "DCA122"; // GOLD (AGENCY LOGO SVG WIKIPEDIA)

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final Pattern DIGITS = Pattern.compile("[\\d]+");

	private static final long RID_ENDS_WITH_A = 1_000L;
	private static final long RID_ENDS_WITH_B = 2_000L;
	private static final long RID_ENDS_WITH_E = 5_000L;
	private static final long RID_ENDS_WITH_N = 14_000L;
	private static final long RID_ENDS_WITH_S = 19_000L;
	private static final long RID_ENDS_WITH_W = 23_000L;

	@Override
	public long getRouteId(@NotNull GRoute gRoute) {
		//noinspection deprecation
		final String routeId = gRoute.getRouteId();
		if (Utils.isDigitsOnly(routeId)) {
			return Long.parseLong(routeId);
		}
		Matcher matcher = DIGITS.matcher(routeId);
		if (matcher.find()) {
			long digits = Long.parseLong(matcher.group());
			String routeIdLC = routeId.toLowerCase(Locale.ENGLISH);
			if (routeIdLC.endsWith("a")) {
				return RID_ENDS_WITH_A + digits;
			} else if (routeIdLC.endsWith("b")) {
				return RID_ENDS_WITH_B + digits;
			} else if (routeIdLC.endsWith("e")) {
				return RID_ENDS_WITH_E + digits;
			} else if (routeIdLC.endsWith("n")) {
				return RID_ENDS_WITH_N + digits;
			} else if (routeIdLC.endsWith("s")) {
				return RID_ENDS_WITH_S + digits;
			} else if (routeIdLC.endsWith("w")) {
				return RID_ENDS_WITH_W + digits;
			}
		}
		throw new MTLog.Fatal("Unexpected route ID for %s!", gRoute);
	}

	private static final String COLOR_SCHOOL_SPECIALS = "00529B"; // blue (not official)

	private static final String COLOR_SENIOR_SPECIALS = "5B6162"; // dark grey (not official)

	@SuppressWarnings("DuplicateBranchesInSwitch")
	@Nullable
	@Override
	public String getRouteColor(@NotNull GRoute gRoute) {
		String routeColor = gRoute.getRouteColor();
		if (ColorUtils.WHITE.equalsIgnoreCase(routeColor)) {
			routeColor = null; // can't be white
		}
		if (StringUtils.isEmpty(routeColor)) {
			int routeId = (int) getRouteId(gRoute);
			switch (routeId) {
			// @formatter:off
			case 1: return "DE242C";
			case 2: return "7F1C7D";
			case 3: return "4D368A";
			case 4: return "8A5032";
			case 5: return "AD4D45";
			case 5 + (int) RID_ENDS_WITH_A: return "AD4D45"; // 5A
			case 6: return "F05B72";
			case 10: return "1C4E9D";
			case 11: return "201B18";
			case 12: return "365981";
			case 13: return "2EA983";
			case 14: return "00A7D8";
			case 14 + (int) RID_ENDS_WITH_A: return "00A7D8"; // 14A
			case 15: return "EE3429";
			case 17: return "1B6A4D";
			case 18: return "333300";
			case 19: return "A3238E";
			case 20: return "00B5DA";
			case 21: return "333300";
			case 22: return "333300";
			case 24: return "8CBA40";
			case 25: return "333300";
			case 26: return "B479A6";
			case 28: return "CA7A2F";
			case 32: return "9DC73E";
			case 33: return "8F2E68";
			case 34: return "CAAD35";
			case 54: return null; // TODO
			case 55: return null; // TODO
			case 71: return COLOR_SCHOOL_SPECIALS;
			case 80: return COLOR_SCHOOL_SPECIALS;
			case 80 + (int) RID_ENDS_WITH_E: return COLOR_SCHOOL_SPECIALS; // 80E
			case 80 + (int) RID_ENDS_WITH_W: return COLOR_SCHOOL_SPECIALS; // 80W
			case 81: return COLOR_SCHOOL_SPECIALS;
			case 81 + (int) RID_ENDS_WITH_A: return COLOR_SCHOOL_SPECIALS; // 81A
			case 81 + (int) RID_ENDS_WITH_B: return COLOR_SCHOOL_SPECIALS; // 81B
			case 81 + (int) RID_ENDS_WITH_N: return COLOR_SCHOOL_SPECIALS; // 81N
			case 81 + (int) RID_ENDS_WITH_S: return COLOR_SCHOOL_SPECIALS; // 81S
			case 82: return COLOR_SCHOOL_SPECIALS;
			case 83: return COLOR_SCHOOL_SPECIALS;
			case 84: return COLOR_SCHOOL_SPECIALS;
			case 86: return COLOR_SCHOOL_SPECIALS;
			case 86 + (int) RID_ENDS_WITH_B: return COLOR_SCHOOL_SPECIALS; // 86B
			case 90: return COLOR_SENIOR_SPECIALS;
			case 91: return COLOR_SENIOR_SPECIALS;
			case 92: return COLOR_SENIOR_SPECIALS;
			case 102: return "221E1F";
			case 120: return "DF241A";
			case 121: return null; // TODO
			case 190: return "DB214C";
			// @formatter:on
			default:
				throw new MTLog.Fatal("Unexpected route color for %s!", gRoute);
			}
		}
		return routeColor;
	}

	@Override
	public void setTripHeadsign(@NotNull MRoute mRoute, @NotNull MTrip mTrip, @NotNull GTrip gTrip, @NotNull GSpec gtfs) {
		if (mRoute.getId() == 81L + RID_ENDS_WITH_N) { // 81N
			if (gTrip.getDirectionIdOrDefault() == 1) { // FIXES 2 directions with same ID
				if ("Bronte and Richview".equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsignWithoutRealTime(gTrip.getTripHeadsign()), 0);
					return;
				} else if ("Loyola and Abbey Park".equals(gTrip.getTripHeadsign())) {
					mTrip.setHeadsignString(cleanTripHeadsignWithoutRealTime(gTrip.getTripHeadsign()), 1);
					return;
				}
				throw new MTLog.Fatal("Unexpected trip head-sign for %s!", gTrip.toStringPlus());
			}
		}
		mTrip.setHeadsignString( //
				cleanTripHeadsignWithoutRealTime(gTrip.getTripHeadsign()), //
				gTrip.getDirectionIdOrDefault() //
		);
	}

	@Override
	public boolean mergeHeadsign(@NotNull MTrip mTrip, @NotNull MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 4L) {
			if (Arrays.asList( //
					"W - Oakville GO", //
					"W - Bronte GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("W - Bronte GO", mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"E - Oakville GO", //
					"E - Clarkson GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("E - Clarkson GO", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 5L) {
			if (Arrays.asList( //
					"Hosp", //
					"Uptown Core", //
					"Dundas / 407 Carpool" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Dundas / 407 Carpool", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 6L) {
			if (Arrays.asList( //
					"Oakville GO", //
					"Bronte GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Bronte GO", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 14L + RID_ENDS_WITH_A) { // 14A
			if (Arrays.asList( //
					"Burloak & Rebecca", //
					"Appleby GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Appleby GO", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 34L) {
			if (Arrays.asList( //
					"Bronte GO", // <>
					"Pine Gln" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Pine Gln", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 81L + RID_ENDS_WITH_N) { // 81N
			if (Arrays.asList( //
					"Bronte & Richview", //
					"Loyola & Abbey Pk", //
					"Abbey Pk" // ++
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Abbey Pk", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 90L) {
			if (Arrays.asList( //
					"17 Stewart St.", //
					"Stewart St.", //
					"John R. Rhodes" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("John R. Rhodes", mTrip.getHeadsignId());
				return true;
			}
		} else if (mTrip.getRouteId() == 121L) {
			if (Arrays.asList( //
					"Industry & South Service", //
					"Southeast Industrial / Oakville GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("Southeast Industrial / Oakville GO", mTrip.getHeadsignId());
				return true;
			}
		}
		throw new MTLog.Fatal("Unexpected trips to merge %s & %s!", mTrip, mTripToMerge);
	}

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^[\\d]+ )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_ONLY = Pattern.compile("( only$)", Pattern.CASE_INSENSITIVE);

	private String cleanTripHeadsignWithoutRealTime(String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, tripHeadsign, getIgnoredWords());
		tripHeadsign = CleanUtils.keepToAndRemoveVia(tripHeadsign);
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = ENDS_WITH_ONLY.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	// trip head signs used for real-time API
	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	private String[] getIgnoredWords() {
		return new String[]{
				"GO", "OTMH", "YMCA",
		};
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(Locale.ENGLISH, gStopName, getIgnoredWords());
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		return Integer.parseInt(gStop.getStopCode()); // use stop code as stop ID
	}
}
