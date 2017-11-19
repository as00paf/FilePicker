package com.pafoid.filepicker;

import android.app.Activity;
import android.content.Intent;

import com.pafoid.filepicker.activities.FileActivity;
import com.pafoid.utils.utils.AppConstants;

/**
 * Created by as00p on 2017-10-05.
 */

public class FilePicker {

    public static final String FILE                     = "file";
    public static final String FILES                    = "files";
    public static final String DIRECTORY                = "dir";
    public static final String DIRECTORIES              = "dirs";
    public static final String SORT                     = "sort";
    public static final String VIEW_LAYOUT              = "layout";
    public static final String VIEW_HIDDEN_FILES        = "hidden";
    public static final String VIEW_FILES_EXTENSIONS    = "ext";
    public static final String INSTRUCTIONS             = "instructions";

    public class RequestCode{
        public static final int SINGLE_FILE             = 6001;
        public static final int MULTIPLE_FILES          = 6002;
        public static final int SINGLE_DIRECTORY        = 6003;
        public static final int MULTIPLE_DIRECTORIES    = 6004;
        public static final int NEW_FILE                = 6005;
        public static final int NEW_DIRECTORY           = 6006;
    }

    public class Action{
        public static final String SELECT_SINGLE_FILE           = "selectSingleFile";
        public static final String SELECT_MULTIPLE_FILES        = "selectMultipleFiles";
        public static final String SELECT_SINGLE_DIRECTORY      = "selectSingleDirectory";
        public static final String SELECT_MULTIPLE_DIRECTORIES  = "selectMultipleDirectories";
        public static final String NEW_FILE                     = "newFile";
        public static final String NEW_DIRECTORY                = "newDir";
    }

    public class Sort{
        public static final int NAME_ASC             = 0;
        public static final int NAME_DSC             = 1;
        public static final int SIZE_ASC             = 2;
        public static final int SIZE_DSC             = 3;
        public static final int DATE_ASC             = 4;
        public static final int DATE_DSC             = 5;
    }

    public class View{
        public static final int LAYOUT_GRID             = 0;
        public static final int LAYOUT_LIST             = 1;
        public static final int LAYOUT_DETAILS          = 2;
    }

    //Files
    public static void selectSingleFile(Activity activity){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.SELECT_SINGLE_FILE);
        activity.startActivityForResult(intent, RequestCode.SINGLE_FILE);
    }

    public static void selectMultipleFile(Activity activity){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.SELECT_MULTIPLE_FILES);
        activity.startActivityForResult(intent, RequestCode.MULTIPLE_FILES);
    }

    public static void newFile(Activity activity){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.NEW_FILE);
        activity.startActivityForResult(intent, RequestCode.NEW_FILE);
    }

    public static void newFile(Activity activity, String extension){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.NEW_FILE);
        intent.putExtra(AppConstants.EXTENSION, extension);
        activity.startActivityForResult(intent, RequestCode.NEW_FILE);
    }

    //Directories
    public static void selectSingleDirectory(Activity activity, boolean showInstructions){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.SELECT_SINGLE_DIRECTORY);
        intent.putExtra(INSTRUCTIONS, showInstructions);
        activity.startActivityForResult(intent, RequestCode.SINGLE_DIRECTORY);
    }

    public static void selectMultipleDirectories(Activity activity, boolean showInstructions){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.SELECT_MULTIPLE_DIRECTORIES);
        intent.putExtra(INSTRUCTIONS, showInstructions);
        activity.startActivityForResult(intent, RequestCode.MULTIPLE_DIRECTORIES);
    }

    public static void newDirectory(Activity activity){
        Intent intent = new Intent(activity, FileActivity.class);
        intent.setAction(Action.NEW_DIRECTORY);
        activity.startActivityForResult(intent, RequestCode.NEW_DIRECTORY);
    }

}
