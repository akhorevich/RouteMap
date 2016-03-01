package nortti.ru.routemap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;

import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FromFragment extends Fragment {
    private ArrayAdapter<String> adapter;


    Geocoder geocoder;
    final static int maxResults = 7;
    List<Address> locationList;
    List<String> locationNameList;

    EditText etSearch;
    ListView lvSearch;
    MapView mapView;
    GoogleMap map;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_to, container, false);

        etSearch = (EditText) v.findViewById(R.id.etSearch);
        etSearch.setFocusable(false);
        lvSearch = (ListView) v.findViewById(R.id.lvSearch);
        mapView = (MapView) v.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);

        map = mapView.getMap();
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return getView();
        }
        map.setMyLocationEnabled(true);

        MapsInitializer.initialize(this.getActivity());

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(55.7, 37.6), 10);
        map.animateCamera(cameraUpdate);
        geocoder = new Geocoder(getActivity());
        etSearch.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                doSearch(etSearch.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        locationNameList = new ArrayList<String>();
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, locationNameList);
        lvSearch.setAdapter(adapter);
lvSearch.setOnScrollListener(new EndlessScrollListener() {
    int offset = 3;

    @Override
    public boolean onLoadMore(int page, int totalItemsCount) {
        customLoadMoreDataFromApi(page);
        return true;
    }
    public void customLoadMoreDataFromApi(int offset) {
        this.offset = offset;
    }
});

        return v;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void doSearch(String query) {


        try {
            locationList = geocoder.getFromLocationName(query, maxResults);


            locationNameList.clear();

            for (Address i : locationList) {
                StringBuilder strReturnedAddress = new StringBuilder("");
                if (i.getFeatureName() == null) {
                    locationNameList.add("unknown");
                } else {
                    for (int a = 0; a < i.getMaxAddressLineIndex(); a++) {
                        strReturnedAddress.append(i.getAddressLine(a)).append(" ");

                    }

                    locationNameList.add(strReturnedAddress.toString());
                }


            }

            adapter.notifyDataSetChanged();
            lvSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    double lat = locationList.get(position).getLatitude();
                    double lng = locationList.get(position).getLongitude();
                    LatLng latLng = new LatLng(lat, lng);
                    map.clear();
                    String address = locationList.get(position).getAddressLine(0).toString();
                    String ltln = latLng.toString();
                    map.addMarker(new MarkerOptions().position(latLng).title(address));
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
                    map.animateCamera(cameraUpdate);
                    ((DataActivity) getActivity()).lat1 = lat;
                    ((DataActivity) getActivity()).lng1 = lng;

                }
            });

        } catch (IOException e) {
            Toast.makeText(getActivity(),
                    "network unavailable or any other I/O problem occurs",
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        String result = "Finished - " + query;
    }
}