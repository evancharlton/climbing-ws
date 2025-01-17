package com.buldreinfo.jersey.jaxb.helpers;

import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableBiMap;

public class GradeHelper {
	public static ImmutableBiMap<Integer, String> getGrades(Setup setup) {
		Map<Integer, String> map = new LinkedHashMap<>();
		if (setup.getGradeSystem().equals(Setup.GRADE_SYSTEM.BOULDER)) {
			map.put(27, "8C");
			map.put(26, "8B+");
			map.put(25, "8B");
			map.put(24, "8A+");
			map.put(23, "8A");
			map.put(22, "7C+");
			map.put(21, "7C");
			map.put(20, "7B+");
			map.put(19, "7B");
			map.put(18, "7A+");
			map.put(17, "7A");
			map.put(16, "6C+");
			map.put(15, "6C");
			map.put(14, "6B+");
			map.put(13, "6B");
			map.put(12, "6A+");
			map.put(11, "6A");
			map.put(10, "5+");
			map.put(9, "5");
			map.put(8, "4+");
			map.put(7, "4");
			map.put(6, "3");
			map.put(0, "n/a");
		}
		else if (setup.getGradeSystem().equals(Setup.GRADE_SYSTEM.CLIMBING)) {
			map.put(64, "9c");
			map.put(63, "9b+/9c");
			map.put(62, "9b+");
			map.put(61, "9b/9b+");
			map.put(60, "9b");
			map.put(59, "9a+/9b");
			map.put(58, "9a+");
			map.put(57, "9a/9a+");
			map.put(56, "10 (9a)");
			map.put(55, "10-/10 (8c+/9a)");
			map.put(54, "10- (8c+)");
			map.put(53, "9+ (8c)");
			map.put(52, "9/9+ (8b+)");
			map.put(51, "9 (8b)");
			map.put(50, "9-/9 (8a+)");
			map.put(49, "9- (8a)");
			map.put(48, "8+/9- (7c+/8a)");
			map.put(47, "8+ (7c+)");
			map.put(46, "8/8+ (7c)");
			map.put(45, "8 (7b+)");
			map.put(44, "8-/8 (7b)");
			map.put(43, "8- (7b)");
			map.put(42, "7+/8- (7a+)");
			map.put(41, "7+ (7a)");
			map.put(40, "7/7+ (6c+)");
			map.put(39, "7 (6c)");
			map.put(38, "7-/7 (6b+)");
			map.put(37, "7- (6b+)");
			map.put(36, "6+/7- (6b)");
			map.put(35, "6+ (6b)");
			map.put(34, "6/6+ (6a+)");
			map.put(33, "6 (6a+)");
			map.put(32, "6-/6 (6a)");
			map.put(31, "6- (6a)");
			map.put(30, "5+/6- (5c)");
			map.put(29, "5+ (5c)");
			map.put(28, "5 (5b)");
			map.put(27, "5- (5a)");
			map.put(26, "4+ (4c)");
			map.put(25, "4 (4b)");
			map.put(24, "4- (4a)");
			map.put(23, "3+");
			map.put(22, "3");
			map.put(21, "3-");
			map.put(0, "n/a");
		}
		else if (setup.getGradeSystem().equals(Setup.GRADE_SYSTEM.ICE)) {
			map.put(109, "WI6+");
			map.put(108, "WI6");
			map.put(107, "WI5+");
			map.put(106, "WI5");
			map.put(105, "WI4+");
			map.put(104, "WI4");
			map.put(103, "WI3+");
			map.put(102, "WI3");
			map.put(101, "WI2+");
			map.put(100, "WI2");
			map.put(0, "n/a");
		}
		else {
			throw new RuntimeException("Invalid gradeSystem: " + setup.getGradeSystem());
		}
		return ImmutableBiMap.copyOf(map);
	}

	public static String intToString(Setup setup, int grade) {
		ImmutableBiMap<Integer, String> grades = getGrades(setup);
		String res = grades.get(grade);
		int i = grade;
		while (res == null && i < Collections.max(grades.keySet())) {
			res = grades.get(++i);
		}
		return Preconditions.checkNotNull(res, "Invalid grade=" + grade + " (isBouldering=" + setup.isBouldering() + ")");
	}

	public static String intToStringBase(Setup setup, int grade) {
		String res = intToString(setup, grade);
		int ix = res.indexOf("(");
		if (ix > 0) {
			res = res.substring(ix+1, ix+3);
		}
		else {
			res = res.replaceAll("\\-", "").replaceAll("\\+", "");
			ix = res.indexOf("/");
			if (ix > 0) {
				res = res.substring(0, ix);
			}
		}
		if (res.startsWith("3")) {
			return "3";
		}
		else if (res.startsWith("4")) {
			return "4";
		}
		else if (res.startsWith("5")) {
			return "5";
		}
		return res;
	}

	public static int stringToInt(Setup setup, String grade) throws SQLException {
		Preconditions.checkNotNull(grade, "grade is null");
		ImmutableBiMap<String, Integer> grades = getGrades(setup).inverse();
		try {
			return grades.get(grade);
		} catch (NullPointerException e) {
			// Check for first part...
			for (String key : grades.keySet()) {
				if (key.contains(" ") && key.substring(0, key.indexOf(" ")).equals(grade)) {
					return grades.get(key);
				}
			}
			// Check for last part...
			for (String key : grades.keySet()) {
				if (key.endsWith("(" + grade + ")")) {
					return grades.get(key);
				}
			}
			throw new RuntimeException("Could not find grade: " + grade + " on idRegion=" + setup.getIdRegion());
		}
	}
}