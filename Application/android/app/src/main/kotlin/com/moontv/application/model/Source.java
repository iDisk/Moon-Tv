package com.moontv.application.model;

import com.google.gson.annotations.SerializedName;

public class Source {

	@SerializedName("external")
	private boolean external;

	@SerializedName("premium")
	private String premium;

	@SerializedName("size")
	private Object size;

	@SerializedName("kind")
	private String kind;

	@SerializedName("id")
	private int id;

	@SerializedName("title")
	private String title;

	@SerializedName("type")
	private String type;

	@SerializedName("url")
	private String url;

	@SerializedName("quality")
	private Object quality;

	public boolean isExternal(){
		return external;
	}

	public String getPremium(){
		return premium;
	}

	public Object getSize(){
		return size;
	}

	public String getKind(){
		return kind;
	}

	public int getId(){
		return id;
	}

	public String getTitle(){
		return title;
	}

	public String getType(){
		return type;
	}

	public String getUrl(){
		return url;
	}

	public Object getQuality(){
		return quality;
	}

	@Override
	public String toString() {
		return "Source{" +
				"external=" + external +
				", premium='" + premium + '\'' +
				", size=" + size +
				", kind='" + kind + '\'' +
				", id=" + id +
				", title='" + title + '\'' +
				", type='" + type + '\'' +
				", url='" + url + '\'' +
				", quality=" + quality +
				'}';
	}
}