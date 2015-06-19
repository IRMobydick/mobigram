package net.mobindustry.telegram.ui.fragments;

import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.melnykov.fab.FloatingActionButton;

import net.mobindustry.telegram.R;
import net.mobindustry.telegram.core.ApiClient;
import net.mobindustry.telegram.core.handlers.BaseHandler;
import net.mobindustry.telegram.core.handlers.MessageHandler;
import net.mobindustry.telegram.model.holder.MessagesFragmentHolder;

import org.drinkless.td.libcore.telegram.TdApi;

public class LocationFragment extends Fragment implements ApiClient.OnApiResultHandler {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private Marker myMarker;
    private Toolbar toolbar;
    private TextView textCurrentPosition;
    private FloatingActionButton buttonSendLocation;
    private FloatingActionButton buttonFoursquare;
    private LatLng userLocation;
    private LocationManager service;

    @Override
    public void onApiResult(BaseHandler output) {}

    public void sendGeoPointMessage(double lat, double lng) {
        long id = MessagesFragmentHolder.getInstance().getChat().id;
        new ApiClient<>(new TdApi.SendMessage(id, new TdApi.InputMessageGeoPoint(lng, lat)),
                new MessageHandler(), this).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
        getActivity().finish();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.location_fragment_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        textCurrentPosition = (TextView) getActivity().findViewById(R.id.textCurrentPosition);

        buttonSendLocation = (FloatingActionButton) getActivity().findViewById(R.id.buttonSendLocation);
        buttonSendLocation.setColorPressedResId(R.color.button_send_location_pressed);
        buttonSendLocation.setColorNormalResId(R.color.button_send_location);
        buttonSendLocation.setShadow(true);
        buttonSendLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = myMarker.getPosition().latitude;
                double lng = myMarker.getPosition().longitude;
                sendGeoPointMessage(lat, lng);
            }
        });

        buttonFoursquare = (FloatingActionButton) getActivity().findViewById(R.id.buttonWatchLocationList);
        buttonFoursquare.setColorPressedResId(R.color.button_foursquare_pressed);
        buttonFoursquare.setColorNormalResId(R.color.button_foursquare);
        buttonFoursquare.setShadow(true);
        buttonFoursquare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar_map);
        toolbar.inflateMenu(R.menu.map_menu);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setTitle(R.string.text_location);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.satellite_item_menu:
                        map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        init();
                        return true;
                    case R.id.hybrid_item_menu:
                        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                        init();
                        return true;
                    case R.id.map_item_menu:
                        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                        init();
                        return true;
                }
                return false;
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                init();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab); //my custom button
        fab.setColorPressedResId(R.color.button_on_map_pressed);
        fab.setColorNormalResId(R.color.button_on_map);
        fab.setShadow(true);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userLocation != null) {
                    map.clear();
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 14));
                    myMarker = map.addMarker(new MarkerOptions()
                            .position(userLocation)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    textCurrentPosition.setText("(" + String.valueOf(userLocation.latitude
                            + " , " + String.valueOf(userLocation.longitude) + ")"));
                }
            }
        });
    }

    private Location getLastKnownLocation() {
        Location location = null;
        if (service.isProviderEnabled(LocationManager.GPS_PROVIDER))
            location = service.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        else if (service.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
            location = service.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        return location;
    }

    private void init() {
        map.setMyLocationEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        service = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);
        Location location = getLastKnownLocation();
        userLocation = new LatLng(location.getLatitude(), location.getLongitude());

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(userLocation)
                .zoom(14)
                .build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
        map.animateCamera(cameraUpdate);
        if (myMarker == null) {
            myMarker = map.addMarker(new MarkerOptions()
                    .position(new LatLng(userLocation.latitude, userLocation.longitude))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            Log.e("LOG", "LAT " + location.getLatitude());
            Log.e("LOG", "LNG " + location.getLongitude());
            textCurrentPosition.setText("(" + String.valueOf(location.getLatitude()
                    + " , " + String.valueOf(location.getLongitude()) + ")"));
        }

        if (map != null) {
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    map.clear();
                    myMarker = map.addMarker(new MarkerOptions()
                            .position(new LatLng(latLng.latitude, latLng.longitude))
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                    textCurrentPosition.setText("(" + String.valueOf(String.valueOf(latLng.latitude)
                            + " , " + String.valueOf(String.valueOf(latLng.longitude)) + ")"));
                }
            });
        }
    }


}