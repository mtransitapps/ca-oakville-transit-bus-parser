package org.mtransit.parser.ca_oakville_transit_bus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.mtransit.parser.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Pair;
import org.mtransit.parser.SplitUtils;
import org.mtransit.parser.SplitUtils.RouteTripSpec;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GSpec;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.gtfs.data.GTripStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MTrip;
import org.mtransit.parser.mt.data.MTripStop;

// http://www.oakville.ca/data/catalogue.html
// http://www.oakville.ca/data/oakville-transit-route-information.html
// http://opendata.oakville.ca/Oakville_Transit_GTFS/Google_Transit.zip
public class OakvilleTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			args = new String[3];
			args[0] = "input/gtfs.zip";
			args[1] = "../../mtransitapps/ca-oakville-transit-bus-android/res/raw/";
			args[2] = ""; // files-prefix
		}
		new OakvilleTransitBusAgencyTools().start(args);
	}

	private HashSet<String> serviceIds;

	@Override
	public void start(String[] args) {
		System.out.printf("\nGenerating Oakville Transit bus data...");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("\nGenerating Oakville Transit bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
	}

	@Override
	public boolean excludingAll() {
		return this.serviceIds != null && this.serviceIds.isEmpty();
	}

	@Override
	public boolean excludeCalendar(GCalendar gCalendar) {
		if (this.serviceIds != null) {
			return excludeUselessCalendar(gCalendar, this.serviceIds);
		}
		return super.excludeCalendar(gCalendar);
	}

	@Override
	public boolean excludeCalendarDate(GCalendarDate gCalendarDates) {
		if (this.serviceIds != null) {
			return excludeUselessCalendarDate(gCalendarDates, this.serviceIds);
		}
		return super.excludeCalendarDate(gCalendarDates);
	}

	@Override
	public boolean excludeTrip(GTrip gTrip) {
		if (this.serviceIds != null) {
			return excludeUselessTrip(gTrip, this.serviceIds);
		}
		return super.excludeTrip(gTrip);
	}

	@Override
	public boolean excludeRoute(GRoute gRoute) {
		return super.excludeRoute(gRoute);
	}

	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		return CleanUtils.cleanLabel(gRoute.getRouteLongName().toLowerCase(Locale.ENGLISH));
	}

	private static final String AGENCY_COLOR = "DCA122"; // GOLD (AGENCY LOGO SVG WIKIPEDIA)

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
	public long getRouteId(GRoute gRoute) {
		if (Utils.isDigitsOnly(gRoute.getRouteId())) {
			return Long.parseLong(gRoute.getRouteId());
		}
		Matcher matcher = DIGITS.matcher(gRoute.getRouteId());
		if (matcher.find()) {
			long digits = Long.parseLong(matcher.group());
			String routeIdLC = gRoute.getRouteId().toLowerCase(Locale.ENGLISH);
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
		System.out.printf("\nUnexpected route ID for %s!\n", gRoute);
		System.exit(-1);
		return -1l;
	}

	private static final String COLOR_DE242C = "DE242C";
	private static final String COLOR_7F1C7D = "7F1C7D";
	private static final String COLOR_4D368A = "4D368A";
	private static final String COLOR_8A5032 = "8A5032";
	private static final String COLOR_AD4D45 = "AD4D45";
	private static final String COLOR_F05B72 = "F05B72";
	private static final String COLOR_1C4E9D = "1C4E9D";
	private static final String COLOR_201B18 = "201B18";
	private static final String COLOR_2EA983 = "2EA983";
	private static final String COLOR_00A7D8 = "00A7D8";
	private static final String COLOR_EE3429 = "EE3429";
	private static final String COLOR_1B6A4D = "1B6A4D";
	private static final String COLOR_A3238E = "A3238E";
	private static final String COLOR_00B5DA = "00B5DA";
	private static final String COLOR_8CBA40 = "8CBA40";
	private static final String COLOR_333300 = "333300";
	private static final String COLOR_B479A6 = "B479A6";
	private static final String COLOR_CA7A2F = "CA7A2F";
	private static final String COLOR_9DC73E = "9DC73E";
	private static final String COLOR_8F2E68 = "8F2E68";
	private static final String COLOR_221E1F = "221E1F";
	private static final String COLOR_DF241A = "DF241A";
	private static final String COLOR_DB214C = "DB214C";

	private static final String COLOR_SCHOOL_SPECIALS = "00529B"; // blue (not official)

	private static final String COLOR_SENIOR_SPECIALS = "5B6162"; // dark grey (not official)

	@Override
	public String getRouteColor(GRoute gRoute) {
		String routeColor = gRoute.getRouteColor();
		if (WHITE.equalsIgnoreCase(routeColor)) {
			routeColor = null; // can't be white
		}
		if (StringUtils.isEmpty(routeColor)) {
			int routeId = (int) getRouteId(gRoute);
			switch (routeId) {
			// @formatter:off
			case 1: return COLOR_DE242C;
			case 2: return COLOR_7F1C7D;
			case 3: return COLOR_4D368A;
			case 4: return COLOR_8A5032;
			case 5: return COLOR_AD4D45;
			case 5 + (int) RID_ENDS_WITH_A: return COLOR_AD4D45; // 5A
			case 6: return COLOR_F05B72;
			case 10: return COLOR_1C4E9D;
			case 11: return COLOR_201B18;
			case 12: return "365981";
			case 13: return COLOR_2EA983;
			case 14: return COLOR_00A7D8;
			case 14 + (int) RID_ENDS_WITH_A: return COLOR_00A7D8; // 14A
			case 15: return COLOR_EE3429;
			case 17: return COLOR_1B6A4D;
			case 18: return COLOR_333300;
			case 19: return COLOR_A3238E;
			case 20: return COLOR_00B5DA;
			case 21: return COLOR_333300;
			case 22: return COLOR_333300;
			case 24: return COLOR_8CBA40;
			case 25: return COLOR_333300;
			case 26: return COLOR_B479A6;
			case 28: return COLOR_CA7A2F;
			case 32: return COLOR_9DC73E;
			case 33: return COLOR_8F2E68;
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
			case 102: return COLOR_221E1F;
			case 120: return COLOR_DF241A;
			case 121: return null; // TODO
			case 190: return COLOR_DB214C;
			// @formatter:on
			default:
				System.out.printf("\nUnexpected route color for %s!\n", gRoute);
				System.exit(-1);
				return null;
			}
		}
		return routeColor;
	}

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		ALL_ROUTE_TRIPS2 = map2;
	}

	@Override
	public int compareEarly(long routeId, List<MTripStop> list1, List<MTripStop> list2, MTripStop ts1, MTripStop ts2, GStop ts1GStop, GStop ts2GStop) {
		if (ALL_ROUTE_TRIPS2.containsKey(routeId)) {
			return ALL_ROUTE_TRIPS2.get(routeId).compare(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop, this);
		}
		return super.compareEarly(routeId, list1, list2, ts1, ts2, ts1GStop, ts2GStop);
	}

	@Override
	public ArrayList<MTrip> splitTrip(MRoute mRoute, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return ALL_ROUTE_TRIPS2.get(mRoute.getId()).getAllTrips();
		}
		return super.splitTrip(mRoute, gTrip, gtfs);
	}

	@Override
	public Pair<Long[], Integer[]> splitTripStop(MRoute mRoute, GTrip gTrip, GTripStop gTripStop, ArrayList<MTrip> splitTrips, GSpec routeGTFS) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return SplitUtils.splitTripStop(mRoute, gTrip, gTripStop, routeGTFS, ALL_ROUTE_TRIPS2.get(mRoute.getId()), this);
		}
		return super.splitTripStop(mRoute, gTrip, gTripStop, splitTrips, routeGTFS);
	}

	@Override
	public void setTripHeadsign(MRoute mRoute, MTrip mTrip, GTrip gTrip, GSpec gtfs) {
		if (ALL_ROUTE_TRIPS2.containsKey(mRoute.getId())) {
			return; // split
		}
		mTrip.setHeadsignString( //
				cleanTripHeadsignWithoutRealTime(gTrip.getTripHeadsign()), //
				gTrip.getDirectionId() == null ? 0 : gTrip.getDirectionId() //
		);
	}

	@Override
	public boolean mergeHeadsign(MTrip mTrip, MTrip mTripToMerge) {
		List<String> headsignsValues = Arrays.asList(mTrip.getHeadsignValue(), mTripToMerge.getHeadsignValue());
		if (mTrip.getRouteId() == 4L) {
			if (Arrays.asList( //
					"WEST - Oakville GO", //
					"WEST - Bronte GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("WEST - Bronte GO", mTrip.getHeadsignId());
				return true;
			}
			if (Arrays.asList( //
					"EAST - Oakville GO", //
					"EAST - Clarkson GO" //
			).containsAll(headsignsValues)) {
				mTrip.setHeadsignString("EAST - Clarkson GO", mTrip.getHeadsignId());
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
		System.out.printf("\nUnexptected trips to merge %s & %s!\n", mTrip, mTripToMerge);
		System.exit(-1);
		return false;
	}

	private static final Pattern GO_ = Pattern.compile("((^|\\W){1}(go)(\\W|$){1})", Pattern.CASE_INSENSITIVE);
	private static final String GO_REPLACEMENT = "$2" + "GO" + "$4";

	private static final Pattern STARTS_WITH_RSN = Pattern.compile("(^[\\d]+ )", Pattern.CASE_INSENSITIVE);

	private static final Pattern ENDS_WITH_VIA = Pattern.compile("( (via) .*$)", Pattern.CASE_INSENSITIVE);

	private String cleanTripHeadsignWithoutRealTime(String tripHeadsign) {
		if (Utils.isUppercaseOnly(tripHeadsign, true, true)) {
			tripHeadsign = tripHeadsign.toLowerCase(Locale.ENGLISH);
		}
		tripHeadsign = STARTS_WITH_RSN.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = ENDS_WITH_VIA.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
		tripHeadsign = GO_.matcher(tripHeadsign).replaceAll(GO_REPLACEMENT);
		tripHeadsign = CleanUtils.CLEAN_AND.matcher(tripHeadsign).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanSlashes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	// trip head signs used for real-time API
	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(GStop gStop) {
		return Integer.parseInt(gStop.getStopCode()); // use stop code as stop ID
	}
}
