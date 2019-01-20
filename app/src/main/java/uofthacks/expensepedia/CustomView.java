package uofthacks.expensepedia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class CustomView extends View {


    public CustomView(Context context) {
        super(context);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        init(null);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(attrs);
    }

    public CustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(attrs);
    }

    private void init(@Nullable AttributeSet set){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        paint.setColor(getResources().getColor(R.color.organu));
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        Rect rect3 = new Rect();
        Rect rect4 = new Rect();




        rect.top = rect.bottom + 600; //the larger the number, the smaller the bar
        rect.left = 118;
        rect.right = rect.left + 156;
        rect.bottom = 2200;


        rect2.top = rect2.bottom + 900;
        rect2.left = 347;
        rect2.right = rect2.left + 156;
        rect2.bottom = 2200;


        rect3.top = rect3.bottom + 400;
        rect3.left = 576;
        rect3.right = rect3.left + 156;
        rect3.bottom = 2200;


        rect4.top = rect4.bottom + 1200;
        rect4.left = 805;
        rect4.right = rect4.left + 156;
        rect4.bottom = 2200;

        canvas.drawRect(rect, paint);
        canvas.drawRect(rect2, paint);
        canvas.drawRect(rect3, paint);
        canvas.drawRect(rect4, paint);
        canvas.drawCircle(196, rect.top, 78, paint);
        canvas.drawCircle(425,rect2.top , 78, paint);
        canvas.drawCircle(654,rect3.top , 78, paint);
        canvas.drawCircle(883,rect4.top , 78, paint);
    }
}
