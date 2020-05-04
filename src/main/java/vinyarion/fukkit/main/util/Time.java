package vinyarion.fukkit.main.util;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

import vinyarion.fukkit.main.data.FSeenData;

public class Time {
	
	public static final int min = 60;
	public static final int hour = min * 60;
	public static final int day = hour * 24;
	public static final int week = day * 7;
	public static final int month = week * 4;
	public static final int year = day * 365;
	
	public static long hhmmss(String hhmmss) {
		String[] sa = hhmmss.split(":");
		String hh = sa.length == 3 ? sa[0] : "0";
		String mm = sa.length == 3 ? sa[1] : sa.length == 2 ? sa[0] : "0";
		String ss = sa.length == 3 ? sa[2] : sa.length == 2 ? sa[1] : sa.length == 3 ? sa[0] : "0";
		long h;
		long m;
		long s;
		try {
			h = Long.parseLong(hh);
		} catch(Exception e) {
			h = 0;
		}
		try {
			m = Long.parseLong(mm);
		} catch(Exception e) {
			m = 0;
		}
		try {
			s = Long.parseLong(ss);
		} catch(Exception e) {
			s = 0;
		}
		return h * hour + m * min + s;
	}
	
	public static boolean ishhmmss(String hhmmss) {
		try {
			String[] sa = hhmmss.split(":");
			String hh = sa.length == 3 ? sa[0] : "0";
			String mm = sa.length == 3 ? sa[1] : sa.length == 2 ? sa[0] : "0";
			String ss = sa.length == 3 ? sa[2] : sa.length == 2 ? sa[1] : sa.length == 3 ? sa[0] : "0";
			Long.parseLong(hh);
			Long.parseLong(mm);
			Long.parseLong(ss);
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	public static String instantNow() {
		return Instant.now().truncatedTo(ChronoUnit.SECONDS).toString();
	}
	
	public static boolean hasPassed(String time, String hhmmss) {
		return since(time) - hhmmss(hhmmss) >= 0;
	}
	
	public static String differenceReadable(String time, String hhmmss) {
		long diff = Time.since(time) - Time.hhmmss(hhmmss);
		return Time.readable(diff < 0 ? -diff : diff);
	}
	
	public static long since(String time) {
		Instant now = Instant.now();
		Instant parsed = Instant.parse(time);
		Instant diff = now;
		diff = diff.minusSeconds(parsed.getLong(ChronoField.INSTANT_SECONDS));
		diff = diff.truncatedTo(ChronoUnit.SECONDS);
		return diff.getEpochSecond();
	}
	
	public static long sinceMillis(String time) {
		Instant now = Instant.now();
		Instant parsed = Instant.parse(time);
		Instant diff = now;
		diff = diff.minusMillis(parsed.toEpochMilli());
		diff = diff.truncatedTo(ChronoUnit.MILLIS);
		return diff.toEpochMilli();
	}
	
	public static String readable(long seconds) {
		String ret = "";
		if(seconds >= year) {
			long l = (seconds / year);
			ret = ret + l + (l == 1 ? " year, " : " years, ");
			seconds = seconds % year;
		}
		if(seconds >= month) {
			long l = (seconds / month);
			ret = ret + l + (l == 1 ? " month, " : " months, ");
			seconds = seconds % month;
		}
		if(seconds >= week) {
			long l = (seconds / week);
			ret = ret + l + (l == 1 ? " week, " : " weeks, ");
			seconds = seconds % week;
		}
		if(seconds >= day) {
			long l = (seconds / day);
			ret = ret + l + (l == 1 ? " day, " : " days, ");
			seconds = seconds % day;
		}
		if(seconds >= hour) {
			long l = (seconds / hour);
			ret = ret + l + (l == 1 ? " hour, " : " hours, ");
			seconds = seconds % hour;
		}
		if(seconds >= min) {
			long l = (seconds / min);
			ret = ret + l + (l == 1 ? " minute, " : " minutes, ");
			seconds = seconds % min;
		}
		ret = ret + seconds + (seconds == 1 ? " second" : " seconds");
		return ret;
	}
	
	public static String readableTicks(long ticks) {
		return readable(ticks/20)+", "+ticks%20+" ticks";
	}
	
	public static String toClockTime(int seconds) {
		String ret = seconds < 0 ? "-" : "";
		seconds = Math.abs(seconds);
		boolean hours;
		if(hours = (seconds >= 3600)) {
			ret += String.valueOf(seconds / 3600);
			seconds %= 3600;
		}
		if(seconds >= 60) {
			String m = String.valueOf(seconds / 60);
			ret += (hours?":":"") + ((m.length() == 1 && hours) ? ("=0"+m) : m);
			seconds %= 60;
		} else {
			ret += (hours?":00":"0");
		}
		String s = String.valueOf(seconds);
		ret += s.length() == 1 ? (":0"+s) : (":"+s);
		return ret;
	}
	
}
