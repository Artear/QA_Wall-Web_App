package com.tn.webqawall;

import android.app.Application;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.qa_wall_logger_client.RemoteLogger;
import com.qa_wall_logger_client.log.Log;

import java.net.URISyntaxException;
import java.util.Arrays;


public class App extends Application
{
    public static final String SOCKET_HOOK_LOG = "log";
    private Socket socket;
    private static RemoteLogger remoteLogger;

    {
        try
        {
            socket = IO.socket("http://tn.codiarte.com:9187");
        } catch (URISyntaxException ignored)
        {
        }
    }

    @Override
    public void onCreate()
    {
        super.onCreate();

        remoteLogger = new RemoteLogger(new RemoteLogger.Listener()
        {
            @Override
            public String onParseToJson(final Log log)
            {
                return new Gson().toJson(log);
            }

            @Override
            public void onSentToNetwork(final String parsedObject)
            {
                android.util.Log.d("LOG", "Sending message: " + parsedObject);
                socket.emit(SOCKET_HOOK_LOG, parsedObject, new Ack()
                {
                    @Override
                    public void call(final Object... args)
                    {
                        android.util.Log.d("LOG", "Message sent: " + Arrays.toString(args));
                    }
                });
            }
        });

        socket.on("connect", new Emitter.Listener()
        {
            @Override
            public void call(final Object... args)
            {
                android.util.Log.d("LOG", "Socket Connected: " + Arrays.toString(args));
            }
        });

        socket.connect();
    }

    public static RemoteLogger getRemoteLogger()
    {
        return remoteLogger;
    }
}
