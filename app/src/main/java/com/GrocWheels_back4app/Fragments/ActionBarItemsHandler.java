package com.GrocWheels_back4app.Fragments;

import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.GrocWheels_back4app.MainActivity;
import com.GrocWheels_back4app.R;
import com.parse.ParseUser;

/**
 * ActionBarItemsHandler, has all the requirements for a fragment and has actionbar items which are
 * present in all the fragments
 * Extemds
 * Created by mkrao on 11/17/2016.
 */

public class ActionBarItemsHandler extends Fragment {


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_sign_out:
               // Logout Line
                ParseUser.logOut();
                Toast toastLogout = Toast.makeText(getActivity(), "Logged Out", Toast.LENGTH_SHORT);
                toastLogout.show();
                ((MainActivity) getActivity()).setGreenActionBar();
                ((MainActivity) getActivity()).replaceFragment(new LoginFragment(),true);
                return true;

            case R.id.menu_profile:
                // Logout Line

                ((MainActivity) getActivity()).replaceFragment(new UserProfile(),true);

                return true;

            case R.id.add_request:
                ((MainActivity) getActivity()).replaceFragment(new AddRequest(), true);
                return (true);

            case R.id.refresh_request:
            //    Call Load Request Method
                Toast toast = Toast.makeText(getActivity(), "Refreshed", Toast.LENGTH_SHORT);
                toast.show();
                return (true);
        }

        return super.onOptionsItemSelected(item);
    }

    // Action Bar Items for Menu items like Sign Out
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_list_items, menu);
    }
}
