package com.team.socero.loopvendor;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Mahrus Kazi on 2018-06-12.
 */

public class NewEvent extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_STORAGE = 1;
    private static final String TAG = "NewEvent";

    Activity activity = this;
    Context context = this;

    EditText eventTitle;
    ImageView editButton;
    ImageView coverPhoto;
    PlaceAutocompleteFragment autocompleteFragment;
    TextView startTimeView;
    TextView startDateView;
    TextView endTimeView;
    TextView endDateView;
    EditText description;

    String startTime, startDate, endTime, endDate;

    //Cover photo
    String image64, imageString;

    EditText ticketName;
    Spinner ticketType;
    String typeSelected = "Paid";
    EditText ticketPrice;
    EditText ticketQuantity;
    EditText minTicketOrder;

    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(
            Context context, Uri uri, String selection,
            String[] selectionArgs
    ) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /* Code is derived form https://stackoverflow.com/questions/20067508/get-real-path-from-uri-android-kitkat-new-storage-access-framework/20559175#20559175 */

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Drive.
     */
    public static boolean isGoogleDriveUri(Uri uri) {
        return "com.google.android.apps.docs.storage".equals(uri.getAuthority());
    }

    public static Bitmap modifyOrientation(Bitmap bitmap, String image_absolute_path) throws IOException {
        ExifInterface ei = new ExifInterface(image_absolute_path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotate(bitmap, 90);

            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotate(bitmap, 180);

            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotate(bitmap, 270);

            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                return flip(bitmap, true, false);

            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                return flip(bitmap, false, true);

            default:
                return bitmap;
        }
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public static Bitmap flip(Bitmap bitmap, boolean horizontal, boolean vertical) {
        Matrix matrix = new Matrix();
        matrix.preScale(horizontal ? -1 : 1, vertical ? -1 : 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        eventTitle = findViewById(R.id.event_title_edit);
        editButton = findViewById(R.id.photo_edit_button);
        coverPhoto = findViewById(R.id.event_photo);
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
        startTimeView = findViewById(R.id.start_time_picker);
        startDateView = findViewById(R.id.start_date_picker);
        endTimeView = findViewById(R.id.end_time_picker);
        endDateView = findViewById(R.id.end_date_picker);
        description = findViewById(R.id.event_description);

        ticketName = findViewById(R.id.ticket_name_edit);
        ticketType = findViewById(R.id.ticket_type_spinner);
        ticketPrice = findViewById(R.id.ticket_price_edit);
        ticketQuantity = findViewById(R.id.ticket_quantity_edit);
        minTicketOrder = findViewById(R.id.ticket_min_order_edit);

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        setupStateEndViews();
        setupSpinner();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == 1 && null != data) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    //Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 80, 80, true);


                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP);


                    image64 = imageString;


                    //decode base64 string to image
                    imageBytes = Base64.decode(image64, Base64.DEFAULT);
                    Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    coverPhoto.setImageBitmap(decodedImage); //display the image.
                } else {
                    Log.d(TAG, "onActivityResult: imageBitmap is null");
                }
            } else if (requestCode == 2 && null != data) //getting image from device's gallery
            {

                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // Get the path from the Uri
                    try {
                        URI uri = new URI(getPath(this, selectedImageUri));

                        String path = getPathFromURI(selectedImageUri);
                        Log.i(TAG, "Image Path : " + path);
                        // Set the image in ImageViev

                        File f = new File(uri.getPath());

                        Bitmap bm = null;
                        try {
                            bm = modifyOrientation(decodeUri(selectedImageUri), f.getAbsolutePath());

                            coverPhoto.setImageBitmap(bm);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        //encode bitmap image to base64 string
                        if (bm != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                            byte[] imageBytes = baos.toByteArray();
                            imageString = Base64.encodeToString(imageBytes, Base64.NO_WRAP);
                            image64 = imageString;
                        }


                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void setupSpinner() {
        List<String> types = new ArrayList<>();

        types.add("Paid");
        types.add("Free");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        ticketType.setAdapter(dataAdapter);

        ticketType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // On selecting a spinner item
                typeSelected = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setupStateEndViews() {
        startTimeView.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {
                        String fixUnder10;
                        if (minute < 10) {
                            fixUnder10 = "0" + minute;

                        } else {
                            fixUnder10 = String.valueOf(minute);
                        }

                        startTime = hourOfDay + ":" + fixUnder10;

                        String am_pm = "AM";
                        String time;
                        if (hourOfDay > 12) {

                            am_pm = "PM";
                            time = (hourOfDay - 12) + ":" + fixUnder10 + " " + am_pm;
                            startTimeView.setText(time);
                        } else if (hourOfDay == 12) {

                            am_pm = "PM";
                            time = (hourOfDay + ":" + fixUnder10 + " " + am_pm);
                            startTimeView.setText(time);
                        } else {
                            time = (hourOfDay + ":" + fixUnder10 + " " + am_pm);
                            startTimeView.setText(time);
                        }

                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        startDateView.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        String date = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        startDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        startDateView.setText(date);

                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        endTimeView.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mHour = c.get(Calendar.HOUR_OF_DAY);
            int mMinute = c.get(Calendar.MINUTE);

            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute) -> {
                        String fixUnder10;
                        if (minute < 10) {
                            fixUnder10 = "0" + minute;

                        } else {
                            fixUnder10 = String.valueOf(minute);
                        }

                        endTime = hourOfDay + ":" + fixUnder10;

                        String am_pm = "AM";
                        String time;
                        if (hourOfDay > 12) {

                            am_pm = "PM";
                            time = (hourOfDay - 12) + ":" + fixUnder10 + " " + am_pm;
                            endTimeView.setText(time);
                        } else if (hourOfDay == 12) {

                            am_pm = "PM";
                            time = (hourOfDay + ":" + fixUnder10 + " " + am_pm);
                            endTimeView.setText(time);
                        } else {
                            time = (hourOfDay + ":" + fixUnder10 + " " + am_pm);
                            endTimeView.setText(time);
                        }

                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        endDateView.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, monthOfYear, dayOfMonth) -> {

                        String date = (monthOfYear + 1) + "-" + dayOfMonth + "-" + year;
                        endDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        endDateView.setText(date);


                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });
    }

    public void postEvent(View view) {

    }

    private void selectImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {
            if (options[item].equals("Take Photo")) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, 1);
                }
            } else if (options[item].equals("Choose from Gallery")) {
                // Here, thisActivity is the current activity
                checkStoragePermission();
                if (ContextCompat.checkSelfPermission(activity,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent1 = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent1.setType("image/*");
                    intent1.setAction(Intent.ACTION_GET_CONTENT);
                    if (intent1.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent1, 2);
                    }
                }

            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new android.support.v7.app.AlertDialog.Builder(this)
                        .setTitle("External Storage Permission Needed")
                        .setMessage("This app needs the Read External Stoarage permission, please accept to select an image from your gallery")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
                            }
                        })
                        .create()
                        .show();

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
            }
        }
    }

    /*End of code derivation */

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    String id = "";
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        id = DocumentsContract.getDocumentId(uri);
                    }
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if (isGoogleDriveUri(uri)) { // Google Drive
                String TAG = "isGoogleDrive";
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(";");
                final String acc = split[0];
                final String doc = split[1];

                    /*
                    * @details google drive document data. - acc , docId.
                    * */
                try {
                    File f = saveFileIntoExternalStorageByUri(context, uri);
                    return f.getAbsolutePath();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {

                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("file".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            }
        }

        return "";
    }

    public File saveFileIntoExternalStorageByUri(Context context, Uri uri) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        int originalSize = inputStream.available();

        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        String fileName = getFileName(context, uri);
        File file = makeEmptyFileIntoExternalStorageWithTitle(fileName);
        bis = new BufferedInputStream(inputStream);
        bos = new BufferedOutputStream(new FileOutputStream(
                file, false));

        byte[] buf = new byte[originalSize];
        bis.read(buf);
        do {
            bos.write(buf);
        } while (bis.read(buf) != -1);

        bos.flush();
        bos.close();
        bis.close();

        return file;

    }

    public File makeEmptyFileIntoExternalStorageWithTitle(String title) {
        try {
            return File.createTempFile(title, null, this.getApplicationContext().getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(getApplicationContext(), contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    // Code derived from https://stackoverflow.com/questions/14051068/java-lang-outofmemoryerror-in-android-while-getting-image-from-gallery-in-androi
    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(
                getContentResolver().openInputStream(selectedImage), null, o2);
    }
}
