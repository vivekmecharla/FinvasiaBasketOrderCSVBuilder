package main.java;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class BuildCSV {

    public static final String INSTRUMENT = "instrument";
    public static final String PUT_EXIT_STRIKE = "put_exit_strike";
    public static final String PUT_ENTRY_STRIKE = "put_entry_strike";
    public static final String CALL_EXIT_STRIKE = "call_exit_strike";
    public static final String CALL_ENTRY_STRIKE = "call_entry_strike";
    public static final String EXPIRY = "expiry";
    public static final String QUANTITY = "quantity";
    public static final String ADJUST_HEDGE = "adjust_hedge";
    public static final String HEDGE_DISTANCE = "hedge_distance";
    public static final String NIFTY = "NIFTY";
    public static final String FINNIFTY = "FINNIFTY";
    public static final String BANKNIFTY = "BANKNIFTY";
    public static final String NFO_OPTIDX = "NFO,OPTIDX,";
    public static final String FA_74029_BUY_C_NRML_MKT_DAY_0 = ",FA74029,BUY,C,NRML,MKT,DAY,0,";
    public static final String FA_74029_SELL_C_NRML_MKT_DAY_0 = ",FA74029,SELL,C,NRML,MKT,DAY,0,";
    public static final String TRUE_5 = ",0,0,09:23,TRUE,5";

    public static void main(String[] args) {
        String inputFile = "input.txt";
        String outputFile = "output.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile)); BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
            String line;
            Map<String, String> tokenMap = new HashMap<>();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(":");
                tokenMap.put(tokens[0], tokens[1]);
            }

            String instrument = tokenMap.get(INSTRUMENT);
            String expiry = tokenMap.get(EXPIRY);
            String quantity = tokenMap.get(QUANTITY);
            String adjustHedge = tokenMap.get(ADJUST_HEDGE);
            int putExitStrikeToken = Integer.parseInt(tokenMap.get(PUT_EXIT_STRIKE));
            int putEntryStrikeToken = Integer.parseInt(tokenMap.get(PUT_ENTRY_STRIKE));
            int callExitStrikeToken = Integer.parseInt(tokenMap.get(CALL_EXIT_STRIKE));
            int callEntryStrikeToken = Integer.parseInt(tokenMap.get(CALL_ENTRY_STRIKE));
            int hedgeDistance = Integer.parseInt(tokenMap.get(HEDGE_DISTANCE));

            int strikeDifference;
            switch (instrument) {
                case BANKNIFTY:
                    strikeDifference = 100;
                    break;
                case NIFTY:
                case FINNIFTY:
                default:
                    strikeDifference = 50;
                    break;
            }

            int hedgeDistanceInPoints = hedgeDistance * strikeDifference;
            int putExitHedgeStrike = putExitStrikeToken - hedgeDistanceInPoints;
            int putEntryHedgeStrike = putEntryStrikeToken - hedgeDistanceInPoints;
            int callExitHedgeStrike = callExitStrikeToken + hedgeDistanceInPoints;
            int callEntryHedgeStrike = callEntryStrikeToken + hedgeDistanceInPoints;


            String putExitStrike = NFO_OPTIDX + instrument + ",PE," + putExitStrikeToken + "," + expiry + FA_74029_BUY_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String putEntryHedge = NFO_OPTIDX + instrument + ",PE," + putEntryHedgeStrike + "," + expiry + FA_74029_BUY_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String putExitHedge = NFO_OPTIDX + instrument + ",PE," + putExitHedgeStrike + "," + expiry + FA_74029_SELL_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String putEntryStrike = NFO_OPTIDX + instrument + ",PE," + putEntryStrikeToken + "," + expiry + FA_74029_SELL_C_NRML_MKT_DAY_0 + quantity + TRUE_5;

            String callExitStrike = NFO_OPTIDX + instrument + ",CE," + callExitStrikeToken + "," + expiry + FA_74029_BUY_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String callEntryHedge = NFO_OPTIDX + instrument + ",CE," + callEntryHedgeStrike + "," + expiry + FA_74029_BUY_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String callExitHedge = NFO_OPTIDX + instrument + ",CE," + callExitHedgeStrike + "," + expiry + FA_74029_SELL_C_NRML_MKT_DAY_0 + quantity + TRUE_5;
            String callEntryStrike = NFO_OPTIDX + instrument + ",CE," + callEntryStrikeToken + "," + expiry + FA_74029_SELL_C_NRML_MKT_DAY_0 + quantity + TRUE_5;


            for (int i = 0; i < 4; i++) {
                bw.write(putExitStrike);
                bw.newLine();

                if (adjustHedge.equalsIgnoreCase("Y")) {
                    bw.write(putEntryHedge);
                    bw.newLine();
                }

                bw.write(callExitStrike);
                bw.newLine();

                if (adjustHedge.equalsIgnoreCase("Y")) {
                    bw.write(callEntryHedge);
                    bw.newLine();
                }
            }

            for (int i = 0; i < 4; i++) {
                if (adjustHedge.equalsIgnoreCase("Y")) {
                    bw.write(putExitHedge);
                    bw.newLine();
                }

                bw.write(putEntryStrike);
                bw.newLine();


                if (adjustHedge.equalsIgnoreCase("Y")) {
                    bw.write(callExitHedge);
                    bw.newLine();
                }

                bw.write(callEntryStrike);
                bw.newLine();
            }
            System.out.println("Conversion successful!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
