package uofthacks.expensepedia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntityHC4;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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

    public AppController(){}

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

    public void addItemsToCategory(List<Item> itemList, String category) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        for (int i = 0; i < itemList.size(); i++) {
            Map<String, String> map = new HashMap<>();
            map.put(category, itemList.get(i).name);

            db.collection("categories").add(map);
        }
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

//        boolean dateExists = false;
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

    public JSONObject imageRead(Bitmap bmp) {
        System.out.println("BYTES");
        System.out.println(bmp.getByteCount());

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 0, stream);

        System.out.println("DIMENSIONS");
        System.out.println(bmp.getHeight());
        System.out.println(bmp.getWidth());

        byte[] byteArray = stream.toByteArray();
        //bmp.recycle();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);
            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            System.out.println(uri.toString() + "URI");
            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);

            //ByteArrayEntityHC4 requestEntity = new ByteArrayEntityHC4(byteArray);



            request.setEntity(BitmapFactory.decodeByteArray(byteArray,0,byteArray.length));

            request.setEntity(reqEntity);
            System.out.println("HERE 8");

            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            System.out.println("HERE 9");
            HttpEntity entity = response.getEntity();
            System.out.println("RESULT");
            System.out.println(response.getEntity());



/*            HttpClient httpclient;
            HttpPost httpPost;
            ArrayList<NameValuePair> postParameters;
            httpclient = new DefaultHttpClient();
            httpPost = new HttpPost(uri);


            postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("Content-Type", "application/octet-stream"));
            postParameters.add(new BasicNameValuePair("Ocp-Apim-Subscription-Key", subscriptionKey));

            httpPost.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));

            HttpResponse response = httpclient.execute(httpPost);
            System.out.println("SUCCCESS");
            HttpEntity entity = response.getEntity();*/

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                return json;

            }

        } catch (IOException e) {
            System.out.println("IOEXception");
            System.out.println(e.getCause());
            System.err.println(e.getMessage());
        } catch (URISyntaxException e) {
            System.out.println("k");
        } catch (JSONException e) {
            System.out.println("lol");
        }

        System.out.println("wtf");
        return null;

    }


    public JSONObject fileImageRead(File file) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().build();

        try {
            URIBuilder uriBuilder = new URIBuilder(uriBase);

            uriBuilder.setParameter("language", "unk");
            uriBuilder.setParameter("detectOrientation", "true");

            // Request parameters.
            URI uri = uriBuilder.build();
            System.out.println("HERE 4");
            HttpPost request = new HttpPost(uri);
            System.out.println("HERE 5");
            request.setHeader("Content-Type", "applications/octet-stream");
            request.setHeader("Ocp-Apim-Subscription-Key", subscriptionKey);
            System.out.println("HERE 6");
//            File file = new File(path);

            FileEntity requestEntity = new FileEntity(file, "image/png");
            request.setEntity(requestEntity);
            System.out.println("HERE 8");



            // Call the REST API method and get the response entity.
            HttpResponse response = httpClient.execute(request);
            System.out.println("HERE 9");
            HttpEntity entity = response.getEntity();
            System.out.println("RESULT");
            System.out.println(response.getEntity());

            if (entity != null) {
                // Format and display the JSON response.
                String jsonString = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonString);
                return json;

            }

        } catch (IOException e) {
            System.out.println(e.getCause());
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

public Map<String, String> getCategories(){ // TODO: Access database
    return null;
}

public void setCategories(Map<String, String> knownCat){
    Map<String, String> newMap = getCategories();
    // union
    newMap.putAll(knownCat);
    //TODO: put back in database
}


public ArrayList<Item> categorize(Map<String, Double> purchases){
    Map<String, Double> knownGoods = new HashMap<>();
    ArrayList<Item> unknownGoods = new ArrayList<>();
    Map<String, String> categories = getCategories();
    for (String purchase: purchases.keySet()){
        if(categories.containsKey(purchase)){
            knownGoods.put(purchase, purchases.get(purchase));
        }
        else{
            unknownGoods.add(new Item(purchase, purchases.get(purchase)));
        }
    }
    updateData(knownGoods);
    return unknownGoods;
//        ArrayList<Item> uncertains = new ArrayList<>();
//        Map<String, Double> certains = new HashMap<>();
//        //try{
//            for (String purchase : purchases.keySet()) {
////                JSONObject prediction;
////                String predCategory;
////                if(prediction.get("name").matches("/Food & Drink")){
////                    predCategory = "Food";
////                }
////                else if(prediction.get("name").matches("/Shopping/Apparel")){
////                    predCategory = "Clothing";
////                }
////                else if(prediction.get("name").matches("/Arts & Entertainment")){
////                    predCategory = "Entertainment";
////                }
////                else{
////                    predCategory = "Other";
////                }
////
////                if(prediction.getDouble("confidence") > 0.8){
////                    certains.put(purchase, purchases.get(purchase));
////                }
////                else{
////                    Item uncertainItem = new Item(purchase, purchases.get(purchase), predCategory);
////                    uncertains.add(uncertainItem);
////                }
//
//            }
//            //if(!certains.isEmpty()){
//                updateData(certains);
//            //}
//        //}catch (JSONException e){
//            // LOL
//        //}
//        return uncertains;
    }

//    public ArrayList<Item> categorize(Map<String, Double> purchases){
//        ArrayList<Item> uncertains = new ArrayList<>();
//        Map<String, Double> certains = new HashMap<>();
//        try{
//            for (String purchase : purchases.keySet()) {
//                JSONObject prediction;
//                String predCategory;
//                if(prediction.get("name").matches("/Food & Drink")){
//                    predCategory = "Food";
//                }
//                else if(prediction.get("name").matches("/Shopping/Apparel")){
//                    predCategory = "Clothing";
//                }
//                else if(prediction.get("name").matches("/Arts & Entertainment")){
//                    predCategory = "Entertainment";
//                }
//                else{
//                    predCategory = "Other";
//                }
//
//                if(prediction.getDouble("confidence") > 0.8){
//                    certains.put(purchase, purchases.get(purchase));
//                }
//                else{
//                    Item uncertainItem = new Item(purchase, purchases.get(purchase), predCategory);
//                    uncertains.add(uncertainItem);
//                }
//            }
//            if(!certains.isEmpty()){
//                updateData(certains);
//            }
//        }catch (JSONException e){
//            // LOL
//        }
//        return uncertains;
//    }

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
                        price = Double.parseDouble(text.subSequence(1, text.length()).toString().replaceAll("\\s+",""));
                    }
                    else{
                        price = Double.parseDouble(text.replaceAll("\\s+",""));
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
