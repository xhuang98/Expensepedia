package uofthacks.expensepedia;

import android.provider.DocumentsContract;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

/*import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.entity.FileEntity;
import org.apache.http.util.EntityUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.HttpPost;*/
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppController {
    private static AppController instance = null;
    private static final String subscriptionKey = "5d2877f2eb5b418a8924292a56188d01";
    private static final String uriBase =
            "https://eastus.api.cognitive.microsoft.com/vision/v2.0/ocr";

    private String[] categories =  new String[]{"Dining", "Clothing", "Entertainment", "Other"};

    private AppController(){}

    public static AppController getInstance(){
        if (instance == null){
            instance = new AppController();
        }
        return instance;
    }

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    Date date = new Date(System.currentTimeMillis());
    String dateFormatted = formatter.format(date);
    // Current date
    private int monthToView = Integer.parseInt(dateFormatted.substring(0, 3));
    private int yearToView = Integer.parseInt(dateFormatted.substring(5, 6));

    public int getMonthToView(){
        return monthToView;
    }

    public int getYearToView() {
        return yearToView;
    }

    public void changeDate(int month, int year){
        monthToView = month;
        yearToView = year;
    }

    /**
     * Returns a map of expense category to expense amount of specified month and year
     */
    public Map<String, Double> getData(int month, int year){
        Map<String, Double> result = new HashMap<String, Double>();
        // map expense type to expenses
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("purchases").document(month + ", " + year);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Document exists");
                        for (String category: categories) {
                            // TODO: use the data somehow
                            //map.put(category, document.get(category));
                        }
                    } else {
                        System.out.println("Document does not exist");
                    }
                } else {
                    System.out.println("Task failed");
                }
            }
        });

        return result;
    }
    /**
     * Adds a map of expense category to expense amount of specified month and year
     */
    public void addData(final Map<String, Double> data, final int month, final int year){
        // add data to database
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("purchases").document(month + ", " + year);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        System.out.println("Document exists");
                        Map<String, Double> updatedMap = new HashMap<>();
                        for (String category: categories) {
                            updatedMap.put(category, (Double) document.get(category) + data.get(category));
                        }

                        db.collection("purchases").document(month + ", " + year)
                                .set(updatedMap);
                    } else {
                        System.out.println("Document does not exist");
                        db.collection("purchases").document(month + ", " + year)
                                .set(data);
                    }
                } else {
                    System.out.println("Error getting document");
                }
            }
        });
    }

    public void updateData(Map<String, Double> newData){
        // Current date
        int year = Integer.parseInt(dateFormatted.substring(0, 3));
        int month = Integer.parseInt(dateFormatted.substring(5, 6));

//        boolean dateExists = false; // TODO: determine whether year + month already exists in database
//        if(dateExists){
//            Map<String, Double> oldData = getData(month, year);
//            for (String category: oldData.keySet()){
//                // update data
//                oldData.put(category, oldData.get(category) + newData.get(category));
//            }
//        }
//        else {
            addData(newData, month, year);
        //}
    }

/*    public JSONObject imageRead(String path) {
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
    }*/
    public ArrayList<Item> categorize(Map<String, Double> purchases){
        ArrayList<Item> uncertains = new ArrayList<>();
        Map<String, Double> certains = new HashMap<>();
        try{
            for (String purchase : purchases.keySet()) {
                // TODO: use classifyText() from Google
                JSONObject prediction;
                String predCategory;
                if(prediction.get("name").matches("/Food & Drink")){
                    predCategory = "Food";
                }
                else if(prediction.get("name").matches("/Shopping/Apparel")){
                    predCategory = "Clothing";
                }
                else if(prediction.get("name").matches("/Arts & Entertainment")){
                    predCategory = "Entertainment";
                }
                else{
                    predCategory = "Other";
                }

                if(prediction.getDouble("confidence") > 0.8){
                    certains.put(purchase, purchases.get(purchase));
                }
                else{
                    Item uncertainItem = new Item(purchase, purchases.get(purchase), predCategory);
                    uncertains.add(uncertainItem);
                }
            }
            if(!certains.isEmpty()){
                updateData(certains);
            }
        }catch (JSONException e){
            // LOL
        }
        return uncertains;
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
