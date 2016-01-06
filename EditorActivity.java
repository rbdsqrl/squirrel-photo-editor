package com.myapp.narendran.squirrelphotoeditor;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Narendran on 05-01-2016.
 */
 
 
public class EditorActivity extends Activity {

    private static ImageView ivPhoto;
    private File sdCard;
    private static Bitmap result;
    private File dir;
    Bitmap bitmapGrey;
    private static Bitmap photo;
    public static final int COLOR_MAX = 0xFF;
    public static final int PICK_IMAGE_GALLERY = 100;
    public static final int PICK_IMAGE_CAMERA =199;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_editor);
        sdCard = Environment.getExternalStorageDirectory();
        dir = new File (sdCard.getAbsolutePath() + "/Sqrl-edtr-saved");
        if(!dir.exists()) {
            dir.mkdirs();
        }

        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        ivPhoto.buildDrawingCache();
        result = BitmapFactory.decodeResource(getResources(),R.drawable.fruit);

    }


    public void PhotoEditor(View view){
        switch(view.getId()){
            case R.id.ibSave:
                if(saveImage())
                    Toast.makeText(getBaseContext(), "File saved!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.ibCamera:

                Intent intentCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                startActivityForResult(intentCamera, PICK_IMAGE_CAMERA);
                break;
            case R.id.ibGallery:
                if (Build.VERSION.SDK_INT <19){
                    Intent intent = new Intent();
                    intent.setType("image/jpeg");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, PICK_IMAGE_GALLERY);
                } else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/jpeg");
                    startActivityForResult(intent, PICK_IMAGE_GALLERY);
                }
                break;
            case R.id.btnSepia:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    Drawable d = new BitmapDrawable(getResources(), bitmapGrey);
                    Drawable res = toSepia(d);
                    ivPhoto.setImageDrawable(toSepia(res));
                    Toast.makeText(getBaseContext(),"Wait while file is generted for saving",Toast.LENGTH_SHORT);
                    result = toSephia(bitmapGrey);}
                else{
                    Drawable d = new BitmapDrawable(getResources(), photo);
                    Drawable res = toSepia(d);
                    ivPhoto.setImageDrawable(toSepia(res));
                    Toast.makeText(getBaseContext(), "Wait while file is generted for saving", Toast.LENGTH_SHORT);
                    result = toSephia(photo);}
                break;
            case R.id.btnGrey:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    ivPhoto.setImageBitmap(toGrayScale(bitmapGrey));
                    result = toGrayScale(bitmapGrey);}
                else{
                    ivPhoto.setImageBitmap(toGrayScale(photo));
                    result = toGrayScale(photo);}
                break;
            case R.id.btnCartoon:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    ivPhoto.setImageBitmap(toCartoon(bitmapGrey));
                    result = toCartoon(bitmapGrey);}
                else{
                    ivPhoto.setImageBitmap(toCartoon(photo));
                    result = toCartoon(photo);}

                break;


            case R.id.btnFlea:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    ivPhoto.setImageBitmap(applyFleaEffect(bitmapGrey));
                    result = applyFleaEffect(bitmapGrey);
                }
                else{
                    ivPhoto.setImageBitmap(applyFleaEffect(photo));
                    result = applyFleaEffect(photo);}
                break;
            case R.id.btnSnow:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    ivPhoto.setImageBitmap(applySnowEffect(bitmapGrey));
                    result = applySnowEffect(bitmapGrey);
                }
                else{
                    ivPhoto.setImageBitmap(applySnowEffect(photo));
                    result = applySnowEffect(photo);}
                break;
            case R.id.btnReflect:
                if(photo == null){
                    ivPhoto.buildDrawingCache();
                    bitmapGrey = ivPhoto.getDrawingCache();
                    ivPhoto.setImageBitmap(applyReflection(bitmapGrey));
                    result = applyReflection(bitmapGrey);
                }
                else{
                    result = applyReflection(photo);
                    ivPhoto.setImageBitmap(applyReflection(photo));}
                break;

        }
    }

    private boolean saveImage() {


        File image = new File(dir, "sqrl-image"+System.currentTimeMillis()+".png");

        FileOutputStream outStream;
        try {

            outStream = new FileOutputStream(image);
            result.compress(Bitmap.CompressFormat.PNG, 100, outStream);

            outStream.flush();
            outStream.close();
            return true;

        } catch (FileNotFoundException e) {
            Toast.makeText(getBaseContext(), "File not found", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Error in saving", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private Uri getUri() {
        String state = Environment.getExternalStorageState();
        if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode){
            case PICK_IMAGE_CAMERA:
                if(resultCode==RESULT_OK) {
                   /* photo = (Bitmap) data.getExtras().get("data");
                    ivPhoto.setImageBitmap(photo);*/
                    File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
                    photo = decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);
                    ivPhoto.setImageBitmap(photo);
                }
                else
                    Toast.makeText(getBaseContext(), "No selection", Toast.LENGTH_SHORT).show();
                break;
            case PICK_IMAGE_GALLERY:

                if(requestCode == PICK_IMAGE_GALLERY && resultCode == RESULT_OK){

                    Uri originalUri = data.getData();



    /* now extract ID from Uri path using getLastPathSegment() and then split with ":"
    then call get Uri to for Internal storage or External storage for media I have used getUri()
    */

                    String id = originalUri.getLastPathSegment().split(":")[1];
                    final String[] imageColumns = {MediaStore.Images.Media.DATA };
                    final String imageOrderBy = null;

                    Uri uri = getUri();
                    String selectedImagePath = "path";

                    Cursor imageCursor = managedQuery(uri, imageColumns,
                            MediaStore.Images.Media._ID + "=" + id, null, imageOrderBy);

                    if (imageCursor.moveToFirst()) {
                        selectedImagePath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                        photo = decodeSampledBitmapFromFile(selectedImagePath,1000,700);
                        ivPhoto.setImageBitmap(photo);
                    }
                    Log.e("path", selectedImagePath); // use selectedImagePath

                }

                else
                    Toast.makeText(getBaseContext(),"No selection",Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private Bitmap decodeSampledBitmapFromFile(String absolutePath, int i, int i1) {

        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(absolutePath, options);

        // Calculate inSampleSize, Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.i("sqrl2",""+height+" "+width);
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;

        if (height > 800)
        {
            inSampleSize = Math.round((float)height / (float)800);
        }
        int expectedWidth = width / inSampleSize;

        if (expectedWidth > 800)
        {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float)width / (float)800);
        }

        options.inSampleSize = inSampleSize;

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(absolutePath, options);
    }

    public static Bitmap applyReflection(Bitmap originalImage) {
        // gap space between original and reflected
        final int reflectionGap = 4;
        // get image size
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // this will not scale but will flip on the Y axis

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        // create a Bitmap with the flip matrix applied to it.
        // we only want the bottom half of the image
        Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0, height/2, width, height/2, matrix, false);

        // create a new bitmap with same width but taller to fit reflection
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height/2), Bitmap.Config.ARGB_8888);

        // create a new Canvas with the bitmap that's big enough for
        // the image plus gap plus reflection
        Canvas canvas = new Canvas(bitmapWithReflection);
        // draw in the original image
        canvas.drawBitmap(originalImage, 0, 0, null);
        // draw in the gap
        Paint defaultPaint = new Paint();
        canvas.drawRect(0, height, width, height + reflectionGap, defaultPaint);
        // draw in the reflection
        canvas.drawBitmap(reflectionImage,0, height + reflectionGap, null);

        // create a shader that is a linear gradient that covers the reflection
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, originalImage.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff,
                Shader.TileMode.CLAMP);
        // set the paint to use this shader (linear gradient)
        paint.setShader(shader);
        // set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

        return bitmapWithReflection;
    }

    public static Bitmap applySnowEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // random object
        Random random = new Random();

        int R, G, B, index = 0, thresHold = 50;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get color
                R = Color.red(pixels[index]);
                G = Color.green(pixels[index]);
                B = Color.blue(pixels[index]);
                // generate threshold
                thresHold = random.nextInt(COLOR_MAX);
                if(R > thresHold && G > thresHold && B > thresHold) {
                    pixels[index] = Color.rgb(COLOR_MAX, COLOR_MAX, COLOR_MAX);
                }
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }


    public static Bitmap applyFleaEffect(Bitmap source) {
        // get image size
        int width = source.getWidth();
        int height = source.getHeight();
        int[] pixels = new int[width * height];
        // get pixel array from source
        source.getPixels(pixels, 0, width, 0, 0, width, height);
        // a random object
        Random random = new Random();

        int index = 0;
        // iteration through pixels
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                // get current index in 2D-matrix
                index = y * width + x;
                // get random color
                int randColor = Color.rgb(random.nextInt(COLOR_MAX),
                        random.nextInt(COLOR_MAX), random.nextInt(COLOR_MAX));
                // OR
                pixels[index] |= randColor;
            }
        }
        // output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, source.getConfig());
        bmOut.setPixels(pixels, 0, width, 0, 0, width, height);
        return bmOut;
    }

    public Bitmap toGrayScale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap toCartoon(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(20);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    public Bitmap toSephia(Bitmap bmpOriginal)
    {
        int width, height, r,g, b, c, gry;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();
        int depth = 20;

        Bitmap bmpSephia = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpSephia);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bmpOriginal, 0, 0, paint);
        for(int x=0; x < width; x++) {
            for(int y=0; y < height; y++) {
                c = bmpOriginal.getPixel(x, y);

                r = Color.red(c);
                g = Color.green(c);
                b = Color.blue(c);

                gry = (r + g + b) / 3;
                r = g = b = gry;

                r = r + (depth * 2);
                g = g + depth;

                if(r > 255) {
                    r = 255;
                }
                if(g > 255) {
                    g = 255;
                }
                bmpSephia.setPixel(x, y, Color.rgb(r, g, b));
            }
        }
        Toast.makeText(getBaseContext(),"DONE!",Toast.LENGTH_SHORT);
        return bmpSephia;
    }

    public static Drawable toSepia(Drawable drawable) {
        if(drawable==null)
            return drawable;
        final ColorMatrix matrixA = new ColorMatrix();
        // making image B&W
        matrixA.setSaturation(0);

        final ColorMatrix matrixB = new ColorMatrix();
        // applying scales for RGB color values
        matrixB.setScale(1f, .95f, .82f, 1.0f);
        matrixA.setConcat(matrixB, matrixA);

        final ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixA);
        drawable.setColorFilter(filter);
        return drawable;
    }
}
