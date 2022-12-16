package com.moontv.application.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class SeasonItem{

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("episodes")
	private List<Episode> episodes;

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public List<Episode> getEpisodes(){
		return episodes;
	}

	@Override
	public String toString() {
		return "SeasonItem{" +
				"id=" + id +
				", title='" + title + '\'' +
				", episodes=" + episodes +
				'}';
	}
}