package com.d0542528.sic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OPcode {
	
	public OPcode() {
		createOPMap();
		createOther();
	}

	private Map<String, String> mapOP;
	private List<String> listOther;

	private void createOPMap() {
		mapOP = new HashMap<String, String>();
		
		mapOP.put("18", "ADD");
		mapOP.put("58", "ADDF");
		mapOP.put("90", "ADDR");
		mapOP.put("40", "AND");
		mapOP.put("B4", "CLEAR");
		mapOP.put("28", "COMP");
		mapOP.put("88", "COMPF");
		mapOP.put("A0", "COMPR");
		mapOP.put("24", "DIV");
		mapOP.put("64", "DIVF");
		mapOP.put("9C", "DIVR");
		mapOP.put("C4", "FIX");
		mapOP.put("C0", "FLOAT");
		mapOP.put("F4", "HIO");
		mapOP.put("3C", "J");
		mapOP.put("30", "JEQ");
		mapOP.put("34", "JGT");
		mapOP.put("38", "JLT");
		mapOP.put("48", "JSUB");
		mapOP.put("00", "LDA");
		mapOP.put("68", "LDB");
		mapOP.put("50", "LDCH");
		mapOP.put("70", "LDF");
		mapOP.put("08", "LDL");
		mapOP.put("6C", "LDS");
		mapOP.put("74", "LDT");
		mapOP.put("04", "LDX");
		mapOP.put("E0", "LPS");
		mapOP.put("20", "MUL");
		mapOP.put("60", "MULF");
		mapOP.put("98", "MULR");
		mapOP.put("C8", "NORM");
		mapOP.put("44", "OR");
		mapOP.put("D8", "RD");
		mapOP.put("AC", "RMO");
		mapOP.put("4C", "RSUB");
		mapOP.put("A4", "SHIFTL");
		mapOP.put("A8", "SHIFTR");
		mapOP.put("F0", "SIO");
		mapOP.put("EC", "SSK");
		mapOP.put("0C", "STA");
		mapOP.put("78", "STB");
		mapOP.put("54", "STCH");
		mapOP.put("80", "STF");
		mapOP.put("D4", "STI");
		mapOP.put("14", "STL");
		mapOP.put("7C", "STS");
		mapOP.put("E8", "STSW");
		mapOP.put("84", "STT");
		mapOP.put("10", "STX");
		mapOP.put("1C", "SUB");
		mapOP.put("5C", "SUBF");
		mapOP.put("94", "SUBR");
		mapOP.put("B0", "SVC");
		mapOP.put("E0", "TD");
		mapOP.put("F8", "TIO");
		mapOP.put("2C", "TIX");
		mapOP.put("B8", "TIXR");
		mapOP.put("DC", "WD");
	}
	
	private void createOther() {
		listOther = new ArrayList<String>();
		listOther.add("BYTE");
		listOther.add("WORD");
		listOther.add("RESB");
		listOther.add("RESW");
	}
	
	/*
	 * 
	 */
	
	public String findXfromString(String op) {
		for(String x : mapOP.keySet()) {
			if(mapOP.get(x).equalsIgnoreCase(op)) {
				return x;
			}
		}
		return null;
	}
	
	public boolean isOPcode(String op) {
		return findXfromString(op) != null;
	}
	
	public boolean isOther(String op) {
		for(String s : listOther) {
			if(s.equalsIgnoreCase(op)) {
				return true;
			}
		}
		return false;
	}
}
