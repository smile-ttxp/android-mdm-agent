package org.flyve.mdm.agent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.flyve.mdm.agent.data.DataStorage;
import org.flyve.mdm.agent.utils.FlyveLog;
import org.flyve.mdm.agent.utils.Helpers;
import org.json.JSONObject;

public class FragmentInformation extends Fragment {

    private IntentFilter mIntent;
    private TextView txtOnline;
    private ImageView imgOnline;
    private DataStorage cache;
    private TextView txtNameUser;
    private TextView txtEmailUser;
    private TextView txtNameSupervisor;
    private TextView txtDescriptionSupervisor;

    private int countEasterEgg;

    @Override
    public void onPause() {
        // unregister the broadcast
        if(mIntent != null) {
            getActivity().unregisterReceiver(broadcastServiceStatus);
            getActivity().unregisterReceiver(broadcastMessage);
            mIntent = null;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        // register the broadcast
        super.onResume();
        LocalBroadcastManager.getInstance(FragmentInformation.this.getActivity()).registerReceiver(broadcastServiceStatus, new IntentFilter("flyve.mqtt.status"));
        LocalBroadcastManager.getInstance(FragmentInformation.this.getActivity()).registerReceiver(broadcastMessage, new IntentFilter("flyve.mqtt.msg"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_information, container, false);

        cache = new DataStorage(FragmentInformation.this.getActivity());

        ImageView imgLogo = (ImageView) v.findViewById(R.id.imgLogo);
        imgLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!cache.getEasterEgg()) {
                    countEasterEgg++;
                    if (countEasterEgg > 6 && countEasterEgg <= 10) {
                        Toast.makeText(FragmentInformation.this.getActivity(), "You have " + countEasterEgg + " Attempts", Toast.LENGTH_SHORT).show();
                    }
                    if (countEasterEgg >= 10) {
                        Toast.makeText(FragmentInformation.this.getActivity(), "Now you have log version agent", Toast.LENGTH_SHORT).show();
                        cache.setEasterEgg(true);
                    }
                }
            }
        });

        txtNameUser = (TextView) v.findViewById(R.id.txtNameUser);
        txtEmailUser = (TextView) v.findViewById(R.id.txtDescriptionUser);

        txtNameSupervisor = (TextView) v.findViewById(R.id.txtNameSupervisor);
        txtDescriptionSupervisor = (TextView) v.findViewById(R.id.txtDescriptionSupervisor);

        RelativeLayout layoutUser = (RelativeLayout) v.findViewById(R.id.rlUser);
        layoutUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openEditUser();
            }
        });

        txtOnline = (TextView) v.findViewById(R.id.txtOnline);
        imgOnline = (ImageView) v.findViewById(R.id.imgOnline);

        loadSupervisor();
        loadClientInfo();

        return v;
    }

    private void loadSupervisor() {
        txtNameSupervisor.setText("Teclib Spain SL");
        txtDescriptionSupervisor.setText("sales@teclib.com");
    }

    private void loadClientInfo() {
        txtNameUser.setText(cache.getUserFirstName() + " " + cache.getUserLastName());
        txtEmailUser.setText(cache.getUserEmail());
    }

    /**
     * broadcastServiceStatus instance that receive service status from MQTTService
     */
    private BroadcastReceiver broadcastServiceStatus = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String msg = intent.getStringExtra("message");

            // status ONLINE / OFFLINE
            if("flyve.mqtt.status".equalsIgnoreCase(action)) {
                try {
                    if (Boolean.parseBoolean(msg)) {
                        txtOnline.setText(getResources().getString(R.string.online));
                        imgOnline.setImageResource(R.drawable.ic_online);
                    } else {
                        txtOnline.setText(getResources().getString(R.string.offline));
                        imgOnline.setImageResource(R.drawable.ic_offline);
                    }
                } catch (Exception ex) {
                    FlyveLog.e(ex.getMessage());
                }
            }
        }
    };

    private BroadcastReceiver broadcastMessage = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String msg = intent.getStringExtra("message");

            // Message from service
            if("flyve.mqtt.msg".equalsIgnoreCase(action)) {

                try {
                    JSONObject json = new JSONObject(msg);

                    String type = json.getString("type");
                    String title = json.getString("title");
                    String body = json.getString("body");

                    if("action".equalsIgnoreCase(type) && "open".equalsIgnoreCase(title) && "splash".equalsIgnoreCase(body)) {
                        openSplash();
                    }

                    if("ERROR".equalsIgnoreCase(type)) {

                        Helpers.snack(FragmentInformation.this.getActivity(), body, "close", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                            }
                        });
                    }

                } catch (Exception ex) {
                    FlyveLog.d("ERROR" + ex.getMessage());
                }
            }
        }
    };

    private void openSplash() {
        Intent intent = new Intent(FragmentInformation.this.getActivity(), SplashActivity.class);
        FragmentInformation.this.getActivity().startActivity(intent);
        FragmentInformation.this.getActivity().finish();
    }

    private void openEditUser() {
        Intent intent = new Intent(FragmentInformation.this.getActivity(), EditUserActivity.class);
        FragmentInformation.this.getActivity().startActivity(intent);
        FragmentInformation.this.getActivity().finish();
    }
}