package com.pafoid.filepicker.adapters;

import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.widget.Filter;
import android.widget.Filterable;

import com.pafoid.filepicker.FilePicker;
import com.pafoid.filepicker.viewHolders.FileViewHolder;
import com.pafoid.utils.adapters.DefaultAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by as00p on 2017-10-05.
 */

public class FileAdapter extends DefaultAdapter<FileViewHolder> implements Filterable, FileViewHolder.Delegate {
    private static final String TAG = "FileAdapter";

    private FileViewHolder.Delegate delegate;

    private boolean showExtension = false;
    private boolean multiSelectionEnabled = false;
    private String selectionType = FilePicker.FILE;

    private ItemFilter mFilter = new ItemFilter();
    private List<File> filteredData = new ArrayList();
    private List<File> selectedItems = new ArrayList();

    public FileAdapter(@LayoutRes int itemRes, Class<FileViewHolder> clazz, boolean multiSelectionEnabled, String selectionType) {
        super(itemRes, clazz);
        this.multiSelectionEnabled = multiSelectionEnabled;
        this.selectionType = selectionType;
    }

    public FileAdapter(@LayoutRes int itemRes, List items, Class<FileViewHolder> clazz, boolean multiSelectionEnabled, String selectionType) {
        super(itemRes, items, clazz);
        filteredData = items;
        this.multiSelectionEnabled = multiSelectionEnabled;
        this.selectionType = selectionType;
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, final int position) {
        holder.setShowExtension(showExtension);
        holder.setData(filteredData.get(position));
        holder.setDelegate(this);

        if(isMultiSelectionEnabled() &&
                ((selectionType == FilePicker.FILE && !filteredData.get(position).isDirectory()) ||
                (selectionType == FilePicker.DIRECTORY && filteredData.get(position).isDirectory()))){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    holder.setIsChecked(!holder.getIsChecked());
                    if(selectedItems.contains(filteredData.get(position))){
                        selectedItems.remove(filteredData.get(position));
                    }else{
                        selectedItems.add(filteredData.get(position));
                    }

                    if(delegate != null){
                        delegate.onItemsSelected(selectedItems.size());
                    }

                    return true;
                }
            });
        }else if(!isMultiSelectionEnabled() && selectionType == FilePicker.DIRECTORY){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClickDirectory(filteredData.get(position), true);

                    return true;
                }
            });
        }
    }

    public FileViewHolder.Delegate getDelegate() {
        return delegate;
    }

    public void setDelegate(FileViewHolder.Delegate delegate) {
        this.delegate = delegate;
    }

    public void setShowExtension(boolean showExtension) {
        this.showExtension = showExtension;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(filteredData == null) return 0;
        return filteredData.size();
    }

    @Override
    public void setItems(List items) {
        super.setItems(items);
        filteredData = items;
    }

    //Filter
    @Override
    public Filter getFilter() {
        return mFilter;
    }

    //Delegate
    @Override
    public void onClickFile(FileViewHolder vh, File file) {
        Log.d(TAG, "Click file !");
        if(delegate != null) delegate.onClickFile(vh, file);

        if(multiSelectionEnabled && selectionType == FilePicker.FILE){
            vh.setIsChecked(!vh.getIsChecked());
            if(selectedItems.contains(file)){
                selectedItems.remove(file);
            }else{
                selectedItems.add(file);
            }

            if(delegate != null){
                delegate.onItemsSelected(selectedItems.size());
            }
        }
    }

    @Override
    public void onClickDirectory(File dir, boolean selected) {
        Log.d(TAG, "Click dir !");

        if(delegate != null) delegate.onClickDirectory(dir, selected);
    }

    @Override
    public void onItemsSelected(int count) {
        Log.d(TAG, "Items Selected !");
        if(delegate != null) delegate.onItemsSelected(count);
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<File> list = items;

            int count = list.size();
            final ArrayList<File> nlist = new ArrayList<File>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<File>) results.values;
            notifyDataSetChanged();
        }
    }

    //Getters/Setters
    public boolean isMultiSelectionEnabled() {
        return multiSelectionEnabled;
    }

    public void setMultiSelectionEnabled(boolean multiSelectionEnabled) {
        this.multiSelectionEnabled = multiSelectionEnabled;
    }

    public String getSelectionType() {
        return selectionType;
    }

    public List<File> getSelectedItems() {
        return selectedItems;
    }
}
