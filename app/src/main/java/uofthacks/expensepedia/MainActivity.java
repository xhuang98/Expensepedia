package uofthacks.expensepedia;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String STATE_IMAGE = "image";
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALLERY = 2;

    private File output = null;
    private String mSelectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppController controller = AppController.getInstance();

        int month = controller.getMonthToView();
        int year = controller.getYearToView();

        Button button = findViewById(R.id.button);

/*        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference col = db.collection("stores");

        Map<String, Object> store = new HashMap<>();
        store.put("name", "Best Buy");

        db.collection("stores")
                .add(store)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        System.out.println("DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println("Error adding document");
                    }
                });*/


        File dir = getExternalFilesDir(Environment.DIRECTORY_DCIM);
        dir.mkdirs();
        output = new File(dir, "mCameraContent.jpeg");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Uri uri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getApplicationContext().getPackageName()
                //        + ".my.package.name.provider", output);

                Uri uri = FileProvider.getUriForFile(
                        getApplicationContext(),
                        getApplicationContext().getApplicationContext()
                                .getPackageName() + ".provider", output);

                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(cameraIntent, REQUEST_CAMERA);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_GALLERY || requestCode == REQUEST_CAMERA) {
                Uri uri = Uri.fromFile(output);

                mSelectedImageUri = uri.toString();

                // Fetch the Bitmap from the Uri.
                Bitmap selectedImage = fetchBitmapFromUri(uri);
                System.out.println("BITMAP");
                System.out.println(selectedImage);
            }
        }
    }

    /**
     * Fetches a bitmap image from the device given the image's uri.
     * @param imageUri Uri of the image on the device (either in the gallery or from the camera).
     * @return A Bitmap representation of the image on the device, correctly orientated.
     */
    private Bitmap fetchBitmapFromUri(Uri imageUri) {
        try {
            // Fetch the Bitmap from the Uri.
            Bitmap selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

            // Fetch the orientation of the Bitmap in storage.
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cursor = getContentResolver().query(imageUri, orientationColumn, null, null, null);
            int orientation = 0;
            if (cursor != null && cursor.moveToFirst()) {
                orientation = cursor.getInt(cursor.getColumnIndex(orientationColumn[0]));
            }
            if(cursor != null) {
                cursor.close();
            }

            // Rotate the bitmap with the found orientation.
            Matrix matrix = new Matrix();
            matrix.setRotate(orientation);
            selectedImage = Bitmap.createBitmap(selectedImage, 0, 0, selectedImage.getWidth(), selectedImage.getHeight(), matrix, true);

            return selectedImage;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
