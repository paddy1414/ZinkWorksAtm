package org.patricknorton.zinkworks.ZinkWorksAtm;

import org.json.JSONArray;
import org.patricknorton.zinkworks.ZinkWorksAtm.Objects.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class RestOperations {
   private static RestOperations instance;

    public synchronized static RestOperations getInstance() {
        if (instance == null) {
            instance = new RestOperations();
            // instance.fillAtm();
        }
        return instance;
    }

    public Response restCall(String method, String url) throws IOException {
        String rawResultsString;
        Response responseObj;
        URL target = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) target.openConnection();
        conn.setRequestMethod(method);

        conn.connect();

        try {
            InputStream response = conn.getInputStream();
            rawResultsString = new BufferedReader(new InputStreamReader(response))
                    .lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            InputStream error = conn.getErrorStream();
             rawResultsString = new BufferedReader(new InputStreamReader(error))
                    .lines().collect(Collectors.joining("\n"));
        }
        responseObj = new Response(rawResultsString, conn.getResponseCode());
        return responseObj;
    }

}
