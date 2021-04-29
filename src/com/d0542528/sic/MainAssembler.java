/*
 * SIC Assembler
 * 
 * author: d0542528 �����
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
	 * �_ʼ�M�g
	 */
	
	private String fileName = "test.SIC";
	
	private String codeName;	//��ʽ���Q
	private String startLoc;	//��ʽ�_�^λַ
	private String startTitle;	//��ʽ�_�^�˻`
	private String endLoc;		//��ʽ�Yβλַ
	private String totalLoc;	//��ʽ���L��
	
	//pairs�ֳ������Ǟ��˙z����A�ε�׃��, debug��
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
		
		return listInputs;
	}
	
	/**
	 * ��inputs�䌦��code
	 * @param inputs ݔ���list
	 * @return Code��list
	 */
	private List<Code> pair(List<String> inputs) {
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
				
				last = "";
				opcode = "";
				next = false;
				isOther = false;
				continue;
			}
			if(op.isOPcode(s)) {
				if(s.equalsIgnoreCase("RSUB")) { //RSUBֻ��һ��, Ҫ���@�e�͌���code
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
			} else if(op.isOther(s)) { //�������op�����ǁ�λ��, һ��Ҫ����һ������code
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
	 * ��pairsӋ��λַ
	 * @param pairs ߀�]Ӌ���λַ��Code list
	 * @return Ӌ���λַ��pairs
	 */
	private List<Code> calculateLoc(List<Code> pairs) {
		List<Code> listCode = new ArrayList<Code>();
		
		//��ʼλַ��10�Mλ
		int i = Integer.parseInt(getStartLoc(), 16);
		for(Code c : pairs) {
			Code newcode = c.copy();
			//������M6λ����0���
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
	 * ��pairsӋ��Object code
	 * @param pairs ߀�]Ӌ���Object code��Code list
	 * @return Ӌ���Object code��pairs
	 */
	private List<Code> calculateObject(List<Code> pairs) {
		List<Code> listCode = new ArrayList<Code>();
		
		return listCode;
	}
	
	/**
	 * ��pairsӋ��records
	 * @param pairs Code list
	 * @return �����ִ�list��records
	 */
	private List<String> createRecord(List<Code> pairs) {
		List<String> records = new ArrayList<String>();
		
		return records;
	}
	
	/*
	 * ݔ��
	 */
	
	private File writeFileCodesFromString(String loc, List<Code> pairs) {
		//��code�D��ݔ�����ִ�
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
				buffer.write(s);
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
		
		return file;
	}
}
