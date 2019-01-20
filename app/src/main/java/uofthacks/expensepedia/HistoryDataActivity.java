package uofthacks.expensepedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class HistoryDataActivity extends AppCompatActivity {
    AppController controller = AppController.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);
    }



    private void viewHistory(int month, int year){
        controller.changeDate(month, year);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
