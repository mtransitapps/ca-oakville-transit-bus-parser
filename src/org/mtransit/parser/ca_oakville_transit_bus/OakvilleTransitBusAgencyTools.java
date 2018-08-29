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
import org.mtransit.parser.mt.data.MDirectionType;
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
		int routeId = (int) getRouteId(gRoute);
		switch (routeId) {
		// @formatter:off
		case 1: return COLOR_DE242C;
		case 2: return COLOR_7F1C7D;
		case 3: return COLOR_4D368A;
		case 4: return COLOR_8A5032;
		case 5: return COLOR_AD4D45;
		case 5 + (int) RID_ENDS_WITH_A: return COLOR_AD4D45;
		case 6: return COLOR_F05B72;
		case 10: return COLOR_1C4E9D;
		case 11: return COLOR_201B18;
		case 12: return "365981";
		case 13: return COLOR_2EA983;
		case 14: return COLOR_00A7D8;
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
		case 71: return COLOR_SCHOOL_SPECIALS;
		case 80: return COLOR_SCHOOL_SPECIALS;
		case 80 + (int) RID_ENDS_WITH_E: return COLOR_SCHOOL_SPECIALS;
		case 80 + (int) RID_ENDS_WITH_W: return COLOR_SCHOOL_SPECIALS;
		case 81: return COLOR_SCHOOL_SPECIALS;
		case 81 + (int) RID_ENDS_WITH_A: return COLOR_SCHOOL_SPECIALS;
		case 81 + (int) RID_ENDS_WITH_B: return COLOR_SCHOOL_SPECIALS;
		case 81 + (int) RID_ENDS_WITH_N: return COLOR_SCHOOL_SPECIALS;
		case 81 + (int) RID_ENDS_WITH_S: return COLOR_SCHOOL_SPECIALS;
		case 82: return COLOR_SCHOOL_SPECIALS;
		case 83: return COLOR_SCHOOL_SPECIALS;
		case 84: return COLOR_SCHOOL_SPECIALS;
		case 86: return COLOR_SCHOOL_SPECIALS;
		case 90: return COLOR_SENIOR_SPECIALS;
		case 91: return COLOR_SENIOR_SPECIALS;
		case 92: return COLOR_SENIOR_SPECIALS;
		case 102: return COLOR_221E1F;
		case 120: return COLOR_DF241A;
		case 121: return null; // TODO
		case 190: return COLOR_DB214C;
		// @formatter:on
		}
		if (isGoodEnoughAccepted()) {
			return gRoute.getRouteColor();
		}
		System.out.printf("\nUnexpected route ID color for %s!\n", gRoute);
		System.exit(-1);
		return null;
	}

	// trip head signs used for real-time API
	// http://busfinder.oakvilletransit.ca/bustime/eta/eta.jsp
	private static final String _407_CARPOOL_RT = "407Carpool";
	private static final String APHS_NORTH_RT = "APHS North";
	private static final String APHS_SOUTH_RT = "APHS South";
	private static final String ASPEN_FORST_RT = "AspenForst";
	private static final String BRONTE_GO_RT = "Bronte GO";
	private static final String BURLOAK_DR_RT = "BurloakDr";
	private static final String CLARKSON_GO_RT = "ClarksonGO";
	private static final String DOWNTOWN_RT = "Downtown";
	private static final String EAST_IND_RT = "East Ind";
	private static final String EAST_RT = "East";
	private static final String FALGARWOOD_RT = "Falgarwood";
	private static final String FROM_GWHS_RT = "From GWHS";
	private static final String FROM_HTCSS_RT = "From HTCSS";
	private static final String GA_NORTH_RT = "GA North";
	private static final String GA_SOUTH_RT = "GA South";
	private static final String HOSPITAL_RT = "Hospital";
	private static final String JOHN_RHODES_RT = "JohnRhodes";
	private static final String KNOX_RT = "Knox";
	private static final String LCSS_NORTH_RT = "LCSS North";
	private static final String MAPLEGROVE_RT = "Maplegrove";
	private static final String OAK_PLACE_RT = "Oak Place";
	private static final String OAK_SENIORS_RT = "OakSeniors";
	private static final String OAKVILLE_GO_RT = "OakvilleGO";
	private static final String OTHS_GO_RT = "OTHS-GO";
	private static final String PALERMO_RT = "Palermo";
	private static final String RIO_CAN_RT = "RioCan";
	private static final String RO_EXPRESS_RT = "RO Express";
	private static final String S_OAK_CENTRE_RT = "SOakCentre";
	private static final String SOUTH_COMM_RT = "SouthComm";
	private static final String TAB_EAST_RT = "TAB East";
	private static final String TAB_WEST_RT = "TAB West";
	private static final String TO_GWHS_RT = "To GWHS";
	private static final String TO_HTCSS_RT = "To HTCSS";
	private static final String TO_OTHS_RT = "To OTHS";
	private static final String UP_GLN_ABBEY_RT = "UpGlnAbbey";
	private static final String UPTOWN_CORE_RT = "UptownCore";
	private static final String VIA_P_GLEN_RT = "via P Glen";
	private static final String VIA_PALERMO_RT = "viaPalermo";
	private static final String WAL_MART_RT = "Wal-Mart";
	private static final String WEST_RT = "West";
	private static final String WIN_PARK_RT = "WinPark";

	private static HashMap<Long, RouteTripSpec> ALL_ROUTE_TRIPS2;
	static {
		HashMap<Long, RouteTripSpec> map2 = new HashMap<Long, RouteTripSpec>();
		map2.put(1l, new RouteTripSpec(1l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, UPTOWN_CORE_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { //
						"1212", // "3172", // Oakville GO Station
								"1213", //
								"1054", //
								"1293", //
								"1665", // "2129", // Trafalgar Rd + Highway 407 GO Carpool
						})) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { //
						"1665", // "2129", // Trafalgar Rd + Highway 407 GO Carpool
								"1293", //
								"1067", //
								"1212", // "3172", // Oakville GO Station
						})) //
				.compileBothTripSort());
		map2.put(2l, new RouteTripSpec(2l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, EAST_RT, // Dundas St + Hampshire Gt
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, WEST_RT) // Bronte GO
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "645", "1232", "152", "949", "130", "910" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "910", "446", "488", "645" })) //
				.compileBothTripSort());
		map2.put(3l, new RouteTripSpec(3l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, HOSPITAL_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, S_OAK_CENTRE_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "877", "645", "1397" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "1397", "483", "645", "1126", "877" })) //
				.compileBothTripSort());
		map2.put(4l, new RouteTripSpec(4l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, EAST_RT, // Clarkson GO
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, WEST_RT) // Bronte GO
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "645", "1274", "1212", "361", "358" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "358", "158", "1212", "1143", "645" })) //
				.compileBothTripSort());
		map2.put(5l, new RouteTripSpec(5l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, _407_CARPOOL_RT) // Walkers Line
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1575", "630", "1397", //
								"148", // !=
								"1293", // ==
								"750", // !=
								"1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", //
								"945", // !=
								"1293", // ==
								"187", // !=
								"254", "1397", "1575" //
						})) //
				.compileBothTripSort());
		map2.put(6L, new RouteTripSpec(6L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, EAST_RT, // Dundas & Hampshire
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, WEST_RT) // Bronte GO
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"645", // "2421", // Bronte GO Station
								"1090", // !=
								"137", // <>
								"697", // <>
								"905", // <>
								"1068", // <>
								"1054", // !=
								"409", // != ==
								"1668", // <> !=
								"367", // <> !=
								"1655", // <> !=
								"326", // != !=
								"1240", // != ==
								"1550", // "3174", // Laird Rd + Ridgeway Dr
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1550", // "3174", // Laird Rd + Ridgeway Dr
								"1230", // != ==
								"763", // != !=
								"1668", // <> !=
								"367", // <> !=
								"1655", // <> !=
								"437", // != ==
								"1077", // !=
								"137", // <>
								"697", // <>
								"905", // <> ==
								"537", // !=
								"1212", // != "3172", // Oakville GO Station
								"1068", // <> !=
								"1517", // != ==
								"981", // ++
								"645", // "2421", // Bronte GO Station
						})) //
				.compileBothTripSort());
		map2.put(10l, new RouteTripSpec(10l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "645", //
								"397", "245", //
								"1278", "404", //
								"881", "1212" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "1212", "591", "267", //
								"142", "206", "1318", //
								"604", "958", //
								"645" })) //
				.compileBothTripSort());
		map2.put(11l, new RouteTripSpec(11l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, MAPLEGROVE_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", //
								"358", //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"358", //
								"446", //
								"984", //
								"1212" //
						})) //
				.compileBothTripSort());
		map2.put(12l, new RouteTripSpec(12l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, "Laird / Ridgeway", //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, CLARKSON_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "358", "1550" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "1550", "991", "358" })) //
				.compileBothTripSort());
		map2.put(13l, new RouteTripSpec(13l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"645", "677", "314", "930", //
								"727", // !=
								"1050", // ==
								"336", // !=
								"1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", //
								"1101", // !=
								"1050", // ==
								"87", // !=
								"884", "1268", "512", "645" //
						})) //
				.compileBothTripSort());
		map2.put(14l, new RouteTripSpec(14l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, BURLOAK_DR_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1543", //
								"963", "933", //
								"1255", // !=
								"877", // ==
								"1182", // !=
								"509", "1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", "595", //
								"259", // !=
								"877", // ==
								"1109", // !=
								"108", //
								"1543" //
						})) //
				.compileBothTripSort());
		map2.put(15l, new RouteTripSpec(15l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, S_OAK_CENTRE_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1100", "963", "511", //
								"1307", // !=
								"515", "139", // ==
								"854", // !=
								"46", "1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", "541", //
								"909", // !=
								"515", "139", // ==
								"1091", // !=
								"1100" })) //
				.compileBothTripSort());
		map2.put(17l, new RouteTripSpec(17l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, DOWNTOWN_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "42", "159", "1212" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "1212", "766", "42" })) //
				.compileBothTripSort());
		map2.put(18l, new RouteTripSpec(18l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, GA_SOUTH_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"645", "507", "1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", "794", "645" //
						})) //
				.compileBothTripSort());
		map2.put(19l, new RouteTripSpec(19l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, UPTOWN_CORE_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "1212", "556", "160", "1293" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "1293", "330", "551", "1212" })) //
				.compileBothTripSort());
		map2.put(20l, new RouteTripSpec(20l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, UPTOWN_CORE_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "1212", "741", "1293" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "1293", "1237", "288", "1212" })) //
				.compileBothTripSort());
		map2.put(21l, new RouteTripSpec(21l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, CLARKSON_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, MAPLEGROVE_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "664", "778", "358" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "358", "675", "664" })) //
				.compileBothTripSort());
		map2.put(22l, new RouteTripSpec(22l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, UP_GLN_ABBEY_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "645", "923", "377" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "377", "204", "645" })) //
				.compileBothTripSort());
		map2.put(24l, new RouteTripSpec(24l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, SOUTH_COMM_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "1212", "1020", "905", "593" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "593", "905", "260", "1212" })) //
				.compileBothTripSort());
		map2.put(25l, new RouteTripSpec(25l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, CLARKSON_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, ASPEN_FORST_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "664", //
								"305", "329", //
								"351", "104", //
								"81", "358" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "358", "446", //
								"430", "158", //
								"753", "948", //
								"664" })) //
				.compileBothTripSort());
		map2.put(26l, new RouteTripSpec(26l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, FALGARWOOD_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "1212", "882", "186" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "186", "501", "1212" })) //
				.compileBothTripSort());
		map2.put(28l, new RouteTripSpec(28l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, GA_NORTH_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"645", "804", "1212" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", "1555", "645" //
						})) //
				.compileBothTripSort());
		map2.put(32l, new RouteTripSpec(32l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, RIO_CAN_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "1100", "963", "877", "645" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "645", "1126", "877", "331", "1100" })) //
				.compileBothTripSort());
		map2.put(33L, new RouteTripSpec(33L, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, PALERMO_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { //
						"645", // "2421", // Bronte GO Station
								"978", // "3323", // Upper Middle Rd West + Trawden Way
								"914", // ++
								"1667", // "2105", // Colonel William Pkwy + Dundas St West
						})) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { //
						"1667", // "2105", // Colonel William Pkwy + Dundas St West
								"61", // ++
								"645", // "2421", // Bronte GO Station
						})) //
				.compileBothTripSort());
		map2.put(34l, new RouteTripSpec(34l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, "Pine Glen", //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, BRONTE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "645", "832", "377" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "377", "645" })) //
				.compileBothTripSort());
		map2.put(71l, new RouteTripSpec(71l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, UPTOWN_CORE_RT, // WOSS North
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, "") // WOSS South
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "556", "160", "1293" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { /* no stops */})) //
				.compileBothTripSort());
		map2.put(80l + RID_ENDS_WITH_E, new RouteTripSpec(80l + RID_ENDS_WITH_E, // 80E
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, FROM_HTCSS_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TO_HTCSS_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "408", "523", "238" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "24", "609", "1136" })) //
				.compileBothTripSort());
		map2.put(80l + RID_ENDS_WITH_W, new RouteTripSpec(80l + RID_ENDS_WITH_W, // 80W
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, TO_HTCSS_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, FROM_HTCSS_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "1183", "1013", "408" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "408", "539", "764" })) //
				.compileBothTripSort());
		map2.put(81l + RID_ENDS_WITH_A, new RouteTripSpec(81l + RID_ENDS_WITH_A, // 81A
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, VIA_PALERMO_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, "") //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "978", "1139", "451" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] {/* no stops */})) //
				.compileBothTripSort());
		map2.put(81l + RID_ENDS_WITH_B, new RouteTripSpec(81l + RID_ENDS_WITH_B, // 81B
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, VIA_P_GLEN_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, "") //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "125", "212", "451" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] {/* no stops */})) //
				.compileBothTripSort());
		map2.put(81L + RID_ENDS_WITH_N, new RouteTripSpec(81L + RID_ENDS_WITH_N, // 81N
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, APHS_NORTH_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, "") //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { //
						"790", // "2393", // Loyola Catholic S. S.
								"451", // ++
								"939", // ++
								"61", // "3331", // Bronte Rd + Richview Blvd
						})) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] {/* no stops */})) //
				.compileBothTripSort());
		map2.put(81L + RID_ENDS_WITH_S, new RouteTripSpec(81L + RID_ENDS_WITH_S, // 81S
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, "", //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, APHS_SOUTH_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { /* no stops */})) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { //
						"790", // "2393", // Nottinghill Gate at Loyola Catholic S.S.
								"879", // ++
								"1554", //
								"1557", // "3182", // Dorval Dr + North Service Rd
						})) //
				.compileBothTripSort());
		map2.put(82l, new RouteTripSpec(82l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, "", //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, LCSS_NORTH_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { /* no stops */})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "790", "182", "61" })) //
				.compileBothTripSort());
		map2.put(83l, new RouteTripSpec(83l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TAB_EAST_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TAB_WEST_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "509", "779", "1212" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "19", "417", "1307" })) //
				.compileBothTripSort());
		map2.put(84l, new RouteTripSpec(84l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TO_OTHS_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OTHS_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", // Oakville GO Station
								"1204", // !=
								"889", // Devon Rd at Oakville Trafalgar High School
								"243", // !=
								"1565" // Kingsway Dr + Wynten Way (west)
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1567", // Wynten Way + Kingsway Dr
								"1545", // !=
								"889" // Devon Rd at Oakville Trafalgar High School
						})) //
				.compileBothTripSort());
		map2.put(86l, new RouteTripSpec(86l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, FROM_GWHS_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, TO_GWHS_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1019", // "2442", // Westoak Trails Blvd + Garth Webb S.S
								"1564", // ++
								"1015", // "2229", // 4th Line + Upper Middle Rd West
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"315", // "2499", // 4th Line + Glen Valley Rd
								"141", // ++
								"1208" // "2443", // West Oak Trails Blvd at Garth Webb S. S.
						})) //
				.compileBothTripSort());
		map2.put(90l, new RouteTripSpec(90l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, WAL_MART_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, JOHN_RHODES_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "53", "1035", "545", "668", "185" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "185", "668", "545", "53", "1035", "877", "53" })) //
				.compileBothTripSort());
		map2.put(91L, new RouteTripSpec(91L, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAK_PLACE_RT, // Wallmart
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAK_SENIORS_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"586", // "1005", // Oakville Senior Citizens Residence
								"877", // <>
								"618", // <>
								"668", // <>
								"185", // "1004", // Walmart
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"185", // "1004", // Walmart
								"668", // <>
								"618", // <>
								"877", // <>
								"586", // "1005", // Oakville Senior Citizens Residence
						})) //
				.compileBothTripSort());
		map2.put(92l, new RouteTripSpec(92l, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, KNOX_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAK_PLACE_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { "668", "694", "668", "823" })) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { "823", "185", "823", "668" })) //
				.compileBothTripSort());
		map2.put(102l, new RouteTripSpec(102l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, CLARKSON_GO_RT, // WinPark-GO
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, WIN_PARK_RT) // WinPark-GO
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { "799", "41", "358" })) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { "358", "1282", "474", "1203", "799" })) //
				.compileBothTripSort());
		map2.put(120l, new RouteTripSpec(120l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, EAST_IND_RT, //
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", "1550" //
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1550", "1212" //
						})) //
				.compileBothTripSort());
		map2.put(121l, new RouteTripSpec(121l, //
				MDirectionType.EAST.intValue(), MTrip.HEADSIGN_TYPE_STRING, EAST_IND_RT, // TODO ?
				MDirectionType.WEST.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.EAST.intValue(), //
						Arrays.asList(new String[] { //
						"1212", // Oakville GO Station
								"558", //
								"1620" // Industry St + South Service Rd (east)
						})) //
				.addTripSort(MDirectionType.WEST.intValue(), //
						Arrays.asList(new String[] { //
						"1620", // Industry St + South Service Rd (east)
								"1624", //
								"1212" // Oakville GO Station
						})) //
				.compileBothTripSort());
		map2.put(190L, new RouteTripSpec(190L, //
				MDirectionType.NORTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, RO_EXPRESS_RT, //
				MDirectionType.SOUTH.intValue(), MTrip.HEADSIGN_TYPE_STRING, OAKVILLE_GO_RT) //
				.addTripSort(MDirectionType.NORTH.intValue(), //
						Arrays.asList(new String[] { //
						"1212", // "3172" Oakville GO Station
								"771", // ++
								"814", // "2317" Glenashton Dr + Taunton Rd
						})) //
				.addTripSort(MDirectionType.SOUTH.intValue(), //
						Arrays.asList(new String[] { //
						"330", // "2870" Glenashton Dr + Taunton Rd
								"1301", // ++
								"1026", // "2240" River Oaks Blvd East + Trafalgar Rd
						})) //
				.compileBothTripSort());
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
		if (isGoodEnoughAccepted()) {
			mTrip.setHeadsignString(cleanTripHeadsign(gTrip.getTripHeadsign()), gTrip.getDirectionId() == null ? 0 : gTrip.getDirectionId());
			return;
		}
		System.out.printf("\n%s: Unexpected trip %s.\n", mRoute.getId(), gTrip);
		System.exit(-1);
	}

	private static final Pattern ENDS_WITH_VIA = Pattern.compile("(via.*$)", Pattern.CASE_INSENSITIVE);

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		tripHeadsign = ENDS_WITH_VIA.matcher(tripHeadsign).replaceAll(StringUtils.EMPTY);
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
