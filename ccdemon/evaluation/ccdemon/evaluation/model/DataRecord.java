package ccdemon.evaluation.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DataRecord {
	
	public static int recordTime = 0;
	
	public static ArrayList<Long> focusIntervals = new ArrayList<Long>();
	public static int toNextTime = 0;
	public static ArrayList<String> toNextTimes = new ArrayList<String>();
	public static int toPrevTime = 0;
	public static ArrayList<String> toPrevTimes = new ArrayList<String>();
	
	public static ArrayList<String> manualEditTimes = new ArrayList<String>();
	public static int manualEditTime = 0;
	public static ArrayList<String> focusGainTimes = new ArrayList<String>();
	public static ArrayList<String> focusLostTimes = new ArrayList<String>();
	
	public static void clear(){
		focusIntervals = new ArrayList<Long>();
		toNextTime = 0;
		toNextTimes = new ArrayList<String>();
		toPrevTime = 0;
		toPrevTimes = new ArrayList<String>();
		manualEditTime = 0;
		manualEditTimes = new ArrayList<>();
		focusGainTimes = new ArrayList<String>();
		focusLostTimes = new ArrayList<String>();
	}
	
	public static void addTimeToTimes(ArrayList<String> times){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SS",Locale.SIMPLIFIED_CHINESE);
		String timeStr = sdf.format(new Date());
		times.add(timeStr);
	}
}
