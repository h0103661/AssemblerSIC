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
	 * 為了排版好看, 會從main取得最長title的長度來調整
	 * @return debug用的code字串
	 */
	public String getStringPair() {
		String output = "";
		if(getTitle() != null && !getTitle().isEmpty()) {
			output += String.format("%-" + MainAssembler.getInstance().getLenTitle() + "s", getTitle());
		} else {
			output += String.format("%-" + MainAssembler.getInstance().getLenTitle() + "s", "");
		}
		
		output += "  ";
		output += String.format("%-5s", getOp());
		
		//value最長就是"title,X", 所以+2
		output += "  ";
		if(getValue() != null && !getValue().isEmpty()) {
			output += String.format("%-" + (MainAssembler.getInstance().getLenTitle() + 2) + "s", getValue());
		} else {
			output += String.format("%-" + (MainAssembler.getInstance().getLenTitle() + 2) + "s", "");
		}
		return output;
	}
	
	/**
	 * loc用
	 * 因為設定時就固定4位了, 只要把沒有loc的補上去
	 * @return debug用的code字串
	 */
	public String getStringLoc() {
		String output = "";
		if(getLoc() != null && !getLoc().isEmpty()) {
			output += getLoc();
		} else {
			output += String.format("%4s", "");
		}
		output += "  ";
		output += getStringPair();
		return output;
	}

	/**
	 * 
	 * @return 寫入檔案用的字串
	 */
	public String getOutput() {
		String output = getStringLoc();
		if(getCode() != null && !getCode().isEmpty()) {
			output += "  ";
			output += getCode();
		}
		return output;
	}
}
