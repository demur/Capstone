package com.udacity.demur.capstone.service;

import android.annotation.SuppressLint;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.geometry.Bounds;
import com.google.maps.android.geometry.Point;
import com.udacity.demur.capstone.MainActivity;
import com.udacity.demur.capstone.R;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.google.maps.android.SphericalUtil.computeDistanceBetween;

public class Utilities {
    /*
     * This function is the product of Levit at https://stackoverflow.com/a/27312494
     * suggested to use by Udacity to implement network connection check
     * */
    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bounds llb2b(LatLngBounds llb) {
        Point ne = MainActivity.PROJECTION.toPoint(llb.northeast);
        Point sw = MainActivity.PROJECTION.toPoint(llb.southwest);
        return new Bounds(Math.min(ne.x, sw.x), Math.max(ne.x, sw.x), Math.min(ne.y, sw.y), Math.max(ne.y, sw.y));
    }

    public static List<Date> getHoliDates() {
        List<Date> hoList = new ArrayList<>();
        Date today = new Date();
        Date tmpDate = null;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat holiSDF = new SimpleDateFormat("dd/MM/yyyy");
        // TODO make this list only default fallback in case there is no info on holidays in Firebase
        List<String> holidays = new ArrayList<>( // https://library.municode.com/nj/jersey_city/codes/code_of_ordinances?nodeId=CH332VETR_ARTIGEPR_S332-1DE
                // Date specific holidays falling on Saturday are observed on Friday; date specific holidays falling on Sunday are observed on Monday.
                Arrays.asList(
                        "10/08/2018", // Columbus Day, the second Monday in October.
                        "11/06/2018", // General Election Day, the Tuesday after the first Monday in November.
                        "11/11/2018", // Veterans' Day, November 11.
                        "11/12/2018", // Date specific holidays falling on Sunday are observed on Monday.
                        "11/22/2018", // Thanksgiving Day, the fourth Thursday in November.
                        "11/23/2018", // The Friday immediately after Thanksgiving.
                        "12/25/2018", // Christmas Day, December 25.
                        "01/01/2019", // New Year's Day, January 1.
                        "01/21/2019", // Martin Luther King's Birthday, the third Monday in January.
                        "02/12/2019", // Abraham Lincoln's Birthday, February 12.
                        "02/18/2019", // George Washington's Birthday, the third Monday in February.
                        "04/19/2019", // Good Friday, falling between March 21 and April 23.
                        "05/27/2019", // Memorial Day, the last Monday in May.
                        "07/04/2019", // Independence Day, July 4.
                        "09/02/2019", // Labor Day, the first Monday in September.
                        "10/14/2019", // Columbus Day, the second Monday in October.
                        "11/05/2019", // General Election Day, the Tuesday after the first Monday in November.
                        "11/11/2019", // Veterans' Day, November 11.
                        "11/28/2019", // Thanksgiving Day, the fourth Thursday in November.
                        "11/29/2019", // The Friday immediately after Thanksgiving.
                        "12/25/2019", // Christmas Day, December 25.
                        "01/01/2020", // New Year's Day, January 1.
                        "01/20/2020", // Martin Luther King's Birthday, the third Monday in January.
                        "02/12/2020", // Abraham Lincoln's Birthday, February 12.
                        "02/17/2020", // George Washington's Birthday, the third Monday in February.
                        "04/10/2020", // Good Friday, falling between March 21 and April 23.
                        "05/25/2020", // Memorial Day, the last Monday in May.
                        "07/03/2020", // Independence Day, July 4.
                        "09/07/2020", // Labor Day, the first Monday in September.
                        "10/12/2020", // Columbus Day, the second Monday in October.
                        "11/03/2020", // General Election Day, the Tuesday after the first Monday in November.
                        "11/11/2020", // Veterans' Day, November 11.
                        "11/26/2020", // Thanksgiving Day, the fourth Thursday in November.
                        "11/27/2020", // The Friday immediately after Thanksgiving.
                        "12/25/2020"  // Christmas Day, December 25.
                ));
        for (String dateStr : holidays) {
            try {
                tmpDate = holiSDF.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (today.compareTo(tmpDate) >= 0) {
                hoList.add(tmpDate);
            }
        }
        return hoList;
    }

    public static boolean isHoliday(Date target, List<Date> dateList) {
        for (Date date : dateList) {
            if (target.compareTo(date) == 0) {
                return true;
            }
        }
        return false;
    }

    private static int getDayOfWeek(Date target) {
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(target);
        switch (clndr.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                return 1;
            case Calendar.TUESDAY:
                return 2;
            case Calendar.WEDNESDAY:
                return 3;
            case Calendar.THURSDAY:
                return 4;
            case Calendar.FRIDAY:
                return 5;
            case Calendar.SATURDAY:
                return 6;
            case Calendar.SUNDAY:
            default:
                return 7;
        }
    }

    public static Date getDate(int year, int month, int day) {
        Calendar clndr = new GregorianCalendar();
        clndr.set(Calendar.YEAR, year);
        clndr.set(Calendar.MONTH, month);
        clndr.set(Calendar.DAY_OF_MONTH, day);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        return clndr.getTime();
    }

    public static Date getDate(int year, int month, int day, int hour, int minute) {
        Calendar clndr = new GregorianCalendar();
        clndr.set(Calendar.YEAR, year);
        clndr.set(Calendar.MONTH, month);
        clndr.set(Calendar.DAY_OF_MONTH, day);
        clndr.set(Calendar.HOUR_OF_DAY, hour);
        clndr.set(Calendar.MINUTE, minute);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        return clndr.getTime();
    }

    private static Date roundDate(Date inputDate) {
        Calendar clndr = new GregorianCalendar();
        clndr.setTime(inputDate);
        clndr.set(Calendar.HOUR_OF_DAY, 0);
        clndr.set(Calendar.MINUTE, 0);
        clndr.set(Calendar.SECOND, 0);
        clndr.set(Calendar.MILLISECOND, 0);
        return clndr.getTime();
    }

    private static final int fullWeek = 24 * 7;

    public static int getAvailHours(String pattern, Calendar calendar) {
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int availHours = 0;

        int posInPattern = (getDayOfWeek(calendar.getTime()) - 1) * 24 + hourOfDay;
        int nextLimit = pattern.indexOf("1", posInPattern);
        if (nextLimit == -1) {
            int firstLimit = pattern.indexOf("1");
            availHours = fullWeek - posInPattern + firstLimit;
            calendar.add(Calendar.HOUR, availHours);
            if (MainActivity.mHolidays.contains(roundDate(((Calendar) calendar.clone()).getTime()))) {
                // TODO: When should holiday end? At midnight or after the whole rule block?
                /*int firstLimitEnd = pattern.indexOf("0",firstLimit);
                calendar.add(Calendar.HOUR, (firstLimitEnd - firstLimit));
                availHours += (firstLimitEnd - firstLimit) + getAvailHours(pattern, (Calendar) calendar.clone());*/
                // ** For now using midnight as an edge
                int remainingHours = 24 - calendar.get(Calendar.HOUR_OF_DAY);
                calendar.add(Calendar.HOUR, remainingHours);
                availHours += remainingHours;
                int additionalHours = getAvailHours(pattern, (Calendar) calendar.clone());
                if (additionalHours > 0) {
                    availHours += additionalHours;
                }
            }
        } else if (nextLimit == posInPattern) {
            if (MainActivity.mHolidays.contains(roundDate(((Calendar) calendar.clone()).getTime()))) {
                // ** For now using midnight as an edge
                int remainingHours = 24 - hourOfDay;
                calendar.add(Calendar.HOUR, remainingHours);
                availHours = remainingHours;
                int additionalHours = getAvailHours(pattern, (Calendar) calendar.clone());
                if (additionalHours > 0) {
                    availHours += additionalHours;
                }
            } else {
                int nextAvailPos = pattern.indexOf("0", posInPattern);
                if (nextAvailPos == -1) {
                    nextAvailPos = pattern.indexOf("0") + fullWeek;
                }
                // ** For now using midnight as an edge
                int remainingHours = 24 - hourOfDay;

                int nextAvail = nextAvailPos - posInPattern;

                if (nextAvail > remainingHours) {
                    availHours -= remainingHours;
                    nextAvail -= remainingHours;
                    calendar.add(Calendar.HOUR, remainingHours);
                    while (nextAvail > 0) {
                        if (MainActivity.mHolidays.contains(roundDate(((Calendar) calendar.clone()).getTime()))) {
                            break;
                        }
                        if (nextAvail > 24) {
                            calendar.add(Calendar.HOUR, 24);
                            availHours -= 24;
                            nextAvail -= 24;
                        } else {
                            availHours -= nextAvail;
                            break;
                        }
                    }
                } else {
                    availHours = -nextAvail;
                }
            }
        } else {
            availHours = nextLimit - posInPattern;
            calendar.add(Calendar.HOUR, availHours);
            if (MainActivity.mHolidays.contains(roundDate(((Calendar) calendar.clone()).getTime()))) {
                // ** For now using midnight as an edge
                int remainingHours = 24 - calendar.get(Calendar.HOUR_OF_DAY);
                calendar.add(Calendar.HOUR, remainingHours);
                availHours += remainingHours;
                int additionalHours = getAvailHours(pattern, (Calendar) calendar.clone());
                if (additionalHours > 0) {
                    availHours += additionalHours;
                }
            }
        }
        return availHours;
    }

    /**
     * Based on `distanceToLine` method from
     * https://github.com/googlemaps/android-maps-utils/blob/master/library/src/com/google/maps/android/PolyUtil.java
     */
    private static int findShortestDistance(final LatLng p, final LatLng start, final LatLng end) {
        double distance;
        LatLng resultPoint;
        if (start.equals(end)) {
            resultPoint = end;
        } else {
            final double s0lat = Math.toRadians(p.latitude);
            final double s0lng = Math.toRadians(p.longitude);
            final double s1lat = Math.toRadians(start.latitude);
            final double s1lng = Math.toRadians(start.longitude);
            final double s2lat = Math.toRadians(end.latitude);
            final double s2lng = Math.toRadians(end.longitude);

            final double s2s1lat = s2lat - s1lat;
            final double s2s1lng = s2lng - s1lng;
            final double u = ((s0lat - s1lat) * s2s1lat + (s0lng - s1lng) * s2s1lng) / (s2s1lat * s2s1lat + s2s1lng * s2s1lng);

            if (u <= 0) {
                resultPoint = start;
            } else if (u >= 1) {
                resultPoint = end;
            } else {
                resultPoint = new LatLng(start.latitude + (u * (end.latitude - start.latitude)), start.longitude + (u * (end.longitude - start.longitude)));
            }

        }
        distance = computeDistanceBetween(p, resultPoint);

        if (MainActivity.mMinDistMarker2Poly == distance) {
            MainActivity.mNearestPolyPointsList.add(resultPoint);
            return 1;// Received distance is the same as the shortest for the moment
        } else if (MainActivity.mMinDistMarker2Poly == -1 || distance < MainActivity.mMinDistMarker2Poly) {
            MainActivity.mMinDistMarker2Poly = distance;
            MainActivity.mNearestPolyPointsList = new ArrayList<>();
            MainActivity.mNearestPolyPointsList.add(resultPoint);
            return 0;// Received distance is the shortest
        }
        return -1;// Received distance is not the shortest
    }

    public static int findShortestDistance(final LatLng p, final List<LatLng> target) {
        int result = -1;
        if (null != p && null != target) {
            int targetSize = target.size();
            if (targetSize == 1) {
                result = findShortestDistance(p, target.get(0), target.get(0));
            } else if (targetSize > 1) {
                targetSize -= 1;
                for (int i = 0; i < targetSize; i++) {
                    int tmpResult = findShortestDistance(p, target.get(i), target.get(i + 1));
                    if (result != 0) {
                        if (tmpResult == 0) {
                            result = 0;
                        } else if (tmpResult == 1 && result == -1) {
                            result = 1;
                        }
                    }
                }
            }
        }
        return result;
    }

    public static String formatMarkerTitle(Context context, long timeInMillis) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(timeInMillis);
        return formatMarkerTitle(context, cal);
    }

    public static String formatMarkerTitle(Context context, Calendar calEnd) {
        String result = periodInBetween(context, calEnd, null);
        result += "\n" + context.getResources().getString(R.string.period_till, MainActivity.markerSDF.format(calEnd.getTime()));
        return result;
    }

    public static String periodInBetween(Context context, Calendar calEnd, Calendar calStart) {
        if (null == calEnd || null == context) {
            return "";
        }
        if (null == calStart) {
            calStart = new GregorianCalendar();
        }
        long seconds = (calEnd.getTimeInMillis() - calStart.getTimeInMillis()) / 1000;
        String period = "";
        if (seconds >= 0) {
            int minutes = (int) (seconds / 60);
            int hours = (int) (seconds / (60 * 60));
            int days = (int) (seconds / (24 * 60 * 60));
            if (days > 0) {
                hours -= days * 24;
                period += context.getResources().getQuantityString(R.plurals.period_days, days, days) + (hours > 0 ? ", " : "");
            }
            if (hours > 0) {
                period += context.getResources().getQuantityString(R.plurals.period_hours, hours, hours);
            } else if (days == 0) {
                period += context.getResources().getQuantityString(R.plurals.period_minutes, minutes, minutes);
            }
        }
        return period;
    }

    public static String periodInBetween(Context context, Calendar calEnd) {
        return periodInBetween(context, calEnd, null);
    }

    public static String formatMarkerSnippet(Context context, Calendar calEnd, Calendar calStart) {
        return (null != calStart ? context.getResources().getString(R.string.period_from, MainActivity.markerSDF.format(calStart.getTime())) + " " : "")
                + context.getResources().getString(R.string.period_till, MainActivity.markerSDF.format(calEnd.getTime()));
    }

    public static String formatMarkerPlaceHolderTitle(Context context, Calendar calEnd, boolean allowed) {
        return formatMarkerPlaceHolderTitle(context, calEnd, null, allowed);
    }

    public static String formatMarkerPlaceHolderTitle(Context context, Calendar calEnd, Calendar calStart, Boolean allowed) {
        if (null == calEnd || null == context) {
            return "";
        }
        if ((null != calStart && calEnd.getTimeInMillis() < calStart.getTimeInMillis()) || null == allowed) {
            return context.getResources().getString(R.string.period_till, MainActivity.markerSDF.format(calEnd.getTime()));
        }
        return (allowed
                ? context.getResources().getString(R.string.parking_allowed_for, periodInBetween(context, calEnd, calStart))
                : context.getResources().getString(R.string.parking_prohibited_for, periodInBetween(context, calEnd, calStart))) + "\n"
                + (null != calStart ? context.getResources().getString(R.string.period_from, MainActivity.markerSDF.format(calStart.getTime())) + "\n" : "")
                + context.getResources().getString(R.string.period_till, MainActivity.markerSDF.format(calEnd.getTime()));
    }

    public static LatLngBounds str2llb(String strBounds) {
        String[] sBndCoords = strBounds.replaceAll("\\(|\\)", "").split(",");
        if (sBndCoords.length == 4) {
            return new LatLngBounds(
                    new LatLng(Double.parseDouble(sBndCoords[0]), Double.parseDouble(sBndCoords[1])),
                    new LatLng(Double.parseDouble(sBndCoords[2]), Double.parseDouble(sBndCoords[3]))
            );
        } else {
            return null;
        }
    }

    public static List<LatLng> str2lllist(String strCoords) {
        String[] sPntCoords = strCoords.replaceAll("\\(|\\)", "").split(",");
        List<LatLng> sPoints = new ArrayList<>();
        int cSize = sPntCoords.length;
        if (cSize > 0 && (cSize % 2) == 0) {
            for (int j = 0; j < cSize; j += 2) {
                sPoints.add(new LatLng(
                        Double.parseDouble(sPntCoords[j]),
                        Double.parseDouble(sPntCoords[j + 1])));
            }
        }
        return sPoints;
    }
}