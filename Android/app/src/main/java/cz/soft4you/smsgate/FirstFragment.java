package cz.soft4you.smsgate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cz.soft4you.smsgate.databinding.FragmentFirstBinding;

public class FirstFragment extends Fragment {
    private static int totalSended;
    private static int totalReceived;
    private FragmentFirstBinding binding;
    private BroadcastReceiver receiver;
    private final String startDateTime = getCurrentDateTime();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.since.setText(startDateTime);

        IntentFilter filter = new IntentFilter("SEND_RECEIVE_SMS");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String data = intent.getStringExtra("key");
                if (Objects.equals(data, "send")) {
                    binding.lastSended.setText(getCurrentDateTime());

                    totalSended++;

                    binding.countSended.setText(String.valueOf(totalSended));
                }
                if (Objects.equals(data, "receive")) {
                    binding.lastReceived.setText(getCurrentDateTime());

                    totalReceived++;

                    binding.countReceived.setText(String.valueOf(totalReceived));
                }
            }
        };

        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver);
    }

    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        Date now = new Date();

        return sdf.format(now);
    }
}