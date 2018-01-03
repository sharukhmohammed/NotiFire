
package sharukh.reminds.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class TinyDB
{

    private SharedPreferences preferences;
    private String DEFAULT_APP_IMAGEDATA_DIRECTORY;
    private String lastImagePath = "";

    public TinyDB(Context appContext)
    {
        preferences = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public static boolean isExternalStorageWritable()
    {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static boolean isExternalStorageReadable()
    {
        String state = Environment.getExternalStorageState();

        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap getImage(String path)
    {
        Bitmap bitmapFromPath = null;
        try
        {
            bitmapFromPath = BitmapFactory.decodeFile(path);

        } catch (Exception e)
        {
            e.printStackTrace();
        }

        return bitmapFromPath;
    }

    public String getSavedImagePath()
    {
        return lastImagePath;
    }

    public String putImage(String theFolder, String theImageName, Bitmap theBitmap)
    {
        if (theFolder == null || theImageName == null || theBitmap == null)
            return null;

        this.DEFAULT_APP_IMAGEDATA_DIRECTORY = theFolder;
        String mFullPath = setupFullPath(theImageName);

        if (!mFullPath.equals(""))
        {
            lastImagePath = mFullPath;
            saveBitmap(mFullPath, theBitmap);
        }

        return mFullPath;
    }

    public boolean putImageWithFullPath(String fullPath, Bitmap theBitmap)
    {
        return !(fullPath == null || theBitmap == null) && saveBitmap(fullPath, theBitmap);
    }

    private String setupFullPath(String imageName)
    {
        File mFolder = new File(Environment.getExternalStorageDirectory(), DEFAULT_APP_IMAGEDATA_DIRECTORY);

        if (isExternalStorageReadable() && isExternalStorageWritable() && !mFolder.exists())
        {
            if (!mFolder.mkdirs())
            {
                Log.e("ERROR", "Failed to setup folder");
                return "";
            }
        }

        return mFolder.getPath() + '/' + imageName;
    }

    private boolean saveBitmap(String fullPath, Bitmap bitmap)
    {
        if (fullPath == null || bitmap == null)
            return false;

        boolean fileCreated = false;
        boolean bitmapCompressed = false;
        boolean streamClosed = false;

        File imageFile = new File(fullPath);

        if (imageFile.exists())
            if (!imageFile.delete())
                return false;

        try
        {
            fileCreated = imageFile.createNewFile();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try
        {
            out = new FileOutputStream(imageFile);
            bitmapCompressed = bitmap.compress(CompressFormat.PNG, 100, out);

        } catch (Exception e)
        {
            e.printStackTrace();
            bitmapCompressed = false;

        } finally
        {
            if (out != null)
            {
                try
                {
                    out.flush();
                    out.close();
                    streamClosed = true;

                } catch (IOException e)
                {
                    e.printStackTrace();
                    streamClosed = false;
                }
            }
        }

        return (fileCreated && bitmapCompressed && streamClosed);
    }

    public int getInt(String key)
    {
        return preferences.getInt(key, 0);
    }

    public ArrayList<Integer> getListInt(String key)
    {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Integer> newList = new ArrayList<Integer>();

        for (String item : arrayToList)
            newList.add(Integer.parseInt(item));

        return newList;
    }

    public long getLong(String key, long defaultValue)
    {
        return preferences.getLong(key, defaultValue);
    }

    public float getFloat(String key)
    {
        return preferences.getFloat(key, 0);
    }

    public double getDouble(String key, double defaultValue)
    {
        String number = getString(key);

        try
        {
            return Double.parseDouble(number);

        } catch (NumberFormatException e)
        {
            return defaultValue;
        }
    }

    public ArrayList<Double> getListDouble(String key)
    {
        String[] myList = TextUtils.split(preferences.getString(key, ""), "‚‗‚");
        ArrayList<String> arrayToList = new ArrayList<String>(Arrays.asList(myList));
        ArrayList<Double> newList = new ArrayList<Double>();

        for (String item : arrayToList)
            newList.add(Double.parseDouble(item));

        return newList;
    }

    public String getString(String key)
    {
        return preferences.getString(key, "");
    }


    public ArrayList<String> getListString(String key)
    {
        return new ArrayList<String>(Arrays.asList(TextUtils.split(preferences.getString(key, ""), "‚‗‚")));
    }

    public boolean getBoolean(String key)
    {
        return preferences.getBoolean(key, false);
    }

    public ArrayList<Boolean> getListBoolean(String key)
    {
        ArrayList<String> myList = getListString(key);
        ArrayList<Boolean> newList = new ArrayList<Boolean>();

        for (String item : myList)
        {
            if (item.equals("true"))
            {
                newList.add(true);
            } else
            {
                newList.add(false);
            }
        }

        return newList;
    }

    public ArrayList<Object> getListObject(String key, Class<?> mClass)
    {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<Object> objects = new ArrayList<Object>();

        for (String jObjString : objStrings)
        {
            Object value = gson.fromJson(jObjString, mClass);
            objects.add(value);
        }
        return objects;
    }

    public Object getObject(String key, Class<?> classOfT)
    {

        String json = getString(key);
        Object value = new Gson().fromJson(json, classOfT);
        if (value == null)
            throw new NullPointerException();
        return value;
    }

    public Reminder getReminder(String key, Class<Reminder> classOfT)
    {

        String json = getString(key);
        Reminder value = new Gson().fromJson(json, classOfT);
        if (value == null)
            throw new NullPointerException();
        return value;
    }

    public ArrayList<Reminder> getListReminder(String key, Class<Reminder> mClass)
    {
        Gson gson = new Gson();

        ArrayList<String> objStrings = getListString(key);
        ArrayList<Reminder> reminders = new ArrayList<Reminder>();

        for (String jObjString : objStrings)
        {
            Reminder value = gson.fromJson(jObjString, mClass);
            reminders.add(value);
        }
        return reminders;
    }

    public void putInt(String key, int value)
    {
        checkForNullKey(key);
        preferences.edit().putInt(key, value).apply();
    }

    public void putListInt(String key, ArrayList<Integer> intList)
    {
        checkForNullKey(key);
        Integer[] myIntList = intList.toArray(new Integer[intList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myIntList)).apply();
    }

    public void putLong(String key, long value)
    {
        checkForNullKey(key);
        preferences.edit().putLong(key, value).apply();
    }

    public void putFloat(String key, float value)
    {
        checkForNullKey(key);
        preferences.edit().putFloat(key, value).apply();
    }

    public void putDouble(String key, double value)
    {
        checkForNullKey(key);
        putString(key, String.valueOf(value));
    }

    public void putListDouble(String key, ArrayList<Double> doubleList)
    {
        checkForNullKey(key);
        Double[] myDoubleList = doubleList.toArray(new Double[doubleList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myDoubleList)).apply();
    }

    public void putString(String key, String value)
    {
        checkForNullKey(key);
        checkForNullValue(value);
        preferences.edit().putString(key, value).apply();
    }

    public void putListString(String key, ArrayList<String> stringList)
    {
        checkForNullKey(key);
        String[] myStringList = stringList.toArray(new String[stringList.size()]);
        preferences.edit().putString(key, TextUtils.join("‚‗‚", myStringList)).apply();
    }

    public void putBoolean(String key, boolean value)
    {
        checkForNullKey(key);
        preferences.edit().putBoolean(key, value).apply();
    }

    public void putListBoolean(String key, ArrayList<Boolean> boolList)
    {
        checkForNullKey(key);
        ArrayList<String> newList = new ArrayList<String>();

        for (Boolean item : boolList)
        {
            if (item)
            {
                newList.add("true");
            } else
            {
                newList.add("false");
            }
        }

        putListString(key, newList);
    }

    public void putObject(String key, Object obj)
    {
        checkForNullKey(key);
        Gson gson = new Gson();
        putString(key, gson.toJson(obj));
    }


    public void putListObject(String key, ArrayList<Object> objArray)
    {
        checkForNullKey(key);
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Object obj : objArray)
        {
            objStrings.add(gson.toJson(obj));
        }
        putListString(key, objStrings);
    }

    public void putReminder(String key, Reminder reminder)
    {
        checkForNullKey(key);
        Gson gson = new Gson();
        putString(key, gson.toJson(reminder));
    }

    public void putListReminder(String key, ArrayList<Reminder> reminderArrayList)
    {
        checkForNullKey(key);
        Gson gson = new Gson();
        ArrayList<String> objStrings = new ArrayList<String>();
        for (Reminder reminder : reminderArrayList)
        {
            objStrings.add(gson.toJson(reminder));
        }
        putListString(key, objStrings);
    }

    public void remove(String key)
    {
        preferences.edit().remove(key).apply();
    }

    public boolean deleteImage(String path)
    {
        return new File(path).delete();
    }

    public void clear()
    {
        preferences.edit().clear().apply();
    }

    public Map<String, ?> getAll()
    {
        return preferences.getAll();
    }

    public void registerOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener)
    {

        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(
            SharedPreferences.OnSharedPreferenceChangeListener listener)
    {

        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    public void checkForNullKey(String key)
    {
        if (key == null)
        {
            throw new NullPointerException();
        }
    }

    public void checkForNullValue(String value)
    {
        if (value == null)
        {
            throw new NullPointerException();
        }
    }
}
