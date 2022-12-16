package com.moontv.application.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class Season{

	@SerializedName("Season")
	private List<SeasonItem> season;

	public List<SeasonItem> getSeason(){
		return season;
	}
}