package com.buldreinfo.jersey.jaxb.model;

import java.util.ArrayList;
import java.util.List;

public class ProfileStatistics {
	public class ProfileStatisticsTick {
		private final String areaName;
		private final boolean areaLockedAdmin;
		private final boolean areaLockedSuperadmin;
		private final String sectorName;
		private final boolean sectorLockedAdmin;
		private final boolean sectorLockedSuperadmin;
		private int num;
		private final int id;
		private final int idTickRepeat;
		private final String subType;
		private final int numPitches;
		private final int idProblem;
		private final boolean lockedAdmin;
		private final boolean lockedSuperadmin;
		private final String name;
		private final String comment;
		private final String date;
		private final String dateHr;
		private final double stars;
		private final boolean fa;
		private final String grade;
		private final int gradeNumber;
		private final double lat;
		private final double lng;
		
		public ProfileStatisticsTick(String areaName, boolean areaLockedAdmin, boolean areaLockedSuperadmin, String sectorName,
				boolean sectorLockedAdmin, boolean sectorLockedSuperadmin, int id, int idTickRepeat, String subType, int numPitches,
				int idProblem, boolean lockedAdmin, boolean lockedSuperadmin, String name, String comment, String date,
				String dateHr, double stars, boolean fa, String grade, int gradeNumber, double lat, double lng) {
			this.areaName = areaName;
			this.areaLockedAdmin = areaLockedAdmin;
			this.areaLockedSuperadmin = areaLockedSuperadmin;
			this.sectorName = sectorName;
			this.sectorLockedAdmin = sectorLockedAdmin;
			this.sectorLockedSuperadmin = sectorLockedSuperadmin;
			this.id = id;
			this.idTickRepeat = idTickRepeat;
			this.subType = subType;
			this.numPitches = numPitches;
			this.idProblem = idProblem;
			this.lockedAdmin = lockedAdmin;
			this.lockedSuperadmin = lockedSuperadmin;
			this.name = name;
			this.comment = comment;
			this.date = date;
			this.dateHr = dateHr;
			this.stars = stars;
			this.fa = fa;
			this.grade = grade;
			this.gradeNumber = gradeNumber;
			this.lat = lat;
			this.lng = lng;
		}
		
		public String getAreaName() {
			return areaName;
		}
		
		public String getComment() {
			return comment;
		}

		public String getDate() {
			return date;
		}

		public String getDateHr() {
			return dateHr;
		}

		public String getGrade() {
			return grade;
		}

		public int getGradeNumber() {
			return gradeNumber;
		}
		
		public int getId() {
			return id;
		}
		
		public int getIdProblem() {
			return idProblem;
		}

		public int getIdTickRepeat() {
			return idTickRepeat;
		}

		public double getLat() {
			return lat;
		}

		public double getLng() {
			return lng;
		}

		public String getName() {
			return name;
		}

		public int getNum() {
			return num;
		}

		public int getNumPitches() {
			return numPitches;
		}

		public String getSectorName() {
			return sectorName;
		}

		public double getStars() {
			return stars;
		}

		public String getSubType() {
			return subType;
		}

		public boolean isAreaLockedAdmin() {
			return areaLockedAdmin;
		}

		public boolean isAreaLockedSuperadmin() {
			return areaLockedSuperadmin;
		}

		public boolean isFa() {
			return fa;
		}

		public boolean isLockedAdmin() {
			return lockedAdmin;
		}

		public boolean isLockedSuperadmin() {
			return lockedSuperadmin;
		}

		public boolean isSectorLockedAdmin() {
			return sectorLockedAdmin;
		}

		public boolean isSectorLockedSuperadmin() {
			return sectorLockedSuperadmin;
		}

		public void setNum(int num) {
			this.num = num;
		}
	}
	private int numImagesCreated;
	private int numVideosCreated;
	private int numImageTags;
	private int numVideoTags;
	private final List<ProfileStatisticsTick> ticks = new ArrayList<>();
	
	public ProfileStatistics() {
	}
	
	public void addTick(String areaName, boolean areaLockedAdmin, boolean areaLockedSuperadmin, String sectorName, boolean sectorLockedAdmin, boolean sectorLockedSuperadmin, int id, int idTickRepeat, String subType, int numPitches, int idProblem, boolean lockedAdmin, boolean lockedSuperadmin, String name, String comment, String date, String dateHr, double stars, boolean fa, String grade, int gradeNumber, double lat, double lng) {
		ticks.add(new ProfileStatisticsTick(areaName, areaLockedAdmin, areaLockedSuperadmin, sectorName, sectorLockedAdmin, sectorLockedSuperadmin, id, idTickRepeat, subType, numPitches, idProblem, lockedAdmin, lockedSuperadmin, name, comment, date, dateHr, stars, fa, grade, gradeNumber, lat, lng));
	}

	public int getNumImagesCreated() {
		return numImagesCreated;
	}
	
	public int getNumImageTags() {
		return numImageTags;
	}
	
	public int getNumVideosCreated() {
		return numVideosCreated;
	}
	
	public int getNumVideoTags() {
		return numVideoTags;
	}
	
	public List<ProfileStatisticsTick> getTicks() {
		return ticks;
	}
	
	public void setNumImagesCreated(int numImagesCreated) {
		this.numImagesCreated = numImagesCreated;
	}

	public void setNumImageTags(int numImageTags) {
		this.numImageTags = numImageTags;
	}

	public void setNumVideosCreated(int numVideosCreated) {
		this.numVideosCreated = numVideosCreated;
	}

	public void setNumVideoTags(int numVideoTags) {
		this.numVideoTags = numVideoTags;
	}
}