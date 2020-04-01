package com.buldreinfo.jersey.jaxb.model;

import java.util.List;

@Deprecated
public class DeprecatedActivity {
	public class Media {
		private final int id;
		private final boolean isMovie;
		public Media(int id, boolean isMovie) {
			super();
			this.id = id;
			this.isMovie = isMovie;
		}
		public int getId() {
			return id;
		}
		public boolean isMovie() {
			return isMovie;
		}
		@Override
		public String toString() {
			return "Media [id=" + id + ", isMovie=" + isMovie + "]";
		}
	}
	public class User {
		private final int id;
		private final String name;
		private final String picture;
		public User(int id, String name, String picture) {
			super();
			this.id = id;
			this.name = name;
			this.picture = picture;
		}
		public int getId() {
			return id;
		}
		public String getName() {
			return name;
		}
		public String getPicture() {
			return picture;
		}
		@Override
		public String toString() {
			return "User [id=" + id + ", name=" + name + ", picture=" + picture + "]";
		}
	}
	private final String timestamp;
	private String timeAgo;
	private final int problemId;
	private final int problemVisibility;
	private final String problemName;
	private int problemRandomMediaId;
	private List<Media> media;
	private final int id;
	private String grade;
	private final int stars;
	private final String name;
	private final String picture;
	private final String description;
	private final String message;
	private final List<User> users;
	public DeprecatedActivity(String timestamp, int problemId, int problemVisibility, String problemName, int problemRandomMediaId,
			List<Media> media, String grade, int id, int stars, String name, String picture, String description, String message,
			List<User> users) {
		super();
		this.timestamp = timestamp;
		this.problemId = problemId;
		this.problemVisibility = problemVisibility;
		this.problemName = problemName;
		this.problemRandomMediaId = problemRandomMediaId;
		this.media = media;
		this.grade = grade;
		this.id = id;
		this.stars = stars;
		this.name = name;
		this.picture = picture;
		this.description = description;
		this.message = message;
		this.users = users;
	}
	public String getDescription() {
		return description;
	}
	public String getGrade() {
		return grade;
	}
	public int getId() {
		return id;
	}
	public List<Media> getMedia() {
		return media;
	}
	public String getMessage() {
		return message;
	}
	public String getName() {
		return name;
	}
	public String getPicture() {
		return picture;
	}
	public int getProblemId() {
		return problemId;
	}
	public String getProblemName() {
		return problemName;
	}
	public int getProblemRandomMediaId() {
		return problemRandomMediaId;
	}
	public int getProblemVisibility() {
		return problemVisibility;
	}
	public int getStars() {
		return stars;
	}
	public String getTimeAgo() {
		return timeAgo;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public List<User> getUsers() {
		return users;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public void setMedia(List<Media> media) {
		this.media = media;
	}
	public void setProblemRandomMediaId(int problemRandomMediaId) {
		this.problemRandomMediaId = problemRandomMediaId;
	}
	public void setTimeAgo(String timeAgo) {
		this.timeAgo = timeAgo;
	}
	@Override
	public String toString() {
		return "Activity [timestamp=" + timestamp + ", timeAgo=" + timeAgo + ", problemId=" + problemId
				+ ", problemVisibility=" + problemVisibility + ", problemName=" + problemName
				+ ", problemRandomMediaId=" + problemRandomMediaId + ", media=" + media + ", id=" + id + ", grade="
				+ grade + ", stars=" + stars + ", name=" + name + ", picture=" + picture + ", description="
				+ description + ", message=" + message + ", users=" + users + "]";
	}
}