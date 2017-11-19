package com.pafoid.filepicker.viewHolders;

import android.graphics.Color;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.pafoid.filepicker.R;
import com.pafoid.utils.utils.DateUtils;
import com.pafoid.utils.utils.FileUtils;
import com.pafoid.utils.utils.SizeUtils;
import com.pafoid.utils.viewHolders.DefaultViewHolder;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by as00p on 2017-04-12.
 */
public class FileViewHolder extends DefaultViewHolder<File> {

    private static final String TAG = "FileViewHolder";

    private CheckedTextView textView;
    private TextView detailsView;
    private ImageView imageView;
    private Delegate delegate;
    private boolean showExtension = false;

    public FileViewHolder(View itemView) {
        super(itemView);
    }

    public FileViewHolder(View itemView, File data) {
        super(itemView, data);
    }

    @Override
    protected void initViews() {
        textView = (CheckedTextView) itemView.findViewById(R.id.label);
        detailsView = (TextView) itemView.findViewById(R.id.details);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(delegate != null){
                    if(data.isDirectory()){
                        delegate.onClickDirectory(data, false);
                    }else{
                        delegate.onClickFile(FileViewHolder.this, data);
                    }
                }
            }
        });
        imageView = (ImageView) itemView.findViewById(R.id.icon);
    }

    @Override
    protected void updateViews() {
        if(data.isDirectory()){
            imageView.setImageResource(R.drawable.ic_folder_black_24dp);
        }else{
            imageView.setImageResource(R.drawable.ic_insert_drive_file_black_24dp);
        }

        if(data.isDirectory()){
            textView.setText(data.getName());
        }else{
            if(showExtension){
                textView.setText(data.getName());
            }else{
                textView.setText(data.getName().replace("." + FileUtils.fileExtension(data.getPath()), ""));
            }
        }

        if(detailsView != null){
            String size = "";
            if(!data.isDirectory()){
                if(data.length() > 0 ){
                    size = SizeUtils.autoConvert(getContext(), data.length()).toUpperCase();
                }else{
                    size = "N/A";
                }

                SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.Format.DATE_MINUTES);
                String date = "";
                if(data.lastModified() > 0){
                    date = sdf.format(new Date(data.lastModified()));
                    detailsView.setText(getContext().getString(R.string.file_details, size, date));
                }else{
                    detailsView.setText(getContext().getString(R.string.dir_details, size));
                }
            }else{
                detailsView.setText("");
            }
        }
    }

    //Getters/Setters
    public boolean getIsChecked() {
        return textView.isChecked();
    }

    public void setIsChecked(boolean isChecked) {
        textView.setChecked(isChecked);

        if (isChecked) {
            itemView.setBackgroundColor(Color.LTGRAY);
        } else {
            itemView.setBackground(null);
        }
    }

    public Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public boolean isShowExtension() {
        return showExtension;
    }

    public void setShowExtension(boolean showExtension) {
        this.showExtension = showExtension;
    }

    public interface Delegate{
        void onClickFile(FileViewHolder vh, File file);
        void onClickDirectory(File dir, boolean select);
        void onItemsSelected(int count);
    }
}
