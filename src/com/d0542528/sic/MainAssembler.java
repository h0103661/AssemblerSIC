/*
 * SIC Assembler
 * 
 * author: d0542528 呂宥融
 */

package com.d0542528.sic;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
	private OPcode OPcode;
	
	private void init() {
		setOp(new OPcode());
		
		start();
	}

	public OPcode getOp() {
		return OPcode;
	}

	public void setOp(OPcode op) {
		this.OPcode = op;
	}
	
	/*
	 * 開始組譯
	 */
	
	private String fileName = "test.SIC";
	private String fileOutput1Name = "test.lst";
	private String fileOutput2Name = "test.obj";
	
	private String codeName;	//程式名稱
	private String startLoc;	//程式開頭位址
	private String startTitle;	//程式開頭標籤
	private int lenTitle;	    //最長title長度
	private String endLoc;		//程式結尾位址
	private String totalLoc;	//程式總長度
	
	//pairs分成三個是為了檢查各階段的變化, debug用
	private void start() {
		List<String> inputs = readFileFromString(fileName);
		setCodeName(inputs);
		setStartLoc(inputs);
		setStartTitle(inputs);
		
		List<Code> listCodePair = pair(inputs);
		setLenTitle(listCodePair);	//獲得title長度, 因為需要得知那些屬於title, 所以需要在pair之後
		
		List<Code> listCodeLoc = calculateLoc(listCodePair);
		
		List<Code> listCodeObj = calculateObject(listCodeLoc);
		
		List<String> listRecord = createRecord(listCodeObj);
		
		writeFileCodesFromString(fileOutput1Name, listCodeObj);
		writeFileRecordsFromString(fileOutput2Name, listRecord);
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
	
	public int getLenTitle() {
		return lenTitle;
	}

	public void setLenTitle(List<Code> inputs) {
		int i = 0;
		for(Code c : inputs) {
			if(c.getTitle() != null && !c.getTitle().isEmpty()) {
				int l = c.getTitle().length();
				if(l > i) {
					i = l;
				}
			}
		}
		if(getCodeName().length() > i) {
			i = getCodeName().length();
		}
		this.lenTitle = i;
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
		System.out.println("[read file] ========================");
		
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
			System.out.println("[read file] read: " + next);
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
		
		System.out.println("[read file] ========================");
		
		return listInputs;
	}
	
	/**
	 * 將inputs配對成code
	 * @param inputs 輸入的list
	 * @return Code的list
	 */
	private List<Code> pair(List<String> inputs) {
		System.out.println("[pair code] ========================");
		
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
				
				System.out.println("[pair code] pair success: " + code.getTitle() + " " + code.getOp() + " " + code.getValue());
				
				last = "";
				opcode = "";
				next = false;
				isOther = false;
				continue;
			}
			if(OPcode.isOPcode(s)) {
				if(s.equalsIgnoreCase("RSUB")) { //RSUB只有一格, 要在這裡就寫入code
					Code code = new Code();
					code.setTitle(last);
					code.setOp(s);
					listCode.add(code);
					
					System.out.println("[pair code] pair success: " + code.getTitle() + " " + code.getOp() + " " + code.getValue());
					
					last = "";
					opcode = "";
					next = false;
					isOther = false;
				} else {
					opcode = s;
					next = true;
					continue;
				}
			} else if(OPcode.isOther(s)) { //如果不是op但是是佔位符, 一樣要在下一步寫入code
				opcode = s;
				next = true;
				isOther = true;
				continue;
			}
			last = s;
		}
		
		System.out.println("[pair code] ========================");
		
		return listCode;
	}
	
	/**
	 * 將pairs計算位址
	 * @param pairs 還沒計算好位址的Code list
	 * @return 計算好位址的pairs
	 */
	private List<Code> calculateLoc(List<Code> pairs) {
		System.out.println("[calculate loc] ========================");
		
		List<Code> listCode = new ArrayList<Code>();
		
		//初始位址的10進位
		int i = Integer.parseInt(getStartLoc(), 16);
		for(Code c : pairs) {
			Code newcode = c.copy();
			//如果不滿4位，用0填充
			newcode.setLoc(String.format("%4s", Integer.toHexString(i).toUpperCase()).replace(' ', '0'));
			listCode.add(newcode);
			System.out.println(newcode.getStringLoc());
			
			if(c.getOp().equalsIgnoreCase("BYTE")) {
				int index = c.getValue().indexOf("\'");
				String s = c.getValue().substring(index + 1, c.getValue().length() - 1);
				double l = s.length();
				if(c.getValue().contains("X")) {
					i += Math.ceil(l/2); //'X' 每2位占一個byte, 無條件進位
				} else {
					i += l; //'C'每1位占一個byte
				}
			}else if(c.getOp().equalsIgnoreCase("RESB")) {
				i += Integer.parseInt(c.getValue());
			}else if(c.getOp().equalsIgnoreCase("RESW")) {
				i += (Integer.parseInt(c.getValue()) * 3);
			}else{
				i += 3;
			}
		}
		
		this.setEndLoc(String.format("%4s", Integer.toHexString(i).toUpperCase()).replace(' ', '0'));
		int start = Integer.parseInt(getStartLoc(), 16);
		int total = i - start;
		this.setTotalLoc(String.format("%4s", Integer.toHexString(total).toUpperCase()).replace(' ', '0'));
		
		System.out.println("[calculate loc] all code calculated");
		System.out.println("[calculate loc] end loc: " + this.getEndLoc());
		System.out.println("[calculate loc] total loc: " + this.getTotalLoc());
		System.out.println("[calculate loc] ========================");
		
		return listCode;
	}
	
	/**
	 * 將pairs計算Object code
	 * @param pairs 還沒計算好Object code的Code list
	 * @return 計算好Object code的pairs
	 */
	private List<Code> calculateObject(List<Code> pairs) {
		System.out.println("[object code] ========================");
		
		List<Code> listCode = new ArrayList<Code>();
		
		for(Code c : pairs) {
			if(c.isOther()) {
				if(c.getOp().equalsIgnoreCase("BYTE")){
					int index = c.getValue().indexOf("\'");
					String s = c.getValue().substring(index + 1, c.getValue().length() - 1);
					if(c.getValue().contains("X")) {
						Code newcode = c.copy();
						newcode.setCode(s);
						listCode.add(newcode);
						
						System.out.println(newcode.getOutput());
					} else {
						//轉成ascii
						String listx = "";
						for(char ch : s.toCharArray()) {
							int x = ch;
							listx += Integer.toHexString(x).toUpperCase();
						}
						
						Code newcode = c.copy();
						newcode.setCode(listx);
						listCode.add(newcode);
						
						System.out.println(newcode.getOutput());
					}
				} else if(c.getOp().equalsIgnoreCase("WORD")){ //轉16進位
					Code newcode = c.copy();
					newcode.setCode(String.format("%6s", Integer.toHexString(Integer.parseInt(c.getValue())).toUpperCase()).replace(' ', '0'));
					listCode.add(newcode);
					
					System.out.println(newcode.getOutput());
				} else { //RESB, RESW
					Code newcode = c.copy();
					listCode.add(newcode);
					
					System.out.println(newcode.getOutput());
				}
			} else if(c.getOp().equalsIgnoreCase("RSUB")){
				String ta = "0000";
				String op = OPcode.findXfromString(c.getOp());
				String code = op + ta;
				
				Code newcode = c.copy();
				newcode.setCode(code);
				listCode.add(newcode);
				
				System.out.println(newcode.getOutput());
			} else {
				if(c.getValue() != null && !c.getValue().isEmpty()) {
					String value = c.getValue();
					if(value.contains(",")) {
						int index = value.indexOf(",");
						value = value.substring(0, index);
					}
					Code tc = getCodeByTitle(pairs, value);
					String ta = tc.getLoc();
					String op = OPcode.findXfromString(c.getOp());
					String code = op + ta;
					
					Code newcode = c.copy();
					newcode.setCode(code);
					listCode.add(newcode);
					
					System.out.println(newcode.getOutput());
				}
			}
		}
		
		System.out.println("[object code] ========================");
		return listCode;
	}
	
	private Code getCodeByTitle(List<Code> list, String title) {
		for(Code c : list) {
			if(c.getTitle().equalsIgnoreCase(title)) {
				return c;
			}
		}
		return null;
	}
	
	/**
	 * 將pairs計算records
	 * @param pairs Code list
	 * @return 寫成字串list的records
	 */
	private List<String> createRecord(List<Code> pairs) {
		System.out.println("[create record] ========================");
		
		List<String> records = new ArrayList<String>();
		
		//H
		String h = "H" + getCodeName() + String.format("%6s", getStartLoc()).replace(' ', '0') + String.format("%6s", getTotalLoc()).replace(' ', '0');
		records.add(h);
		System.out.println(h);
		
		//T
		String loc = getStartLoc();
		boolean nextLoc = false;
		String list = "";
		for(Code c : pairs) {
			if(nextLoc) {
				nextLoc = false;
				loc = c.getLoc();
			}
			
			//遇到RESB, RESW時
			if(c.isOther()) {
				if(c.getOp().equalsIgnoreCase("RESB") || c.getOp().equalsIgnoreCase("RESW")) {
					if(!list.isEmpty()) {
						//遇到RESB, RESW時, list有objcode則寫入一個T record
						String t = getTrecord(loc, list);
						records.add(t);
						System.out.println(t);
					}
					//如果列表是空的, 不用寫record (用於處理開頭是RESB, 或連續的RESB)
					
					nextLoc = true;
					loc = "";
					list = "";
					continue;
				}
			}
			
			//如果加了這個code, 列表會滿時, 直接先寫一張record, 再以這個code為開頭
			//列表滿是超過1F(16進位), 也就是不能超過31(10進位), 每2個字元是1, 所以總共是不能超過62
			String temp = list + c.getCode();
			if(temp.length() >= 62) {
				String t = getTrecord(loc, list);
				records.add(t);
				System.out.println(t);
				
				loc = c.getLoc();
				list = c.getCode();
				continue;
			}
			
			//如果沒有遇到占位符, 且沒有滿record, 將目前的code放入list
			list += c.getCode();
		}
		
		//END時, list有objcode則寫入一個T record
		if(!list.isEmpty()) {
			records.add(getTrecord(loc, list));
		}
		
		//E
		String e = "E" + String.format("%6s", getStartLoc()).replace(' ', '0');
		records.add(e);
		System.out.println(e);
		
		System.out.println("[create record] ========================");
		return records;
	}
	
	private String getTrecord(String loc, String list) {
		//T
		String record = "T";
		
		//開始位址, 6位
		record += String.format("%6s", loc).replace(' ', '0');
		
		//每2位占一個byte, 無條件進位
		double ld = list.length();
		int li = (int) Math.ceil(ld/2);
		String len = String.format("%2s", Integer.toHexString(li).toUpperCase()).replace(' ', '0');
		record += len;
		
		//objectcodes
		record += list;
		
		return record;
	}
	
	/*
	 * 輸出
	 */
	
	private File writeFileCodesFromString(String loc, List<Code> pairs) {
		System.out.println("[write file] write listing file");
		
		//將code轉成輸出的字串
		List<String> outputs = new ArrayList<String>();
		
		//START
		Code start = new Code();
		start.setLoc(getStartLoc());
		start.setTitle(getCodeName());
		start.setOp("START");
		start.setValue(getStartLoc());
		outputs.add(start.getOutput());
		
		//T
		for(Code code : pairs) {
			outputs.add(code.getOutput());
		}
		
		//START
		Code end = new Code();
		end.setLoc(getEndLoc());
		end.setOp("END");
		end.setValue(getStartTitle());
		outputs.add(end.getOutput());
		
		return writeFileFromString(loc, outputs);
	}
	
	private File writeFileRecordsFromString(String loc, List<String> records) {
		System.out.println("[write file] write object file");
		
		return writeFileFromString(loc, records);
	}
	
	private File writeFileFromString(String loc, List<String> outputs) {
		System.out.println("[write file] start");
		
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
				buffer.write(s + "\n");
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
		
		System.out.println("[write file] finish");
		return file;
	}
}
