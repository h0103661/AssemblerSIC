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
	
	private String folder = "";
	private String fileName = "test.SIC";
	
	//pairs�ֳ������Ǟ��˙z����A�ε�׃��, debug��
	private void start() {
		List<String> inputs = readFileFromString(folder + File.separator + fileName);
		List<Code> pairsA = pair(inputs);
		List<Code> pairsB = calculateLoc(pairsA);
		List<Code> pairsC = calculateObject(pairsB);
		List<String> records = createRecord(pairsC);
		writeFileCodesFromString(folder + File.separator + fileName, pairsC);
		writeFileRecordsFromString(folder + File.separator + fileName, records);
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
		//FileInputStream����byte���λ
		//FileReader������Ԫ���λ
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
		return null;
	}
	
	/**
	 * ��pairsӋ��λַ
	 * @param pairs ߀�]Ӌ���λַ��Code list
	 * @return Ӌ���λַ��pairs
	 */
	private List<Code> calculateLoc(List<Code> pairs) {
		
		return pairs;
	}
	
	/**
	 * ��pairsӋ��Object code
	 * @param pairs ߀�]Ӌ���Object code��Code list
	 * @return Ӌ���Object code��pairs
	 */
	private List<Code> calculateObject(List<Code> pairs) {
		
		return pairs;
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
