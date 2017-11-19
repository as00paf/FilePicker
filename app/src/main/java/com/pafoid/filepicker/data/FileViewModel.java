package com.pafoid.filepicker.data;

import android.arch.lifecycle.MutableLiveData;

import com.pafoid.filepicker.FilePicker;
import com.pafoid.utils.data.ListViewModel;
import com.pafoid.utils.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created by as00p on 2017-08-07.
 */

public class FileViewModel extends ListViewModel {

    private File currentDirectory = FileUtils.getRootFolder();
    private int sort;
    private boolean showHidden = false;

    public FileViewModel() {

    }

    @Override
    protected MutableLiveData<List> loadData() {
        ArrayList<File> list = FileUtils.getDirectoryContent(getCurrentDirectory(), showHidden);
        //Sort by type (dir/file) & name (ASC)
        Collections.sort(list, getComparator());

        MutableLiveData liveData = new MutableLiveData();
        liveData.setValue(list);
        return liveData;
    }

    private Comparator<File> getComparator() {
        switch (sort){
            case FilePicker.Sort.NAME_ASC:
                return ascNameComparator;
            case FilePicker.Sort.NAME_DSC:
                return dscNameComparator;
            case FilePicker.Sort.SIZE_ASC:
                return ascSizeComparator;
            case FilePicker.Sort.SIZE_DSC:
                return dscSizeComparator;
            case FilePicker.Sort.DATE_ASC:
                return ascDateComparator;
            case FilePicker.Sort.DATE_DSC:
                return dscDateComparator;
        }

        return null;
    }

    //Comparators
    //TODO : make only one ?
    Comparator<File> ascNameComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return file.getName().compareTo(t1.getName());
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return file.getName().compareTo(t1.getName());
            }
            return 0;
        }
    };

    Comparator<File> dscNameComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return t1.getName().compareTo(file.getName());
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return t1.getName().compareTo(file.getName());
            }
            return 0;
        }
    };

    Comparator<File> ascSizeComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return Long.compare(file.getTotalSpace(), t1.getTotalSpace());
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return Long.compare(file.length(), t1.length());
            }
            return 0;
        }
    };

    Comparator<File> dscSizeComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return Long.compare(t1.getTotalSpace(), file.getTotalSpace());
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return Long.compare(t1.length(), file.length());
            }
            return 0;
        }
    };

    Comparator<File> ascDateComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return new Date(file.lastModified()).compareTo(new Date(t1.lastModified()));
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return new Date(file.lastModified()).compareTo(new Date(t1.lastModified()));
            }
            return 0;
        }
    };

    Comparator<File> dscDateComparator = new Comparator<File>() {
        @Override
        public int compare(File file, File t1) {
            if(file.isDirectory() && !t1.isDirectory()) return -1;
            if(!file.isDirectory() && t1.isDirectory()) return 1;

            if(file.isDirectory() && t1.isDirectory()){
                return new Date(t1.lastModified()).compareTo(new Date(file.lastModified()));
            }
            if(!file.isDirectory() && !t1.isDirectory()){
                return new Date(t1.lastModified()).compareTo(new Date(file.lastModified()));
            }
            return 0;
        }
    };


    //Getters/Setters
    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void setCurrentDirectory(File currentDirectory) {
        this.currentDirectory = currentDirectory;
        data.postValue(loadData().getValue());
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
        data.postValue(loadData().getValue());
    }

    public void setShowHidden(boolean showHidden) {
        this.showHidden = showHidden;
        data.postValue(loadData().getValue());
    }
}
