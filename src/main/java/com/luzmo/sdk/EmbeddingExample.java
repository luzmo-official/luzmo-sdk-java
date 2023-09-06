package com.luzmo.sdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import org.json.JSONObject;
import com.google.common.collect.ImmutableMap;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EmbeddingExample {
	
	public static void main(String[] args) throws Exception {
		// Run a lightweight webserver on localhost:5000
		HttpServer server = HttpServer.create(new InetSocketAddress(5000), 0);
		server.createContext("/", new IndexHandler());
		server.setExecutor(null);
		server.start();
		System.out.println("Server listening on port 5000");
  }

  static class IndexHandler implements HttpHandler {
        
		public void handle(HttpExchange t) throws IOException {
			String response;
			try {
				// Setup connection
				Luzmo client = new Luzmo("< Your API key >", "< Your API token >");

				// On page requests of pages containing embedded dashboards, request an "authorization"
				JSONObject authorization = client.create("authorization", ImmutableMap.builder()
					.put("type", "sso")
					.put("expiry", "24 hours")
					.put("inactivity_interval", "10 minutes")
					.put("username", "< A unique and immutable identifier for your end user >")
					.put("name", "< End user name >")
					.put("email", "< End user e-mail >")
					.put("suborganization", "< Suborganization name >")
					.put("integration_id", "< Integration ID >")
					.put("role", "viewer")
					.build()
				);
				
				// You'd probably want to use a template engine for this; 
				response = 
					"<!DOCTYPE html>" +
						"<html>" +
						"  <head>" +
						"    <meta charset=\"UTF-8\">" +
						"    <title>Luzmo embedding example</title>" +
						"  </head>" +
						"  <body>" +
						"    <div style=\"margin-left: 28px; width: 650px;\">" +
						"      <h1 style=\"font-weight: 200;\">Luzmo embedding example</h1>" +
						"      <p>This page contains an example of an embedded dashboard of Luzmo. The dashboard data is securely filtered server-side, so clients can only access data to which your application explicitly grants access (in this case, the \"Damflex\" product).</p>" +
						"    </div>" +
					  "    <luzmo-dashboard dashboardSlug=\"my-dashboard-slug\" authKey=\"" + authorization.getString("id") + "\" authToken=\"" + authorization.getString("token") + "\">" +
						"    </luzmo-dashboard>" +
						"    <script src=\"https://cdn.luzmo.com/js/luzmo-embed/5.0.0/luzmo-embed.min.js\"></script>" +
						"  </body>" +
						"</html>";
				
				t.sendResponseHeaders(200, response.length());
			}
			catch (Exception e) {
				response = "Oops, an error occurred during the connection to Luzmo: " + e.getMessage();
				t.sendResponseHeaders(500, response.length());
			}
        	
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
  	}
  }
}
