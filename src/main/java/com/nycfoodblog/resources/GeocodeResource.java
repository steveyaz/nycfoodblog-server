package com.nycfoodblog.resources;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import javax.annotation.security.PermitAll;
import javax.net.ssl.HttpsURLConnection;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.Lists;

import org.json.JSONArray;
import org.json.JSONObject;

@Path("/geocode")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Timed
public class GeocodeResource {

    private final String googleApiKey;

    public GeocodeResource(String googleApiKey) {
        this.googleApiKey = googleApiKey;
    }

    @GET
    @Path("/{address}")
    @PermitAll
    public Response geocode(@PathParam("address") String address) {
        String geocode_url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + this.googleApiKey;
        URL url;
        try {
            url = new URL(geocode_url);
            HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
            String jsonText = readAll(br);
            JSONObject json = new JSONObject(jsonText);
            System.out.println(json.toString());
            br.close();
            if (json.has("results")
                    && json.getJSONArray("results").length() > 0
                    && json.getJSONArray("results").getJSONObject(0).has("geometry")
                    && json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").has("location")
                    && json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").has("lng")
                    && json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").has("lat")) {
                return Response.ok(Lists.newArrayList(
                        Double.valueOf(json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat")),
                        Double.valueOf(json.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng")))).build();
            } else {
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } catch (IOException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
