/*
 * SIC Assembler
 * 
 * author: d0542528 �����
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
	 * ��ʽ�_�^
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
	 * ��ʽ��ʼ��
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
	 * �_ʼ�M�g
	 */
	
	private String fileName = "test.SIC";
	private String fileOutput1Name = "test.lst";
	private String fileOutput2Name = "test.obj";
	
	private String codeName;	//��ʽ���Q
	private String startLoc;	//��ʽ�_�^λַ
	private String startTitle;	//��ʽ�_�^�˻`
	private int lenTitle;	    //���Ltitle�L��
	private String endLoc;		//��ʽ�Yβλַ
	private String totalLoc;	//��ʽ���L��
	
	//pairs�ֳ������Ǟ��˙z����A�ε�׃��, debug��
	private void start() {
		List<String> inputs = readFileFromString(fileName);
		setCodeName(inputs);
		setStartLoc(inputs);
		setStartTitle(inputs);
		
		List<Code> listCodePair = pair(inputs);
		setLenTitle(listCodePair);	//�@��title�L��, �����Ҫ��֪��Щ���title, ������Ҫ��pair֮��
		
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
					System.out.println("[ERROR] STARTǰ��]�г�ʽ���Q!");
					return;
				} else {
					this.codeName = last;
					return;
				}
			}
			last = s;
		}
		System.out.println("[ERROR] ���ҳ�ʽ���Q�r�Ҳ���START!");
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
		System.out.println("[ERROR] ���ҳ�ʽ�_�^λַ�r�Ҳ���START��START֮��]���ִ�!");
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
		System.out.println("[ERROR] ���ҳ�ʽ�_�^λַ�r�Ҳ���END��END֮��]���ִ�!");
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
	 * ��loc�xȡ�n��, �K�a����Ԫlist
	 * @param loc λ��
	 * @return ��Ԫlist
	 */
	private List<String> readFileFromString(String loc) {
		System.out.println("[read file] ========================");
		
		List<String> listInputs = new ArrayList<String>();
		/*
		 * �xȡ�n��
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
		 * �xȡ��Ԫ
		 */
		while(scan.hasNext()){
			String next = scan.next();
			listInputs.add(next);
			System.out.println("[read file] read: " + next);
		}
		
		/*
		 * �Y���xȡ
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
	 * ��inputs�䌦��code
	 * @param inputs ݔ���list
	 * @return Code��list
	 */
	private List<Code> pair(List<String> inputs) {
		System.out.println("[pair code] ========================");
		
		List<Code> listCode = new ArrayList<Code>();
		
		String last = "";
		String opcode = "";
		boolean next = false;
		boolean isOther = false;
		for(String s : inputs) {
			if(next) { //�����һ����op�͌���code
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
				if(s.equalsIgnoreCase("RSUB")) { //RSUBֻ��һ��, Ҫ���@�e�͌���code
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
			} else if(OPcode.isOther(s)) { //�������op�����ǁ�λ��, һ��Ҫ����һ������code
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
	 * ��pairsӋ��λַ
	 * @param pairs ߀�]Ӌ���λַ��Code list
	 * @return Ӌ���λַ��pairs
	 */
	private List<Code> calculateLoc(List<Code> pairs) {
		System.out.println("[calculate loc] ========================");
		
		List<Code> listCode = new ArrayList<Code>();
		
		//��ʼλַ��10�Mλ
		int i = Integer.parseInt(getStartLoc(), 16);
		for(Code c : pairs) {
			Code newcode = c.copy();
			//������M4λ����0���
			newcode.setLoc(String.format("%4s", Integer.toHexString(i).toUpperCase()).replace(' ', '0'));
			listCode.add(newcode);
			System.out.println(newcode.getStringLoc());
			
			if(c.getOp().equalsIgnoreCase("BYTE")) {
				int index = c.getValue().indexOf("\'");
				String s = c.getValue().substring(index + 1, c.getValue().length() - 1);
				double l = s.length();
				if(c.getValue().contains("X")) {
					i += Math.ceil(l/2); //'X' ÿ2λռһ��byte, �o�l���Mλ
				} else {
					i += l; //'C'ÿ1λռһ��byte
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
	 * ��pairsӋ��Object code
	 * @param pairs ߀�]Ӌ���Object code��Code list
	 * @return Ӌ���Object code��pairs
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
						//�D��ascii
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
				} else if(c.getOp().equalsIgnoreCase("WORD")){ //�D16�Mλ
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
	 * ��pairsӋ��records
	 * @param pairs Code list
	 * @return �����ִ�list��records
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
			
			//����RESB, RESW�r
			if(c.isOther()) {
				if(c.getOp().equalsIgnoreCase("RESB") || c.getOp().equalsIgnoreCase("RESW")) {
					if(!list.isEmpty()) {
						//����RESB, RESW�r, list��objcode�t����һ��T record
						String t = getTrecord(loc, list);
						records.add(t);
						System.out.println(t);
					}
					//����б��ǿյ�, ���Ì�record (���̎���_�^��RESB, ���B�m��RESB)
					
					nextLoc = true;
					loc = "";
					list = "";
					continue;
				}
			}
			
			//��������@��code, �б���M�r, ֱ���Ȍ�һ��record, �����@��code���_�^
			//�б�M�ǳ��^1F(16�Mλ), Ҳ���ǲ��ܳ��^31(10�Mλ), ÿ2����Ԫ��1, ���Կ����ǲ��ܳ��^62
			String temp = list + c.getCode();
			if(temp.length() >= 62) {
				String t = getTrecord(loc, list);
				records.add(t);
				System.out.println(t);
				
				loc = c.getLoc();
				list = c.getCode();
				continue;
			}
			
			//����]������ռλ��, �қ]�НMrecord, ��Ŀǰ��code����list
			list += c.getCode();
		}
		
		//END�r, list��objcode�t����һ��T record
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
		
		//�_ʼλַ, 6λ
		record += String.format("%6s", loc).replace(' ', '0');
		
		//ÿ2λռһ��byte, �o�l���Mλ
		double ld = list.length();
		int li = (int) Math.ceil(ld/2);
		String len = String.format("%2s", Integer.toHexString(li).toUpperCase()).replace(' ', '0');
		record += len;
		
		//objectcodes
		record += list;
		
		return record;
	}
	
	/*
	 * ݔ��
	 */
	
	private File writeFileCodesFromString(String loc, List<Code> pairs) {
		System.out.println("[write file] write listing file");
		
		//��code�D��ݔ�����ִ�
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
		 * �xȡ�n��
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
		 * �����ִ�
		 */
		for(String s : outputs) {
			try {
				buffer.write(s + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		/*
		 * �Y���xȡ
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
