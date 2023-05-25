package com.example.memorygame;

import android.content.Context;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.util.Log;
import android.widget.BaseAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class GridAdapter extends BaseAdapter {
    private Context mContext;
    private Integer mCols, mRows;

    private String pictureCollection;
    private Resources mRes;

    private ArrayList<String> arrPict; // массив картинок
    private ArrayList<Status> arrStatus; // состояние ячеек

    private int picsCount = 20; // TODO HARDCODED
    private static enum Status {CELL_OPEN, CELL_CLOSE, CELL_DELETE};

    public int turnCount;

    public GridAdapter(Context context, int cols, int rows) {
        mContext = context;

        if (cols*rows > picsCount) {
            mCols = 5; // TODO HARDCODED
            mRows = 4;
        }else {
            mCols = cols;
            mRows = rows;
        }



        turnCount = 0;

        arrPict = new ArrayList<String>();
        arrStatus = new ArrayList<Status>();
        pictureCollection = "f";
        mRes = mContext.getResources();
        makePictArray();
        closeAllCells();
        listRaw();
    }

    private void closeAllCells() {
        arrStatus.clear();
        for (int i = 0; i < mCols * mRows; i++)
            arrStatus.add(Status.CELL_CLOSE);
    }

    private void makePictArray () {
        // очищаем вектор
        arrPict.clear();

        ArrayList<Integer> picsIndexes = new ArrayList<Integer>();
        for (int i = 1; i <= picsCount; i++) picsIndexes.add(i);
        Collections.shuffle(picsIndexes);
        // добавляем
        for (int i = 1; i <= ((mCols * mRows) / 2); i++)
        {
            arrPict.add (pictureCollection + Integer.toString (picsIndexes.get(i)));
            arrPict.add (pictureCollection + Integer.toString (picsIndexes.get(i)));
        }
        // перемешиваем
        Collections.shuffle(arrPict);
    }

    public void checkOpenCells() {
        int first = arrStatus.indexOf(Status.CELL_OPEN);
        int second = arrStatus.lastIndexOf(Status.CELL_OPEN);
        if (first == second)
            return;
        if (arrPict.get(first).equals (arrPict.get(second)))
        {
            arrStatus.set(first, Status.CELL_DELETE);
            arrStatus.set(second, Status.CELL_DELETE);
        }
        else
        {
            arrStatus.set(first, Status.CELL_CLOSE);
            arrStatus.set(second, Status.CELL_CLOSE);

        }
        turnCount++;
        notifyDataSetChanged();

    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }
    public void openCell(int position) {
        if (arrStatus.get(position) != Status.CELL_DELETE)
            arrStatus.set(position, Status.CELL_OPEN);

        notifyDataSetChanged();
    }

    public boolean checkGameOver() {
        if (arrStatus.indexOf(Status.CELL_CLOSE) < 0)
            return true;
        return false;
    }

    @Override
    public int getCount() {
        return mCols * mRows;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ImageView view; // для вывода картинки
        if (convertView == null)
            view = new ImageView(mContext);
        else
            view = (ImageView) convertView;

        switch (arrStatus.get(position))
        {
            case CELL_OPEN:
                // Получаем идентификатор ресурса для картинки,
                // которая находится в векторе vecPict на позиции position
                Integer drawableId = mRes.getIdentifier(arrPict.get(position), "raw", mContext.getPackageName());
                view.setImageResource(drawableId);
                break;
            case CELL_CLOSE:
                view.setImageResource(R.raw.back1_s);
                break;
            default:
                drawableId = mRes.getIdentifier(arrPict.get(position), "raw", mContext.getPackageName());
                view.setImageResource(drawableId);
                //view.setImageResource(R.drawable.ic_launcher_foreground);
        }



        return view;
    }

    public void listRaw(){
        Field[] fields=R.raw.class.getFields();
        for(int count=0; count < fields.length; count++){
            Log.i("Raw Asset: ", fields[count].getName());
        }
    }

}
