package javabot.RaynorsRaiders;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.StringTokenizer;

public class CoreSupportGenome {
	int bloodFrequency;
		int pivotBF;
		int rangeBF;
	int responsePotential;
		int pivotRP;
		int rangeRP;
	int spread;
		int pivotS;
		int rangeS;
	int defensiveness;
		int pivotD;
		int rangeD;
	File genFile;
	Scanner genScanner;
	Writer genWriter;
	int seed;
	int phase;
	
	CoreSupportGenome() {
		pivotBF = 50;
		rangeBF = 50;
		pivotRP = 50;
		rangeRP = 50;
		pivotS = 50;
		rangeS = 50;
		pivotD = 50;
		rangeD = 50;
		seed = (int) (Math.random() * 100); // % 100
		phase = (int) (Math.random() * 100);
		GenomeOnline();
		GenomeLoadPrev();
		bloodFrequency = GenomeRandGen(pivotBF, rangeBF);
		responsePotential = GenomeRandGen(pivotRP, rangeRP);
		spread = GenomeRandGen(pivotS, rangeS);
		defensiveness = GenomeRandGen(pivotD, rangeD);
	}
	
	public void GenomeOnline() {
		genFile = new File("genomeStorage.log");
		try {
			genFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			genScanner = new Scanner(genFile);
		} catch (FileNotFoundException e) {
			genScanner = null;
		}
		try {
			genWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(genFile, true)));
		} catch (FileNotFoundException e) {
			genWriter = null;
		}
	}
	
	public void GenomeLoadPrev() {
		int won = 0, multiplier = 1;
		if (genFile == null)
			return;
		StringTokenizer tokenTastic;
		String toTokenize;
		while(genScanner.hasNextLine()) {
			toTokenize = genScanner.nextLine();
			tokenTastic = new StringTokenizer(toTokenize, ":");
			won = Integer.parseInt(tokenTastic.nextToken());
			if (won == 1)
				multiplier = 2;
			else
				multiplier = 1;
			pivotBF = (pivotBF + Integer.parseInt(tokenTastic.nextToken())) / 2;
			rangeBF = (rangeBF + Integer.parseInt(tokenTastic.nextToken())) / 2;
			pivotRP = (pivotRP + Integer.parseInt(tokenTastic.nextToken())) / 2;
			rangeRP = (rangeRP + Integer.parseInt(tokenTastic.nextToken())) / 2;
			pivotS = (pivotS + Integer.parseInt(tokenTastic.nextToken())) / 2;
			rangeS = (rangeS + Integer.parseInt(tokenTastic.nextToken())) / 2;
			pivotD = (pivotD + Integer.parseInt(tokenTastic.nextToken())) / 2;
			rangeD = (rangeD + Integer.parseInt(tokenTastic.nextToken())) / 2;
		}
		genScanner.close();
	}
	
	public void GenomeWriteNewEntry(boolean winner) {
		int won = 0;
		if(winner) {
			won = 1;
			rangeBF--;
			rangeRP--;
			rangeS--;
			rangeD--;
		}
		else {
			won = 0;
			rangeBF++;
			rangeRP++;
			rangeS++;
			rangeD++;
		}
		try {
			genWriter.write(
			won+":"+
			bloodFrequency+":"+
			rangeBF+":"+
			responsePotential+":"+
			rangeRP+":"+
			spread+":"+
			rangeS+":"+
			defensiveness+":"+
			rangeD+":"+
			"\n"
			 );
			genWriter.close();
		} catch (IOException e) {
			 //do nothing
		}
		System.out.println("Writing to gFile..."+
				won+":"+
			pivotBF+":"+
			rangeBF+":"+
			pivotRP+":"+
			rangeRP+":"+
			pivotS+":"+
			rangeS+":"+
			pivotD+":"+
			rangeD+":"
			 );
	}
	
	public int GenomeRandGen(int pivot, int range) {
		int toRet = (Math.abs(seed) % (range * 2)) + (pivot - range);

		if (toRet < 5)
			toRet = 5;
		if (toRet > 95)
			toRet = 95;
		
		seed += phase;
		return toRet;
	}
}