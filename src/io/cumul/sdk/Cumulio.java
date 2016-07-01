package io.cumul.sdk;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Cumulio {
	
	private final static String APP = "https://app.cumul.io";
	private final static String HOST = "https://api.cumul.io";
	private final static int PORT = 443;
	private final static String VERSION = "0.1.0";
	
	private String key;
	private String token;
	
	public Cumulio(String key, String token) {
		this.key = key;
		this.token = token;
	}
	
	public JSONObject create(String resource, JSONObject properties) throws IOException {
		return create(resource, properties, null);
	}
	
	public JSONObject create(String resource, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return create(resource, new JSONObject(properties), null);
	}
	
	public JSONObject create(String resource, @SuppressWarnings("rawtypes") ImmutableMap properties, @SuppressWarnings("rawtypes") ImmutableList associations) throws IOException {
		return create(resource, new JSONObject(properties), new JSONArray(associations));
	}
	
	public JSONObject create(String resource, JSONObject properties, JSONArray associations) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "create");
		query.put("properties", properties);
		query.put("associations", associations);
		return _emit(resource, "POST", query);
	}
	
	public JSONObject get(String resource, @SuppressWarnings("rawtypes") ImmutableMap filter) throws IOException {
		return get(resource, new JSONObject(filter));
	}
	
	public JSONObject get(String resource, JSONObject filter) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "get");
		query.put("find", filter);
		return _emit(resource, "SEARCH", query);
	}
	
	public JSONObject delete(String resource, String id) throws IOException {
		return delete(resource, id, new JSONObject());
	}
	
	public JSONObject delete(String resource, String id, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return delete(resource, id, new JSONObject(properties));
	}
	
	public JSONObject delete(String resource, String id, JSONObject properties) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "delete");
		query.put("id", id);
		query.put("properties", properties);
		return _emit(resource, "DELETE", query);
	}
	
	public JSONObject update(String resource, String id, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return update(resource, id, new JSONObject(properties));
	}
	
	public JSONObject update(String resource, String id, JSONObject properties) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "update");
		query.put("id", id);
		query.put("properties", properties);
		return _emit(resource, "PATCH", query);
	}
	
	public JSONObject associate(String resource, String id, String associationRole, String associationId) throws IOException {
		return associate(resource, id, associationRole, associationId, new JSONObject());
	}
	
	public JSONObject associate(String resource, String id, String associationRole, String associationId, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return associate(resource, id, associationRole, associationId, new JSONObject(properties));
	}
	
	public JSONObject associate(String resource, String id, String associationRole, String associationId, JSONObject properties) throws IOException {
		JSONObject association = new JSONObject();
		association.put("role", associationRole);
		association.put("id", associationId);
		
		JSONObject query = new JSONObject();
		query.put("action", "associate");
		query.put("id", id);
		query.put("resource", association);
		query.put("properties", properties);
		
		return _emit(resource, "LINK", query);
	}
	
	public JSONObject dissociate(String resource, String id, String associationRole, String associationId) throws IOException {
		JSONObject association = new JSONObject();
		association.put("role", associationRole);
		association.put("id", associationId);
		
		JSONObject query = new JSONObject();
		query.put("action", "associate");
		query.put("id", id);
		query.put("resource", association);
		
		return _emit(resource, "UNLINK", query);
	}
	
	public JSONObject query(JSONObject filter) throws IOException {
		return get("data", filter);
	}
	
	public JSONObject query(@SuppressWarnings("rawtypes") ImmutableMap filter) throws IOException {
		return get("data", filter);
	}
	
	public URL iframe(String dashboardId, JSONObject authorization) throws IOException {
		return new URL(APP + "/s/" + dashboardId + "?key=" + authorization.getString("id") + "&token=" + authorization.getString("token"));
	}
	
	private JSONObject _emit(String resource, String action, JSONObject query) throws IOException {
		query.put("key", this.key);
		query.put("token", this.token);
		query.put("version", VERSION);
		String payload = query.toString();
				
		URL url = new URL(HOST + ':' + PORT + '/' + VERSION + '/' + resource);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("X-HTTP-Method-Override", "PATCH");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Content-Length", Integer.toString(payload.length()));
		conn.setDoOutput(true);
		
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeBytes(payload);
		dos.close();
		
		String line;
		StringBuilder result = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while((line = br.readLine()) != null) {
			result.append(line);
	    }
		
		try {
			return new JSONObject(result.toString());
		}
		catch (JSONException e) {
			JSONObject emptyResult = new JSONObject();
			emptyResult.put("result", result);
			return emptyResult;
		}
	}

}
