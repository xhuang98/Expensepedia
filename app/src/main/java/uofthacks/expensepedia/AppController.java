package uofthacks.expensepedia;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppController {
    private static AppController instance = null;

    private AppController(){}

    public static AppController getInstance(){
        if (instance == null){
            instance = new AppController();
        }
        return instance;
    }

    /**
     * Returns a map of expense category to expense amount of specified month and year
     */
    public Map<String, Double> getData(int month, int year){
        Map<String, Double> result = new HashMap<String, Double>();
        // TODO: map expense type to expenses
        return result;
    }

    /**
     * Adds a map of expense category to expense amount of specified month and year
     */
    public void addData(Map<String, Double> data, int month, int year){
        // TODO: add data to database
    }

    public void updateData(Map<String, Double> newData){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        String dateFormatted = formatter.format(date);
        // Current date
        int year = Integer.parseInt(dateFormatted.substring(0, 3));
        int month = Integer.parseInt(dateFormatted.substring(5, 6));

        boolean dateExists = false; // TODO: determine whether year + month already exists in database
        if(dateExists){
            Map<String, Double> oldData = getData(month, year);
            for (String category: oldData.keySet()){
                // update data
                oldData.put(category, oldData.get(category) + newData.get(category));
            }
        }
        else {
            addData(newData, month, year);
        }
    }

    public JSONObject imageRead(String path){
        // TODO: convert image to JSON using API
        return null;
    }

    /**
     * Extracts purchased items and their prices from JSON object of texts in image
     */
    public Map<String, Double> extractInfo(JSONObject json){
        Map<String, Double> result = new HashMap<String, Double>();
        try {
            JSONArray lines = json.getJSONObject("recognitionResult").getJSONArray("lines");
            for(int i = 1; i < lines.length(); i++){
                Double price;
                String obj;
                JSONObject line = lines.getJSONObject(i);
                String text = line.getString("text");
                JSONArray words = line.getJSONArray("words");
                if (words.length() == 1 && text.matches("^\\$?\\s*(\\d\\s*)+\\.\\s*\\d\\s*\\d$")){
                    if(text.matches("^\\$\\s*(\\d\\s*)+\\.\\s*\\d\\s*\\d$")){
                        price = Double.parseDouble(text.subSequence(1, text.length()).toString());
                    }
                    else{
                        price = Double.parseDouble(text);
                    }
                    obj = lines.getJSONObject(i-1).getString("text");
                    result.put(obj, price);
                }
            }
        }
        catch (JSONException e){
            // uh oh
        }
        return result;
    }
}
