package net.mobindustry.mobigram.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.mobindustry.mobigram.R;
import net.mobindustry.mobigram.model.foursquare.FoursquareVenue;
import net.mobindustry.mobigram.utils.ImageLoaderHelper;
import net.mobindustry.mobigram.utils.Utils;

import java.io.Serializable;

public class FoursquareAdapter extends ArrayAdapter<FoursquareVenue> implements Serializable {

    private LayoutInflater inflater;

    public FoursquareAdapter(Context context) {
        super(context, 0);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_foursquare_list, parent, false);
        }

        ImageView iconPlace = (ImageView) convertView.findViewById(R.id.iconPlace);
        TextView textPlaceName = (TextView) convertView.findViewById(R.id.textPlaceName);
        TextView textPlaceAddress = (TextView) convertView.findViewById(R.id.textPlaceAddress);
        Utils.verifySetBackground(iconPlace, Utils.getShapeDrawable(R.dimen.foursquare_icon_size, Color.GRAY));
        FoursquareVenue foursquareVenue = getItem(position);
        if (foursquareVenue != null) {
            if (foursquareVenue.getFoursquareCategories().size() > 0) {
                ImageLoaderHelper.displayImage(foursquareVenue.getFoursquareCategories().get(0).getFoursquareCategoryIcon().getIconUrl(), iconPlace);
            }
            textPlaceName.setText(foursquareVenue.getName());
            textPlaceAddress.setText(foursquareVenue.getFoursquareLocation().getAddress());
        }
        return convertView;
    }
}
