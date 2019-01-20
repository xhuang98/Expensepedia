package uofthacks.expensepedia;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

public class HistoryDataActivity extends AppCompatActivity {
    AppController controller = AppController.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);
        ImageButton butt = findViewById(R.id.maybutt);

        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HistoryDataActivity.this, MayActivity.class));
            }
        });
    }



    private void viewHistory(int month, int year){
        controller.changeDate(month, year);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
