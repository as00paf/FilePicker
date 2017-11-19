package com.pafoid.filepicker.activities;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pafoid.filepicker.FilePicker;
import com.pafoid.filepicker.R;
import com.pafoid.filepicker.adapters.FileAdapter;
import com.pafoid.filepicker.data.FileViewModel;
import com.pafoid.filepicker.viewHolders.FileViewHolder;
import com.pafoid.utils.activities.ViewModelRefreshableListActivity;
import com.pafoid.utils.permissions.PermissionManager;
import com.pafoid.utils.utils.AppConstants;
import com.pafoid.utils.utils.FileUtils;
import com.pafoid.utils.utils.KeyboardUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by as00p on 2017-10-05.
 */

public class FileActivity extends ViewModelRefreshableListActivity implements FileViewHolder.Delegate, View.OnClickListener {

    private static final String TAG = "FileActivity";

    //Data
    private String action;
    private String extension;

    //Search stuff
    private boolean isSearchOpened = false;
    private MenuItem mSearchAction;
    private SearchView searchView;

    //Views
    private EditText newName;
    private ImageButton doneButton;
    private Menu menu;

    @Override
    protected void onStart() {
        super.onStart();
        initPermissions();
    }

    private void initPermissions() {
        if(!PermissionManager.getInstance(this).isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)){
            PermissionManager.getInstance(this).requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Intent intent = getIntent();
        if(intent != null){
            action = intent.getAction();
        }

        super.onCreate(savedInstanceState);

        initLayout();

        toolbar.setTitle(((FileViewModel) viewModel).getCurrentDirectory().getName());
        compatDelegate.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int sort = preferences.getInt(FilePicker.SORT, FilePicker.Sort.NAME_ASC);
        ((FileViewModel) viewModel).setSort(sort);

        if(action.equals(FilePicker.Action.SELECT_MULTIPLE_DIRECTORIES) ||
            action.equals(FilePicker.Action.SELECT_SINGLE_DIRECTORY)){
            boolean showInstructions = intent.getBooleanExtra(FilePicker.INSTRUCTIONS, true);
            if(showInstructions) Toast.makeText(this, R.string.folder_instructions, Toast.LENGTH_LONG).show();
        }else if(action.equals(FilePicker.Action.NEW_DIRECTORY) || action.equals(FilePicker.Action.NEW_FILE)){
            if(!PermissionManager.getInstance(this).isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                PermissionManager.getInstance(this).requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }

            extension = intent.getStringExtra(AppConstants.EXTENSION);
            if(extension != null){
                extension.replace(".", "");
            }

            doneButton = findViewById(R.id.btn_done);
            doneButton.setOnClickListener(this);
            doneButton.setVisibility(View.GONE);

            newName = findViewById(R.id.new_name);
            KeyboardUtils.hideKeyboard(this, newName);
            newName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        // Do whatever you want here
                        onDoneNewFile();
                        return true;
                    }
                    return false;
                }
            });

            newName.setHint(action.equals(FilePicker.Action.NEW_DIRECTORY) ? R.string.hint_new_folder : R.string.hint_new_file);

            newName.addTextChangedListener(new TextWatcher() {
                private boolean selectionWasOk = true;
                private String currentText;

                @Override
                public void beforeTextChanged(CharSequence charSequence, int cursorPosition, int i1, int i2) {
                    int extIndex = charSequence.toString().indexOf("." + extension);
                    if(extension !=  null && extIndex > -1){
                        if(cursorPosition > extIndex) {
                            currentText = charSequence.toString();
                            newName.setSelection(extIndex);
                            selectionWasOk = false;
                        }
                    }
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(!selectionWasOk){
                        String addedCharacter = charSequence.toString().substring(i, i+1);
                        currentText = currentText.substring(0, currentText.indexOf("." + extension)) + addedCharacter + currentText.substring(currentText.indexOf("." + extension), currentText.length());
                        Spannable spannedText = new SpannableString(currentText);
                        spannedText.setSpan(new ForegroundColorSpan(getColor(R.color.black_54)), currentText.length() - 4, currentText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        newName.removeTextChangedListener(this);
                        newName.setText(spannedText);
                        newName.setSelection(newName.getText().length() - extension.length() - 1);
                        newName.addTextChangedListener(this);
                        return;
                    }

                    if(charSequence.length()>0){
                        doneButton.setVisibility(View.VISIBLE);
                    }else{
                        doneButton.setVisibility(View.GONE);
                    }

                    if(extension != null){
                        charSequence = charSequence.toString().replace("." + extension, "");

                        Spannable spannedText = new SpannableString(charSequence + "." + extension);
                        spannedText.setSpan(new ForegroundColorSpan(getColor(R.color.black_54)), charSequence.length(), spannedText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        newName.removeTextChangedListener(this);
                        newName.setText(spannedText);
                        newName.setSelection(newName.getText().length() - extension.length() - 1);
                        newName.addTextChangedListener(this);
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    selectionWasOk = true;
                }
            });
        }
    }

    @Override
    protected int getLayoutResource() {
        if(action != null &&
            (action.equals(FilePicker.Action.NEW_DIRECTORY) || action.equals(FilePicker.Action.NEW_FILE))){
            return R.layout.activity_new_file;
        }
        return super.getLayoutResource();
    }

    private void initLayout() {
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int layout = preferences.getInt(FilePicker.VIEW_LAYOUT, FilePicker.View.LAYOUT_GRID);

        RecyclerView.LayoutManager manager;
        if(layout == FilePicker.View.LAYOUT_GRID){
            manager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }else{
            manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        }

        recyclerView.setLayoutManager(manager);

    }

    @Override
    protected RecyclerView.Adapter getAdapter() {
        SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int layout = preferences.getInt(FilePicker.VIEW_LAYOUT, FilePicker.View.LAYOUT_GRID);
        int res;

        switch(layout){
            case FilePicker.View.LAYOUT_GRID:
                res = R.layout.item_file_grid;
                break;
            case FilePicker.View.LAYOUT_LIST:
                res = R.layout.item_file_list;
                break;
            case FilePicker.View.LAYOUT_DETAILS:
                res = R.layout.item_file_details;
                break;
            default:
                res = R.layout.item_file_grid;
                break;
        }

        boolean showFileExt = preferences.getBoolean(FilePicker.VIEW_FILES_EXTENSIONS, false);

        if(adapter == null){
            boolean multiSelectionEnabled = action == FilePicker.Action.SELECT_MULTIPLE_DIRECTORIES
                    || action == FilePicker.Action.SELECT_MULTIPLE_FILES;

            String selectionType = action == FilePicker.Action.SELECT_MULTIPLE_DIRECTORIES
                    || action == FilePicker.Action.SELECT_SINGLE_DIRECTORY ? FilePicker.DIRECTORY : FilePicker.FILE;

            adapter = new FileAdapter(res, data, FileViewHolder.class, multiSelectionEnabled, selectionType);
            ((FileAdapter) adapter).setDelegate(this);
            ((FileAdapter) adapter).setShowExtension(showFileExt);
        }else{
            initLayout();
            ((FileAdapter) adapter).setShowExtension(showFileExt);
            ((FileAdapter) adapter).setItemRes(res);
            ((FileAdapter) adapter).setItems(data);
        }
        return adapter;
    }

    @Override
    protected Class<FileViewModel> getViewModelClass() {
        return FileViewModel.class;
    }

    @Override
    public void onChanged(@Nullable List list) {
        super.onChanged(list);
        updateTitle();
    }

    private void updateTitle() {
        String title = ((FileViewModel) viewModel).getCurrentDirectory().getName();
        if(title.isEmpty()){
            switch (action){
                case FilePicker.Action.NEW_FILE:
                    title = getString(R.string.new_file);
                    break;
                case FilePicker.Action.NEW_DIRECTORY:
                    title = getString(R.string.new_folder);
                    break;
                case FilePicker.Action.SELECT_SINGLE_FILE:
                    title = getString(R.string.select_file);
                    break;
                case FilePicker.Action.SELECT_MULTIPLE_FILES:
                    title = getString(R.string.select_files);
                    break;
                case FilePicker.Action.SELECT_SINGLE_DIRECTORY:
                    title = getString(R.string.select_folder);
                    break;
                case FilePicker.Action.SELECT_MULTIPLE_DIRECTORIES:
                    title = getString(R.string.select_folders);
                    break;
            }
        }else{
            title = String.valueOf(title.charAt(0)).toUpperCase() + title.subSequence(1, title.length());
        }

        toolbar.setTitle(title);
    }

    //Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mSearchAction = menu.findItem(R.id.action_search);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_file, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);

        searchView = (SearchView) menuItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.clearFocus();
            }
        });

        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW
                | MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

                ActionBar action = compatDelegate.getSupportActionBar();
                mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                action.setTitle(getResources().getString(R.string.app_name));
                action.setDisplayShowCustomEnabled(false);
                action.setDisplayShowTitleEnabled(true);
                action.setDisplayShowHomeEnabled(true);

                isSearchOpened = false;

                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {

                return true;  // Return true to expand action view
            }
        });

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        searchView.setLayoutParams(params);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == android.R.id.home) {
            if (isSearchOpened) {
                closeSearchMenu();
            }else{
                onBack();
            }
        } else if (i == R.id.action_search) {
            Log.d(TAG, "Search");
            handleMenuSearch();
        } else if (i == R.id.action_sort) {
            Log.d(TAG, "Sort");
            showSortDialog();

        } else if (i == R.id.action_view) {
            Log.d(TAG, "View");
            showViewSettingsDialog();
        } else if (i == R.id.action_done) {
            Log.d(TAG, "Done");

            Intent returnIntent = new Intent();
            Bundle bundle = new Bundle();
            String key = action == FilePicker.Action.SELECT_MULTIPLE_DIRECTORIES
                    || action == FilePicker.Action.SELECT_SINGLE_DIRECTORY ? FilePicker.DIRECTORIES : FilePicker.FILES;
            bundle.putSerializable(key, (ArrayList<File>) ((FileAdapter) adapter).getSelectedItems());
            returnIntent.putExtras(bundle);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isSearchOpened) {
            closeSearchMenu();
            return;
        }

        onBack();
    }

    private void onBack() {
        if(((FileViewModel) viewModel).getCurrentDirectory().equals(FileUtils.getRootFolder())){
            Intent returnIntent = new Intent();
            setResult(Activity.RESULT_CANCELED, returnIntent);
            finish();
        }else{
            File parentDir = ((FileViewModel) viewModel).getCurrentDirectory().getParentFile();
            if(parentDir != null){
                ((FileViewModel) viewModel).setCurrentDirectory(parentDir);
            }
        }
    }

    //Dialogs
    private void showSortDialog() {
        final SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int sort = preferences.getInt(FilePicker.SORT, FilePicker.Sort.NAME_ASC);

        LayoutInflater inflater = this.getLayoutInflater();
        View titleView = inflater.inflate(R.layout.custom_title, null);
        TextView titleTextView = titleView.findViewById(R.id.title);
        titleTextView.setText(R.string.sort);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Alert_Dialog_Style)
        .setIcon(R.drawable.ic_sort_white_24dp)
        .setCustomTitle(titleView)
        .setTitle(R.string.sort)
        .setSingleChoiceItems(R.array.sort_items, sort, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                preferences.edit().putInt(FilePicker.SORT, item).commit();

                Toast.makeText(getApplicationContext(),
                        "Sorting : "+getResources().getStringArray(R.array.sort_items)[item], Toast.LENGTH_SHORT).show();
                dialog.dismiss();// dismiss the alertbox after chose option

                ((FileViewModel) viewModel).setSort(item);
            }
        });
        AlertDialog alert = dialog.create();
        alert.show();
    }

    private void showViewSettingsDialog() {
        final SharedPreferences preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        int layout = preferences.getInt(FilePicker.VIEW_LAYOUT, FilePicker.View.LAYOUT_GRID);
        boolean showHidden = preferences.getBoolean(FilePicker.VIEW_HIDDEN_FILES, true);
        boolean showFileExt = preferences.getBoolean(FilePicker.VIEW_FILES_EXTENSIONS, false);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this, R.style.Alert_Dialog_Style)
                .setView(R.layout.dialog_view_settings)
                .setIcon(R.drawable.ic_remove_red_eye_white_24dp)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        RadioGroup layoutGroup = ((AlertDialog) dialogInterface).findViewById(R.id.layout_settings);
                        CheckBox hiddenFiles = ((AlertDialog) dialogInterface).findViewById(R.id.chk_hidden_files);
                        CheckBox fileExtension = ((AlertDialog) dialogInterface).findViewById(R.id.chk_file_ext);

                        int radioButtonID = layoutGroup.getCheckedRadioButtonId();
                        View radioButton = layoutGroup.findViewById(radioButtonID);
                        int layout = layoutGroup.indexOfChild(radioButton);

                        boolean showHidden = hiddenFiles.isChecked();
                        boolean showFileExt = fileExtension.isChecked();

                        preferences.edit()
                                .putInt(FilePicker.VIEW_LAYOUT, layout)
                                .putBoolean(FilePicker.VIEW_HIDDEN_FILES, showHidden)
                                .putBoolean(FilePicker.VIEW_FILES_EXTENSIONS, showFileExt)
                                .commit();

                        ((FileViewModel) viewModel).setShowHidden(showHidden);

                        onRefresh();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog alert = dialog.create();
        alert.show();

        RadioGroup layoutGroup = alert.findViewById(R.id.layout_settings);
        CheckBox hiddenFiles = alert.findViewById(R.id.chk_hidden_files);
        CheckBox fileExtension = alert.findViewById(R.id.chk_file_ext);

        //Init
        ((RadioButton)layoutGroup.getChildAt(layout)).setChecked(true);
        hiddenFiles.setChecked(showHidden);
        fileExtension.setChecked(showFileExt);
    }

    //Search stuff
    private void handleMenuSearch() {
        if (isSearchOpened) {
            closeSearchMenu();
        } else {
            openSearchMenu();
        }
    }

    private void openSearchMenu() {
        ActionBar action = compatDelegate.getSupportActionBar();
        action.setCustomView(R.layout.search_bar);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                ((FileAdapter) adapter).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                ((FileAdapter) adapter).getFilter().filter(newText);
                return false;
            }
        });

        //add the close icon
        mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_clear_white_24dp));
        action.setTitle(getResources().getString(R.string.app_name));
        searchView.requestFocus();
        isSearchOpened = true;
    }

    private void closeSearchMenu() {
        //Hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);

        ActionBar action = compatDelegate.getSupportActionBar();
        mSearchAction.collapseActionView();
        mSearchAction.setIcon(getResources().getDrawable(R.drawable.ic_search_white_24dp));
        action.setTitle(getResources().getString(R.string.app_name));
        action.setDisplayShowCustomEnabled(false);
        action.setDisplayShowTitleEnabled(true);
        action.setDisplayShowHomeEnabled(true);

        isSearchOpened = false;
    }


    //Delegate
    @Override
    public void onClickFile(FileViewHolder vh, File file) {
        Log.d(TAG, "Click file !");
        if(action == FilePicker.Action.SELECT_SINGLE_FILE){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(FilePicker.FILE, file);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    @Override
    public void onClickDirectory(File dir, boolean selected) {
        Log.d(TAG, "Click dir !");
        if(selected){
            Intent returnIntent = new Intent();
            returnIntent.putExtra(FilePicker.DIRECTORY, dir);
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }else{
            ((FileViewModel) viewModel).setCurrentDirectory(dir);
        }
        //((FileViewModel) viewModel).setCurrentDirectory(dir);


    }

    @Override
    public void onItemsSelected(int count) {
        Log.d(TAG, count + " items selected");

        menu.clear();
        if(count > 0){
            String itemType = action == FilePicker.Action.SELECT_MULTIPLE_FILES ? getResources().getQuantityString(R.plurals.file,  count) : getString(R.string.folder);
            toolbar.setTitle(getString(R.string.selected_items, count, itemType));
            getMenuInflater().inflate(R.menu.menu_done, menu);
        }else{
            updateTitle();
            onCreateOptionsMenu(menu);
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btn_done){
            if(action.equals(FilePicker.Action.NEW_DIRECTORY)){
                createFolder();
            }

            onDoneNewFile();
        }
    }

    private void createFolder() {
        String folderName = newName.getText().toString();
        File folder = new File(((FileViewModel) viewModel).getCurrentDirectory(), folderName);
        boolean createdFolder = folder.mkdir();
        if(createdFolder){
            Log.d(TAG, "Folder created successfully!");
        }else{
            Log.e(TAG, "Folder creation error!");
        }
    }

    private void onDoneNewFile() {
        Log.d(TAG, "Done");
        KeyboardUtils.hideKeyboard(this, newName);

        String fileName = newName.getText().toString();
        File file = new File(((FileViewModel) viewModel).getCurrentDirectory(), fileName);
        Intent returnIntent = new Intent();
        returnIntent.putExtra(FilePicker.FILE, file);
        setResult(Activity.RESULT_OK, returnIntent);
        finish();

    }
}
