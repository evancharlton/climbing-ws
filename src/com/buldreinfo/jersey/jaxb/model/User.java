package com.buldreinfo.jersey.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import com.buldreinfo.jersey.jaxb.metadata.beans.IMetadata;

public class User implements IMetadata {
	public class Tick {
		private final int num;
		private final int id;
		private final int idProblem;
		private final int visibility;
		private final String areaName;
		private final String sectorName;
		private final String name;
		private final String comment;
		private final String date;
		private final String dateHr;
		private final double stars;
		private final boolean fa;
		private final String grade;
		private final int gradeNumber;

		public Tick(int num, int id, int idProblem, int visibility, String areaName, String sectorName, String name, String comment, String date, String dateHr, double stars, boolean fa, String grade, int gradeNumber) {
			this.num = num;
			this.id = id;
			this.idProblem = idProblem;
			this.visibility = visibility;
			this.areaName = areaName;
			this.sectorName = sectorName;
			this.name = name;
			this.comment = comment;
			this.date = date;
			this.dateHr = dateHr;
			this.stars = stars;
			this.fa = fa;
			this.grade = grade;
			this.gradeNumber = gradeNumber;
		}
		
		public int getNum() {
			return num;
		}
		
		public String getAreaName() {
			return areaName;
		}
		
		public String getSectorName() {
			return sectorName;
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

		public String getName() {
			return name;
		}

		public double getStars() {
			return stars;
		}

		public int getVisibility() {
			return visibility;
		}

		public boolean isFa() {
			return fa;
		}

		@Override
		public String toString() {
			return "Tick [num=" + num + ", id=" + id + ", idProblem=" + idProblem + ", visibility=" + visibility
					+ ", areaName=" + areaName + ", sectorName=" + sectorName + ", name=" + name + ", comment="
					+ comment + ", date=" + date + ", dateHr=" + dateHr + ", stars=" + stars + ", fa=" + fa + ", grade="
					+ grade + ", gradeNumber=" + gradeNumber + "]";
		}
	}
	private final boolean readOnly;
	private final int id;
	private final String picture;
	private final String name;
	private final int numImagesCreated;
	private final int numVideosCreated;
	private final int numImageTags;
	private final int numVideoTags;
	private final List<Tick> ticks = new ArrayList<>();
	private Metadata metadata;
	
	public User(boolean readOnly, int id, String picture, String name, int numImagesCreated, int numVideosCreated, int numImageTags, int numVideoTags) {
		this.readOnly = readOnly;
		this.id = id;
		this.picture = picture;
		this.name = name;
		this.numImagesCreated = numImagesCreated;
		this.numVideosCreated = numVideosCreated;
		this.numImageTags = numImageTags;
		this.numVideoTags = numVideoTags;
	}
	
	public void addTick(int id, int idProblem, int visibility, String areaName, String sectorName, String name, String comment, String date, String dateHr, double stars, boolean fa, String grade, int gradeNumber) {
		ticks.add(new Tick(ticks.size(), id, idProblem, visibility, areaName, sectorName, name, comment, date, dateHr, stars, fa, grade, gradeNumber));
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public Metadata getMetadata() {
		return metadata;
	}
	
	public String getName() {
		return name;
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
	
	public String getPicture() {
		return picture;
	}

	public List<Tick> getTicks() {
		return ticks;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "User [readOnly=" + readOnly + ", id=" + id + ", picture=" + picture + ", name=" + name
				+ ", numImagesCreated=" + numImagesCreated + ", numVideosCreated=" + numVideosCreated
				+ ", numImageTags=" + numImageTags + ", numVideoTags=" + numVideoTags + ", ticks=" + ticks
				+ ", metadata=" + metadata + "]";
	}
}