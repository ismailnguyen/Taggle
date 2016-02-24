package com.gnr.esgi.taggle.listener;

import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import com.gnr.esgi.taggle.Taggle;
import com.gnr.esgi.taggle.R;
import com.gnr.esgi.taggle.adapter.ListTagsAdapter;
import com.gnr.esgi.taggle.model.Tag;

public class TagsMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {

    private ListView listView;
    private ListTagsAdapter adapter;
    private Toolbar toolbar;
    private AppBarLayout appBar;
    private FloatingActionButton floatingActionButton;

    public TagsMultiChoiceModeListener(ListView listView,
                                           ListTagsAdapter adapter,
                                           Toolbar toolbar,
                                           AppBarLayout appBar,
                                           FloatingActionButton floatingActionButton) {
        this.listView = listView;
        this.adapter = adapter;
        this.toolbar = toolbar;
        this.appBar = appBar;
        this.floatingActionButton = floatingActionButton;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        // Capture total checked items
        final int checkedCount = listView.getCheckedItemCount();

        String message = new StringBuilder()
                .append(checkedCount)
                .append(" ")
                .append(checkedCount > 1
                        ? " tags selected"
                        : " tag selected")
                .toString();

        // Set the CAB title according to total checked items
        mode.setTitle(message);

        // Calls toggleSelection method fromCursor adapter Class
        adapter.toggleSelection(position);
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.activity_tags, menu);
        toolbar.setVisibility(View.GONE);
        appBar.setVisibility(View.GONE);
        floatingActionButton.setVisibility(View.GONE);

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tag_delete:
                // Calls getSelectedIds method fromCursor ListViewAdapter Class
                SparseBooleanArray selected = adapter.getSelectedIds();

                // Captures all selected ids with a loop
                for (int i = (selected.size() - 1); i >= 0; i--) {
                    if (selected.valueAt(i)) {
                        Tag selectedItem = adapter.getItem(selected.keyAt(i));

                        // Update database, delete item
                        Taggle.getDao().deleteTag(selectedItem.getName());

                        // Remove selected items following the ids
                        adapter.remove(selectedItem);

                        // Delete articles fromCursor this tag on database
                        Taggle.getDao().deleteArticles(selectedItem);
                    }
                }

                // Close CAB
                mode.finish();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        adapter.removeSelection();
        toolbar.setVisibility(View.VISIBLE);
        appBar.setVisibility(View.VISIBLE);
        floatingActionButton.setVisibility(View.VISIBLE);
    }
}
