/*
 * SIC Assembler
 * 
 * author: d0542528 呂宥融
 */

package com.d0542528.sic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MainAssembler {
	
	/*
	 * 程式開頭
	 */
	private static MainAssembler instance;

	public static void main(String[] args) {
		instance = new MainAssembler();
        instance.init();
	}
	
	public static MainAssembler getInstance() {
		return instance;
	}

	public static void setInstance(MainAssembler instance) {
		MainAssembler.instance = instance;
	}
	
	/*
	 * 程式初始化
	 */
	private OPcode op;
	
	private void init() {
		setOp(new OPcode());
		
		start();
	}

	public OPcode getOp() {
		return op;
	}

	public void setOp(OPcode op) {
		this.op = op;
	}
	
	/*
	 * 開始組譯
	 */
	
	private String fileName = "test.SIC";
	
	private String codeName;	//程式名稱
	private String startLoc;	//程式開頭位址
	private String startTitle;	//程式開頭標籤
	private String endLoc;		//程式結尾位址
	private String totalLoc;	//程式總長度
	
	//pairs分成三個是為了檢查各階段的變化, debug用
	private void start() {
		List<String> inputs = readFileFromString(fileName);
		setCodeName(inputs);
		setStartLoc(inputs);
		setStartTitle(inputs);
		
		List<Code> pairsA = pair(inputs);
		/*
		for(Code c : pairsA) {
			System.out.println(c.getStringPair());
		}*/
		
		
		List<Code> pairsB = calculateLoc(pairsA);
		/*
		 * for(Code c : pairsB) {
			System.out.println(c.getStringLoc());
		}
		System.out.println(getEndLoc());
		System.out.println(getTotalLoc());*/
		
		List<Code> pairsC = calculateObject(pairsB);
		
		/*
		List<String> records = createRecord(pairsC);
		writeFileCodesFromString(fileName, pairsC);
		writeFileRecordsFromString(fileName, records);
		*/
	}

	/*
	 * getter & setter
	 */
	
	public String getCodeName() {
		return codeName;
	}

	public void setCodeName(List<String> inputs) {
		String last = "";
		for(String s : inputs) {
			if(s.equalsIgnoreCase("START")) {
				if(last.isEmpty()) {
					System.out.println("[ERROR] START前面沒有程式名稱!");
					return;
				} else {
					this.codeName = last;
					return;
				}
			}
			last = s;
		}
		System.out.println("[ERROR] 尋找程式名稱時找不到START!");
	}

	public String getStartLoc() {
		return startLoc;
	}

	public void setStartLoc(List<String> inputs) {
		boolean next = false;
		for(String s : inputs) {
			if(next) {
				this.startLoc = s;
				return;
			}
			if(s.equalsIgnoreCase("START")) {
				next = true;
			}
		}
		System.out.println("[ERROR] 尋找程式開頭位址時找不到START或START之後沒有字串!");
	}

	public String getStartTitle() {
		return startTitle;
	}

	public void setStartTitle(List<String> inputs) {
		boolean next = false;
		for(String s : inputs) {
			if(next) {
				this.startTitle = s;
				return;
			}
			if(s.equalsIgnoreCase("END")) {
				next = true;
			}
		}
		System.out.println("[ERROR] 尋找程式開頭位址時找不到END或END之後沒有字串!");
	}
	
	public String getEndLoc() {
		return endLoc;
	}

	public void setEndLoc(String endLoc) {
		this.endLoc = endLoc;
	}

	public String getTotalLoc() {
		return totalLoc;
	}

	public void setTotalLoc(String totalLoc) {
		this.totalLoc = totalLoc;
	}

	
	/*
	 * function
	 */
	
	/**
	 * 從loc讀取檔案, 並產生字元list
	 * @param loc 位置
	 * @return 字元list
	 */
	private List<String> readFileFromString(String loc) {
		List<String> listInputs = new ArrayList<String>();
		/*
		 * 讀取檔案
		 */
		File file = new File(loc);
		FileReader reader = null;
		try {
			reader = new FileReader(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader buffer = new BufferedReader(reader);
		Scanner scan = new Scanner(buffer);
		
		/*
		 * 讀取字元
		 */
		while(scan.hasNext()){
			String next = scan.next();
			listInputs.add(next);
		}
		
		/*
		 * 結束讀取
		 */
		scan.close();
		try {
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return listInputs;
	}
	
	/**
	 * 將inputs配對成code
	 * @param inputs 輸入的list
	 * @return Code的list
	 */
	private List<Code> pair(List<String> inputs) {
		List<Code> listCode = new ArrayList<Code>();
		
		String last = "";
		String opcode = "";
		boolean next = false;
		boolean isOther = false;
		for(String s : inputs) {
			if(next) { //如果上一個是op就寫入code
				Code code = new Code();
				code.setTitle(last);
				code.setOp(opcode);
				code.setValue(s);
				code.setOther(isOther);
				listCode.add(code);
				
				last = "";
				opcode = "";
				next = false;
				isOther = false;
				continue;
			}
			if(op.isOPcode(s)) {
				if(s.equalsIgnoreCase("RSUB")) { //RSUB只有一格, 要在這裡就寫入code
					Code code = new Code();
					code.setTitle(last);
					code.setOp(s);
					listCode.add(code);
					
					last = "";
					opcode = "";
					next = false;
					isOther = false;
				} else {
					opcode = s;
					next = true;
					continue;
				}
			} else if(op.isOther(s)) { //如果不是op但是是佔位符, 一樣要在下一步寫入code
				opcode = s;
				next = true;
				isOther = true;
				continue;
			}
			last = s;
		}
		
		return listCode;
	}
	
	/**
	 * 將pairs計算位址
	 * @param pairs 還沒計算好位址的Code list
	 * @return 計算好位址的pairs
	 */
	private List<Code> calculateLoc(List<Code> pairs) {
		List<Code> listCode = new ArrayList<Code>();
		
		//初始位址的10進位
		int i = Integer.parseInt(getStartLoc(), 16);
		for(Code c : pairs) {
			Code newcode = c.copy();
			//如果不滿6位，用0填充
			newcode.setLoc(String.format("%6s", Integer.toHexString(i).toUpperCase()).replace(' ', '0'));
			listCode.add(newcode);
			
			if(c.getOp().equalsIgnoreCase("BYTE")) {
				if(c.getValue().contains("X")) {
					i += 1;
				} else {
					int index = c.getValue().indexOf("\'");
					String s = c.getValue().substring(index + 1, c.getValue().length() - 1);
					i += s.length();
				}
			}else if(c.getOp().equalsIgnoreCase("RESB")) {
				i += Integer.parseInt(c.getValue());
			}else if(c.getOp().equalsIgnoreCase("RESW")) {
				i += (Integer.parseInt(c.getValue()) * 3);
			}else{
				i += 3;
			}
		}
		
		this.setEndLoc(String.format("%6s", Integer.toHexString(i).toUpperCase()).replace(' ', '0'));
		int start = Integer.parseInt(getStartLoc(), 16);
		int total = i - start;
		this.setTotalLoc(String.format("%6s", Integer.toHexString(total).toUpperCase()).replace(' ', '0'));
		
		return listCode;
	}
	
	/**
	 * 將pairs計算Object code
	 * @param pairs 還沒計算好Object code的Code list
	 * @return 計算好Object code的pairs
	 */
	private List<Code> calculateObject(List<Code> pairs) {
		List<Code> listCode = new ArrayList<Code>();
		
		return listCode;
	}
	
	/**
	 * 將pairs計算records
	 * @param pairs Code list
	 * @return 寫成字串list的records
	 */
	private List<String> createRecord(List<Code> pairs) {
		List<String> records = new ArrayList<String>();
		
		return records;
	}
	
	/*
	 * 輸出
	 */
	
	private File writeFileCodesFromString(String loc, List<Code> pairs) {
		//將code轉成輸出的字串
		List<String> outputs = new ArrayList<String>();
		for(Code code : pairs) {
			outputs.add(code.getOutput());
		}
		return writeFileFromString(loc, outputs);
	}
	
	private File writeFileRecordsFromString(String loc, List<String> records) {
		return writeFileFromString(loc, records);
	}
	
	private File writeFileFromString(String loc, List<String> outputs) {
		/*
		 * 讀取檔案
		 */
		File file = new File(loc);
		FileWriter writer = null;
		try {
			writer = new FileWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter buffer = new BufferedWriter(writer);
		
		/*
		 * 寫入字串
		 */
		for(String s : outputs) {
			try {
				buffer.write(s);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * 結束讀取
		 */
		try {
			buffer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return file;
	}
}
