package main.taskmanager.javaActions;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import main.taskmanager.R;

/**
 * Class MainDrawerListAdapter.
 * Used to create an adapter for the drawer on home page
 */

public class MainDrawerListAdapter extends ArrayAdapter<User> {

    /**
     * viewHoder Class used to get a text view for the user name and point amount
     */

    private static class ViewHolder {
        private TextView userName;
        private TextView pointAmount;
    }

    /**
     * The constructor
     * @param textViewResourceId id for name's text view.
     * @param items all the users.
     * @param context Application context.
     */

    public MainDrawerListAdapter(Context context, int textViewResourceId, ArrayList<User> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.drawer_list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.userName = (TextView) convertView.findViewById(R.id.userName);
            viewHolder.pointAmount = (TextView) convertView.findViewById(R.id.pointAmount);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        User user = getItem(position);
        if (user!= null) {
            viewHolder.userName.setText(SimpleAction.capitalizeString(user.getUsername()) + " \"" + user.getTitle() + "\"");
            viewHolder.pointAmount.setText(String.valueOf(user.getPointAmount()));
        }

        return convertView;


    }
}