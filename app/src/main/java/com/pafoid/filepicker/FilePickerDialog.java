package com.pafoid.filepicker;

import android.support.v4.app.FragmentManager;

import com.pafoid.filepicker.views.FileDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by as00p on 2017-10-05.
 */

public class FilePickerDialog {

    //Files
    public static FileDialog selectSingleFile(FragmentManager fragmentManager, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.SELECT_SINGLE_FILE);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    public static FileDialog selectMultipleFile(FragmentManager fragmentManager, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.SELECT_MULTIPLE_FILES);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    public static FileDialog newFile(FragmentManager fragmentManager, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.NEW_FILE);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    public static FileDialog newFile(FragmentManager fragmentManager, String extension, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.NEW_FILE, extension);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    //Directories
    public static FileDialog selectSingleDirectory(FragmentManager fragmentManager, boolean showInstructions, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.SELECT_SINGLE_FILE, showInstructions);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    public static FileDialog selectMultipleDirectories(FragmentManager fragmentManager, boolean showInstructions, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.SELECT_SINGLE_FILE, showInstructions);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    public static FileDialog newDirectory(FragmentManager fragmentManager, Delegate delegate){
        FileDialog dialog = FileDialog.newInstance(FilePicker.Action.SELECT_SINGLE_FILE);
        dialog.show(fragmentManager, FileDialog.TAG);
        dialog.setDelegate(delegate);
        return dialog;
    }

    //Delegate
    public interface Delegate{
        void onCancel();
        void onFileSelected(File file);
        void onFilesSelected(ArrayList<File> files);
        void onFileCreated(File file);
        void onFolderSelected(File dir);
        void onFoldersSelected(ArrayList<File> dirs);
        void onFolderCreated(File dir);
    }
}
