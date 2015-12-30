package com.tn.webqawall;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.qa_wall_logger_client.RemoteLogger;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.tn.webqawall.socket.event.Page;
import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.module.BuildModule;
import io.palaima.debugdrawer.module.DeviceModule;
import io.palaima.debugdrawer.module.NetworkModule;
import io.palaima.debugdrawer.module.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

/**
 * Created by David Tolchinsky on 14/07/2015.
 */
public class MainActivity extends FragmentActivity
{
    private final static String URL_FROM_INTENT = "URL_FROM_INTENT";
    public static final String WAKE_LOCK_TAG = "WAKE_LOCK_TAG";
    private WebViewFragment webViewFragment;
    private PowerManager.WakeLock wakeLock;
    private DebugDrawer debugDrawer;

    public static final String TAG = "SOCKET";
    private static RemoteLogger remoteLogger;
    private Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.d("WQWLog", "Action Value: " + getIntent().getAction());
        Log.d("WQWLog", "Has Extras: " + (getIntent().getExtras() != null));

        webViewFragment = new WebViewFragment();

        if (getIntent().getAction().equals("android.intent.action.MAIN") &&
                getIntent().getExtras() != null)
        {
            Log.d("WQWLog", "Extra Value on URL_FROM_INTENT: " + getIntent().getExtras().get(URL_FROM_INTENT));
            String url = getIntent().getExtras().getString(URL_FROM_INTENT);

            webViewFragment.setUrl(url);
        }

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.main_activity);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.relative_qa_wall, webViewFragment);
        fragmentTransaction.commit();

        if (BuildConfig.DEBUG)
        {
            debugDrawer = new DebugDrawer.Builder(this).modules(
                    new ScalpelModule(this),
                    new DeviceModule(this),
                    new BuildModule(this),
                    new NetworkModule(this),
                    new SettingsModule(this)
            ).build();
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        if (debugDrawer != null)
        {
            debugDrawer.onStart();
        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        if (debugDrawer != null)
        {
            debugDrawer.onStop();
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("http://tn.codiarte.com/public/QA_Wall-Logger_Server-Helper/get_ip.php").build();
        client.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(final Request request, final IOException e)
            {
                Toast.makeText(MainActivity.this, "Error getting Node IP", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(final Response response) throws IOException
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    socket = IO.socket("http://" + jsonObject.getString("localIp") + ":" + jsonObject.getString("socket_port"));

                    remoteLogger = new RemoteLogger(new RemoteLogger.Listener()
                    {
                        @Override
                        public String onParseToJson(final com.qa_wall_logger_client.log.Log log)
                        {
                            return new Gson().toJson(log);
                        }

                        @Override
                        public void onSentToNetwork(final String parsedObject)
                        {
                            android.util.Log.d(TAG, "Sending message: " + parsedObject);
                            socket.emit(com.tn.webqawall.socket.event.Log.EVENT_NAME, parsedObject, new Ack()
                            {
                                @Override
                                public void call(final Object... args)
                                {
                                    android.util.Log.d(TAG, "Message sent: " + Arrays.toString(args));
                                }
                            });
                        }
                    });

                    socket.on("connect", new Emitter.Listener()
                    {
                        @Override
                        public void call(final Object... args)
                        {
                            android.util.Log.d(TAG, "Socket Connected: " + Arrays.toString(args));
                        }
                    });

                    socket.on("disconnect", new Emitter.Listener()
                    {
                        @Override
                        public void call(final Object... args)
                        {
                            android.util.Log.d(TAG, "Socket Disconnected: " + Arrays.toString(args));
                        }
                    });

                    socket.on(Page.EVENT_NAME, new Emitter.Listener()
                    {
                        @Override
                        public void call(final Object... args)
                        {
                            runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    Page pageEvent = new Gson().fromJson(String.valueOf(args[0]), Page.class);

                                    webViewFragment.setUrl(pageEvent.getUrl());
                                }
                            });
                        }
                    });

                } catch (JSONException | URISyntaxException e)
                {
                    Log.e(TAG, "Error connecting to Socket " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Error parsing the Node IP", Toast.LENGTH_LONG).show();
                }


                socket.connect();
            }
        });

        //Prevent screen sleep
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, WAKE_LOCK_TAG);
        wakeLock.acquire();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        if (socket != null)
        {
            socket.disconnect();
            socket.off(Page.EVENT_NAME);
        }

        //Release screen lock
        wakeLock.release();
    }

    @Override
    public void onBackPressed()
    {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.relative_qa_wall);

        if (currentFragment == webViewFragment)
        {
            if (((WebViewFragment) currentFragment).canGoBack())
            {
                ((WebViewFragment) currentFragment).goBack();
            } else
            {
                super.onBackPressed();
            }
        }
    }

}
