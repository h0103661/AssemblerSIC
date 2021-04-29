package com.d0542528.sic;

public class Code {
	
	private String loc;
	private String title;
	private String op;
	private String value;
	private String code;
	
	private boolean isOther;

	public Code() {
		
	}
	
	/*
	 * getter & setter
	 */
	
	public String getLoc() {
		return loc;
	}

	public void setLoc(String loc) {
		this.loc = loc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOp() {
		return op;
	}

	public void setOp(String op) {
		this.op = op;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public boolean isOther() {
		return isOther;
	}

	public void setOther(boolean isOther) {
		this.isOther = isOther;
	}

	/**
	 * 
	 * @return debug用的code字串
	 */
	public String getPairString() {
		String output = "";
		if(getTitle() != null && !getTitle().isEmpty()) {
			output += getTitle();
		}
		output += "\t";
		output += getOp();
		if(getValue() != null && !getValue().isEmpty()) {
			output += "\t";
			output += getValue();
		}
		return output;
	}

	/**
	 * 
	 * @return 寫入檔案用的字串
	 */
	public String getOutput() {
		return "";
	}
}
