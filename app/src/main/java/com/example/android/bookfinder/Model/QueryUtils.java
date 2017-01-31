package com.example.android.bookfinder.Model;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();

    private QueryUtils() {
    }

    // Returns a list of books parsed from the JSON Response
    public static List<Book> extractBooks(String requestUrl) {
        Log.i(LOG_TAG, "Extracting book information");

        // Create the URL object
        URL url = createUrl(requestUrl);

        // Make an HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Could not establish HTTP connection", e);
        }

        // Return the list of earthquakes
        return extractFeaturesFromJson(jsonResponse);
    }

    // Returns a new URL object from stringUrl
    private static URL createUrl(String stringUrl) {
        Log.i(LOG_TAG, "Creating url.");
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error creating url.", e);
        }
        return url;
    }

    // Makes an HTTP request to the URL and returns a String response
    private static String makeHttpRequest(URL url) throws IOException {
        Log.i(LOG_TAG, "Making HTTP Request.");
        String jsonResponse = "";

        // Method will return an empty String if url is null
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            // Check if a 200 response was received
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error creating connection", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        return jsonResponse;
    }

    // Convert the inputStream into a string which contains the JSON response
    private static String readFromStream(InputStream inputStream) throws IOException {
        Log.i(LOG_TAG, "Reading from stream.");
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }

        return output.toString();
    }

    // Takes the JSON Response and returns the book title, book authors and book url
    private static List<Book> extractFeaturesFromJson(String jsonResponse) {
        Log.i(LOG_TAG, "Parsing JSON response.");
        // If JSON response is empty, then return null
        if (TextUtils.isEmpty(jsonResponse)) {
            Log.i(LOG_TAG, "There is no JSON response");
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray itemsArray = baseJsonResponse.getJSONArray("items");

            // If there are features in baseJsonResponse
            if (itemsArray.length() > 0) {
                for (int i = 0, j = itemsArray.length(); i < j; i++) {
                    // Extract currentItem object
                    JSONObject currentItem = itemsArray.getJSONObject(i);

                    // Get the volume info object
                    JSONObject volumeInfo = currentItem.getJSONObject("volumeInfo");

                    // Extract the book title
                    String bookTitle = volumeInfo.getString("title");
                    Log.i(LOG_TAG, "Title: " + bookTitle);

                    // Checks if author is contained in JSON response
                    StringBuilder authorsBuild = new StringBuilder("");
                    String authors;

                    try {
                        JSONArray authorArray = volumeInfo.getJSONArray("authors");
                        for (int a = 0, b = authorArray.length(); a < b; a++) {
                            if (a > 0) {
                                authorsBuild.append(", ");
                            }
                            authorsBuild.append(authorArray.getString(a));
                        }

                        authors = authorsBuild.toString();
                    } catch (JSONException e) {
                        authors = "";
                        e.printStackTrace();
                    }

                    Log.i(LOG_TAG, "Authors: " + authors);

                    // Get the information link
                    String infoLink = volumeInfo.getString("infoLink");
                    Log.i(LOG_TAG, "Link: " + infoLink);

                    // Add new Book object to Book list
                    books.add(new Book(bookTitle, authors, infoLink));
                    Log.i(LOG_TAG, "SUCCESS!");
                }
                return books;
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing the JSON Response.", e);
        }
        return null;
    }
}

