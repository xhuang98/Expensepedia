package uofthacks.expensepedia;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AppController {
    private static AppController instance = null;
    private static final String subscriptionKey = "5d2877f2eb5b418a8924292a56188d01";
    private static final String uriBase =
            "https://eastus.api.cognitive.microsoft.com/vision/v2.0/ocr";

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

    public JSONObject imageRead(String path) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            HttpPost request = new HttpPost(uri);

            request.setHeader("Content-Type", "applications/octet-stream");

            File file = new File(path);
            FileEntity requestEntity = new FileEntity(file, "image/png");

            request.setEntity(requestEntity);

            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                return json;
//                System.out.println("REST Response:\n");
//                System.out.println(json.toString(2));
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
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
