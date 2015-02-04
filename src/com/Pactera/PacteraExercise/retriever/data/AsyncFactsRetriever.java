package com.Pactera.PacteraExercise.retriever.data;

import android.os.AsyncTask;
import android.util.Log;
import com.Pactera.PacteraExercise.model.FactsList;
import com.google.gson.Gson;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * This class asynchronously creates a httpConnection with the server of the given uri
 * and downloads the json message.  The json is then decoded and inserted int he the FacstList
 * model objects.  Any listeners register to this will be informed of the new data when it arrives.
 * Note Only one listener can be registered at any time.
 */
public class AsyncFactsRetriever extends AsyncTask<String, Void, FactsList> {

    FactsItemsListener factItemsListener = null ;


    public void fetch(String uri, FactsItemsListener callback) {
        this.factItemsListener = callback ;
        this.execute(uri);
    }

    @Override
    protected FactsList doInBackground(String... params) {
        String url = params[0];

        try {
            FactsList factsList = getItemsFromServer(url);
            return factsList;
        } catch (Exception e) {
            Log.i("AsyncFactsRetriever", "Could not retrieve Facts " + e.getMessage());
        }
        return null;
    }


    /**
     * AsyncTask returned from retrieving facts. If successful notify all who wants to know.
     */
    @Override
    protected void onPostExecute(FactsList factsList) {
        factItemsListener.factItemsUpdated(factsList);
    }


    // Given the url setup a http get connection and receive the stream
    // of json data.  Pass that into a gson parser to receive the DOM.
    private FactsList getItemsFromServer(String url) throws IOException {
        InputStream inputStream = null;
        try {
            HttpClient httpClient = new DefaultHttpClient();
            final HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Accept-Charset", "iso-8859-1, unicode-1-1;q=0.8");
            HttpResponse httpResponse = httpClient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();
            if (inputStream != null) {
                return convertJsonStreamToDOM(inputStream);
            }
            return null;
        }finally {
            if ( inputStream != null ) {
                inputStream.close();
            }
        }
    }

    // Get the input stream containing the Json message and convert it into the FactsList DOM
    private FactsList convertJsonStreamToDOM(InputStream inputStream) throws UnsupportedEncodingException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "iso-8859-1");
        final Gson gson = new Gson();
        FactsList factsList = gson.fromJson(inputStreamReader, FactsList.class);
        return factsList;
    }


}
