package org.mtransit.parser.ca_oakville_transit_bus;

import java.util.HashSet;
import java.util.Locale;
import java.util.regex.Pattern;

import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.Utils;
import org.mtransit.parser.gtfs.data.GCalendar;
import org.mtransit.parser.gtfs.data.GCalendarDate;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirectionType;
import org.mtransit.parser.mt.data.MRoute;
import org.mtransit.parser.mt.data.MSpec;
import org.mtransit.parser.mt.data.MTrip;

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
		System.out.printf("Generating Oakville Transit bus data...\n");
		long start = System.currentTimeMillis();
		this.serviceIds = extractUsefulServiceIds(args, this);
		super.start(args);
		System.out.printf("Generating Oakville Transit bus data... DONE in %s.\n", Utils.getPrettyDuration(System.currentTimeMillis() - start));
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
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public String getRouteLongName(GRoute gRoute) {
		return MSpec.cleanLabel(gRoute.route_long_name.toLowerCase(Locale.ENGLISH));
	}

	private static final String AGENCY_COLOR = "DCA122"; // GOLD (AGENCY LOGO SVG WIKIPEDIA)

	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	private static final String ROUTE_5A_RTS = "5A";

	@Override
	public long getRouteId(GRoute gRoute) {
		if (ROUTE_5A_RTS.equalsIgnoreCase(gRoute.route_short_name)) {
			return 1005l;
		}
		return super.getRouteId(gRoute);
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

	@Override
	public String getRouteColor(GRoute gRoute) {
		if (ROUTE_5A_RTS.equalsIgnoreCase(gRoute.route_short_name)) {
			return COLOR_AD4D45;
		}
		int routeId = Integer.parseInt(gRoute.route_id);
		switch (routeId) {
		// @formatter:off
		case 1: return COLOR_DE242C;
		case 2: return COLOR_7F1C7D;
		case 3: return COLOR_4D368A;
		case 4: return COLOR_8A5032;
		case 5: return COLOR_AD4D45;
		case 6: return COLOR_F05B72;
		case 10: return COLOR_1C4E9D;
		case 11: return COLOR_201B18;
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
		case 102: return COLOR_221E1F;
		case 120: return COLOR_DF241A;
		case 190: return COLOR_DB214C;
		// @formatter:on
		default:
			System.out.println("getRouteColor() > Unexpected route ID color '" + routeId + "' (" + gRoute + ")");
			System.exit(-1);
			return null;
		}
	}

	private static final String AM_HEADSIGN = "AM";
	private static final String PM_HEADSIGN = "PM";
	private static final String LOOP_HEADSIGN = "Loop";

	private static final String AM = " - AM".toLowerCase(Locale.ENGLISH);
	private static final String PM = " - PM".toLowerCase(Locale.ENGLISH);
	private static final String AM_PEAK = " - PEAK-AM".toLowerCase(Locale.ENGLISH);
	private static final String PM_PEAK = " - PEAK-PM".toLowerCase(Locale.ENGLISH);
	private static final String IN = " - IN".toLowerCase(Locale.ENGLISH);
	private static final String OUT = " - OUT".toLowerCase(Locale.ENGLISH);
	private static final String LOOP = " - LOOP".toLowerCase(Locale.ENGLISH);

	private static final String BRONTE_GO = "Bronte GO";
	private static final String CLARKSON_GO = "Clarkson GO";
	private static final String OAKVILLE_GO = "Oakville GO";
	private static final String BURLOAK_DR = "Burloak Dr";
	private static final String SOUTH_CENTER = "South Center";
	private static final String UPTOWN_CORE = "Uptown Core";
	private static final String MAPLE_GROVE = "Maple Grove";
	private static final String PINE_GLEN = "Pine Glen";
	private static final String SOUTH_COMMON = "South Common";
	private static final String RIO_CAN_CENTRE = "RioCan Centre";
	private static final String GLEN_ABBEY = "Glen Abbey";

	@Override
	public void setTripHeadsign(MRoute route, MTrip mTrip, GTrip gTrip) {
		String shapeIdLC = gTrip.shape_id.toLowerCase(Locale.ENGLISH);
		if (route.id == 1l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(UPTOWN_CORE, 1);
				return;
			}
		} else if (route.id == 2l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignDirection(MDirectionType.WEST);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignDirection(MDirectionType.EAST);
				return;
			}
		} else if (route.id == 3l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString("South Centre", 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString("Dundas & Proudfoot", 1);
				return;
			}
		} else if (route.id == 4l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignDirection(MDirectionType.WEST);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignDirection(MDirectionType.EAST);
				return;
			}
		} else if (route.id == 5l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(UPTOWN_CORE, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString("Palermo", 1);
				return;
			}
		} else if (route.id == 6l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignDirection(MDirectionType.WEST);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignDirection(MDirectionType.EAST);
				return;
			}
		} else if (route.id == 11l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(MAPLE_GROVE, 1);
				return;
			}
		} else if (route.id == 13l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(BRONTE_GO, 1);
				return;
			}
		} else if (route.id == 14l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(BURLOAK_DR, 1);
				return;
			}
		} else if (route.id == 15l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(SOUTH_CENTER, 1);
				return;
			}
		} else if (route.id == 18l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(GLEN_ABBEY, 1);
				return;
			}
		} else if (route.id == 19l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(UPTOWN_CORE, 1);
				return;
			}
		} else if (route.id == 20l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(UPTOWN_CORE, 1);
				return;
			}
		} else if (route.id == 21l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(MAPLE_GROVE, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(CLARKSON_GO, 1);
				return;
			}
		} else if (route.id == 22l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(BRONTE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(PINE_GLEN, 1);
				return;
			}
		} else if (route.id == 24l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(SOUTH_COMMON, 1);
				return;
			}
		} else if (route.id == 28l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(OAKVILLE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(GLEN_ABBEY, 1);
				return;
			}
		} else if (route.id == 32l) {
			if (shapeIdLC.contains(IN)) {
				mTrip.setHeadsignString(BRONTE_GO, 0);
				return;
			} else if (shapeIdLC.contains(OUT)) {
				mTrip.setHeadsignString(RIO_CAN_CENTRE, 1);
				return;
			}
		}
		if (shapeIdLC.contains(LOOP)) {
			mTrip.setHeadsignString(LOOP_HEADSIGN, 0);
			return;
		}
		if (shapeIdLC.contains(AM)) {
			mTrip.setHeadsignString(AM_HEADSIGN, 0);
			return;
		} else if (shapeIdLC.contains(PM)) {
			mTrip.setHeadsignString(PM_HEADSIGN, 1);
			return;
		}
		if (shapeIdLC.contains(AM_PEAK)) {
			mTrip.setHeadsignString(AM_HEADSIGN, 0);
			return;
		} else if (shapeIdLC.contains(PM_PEAK)) {
			mTrip.setHeadsignString(PM_HEADSIGN, 1);
			return;
		}
		System.out.println("Unexpected trip " + gTrip);
		System.exit(-1);
	}

	@Override
	public String cleanTripHeadsign(String tripHeadsign) {
		return MSpec.cleanLabel(tripHeadsign);
	}

	private static final Pattern FIRST = Pattern.compile("(first )", Pattern.CASE_INSENSITIVE);
	private static final String FIRST_REPLACEMENT = "1st ";
	private static final Pattern SECOND = Pattern.compile("(second )", Pattern.CASE_INSENSITIVE);
	private static final String SECOND_REPLACEMENT = "2nd ";
	private static final Pattern THIRD = Pattern.compile("(third )", Pattern.CASE_INSENSITIVE);
	private static final String THIRD_REPLACEMENT = "3rd ";
	private static final Pattern FOURTH = Pattern.compile("(fourth )", Pattern.CASE_INSENSITIVE);
	private static final String FOURTH_REPLACEMENT = "4th";
	private static final Pattern FIFTH = Pattern.compile("(fifth )", Pattern.CASE_INSENSITIVE);
	private static final String FIFTH_REPLACEMENT = "5th ";
	private static final Pattern SIXTH = Pattern.compile("(sixth )", Pattern.CASE_INSENSITIVE);
	private static final String SIXTH_REPLACEMENT = "6th ";
	private static final Pattern SEVENTH = Pattern.compile("(seventh )", Pattern.CASE_INSENSITIVE);
	private static final String SEVENTH_REPLACEMENT = "7th ";
	private static final Pattern EIGHTH = Pattern.compile("(eighth )", Pattern.CASE_INSENSITIVE);
	private static final String EIGHTH_REPLACEMENT = "8th ";
	private static final Pattern NINTH = Pattern.compile("(ninth )", Pattern.CASE_INSENSITIVE);
	private static final String NINTH_REPLACEMENT = "9th ";

	private static final Pattern AND = Pattern.compile("( and )", Pattern.CASE_INSENSITIVE);
	private static final String AND_REPLACEMENT = " & ";

	private static final Pattern AT = Pattern.compile("( at )", Pattern.CASE_INSENSITIVE);
	private static final String AT_REPLACEMENT = " / ";

	@Override
	public String cleanStopName(String gStopName) {
		gStopName = AT.matcher(gStopName).replaceAll(AT_REPLACEMENT);
		gStopName = AND.matcher(gStopName).replaceAll(AND_REPLACEMENT);
		gStopName = FIRST.matcher(gStopName).replaceAll(FIRST_REPLACEMENT);
		gStopName = SECOND.matcher(gStopName).replaceAll(SECOND_REPLACEMENT);
		gStopName = THIRD.matcher(gStopName).replaceAll(THIRD_REPLACEMENT);
		gStopName = FOURTH.matcher(gStopName).replaceAll(FOURTH_REPLACEMENT);
		gStopName = FIFTH.matcher(gStopName).replaceAll(FIFTH_REPLACEMENT);
		gStopName = SIXTH.matcher(gStopName).replaceAll(SIXTH_REPLACEMENT);
		gStopName = SEVENTH.matcher(gStopName).replaceAll(SEVENTH_REPLACEMENT);
		gStopName = EIGHTH.matcher(gStopName).replaceAll(EIGHTH_REPLACEMENT);
		gStopName = NINTH.matcher(gStopName).replaceAll(NINTH_REPLACEMENT);
		return MSpec.cleanLabel(gStopName);
	}
}
