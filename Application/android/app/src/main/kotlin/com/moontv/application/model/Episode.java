package com.moontv.application.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Episode {

	@SerializedName("duration")
	private Object duration;

	@SerializedName("downloadas")
	private String downloadas;

	@SerializedName("image")
	private String image;

	@SerializedName("sources")
	private List<Source> sources;

	@SerializedName("playas")
	private String playas;

	@SerializedName("description")
	private String description;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	public Object getDuration(){
		return duration;
	}

	public String getDownloadas(){
		return downloadas;
	}

	public String getImage(){
		return image;
	}

	public List<Source> getSources(){
		return sources;
	}

	public String getPlayas(){
		return playas;
	}

	public String getDescription(){
		return description;
	}

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}


	@Override
	public String toString() {
		return "Episode{" +
				"duration=" + duration +
				", downloadas='" + downloadas + '\'' +
				", image='" + image + '\'' +
				", sources=" + sources +
				", playas='" + playas + '\'' +
				", description='" + description + '\'' +
				", id=" + id +
				", title='" + title + '\'' +
				'}';
	}
}