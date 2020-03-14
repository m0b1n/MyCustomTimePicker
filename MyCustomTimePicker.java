import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.infocoil.flamingo.R;

import java.util.Locale;

public class MyCustomTimePicker
        extends View
        implements View.OnTouchListener
{
    Paint mPaint, otherPaint, outerPaint, mTextPaint;

    private int   radius = 300;
    private float sweep  = 10;

    private float centerH;
    private float centerW;

    private int m, h;
    private String hour, minute = "00";

    private Paint paint1;
    private Paint paint;
    private RectF rectF = new RectF();

    private Bitmap thumbBitmap;

    public MyCustomTimePicker( Context context,
                   @Nullable AttributeSet attrs )
    {
        super( context,
               attrs );
        init( context );
    }

    public MyCustomTimePicker( Context context )
    {
        super( context );
        init( context );
    }

    @Override
    protected void onLayout( boolean changed,
                             int left,
                             int top,
                             int right,
                             int bottom )
    {
        super.onLayout( changed,
                        left,
                        top,
                        right,
                        bottom );

        centerW = ( right - left ) / 2;
        centerH = ( bottom - top ) / 2;

        float left1   = centerW - radius;
        float top1    = centerH - radius;
        float right1  = centerW + radius;
        float bottom1 = centerH + radius;

        rectF.set( left1,
                   top1,
                   right1,
                   bottom1 );
    }

    private void init( Context context )
    {
        this.setOnTouchListener( this );

        mPaint = new Paint();
        mPaint.setAntiAlias( true );

        mPaint.setStyle( Paint.Style.STROKE );
        mPaint.setColor( Color.BLUE );
        mPaint.setStrokeWidth( 5 );

        mTextPaint = new Paint( Paint.LINEAR_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG );
        mTextPaint.setColor( Color.BLACK );
        mTextPaint.setTextSize( pxFromDp( context,
                                          24 ) );

        otherPaint = new Paint();

        outerPaint = new Paint();
        outerPaint.setStyle( Paint.Style.FILL );
        outerPaint.setColor( Color.YELLOW );

        paint1 = new Paint();
        paint1.setAntiAlias( true );
        paint1.setColor( Color.GRAY );
        paint1.setStrokeWidth( 85 );
        paint1.setStrokeCap( Paint.Cap.ROUND );
        paint1.setStyle( Paint.Style.STROKE );

        paint = new Paint();
        paint.setAntiAlias( true );

        int[] colors = {
                0xFFFFFF88, // yellow
                0xFF0088FF, // blue
                0xFF000000, // black
                0xFFFFFF88  // yellow
        };

        float[] positions = { 0.0f, 0.33f, 0.66f, 1.0f };

        paint.setShader( new SweepGradient( centerW,
                                            centerH,
                                            colors,
                                            null ) );
        paint.setStrokeWidth( 65 );
        paint.setStrokeCap( Paint.Cap.ROUND );
        paint.setStyle( Paint.Style.STROKE );

        thumbBitmap = BitmapFactory.decodeResource( getContext().getResources(),
                                                    R.drawable.drag );

        otherPaint.setColor( Color.BLACK );
        otherPaint.setStyle( Paint.Style.FILL );
        otherPaint.setTextSize( 60 );
        Typeface typeface = ResourcesCompat.getFont( getContext(),
                                                     R.font.iransans );
        otherPaint.setTypeface( typeface );
    }

    @Override
    protected void onDraw( Canvas canvas )
    {
        super.onDraw( canvas );

        //background
        canvas.drawArc( rectF,
                        270,
                        360,
                        false,
                        paint1 );
        //foreground
        canvas.drawArc( rectF,
                        270,
                        sweep,
                        false,
                        paint );

        Point thumbPoint = calculatePointOnArc( ( int ) centerW,
                                                ( int ) centerH,
                                                radius,
                                                270 + sweep );

        thumbPoint.x = thumbPoint.x - ( thumbBitmap.getWidth() / 2 );
        thumbPoint.y = thumbPoint.y - ( thumbBitmap.getHeight() / 2 );

        canvas.drawBitmap( thumbBitmap,
                           thumbPoint.x,
                           thumbPoint.y,
                           otherPaint );

        canvas.drawText( String.format( Locale.getDefault(),
                                        "%d:%s",
                                        h,
                                        minute ),
                         centerW - 50,
                         centerH,
                         otherPaint );
    }

    public static float pxFromDp( final Context context,
                                  final float dp )
    {
        return dp * context.getResources()
                           .getDisplayMetrics().density;
    }

    @Override
    public boolean onTouch( View v,
                            MotionEvent event )
    {
        final int X1 = ( int ) event.getX();
        final int Y1 = ( int ) event.getY();

        Point A = new Point( ( int ) centerW,
                             ( int ) centerH );
        Point B = new Point( X1,
                             Y1 );
        Point C = new Point( ( int ) centerW,
                             ( int ) centerH - radius );
        double AB;
        double AC;
        double BC;
        AB = Math.sqrt( Math.pow( B.x - A.x,
                                  2 ) + Math.pow( B.y - A.y,
                                                  2 ) );
        AC = Math.sqrt( Math.pow( C.x - A.x,
                                  2 ) + Math.pow( C.y - A.y,
                                                  2 ) );
        BC = Math.sqrt( Math.pow( C.x - B.x,
                                  2 ) + Math.pow( C.y - B.y,
                                                  2 ) );
        double ratio = ( AB * AB + AC * AC - BC * BC ) / ( 2 * AC * AB );
        double degre = Math.acos( ratio ) * ( 180 / Math.PI );

        if ( X1 < centerW )
        {
            degre = 360 - degre;
        }

        double nn  = ( int ) ( degre / 1.25 );
        double m11 = nn * 1.25;

        int    ratePerDegree = 4;
        double as            = m11 * ratePerDegree;
        double hour          = ( int ) as / 60;
        double min           = as % 60;

        h = ( int ) hour;
        m = ( int ) min;

        if ( m < 10 )
        {
            minute = "0" + m;
        }
        else
        {
            minute = Integer.toString( m );
        }

        switch ( event.getAction() & MotionEvent.ACTION_MASK )
        {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                double n = ( int ) ( degre / 1.25 );
                double m1 = n * 1.25;

                sweep = ( float ) m1;
                this.invalidate();
                break;
        }
        return true;
    }

    public void proc( int x,
                      int y )
    {
        Point A = new Point( ( int ) centerW,
                             ( int ) centerH );
        Point B = new Point( x,
                             y );
        Point C = new Point( ( int ) centerW,
                             ( int ) centerH - radius );
        double AB;
        double AC;
        double BC;
        AB = Math.sqrt( Math.pow( B.x - A.x,
                                  2 ) + Math.pow( B.y - A.y,
                                                  2 ) );
        AC = Math.sqrt( Math.pow( C.x - A.x,
                                  2 ) + Math.pow( C.y - A.y,
                                                  2 ) );
        BC = Math.sqrt( Math.pow( C.x - B.x,
                                  2 ) + Math.pow( C.y - B.y,
                                                  2 ) );
        double ratio = ( AB * AB + AC * AC - BC * BC ) / ( 2 * AC * AB );
        double degre = Math.acos( ratio ) * ( 180 / Math.PI );

        if ( x < centerW )
        {
            degre = 360 - degre;
        }

        sweep = ( float ) degre;
        this.invalidate();


    }

    @Override
    public boolean performClick()
    {
        return super.performClick();
    }

    private Point calculatePointOnArc( int circleCeX,
                                       int circleCeY,
                                       int circleRadius,
                                       float endAngle )
    {
        Point  point          = new Point();
        double endAngleRadian = endAngle * ( Math.PI / 180 );

        int pointX = ( int ) Math.round( ( circleCeX + circleRadius * Math.cos( endAngleRadian ) ) );
        int pointY = ( int ) Math.round( ( circleCeY + circleRadius * Math.sin( endAngleRadian ) ) );

        point.x = pointX;
        point.y = pointY;

        return point;
    }

    public int getM()
    {
        return m;
    }

    public int getH()
    {
        return h;
    }

    public String getMinute()
    {
        return minute;
    }
}