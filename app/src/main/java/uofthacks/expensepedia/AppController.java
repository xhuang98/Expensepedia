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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppController {
    private static AppController instance = null;

    private String[] categories =  new String[]{"Dining", "Clothing", "Entertainment", "Other"};

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
        // TODO: add data to database
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
