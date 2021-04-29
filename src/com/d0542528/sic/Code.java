package com.d0542528.sic;

public class Code {
	
	private String loc = "";
	private String title = "";
	private String op = "";
	private String value = "";
	private String code = "";
	
	private boolean isOther = false;

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
	
	public Code copy() {
		Code newcode = new Code();
		if(getLoc() != null && !getLoc().isEmpty()) {
			newcode.setLoc(getLoc());
		}
		if(getTitle() != null && !getTitle().isEmpty()) {
			newcode.setTitle(getTitle());
		}
		if(getOp() != null && !getOp().isEmpty()) {
			newcode.setOp(getOp());
		}
		if(getValue() != null && !getValue().isEmpty()) {
			newcode.setValue(getValue());
		}
		if(getCode() != null && !getCode().isEmpty()) {
			newcode.setCode(getCode());
		}
		newcode.setOther(isOther());
		return newcode;
	}

	/**
	 * pair用
	 * @return debug用的code字串
	 */
	public String getStringPair() {
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
	 * loc用
	 * @return debug用的code字串
	 */
	public String getStringLoc() {
		String output = "";
		if(getLoc() != null && !getLoc().isEmpty()) {
			output += getLoc();
		}
		output += "\t";
		output += getStringPair();
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
