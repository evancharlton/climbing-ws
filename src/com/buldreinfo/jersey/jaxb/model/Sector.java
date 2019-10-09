package com.buldreinfo.jersey.jaxb.model;

import java.util.ArrayList;
import java.util.List;

import com.buldreinfo.jersey.jaxb.metadata.beans.IMetadata;

public class Sector implements IMetadata {
	public class Problem {
		private final int id;
		private final int visibility;
		private final int nr;
		private final String name;
		private final String comment;
		private final int gradeNumber;
		private final String grade;
		private final String fa;
		private final int numPitches;
		private final boolean hasImages;
		private final boolean hasMovies;
		private final double lat;
		private final double lng;
		private final int numTicks;
		private final double stars;
		private final boolean ticked;
		private final Type t;
		private final boolean danger;
		
		public Problem(int id, int visibility, int nr, String name, String comment, int gradeNumber, String grade, String fa,
				int numPitches,
				boolean hasImages, boolean hasMovies, double lat, double lng, int numTicks, double stars, boolean ticked, Type t,
				boolean danger) {
			this.id = id;
			this.visibility = visibility;
			this.nr = nr;
			this.name = name;
			this.comment = comment;
			this.gradeNumber = gradeNumber;
			this.grade = grade;
			this.fa = fa;
			this.numPitches = numPitches;
			this.hasImages = hasImages;
			this.hasMovies = hasMovies;
			this.lat = lat;
			this.lng = lng;
			this.numTicks = numTicks;
			this.stars = stars;
			this.ticked = ticked;
			this.t = t;
			this.danger = danger;
		}

		public String getComment() {
			return comment;
		}

		public String getFa() {
			return fa;
		}

		public String getGrade() {
			return grade;
		}

		public int getGradeNumber() {
			return gradeNumber;
		}
		
		public boolean getHasImages() {
			return hasImages;
		}
		
		public boolean getHasMovies() {
			return hasMovies;
		}
		
		public int getId() {
			return id;
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

		public int getNr() {
			return nr;
		}

		public int getNumPitches() {
			return numPitches;
		}

		public int getNumTicks() {
			return numTicks;
		}

		public double getStars() {
			return stars;
		}

		public Type getT() {
			return t;
		}

		public int getVisibility() {
			return visibility;
		}

		public boolean isDanger() {
			return danger;
		}

		public boolean isTicked() {
			return ticked;
		}
	}
	
	private final boolean orderByGrade;
	private final int areaId;
	private final int areaVisibility;
	private final String areaName;
	private final String canonical;
	private final int id;
	private final int visibility;
	private final String name;
	private final String comment;
	private final double lat;
	private final double lng;
	private final String polygonCoords;
	private final String polyline;
	private final List<Media> media;
	private final List<Problem> problems = new ArrayList<>();
	private final List<NewMedia> newMedia;
	private final long hits;
	private Metadata metadata;
	
	public Sector(boolean orderByGrade, int areaId, int areaVisibility, String areaName, String canonical, int id, int visibility, String name, String comment, double lat, double lng, String polygonCoords, String polyline, List<Media> media, List<NewMedia> newMedia, long hits) {
		this.orderByGrade = orderByGrade;
		this.areaId = areaId;
		this.areaVisibility = areaVisibility;
		this.areaName = areaName;
		this.canonical = canonical;
		this.id = id;
		this.visibility = visibility;
		this.name = name;
		this.comment = comment;
		this.lat = lat;
		this.lng = lng;
		this.polygonCoords = polygonCoords;
		this.polyline = polyline;
		this.media = media;
		this.newMedia = newMedia;
		this.hits = hits;
	}
	
	public void addProblem(int id, int visibility, int nr, String name, String comment, int gradeNumber, String grade, String fa, int numPitches, boolean hasImages, boolean hasMovies, double lat, double lng, int numTicks, double stars, boolean ticked, Type t, boolean danger) {
		this.problems.add(new Problem(id, visibility, nr, name, comment, gradeNumber, grade, fa, numPitches, hasImages, hasMovies, lat, lng, numTicks, stars, ticked, t, danger));
	}
	
	public int getAreaId() {
		return areaId;
	}

	public String getAreaName() {
		return areaName;
	}
	
	public int getAreaVisibility() {
		return areaVisibility;
	}
	
	public String getCanonical() {
		return canonical;
	}
	
	public String getComment() {
		return comment;
	}
	
	public long getHits() {
		return hits;
	}
	
	public int getId() {
		return id;
	}

	public double getLat() {
		return lat;
	}

	public double getLng() {
		return lng;
	}
	
	public List<Media> getMedia() {
		return media;
	}
	
	@Override
	public Metadata getMetadata() {
		return metadata;
	}
	
	public String getName() {
		return name;
	}
	
	public List<NewMedia> getNewMedia() {
		return newMedia;
	}
	
	public String getPolygonCoords() {
		return polygonCoords;
	}

	public String getPolyline() {
		return polyline;
	}
	
	public List<Problem> getProblems() {
		return problems;
	}
	
	public int getVisibility() {
		return visibility;
	}
	
	public boolean isOrderByGrade() {
		return orderByGrade;
	}
	
	@Override
	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	@Override
	public String toString() {
		return "Sector [areaId=" + areaId + ", areaVisibility=" + areaVisibility + ", areaName=" + areaName
				+ ", canonical=" + canonical + ", id=" + id + ", visibility=" + visibility + ", name=" + name
				+ ", comment=" + comment + ", lat=" + lat + ", lng=" + lng + ", polygonCoords=" + polygonCoords + ", polyline=" + polyline
				+ ", media=" + media + ", problems=" + problems + ", newMedia=" + newMedia + ", metadata=" + metadata
				+ "]";
	}
}