package ccdemon.evaluation.util;

import java.util.ArrayList;

public class DataAnalyzer {

	public static void main(String[] args){
		//manual enter
//		String start_times = "xx:xx,yy:yy,aa:aa,bb:bb,pp:pp,qq:qq"; //the last one is the end time
//		String starts_times = "14:17,14:20,14:24,14:26,14:29,14:32,14:37"; //fukun
//		String start_times = "14:05,14:06,14:11,14:14,14:15,14:22,14:30"; //chaining
//		String start_times = "13:55,13:58,14:03,14:05,14:08,14:18,14:38"; //liuwenjian
//		String start_times = "13:57,14:02,14:11,14:13,14:15,14:19,14:24,14:29,14:35"; //liuwenguang
//		String start_times = "13:58,14:01,14:05,14:09,14:11,14:15,14:32"; //gaodongjing
//		String start_times = "13:54,13:58,14:04,14:06,14:08,14:23,14:33"; //huaxiajin
		String start_times = "14:07,14:10,14:24,14:30,14:32,14:36,14:43"; //liwenqi
		//datas
		String each_configuration_time = "10414ms,3099ms,3340ms,1601ms,1715ms,3917ms,1193ms,232ms,186ms,164ms,334ms,28884ms,12006ms,11359ms,942ms,3601ms,463ms,4284ms,10899ms,29125ms,452ms,347ms,229ms,457ms,921ms,8924ms,997ms,2993ms,547ms,3013ms,330ms,618ms,3716ms,4805ms,391ms,514ms,331ms,210ms,412ms,12254ms,317ms,475ms,206ms,198ms,101397ms,1241ms,634ms,279ms,410ms,208ms,396ms,4823ms,315ms,3917ms,2009ms,7840ms,30514ms,3133ms,9950ms,604ms,114ms,115ms,98ms,142ms,275ms,216ms,206ms,179ms,811ms,307ms,357ms,371ms,17878ms,4346ms,712ms,168ms,139ms,161ms,154ms,149ms,152ms,156ms,159ms,406ms,189ms,325ms,969ms,1913ms,22838ms,6089ms,13830ms,1905ms,2686ms,5827ms,11177ms,2604ms,1857ms,1115ms,2786ms,9460ms,3367ms,403ms,318ms,190ms,388ms,466ms,622ms,558ms,";
		String each_go_next_time = "14:08:26:261,14:08:36:717,14:08:39:924,14:08:43:321,14:08:44:989,14:08:46:745,14:11:15:746,14:11:19:705,14:11:20:955,14:11:21:231,14:11:21:443,14:11:21:640,14:11:21:994,14:11:50:893,14:12:14:304,14:12:15:278,14:12:18:904,14:12:19:393,14:12:23:701,14:13:03:765,14:13:04:263,14:13:04:625,14:13:04:878,14:13:05:351,14:13:06:286,14:13:16:221,14:13:19:247,14:13:19:824,14:13:22:850,14:13:23:212,14:13:23:845,14:13:27:577,14:13:32:395,14:13:32:822,14:13:33:348,14:13:33:706,14:13:33:934,14:13:34:360,14:13:46:626,14:13:46:966,14:13:47:454,14:13:47:682,14:13:47:894,14:15:29:306,14:15:30:572,14:15:31:229,14:15:31:521,14:15:31:955,14:15:32:182,14:15:32:593,14:15:37:433,14:15:37:779,14:26:11:342,14:26:52:399,14:27:02:383,14:27:03:72,14:27:03:252,14:27:03:441,14:27:03:604,14:27:03:802,14:27:04:133,14:27:04:392,14:27:04:635,14:27:04:856,14:27:05:726,14:27:06:65,14:27:06:444,14:27:06:839,14:27:24:763,14:27:29:157,14:27:29:902,14:27:30:90,14:27:30:255,14:27:30:435,14:27:30:608,14:27:30:772,14:27:30:944,14:27:31:125,14:27:31:344,14:27:31:778,14:27:31:983,14:27:32:323,14:27:33:343,14:27:35:273,14:27:58:143,14:28:04:251,14:28:18:118,14:28:20:43,14:28:22:746,14:28:28:586,14:28:39:795,14:28:42:441,14:28:44:342,14:28:45:476,14:28:48:276,14:30:10:934,14:30:20:423,14:30:23:854,14:30:24:303,14:30:24:659,14:30:24:879,14:30:25:290,14:30:25:794,14:30:26:436,14:30:27:09,14:32:28:193,14:34:19:342,14:38:13:313,14:38:42:705,";
		String each_go_prev_time = "14:12:02:927,";
		String each_manual_edit_time = "";
		//helper
		String each_focus_lost_time = "14:08:36:718,14:08:39:925,14:08:43:322,14:08:44:989,14:08:46:745,14:11:19:706,14:11:20:955,14:11:21:232,14:11:21:444,14:11:21:640,14:11:21:995,14:11:50:893,14:12:02:927,14:12:14:304,14:12:15:278,14:12:18:904,14:12:19:393,14:12:23:701,14:12:34:639,14:13:03:766,14:13:04:264,14:13:04:626,14:13:04:879,14:13:05:351,14:13:06:286,14:13:15:222,14:13:16:221,14:13:19:247,14:13:19:824,14:13:22:850,14:13:23:213,14:13:23:845,14:13:27:577,14:13:32:395,14:13:32:822,14:13:33:349,14:13:33:706,14:13:33:934,14:13:34:360,14:13:46:626,14:13:46:966,14:13:47:455,14:13:47:682,14:13:47:894,14:15:29:306,14:15:30:573,14:15:31:229,14:15:31:521,14:15:31:955,14:15:32:183,14:15:32:593,14:15:37:433,14:15:37:779,14:15:41:708,14:15:43:719,14:15:51:561,14:16:22:77,14:16:25:212,14:27:02:384,14:27:03:72,14:27:03:252,14:27:03:441,14:27:03:604,14:27:03:802,14:27:04:133,14:27:04:392,14:27:04:636,14:27:04:857,14:27:05:727,14:27:06:66,14:27:06:444,14:27:06:839,14:27:24:763,14:27:29:157,14:27:29:902,14:27:30:90,14:27:30:255,14:27:30:435,14:27:30:608,14:27:30:772,14:27:30:944,14:27:31:126,14:27:31:344,14:27:31:779,14:27:31:983,14:27:32:323,14:27:33:343,14:27:35:274,14:27:58:143,14:28:04:251,14:28:18:118,14:28:20:43,14:28:22:746,14:28:28:586,14:28:39:795,14:28:42:441,14:28:44:343,14:28:45:477,14:28:48:276,14:30:20:424,14:30:23:854,14:30:24:303,14:30:24:659,14:30:24:879,14:30:25:290,14:30:25:795,14:30:26:436,14:30:27:10,";
		//count statistics
		double[] count_avg_config_time = new double[6];
//		int[] helpLWG = new int[8];
		int[] count_go_next_time = new int[6];
		int[] count_go_prev_time = new int[6];
		int[] count_manual_edit_time = new int[6];
		
		String[] focusLostTimesString = each_focus_lost_time.split(",");
		long[] focusLostTimes = new long[focusLostTimesString.length];
		for(int i = 0; i < focusLostTimes.length; i++){
			String[] splitTime = focusLostTimesString[i].split(":");			
			focusLostTimes[i] = Long.parseLong(splitTime[3]) + Long.parseLong(splitTime[2])*1000 
					+ Long.parseLong(splitTime[1])*1000*60 + Long.parseLong(splitTime[0])*1000*60*60;
		}
		
		String[] startTimesString = start_times.split(",");
		long[] startTimes = new long[startTimesString.length];
		for(int i = 0; i < startTimes.length; i++){
			String[] splitTime = startTimesString[i].split(":");	
			startTimes[i] = Long.parseLong(splitTime[1])*1000*60 + Long.parseLong(splitTime[0])*1000*60*60;
		}

		
		String[] configurationTimesString = each_configuration_time.split("ms,");
		int[] configurationTimes = new int[configurationTimesString.length];
		for(int i = 0; i < configurationTimes.length; i++){
			configurationTimes[i] = Integer.parseInt(configurationTimesString[i]);
		}
		
		String[] goNextTimesString = each_go_next_time.split(",");
		long[] goNextTimes = new long[goNextTimesString.length];
		for(int i = 0; i < goNextTimes.length; i++){
			String[] splitTime = goNextTimesString[i].split(":");			
			goNextTimes[i] = Long.parseLong(splitTime[3]) + Long.parseLong(splitTime[2])*1000 
					+ Long.parseLong(splitTime[1])*1000*60 + Long.parseLong(splitTime[0])*1000*60*60;
		}
		
		String[] goPrevTimesString = each_go_prev_time.split(",");
		long[] goPrevTimes = new long[goPrevTimesString.length];
		if(each_go_prev_time.length() > 0){
			for(int i = 0; i < goPrevTimes.length; i++){
				String[] splitTime = goPrevTimesString[i].split(":");			
				goPrevTimes[i] = Long.parseLong(splitTime[3]) + Long.parseLong(splitTime[2])*1000 
						+ Long.parseLong(splitTime[1])*1000*60 + Long.parseLong(splitTime[0])*1000*60*60;
			}
		}
		
		String[] manualEditTimesString = each_manual_edit_time.split(",");
		long[] manualEditTimes = new long[manualEditTimesString.length];
		if(each_manual_edit_time.length() > 0){
			for(int i = 0; i < manualEditTimes.length; i++){
				String[] splitTime = manualEditTimesString[i].split(":");			
				manualEditTimes[i] = Long.parseLong(splitTime[3]) + Long.parseLong(splitTime[2])*1000 
						+ Long.parseLong(splitTime[1])*1000*60 + Long.parseLong(splitTime[0])*1000*60*60;
			}
		}
		
		
		for(int i = 0; i < startTimes.length - 1; i++){
			//calculate average configuration time
			int left = 0; int right = 0;
			while(left < focusLostTimes.length && focusLostTimes[left] < startTimes[i]){
				left++;
			}
			while(right < focusLostTimes.length && focusLostTimes[right] < startTimes[i+1]){
				right++;
			}
			right--;
			ArrayList<Double> tmp = new ArrayList<Double>();
			for(int x = left; x <= right; x++){
				tmp.add((double) configurationTimes[x]);
//				helpLWG[i]++;
			}
			count_avg_config_time[i] = calculateScoreMean(tmp);
			
			//count go next time
			left = 0; right = 0;
			while(left < goNextTimes.length && goNextTimes[left] < startTimes[i]){
				left++;
			}
			while(right < goNextTimes.length && goNextTimes[right] < startTimes[i+1]){
				right++;
			}
			right--;
			count_go_next_time[i] = right - left + 1;
			
			//count go prev time
			if(goPrevTimes.length > 0){
				left = 0; right = 0;
				while(left < goPrevTimes.length && goPrevTimes[left] < startTimes[i]){
					left++;
				}
				while(right < goPrevTimes.length && goPrevTimes[right] < startTimes[i+1]){
					right++;
				}
				right--;
				count_go_prev_time[i] = right - left + 1;
			}
			
			//count manual edit time
			if(manualEditTimes.length > 0){
				left = 0; right = 0;
				while(left < manualEditTimes.length && manualEditTimes[left] < startTimes[i]){
					left++;
				}
				while(right < manualEditTimes.length && manualEditTimes[right] < startTimes[i+1]){
					right++;
				}
				right--;
				count_manual_edit_time[i] = right - left + 1;
			}
		}
		

		for(int i = 0; i < startTimes.length - 1; i++){
			System.out.println("Task" + (i+1) + ":");
			System.out.println("count_avg_config_time: " + count_avg_config_time[i]);
//			System.out.println("helpLWG: " + helpLWG[i]);
			System.out.println("count_go_next_time: " + count_go_next_time[i]);
			System.out.println("count_go_prev_time: " + count_go_prev_time[i]);
			System.out.println("count_manual_edit_time: " + count_manual_edit_time[i]);
		}
	}
	
	private static double calculateScoreMean(ArrayList<Double> rawScores) {
		double scoreAll = 0.0;
		for (Double score : rawScores) {
			scoreAll += score;
		}
		return scoreAll/rawScores.size();
	}
}
