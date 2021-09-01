package io.cumul.sdk;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class Cumulio {
	
	private final static String API_HOST = "https://api.cumul.io";
	private final static String VERSION = "0.1.0";
	
	private String key;
	private String token;
	private String api_host;

	/**
	 *
	 * @param key String, your Cumul.io API Key
	 * @param token String, your Cumul.io API Token
	 */
	public Cumulio(String key, String token) {
		this(key, token, API_HOST);
	}

	/**
	 *
	 * @param key String, your Cumul.io API Key
	 * @param token String, your Cumul.io API Token
	 * @param api_host String, Cumul.io API endpoint
	 */
	public Cumulio(String key, String token, String api_host) {
		this.key = key;
		this.token = token;
		this.api_host = api_host;
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param properties JSONObject of resource properties
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'create' call could not be succesfully completed
	 */
	public JSONObject create(String resource, JSONObject properties) throws IOException {
		return create(resource, properties, null);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param properties JSONObject of resource properties
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'create' call could not be succesfully completed
	 */
	public JSONObject create(String resource, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return create(resource, new JSONObject(properties), null);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param properties ImmutableMap of resource properties
	 * @param associations ImmutableList of associations
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'create' call could not be succesfully completed
	 */
	public JSONObject create(String resource, @SuppressWarnings("rawtypes") ImmutableMap properties, @SuppressWarnings("rawtypes") ImmutableList associations) throws IOException {
		return create(resource, new JSONObject(properties), new JSONArray(associations));
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param properties JSONObject of resource properties
	 * @param associations JSONArray of associations
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'create' call could not be succesfully completed
	 */
	public JSONObject create(String resource, JSONObject properties, JSONArray associations) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "create");
		query.put("properties", properties);
		query.put("associations", associations);
		return _emit(resource, "POST", query);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param filter ImmutableMap of filters
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'get' call could not be succesfully completed
	 */
	public JSONObject get(String resource, @SuppressWarnings("rawtypes") ImmutableMap filter) throws IOException {
		return get(resource, new JSONObject(filter));
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param filter JSONObject of filters
	 * @return JSONObject response if successful
	 * @throws IOException In case the 'get' call could not be succesfully completed
	 */
	public JSONObject get(String resource, JSONObject filter) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "get");
		query.put("find", filter);
		return _emit(resource, "SEARCH", query);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String id of resouce to be deleted
	 * @return Empty Response
	 * @throws IOException In case the 'delete' call could not be succesfully completed
	 */
	public JSONObject delete(String resource, String id) throws IOException {
		return delete(resource, id, new JSONObject());
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String id of resource to be deleted
	 * @param properties ImmutableMap properties of resource to be deleted
	 * @return Empty response
	 * @throws IOException In case the 'delete' call could not be succesfully completed
	 */
	public JSONObject delete(String resource, String id, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return delete(resource, id, new JSONObject(properties));
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String id of resource to be deleted
	 * @param properties JSONObject properties of resource to be deleted
	 * @return Empty response
	 * @throws IOException In case the 'delete' call could not be succesfully completed
	 */
	public JSONObject delete(String resource, String id, JSONObject properties) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "delete");
		query.put("id", id);
		query.put("properties", properties);
		return _emit(resource, "DELETE", query);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String id of resource to be updated
	 * @param properties ImmutableMap properties of resource to be updated
	 * @return JSONObject response
	 * @throws IOException In case the 'update' call could not be succesfully completed
	 */
	public JSONObject update(String resource, String id, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return update(resource, id, new JSONObject(properties));
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String id of resource to be updated
	 * @param properties JSONObject properties of resource to be updated
	 * @return JSONObject response
	 * @throws IOException In case the 'update' call could not be succesfully completed
	 */
	public JSONObject update(String resource, String id, JSONObject properties) throws IOException {
		JSONObject query = new JSONObject();
		query.put("action", "update");
		query.put("id", id);
		query.put("properties", properties);
		return _emit(resource, "PATCH", query);
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String, id of resource
	 * @param associationRole String
	 * @param associationId String
	 * @return JSONObject
	 * @throws IOException In case the 'associate' call could not be succesfully completed
	 */
	public JSONObject associate(String resource, String id, String associationRole, String associationId) throws IOException {
		return associate(resource, id, associationRole, associationId, new JSONObject());
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String, id of resource
	 * @param associationRole String
	 * @param associationId String
	 * @param properties ImmutableMap of properties
	 * @return JSONObject
	 * @throws IOException In case the 'associate' call could not be succesfully completed
	 */
	public JSONObject associate(String resource, String id, String associationRole, String associationId, @SuppressWarnings("rawtypes") ImmutableMap properties) throws IOException {
		return associate(resource, id, associationRole, associationId, new JSONObject(properties));
	}

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String, id of resource
	 * @param associationRole String
	 * @param associationId String
	 * @param properties JSONObject of properties
	 * @return JSONObject
	 * @throws IOException In case the 'associate' call could not be succesfully completed
	 */
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

	/**
	 *
	 * @param resource String, the resource type E.g.'securable'
	 * @param id String, id of resource
	 * @param associationRole String
	 * @param associationId String
	 * @return JSONObject
	 * @throws IOException In case the 'dissociate' call could not be succesfully completed
	 */
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

	/**
	 *
	 * @param filter
	 * @return
	 * @throws IOException In case the 'query' call could not be succesfully completed
	 */
	public JSONObject query(JSONObject filter) throws IOException {
		return get("data", filter);
	}

	/**
	 *
	 * @param filter
	 * @return
	 * @throws IOException In case the 'query' call could not be succesfully completed
	 */
	public JSONObject query(@SuppressWarnings("rawtypes") ImmutableMap filter) throws IOException {
		return get("data", filter);
	}

	/**
	 *
	 * @param resource
	 * @param action
	 * @param query
	 * @return
	 * @throws IOException In case the call could not be succesfully completed
	 */
	private JSONObject _emit(String resource, String action, JSONObject query) throws IOException {
		query.put("key", this.key);
		query.put("token", this.token);
		query.put("version", VERSION);
		String payload = query.toString();
				
		URL url = new URL(this.api_host + '/' + VERSION + '/' + resource);
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
		InputStream _is;

		if (conn.getResponseCode() < 400) {
			_is = conn.getInputStream();
		} else {
			/* error from server */
			_is = conn.getErrorStream();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(_is));
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
