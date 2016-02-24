package com.gnr.esgi.taggle.activitie;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.adapter.ListTagsAdapter;
import com.gnr.esgi.taggle.helper.TagHelper;
import com.gnr.esgi.taggle.listener.TagsMultiChoiceModeListener;
import com.gnr.esgi.taggle.model.Tag;
import com.gnr.esgi.taggle.util.Config;
import com.gnr.esgi.taggle.util.StringUtil;
import java.util.ArrayList;
import java.util.List;

public class TagSettingsActivity extends AppCompatActivity {

    List<Tag> tagsList = new ArrayList<>();
    ListView tagsListView;
    ListTagsAdapter tagsAdapter;
    FloatingActionButton floatingActionButton;
    AppBarLayout appBar;
    Toolbar toolbar;
    EditText searchTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(Config.getTheme());
        setContentView(R.layout.activity_tag_settings);

        appBar = (AppBarLayout) findViewById(R.id.tag_settings_app_bar);
        toolbar = (Toolbar) findViewById(R.id.tag_setting_toolbar);
        setSupportActionBar(toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.action_tag_add);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewTagDialog(
                        searchTag.getText().length() > 0
                                ? searchTag.getText().toString()
                                : null
                );
            }
        });

        tagsListView = (ListView) findViewById(R.id.tag_settings_list);

        tagsAdapter = new ListTagsAdapter(getApplicationContext(), tagsList);
        tagsListView.setAdapter(tagsAdapter);
        tagsListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        tagsListView.setMultiChoiceModeListener(
                new TagsMultiChoiceModeListener(
                        tagsListView,
                        tagsAdapter,
                        toolbar,
                        appBar,
                        floatingActionButton
                )
        );

        tagsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectTag(
                        tagsList.get(
                                position
                        )
                );
            }
        });

        searchTag = (EditText) findViewById(R.id.tag_setting_search);
        searchTag.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                List<Tag> tagsList = TagHelper.getTags();

                if(s.length() > 0) {
                    TagSettingsActivity.this.tagsList.clear();
                    List<Tag> tempTags = new ArrayList<Tag>();

                    for (Tag tag : tagsList) {
                        // Compare existing tags with search (ignore capitalization)
                        if(tag.getName().startsWith(StringUtil.capitalize(s.toString()))) {
                            tempTags.add(tag);
                        }
                    }

                    TagSettingsActivity.this.tagsList = tempTags;
                }
                else {
                    TagSettingsActivity.this.tagsList = tagsList;
                }

                tagsAdapter.swapItems(TagSettingsActivity.this.tagsList);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        tagsList = TagHelper.getTags();

        tagsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        tagsAdapter.swapItems(tagsList);
    }

    public void showNewTagDialog(String tagName) {
        final EditText input = new EditText(TagSettingsActivity.this);

        if (tagName != null)
            input.setText(tagName);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);

        final AlertDialog.Builder builder
                = new AlertDialog
                .Builder(this)
                .setTitle(R.string.addTagDialog_title)
                .setCancelable(false)
                .setPositiveButton(R.string.addTagDialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Create new tage (capitalize first letter)
                        Tag tag = new Tag(
                                StringUtil.capitalize(
                                        input.getText().toString()
                                )
                        );

                        // Add new tag to database
                        Taggle.getDao().addTag(tag);

                        // Update tag list with new added tag
                        tagsAdapter.swapItems(TagHelper.getTags());

                        dialog.dismiss();
                    }
                })
                .setNegativeButton(R.string.addTagDialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setView(input);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void selectTag(Tag tag) {
        Taggle.getSession().setCurrentTag(tag);

        finish();
    }
}
