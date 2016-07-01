package io.cumul.sdk;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONObject;
import com.google.common.collect.ImmutableList;
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
    			Cumulio client = new Cumulio("< Your API key >","< Your API token >");
    			
    			String dashboardId = "1d5db81a-3f88-4c17-bb4c-d796b2093dac";
    			Date expiry = new Date(Calendar.getInstance().getTimeInMillis() + 5*60*1000);
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    			
    			// On page requests of pages containing embedded dashboards, request an "authorization"
    			JSONObject authorization = client.create("authorization", ImmutableMap.builder()
    				.put("type","temporary")
    				// User restrictions
    				.put("expiry", sdf.format(expiry))
					// Data & dashboard restrictions
					.put("securables", ImmutableList.of(
						"4db23218-1bd5-44f9-bd2a-7e6051d69166",
						"f335be80-b571-40a1-9eff-f642a135b826",
						dashboardId
					))
					.put("filters", ImmutableList.of(
						ImmutableMap.builder()
							.put("clause",       "where")
							.put("origin",       "global")
							.put("securable_id", "4db23218-1bd5-44f9-bd2a-7e6051d69166")
							.put("column_id",    "3e2b2a5d-9221-4a70-bf26-dfb85be868b8")
						    .put("expression",   "? = ?")
						    .put("value",        "Damflex")
						    .build()
					))
					// Presentation options
					.put("locale_id","en")
					.put("screenmode", "desktop")
					.build()
    			);
    			
    			// Generate the embedding url
    			URL url = client.iframe(dashboardId, authorization);
    			
    			response = 
    				"<!DOCTYPE html>" +
			        "<html>" +
			        "  <head>" +
			        "    <meta charset=\"UTF-8\">" +
			        "    <title>Cumul.io embedding example</title>" +
			        "  </head>" +
			        "  <body>" +
			        "    <div style=\"margin-left: 28px; width: 650px;\">" +
			        "      <h1 style=\"font-weight: 200;\">Cumul.io embedding example</h1>" +
			        "      <p>This page contains an example of an embedded dashboard of Cumul.io. The dashboard data is securely filtered server-side, so clients can only access data to which your application explicitly grants access (in this case, the \"Damflex\" product).</p>" +
			        "    </div>" +
			        "    <iframe src=\"" + url + "\" style=\"border: 0; width: 1024px; height: 650px;\"></iframe>" +
			        "  </body>" +
			        "</html>";
    			
    			t.sendResponseHeaders(200, response.length());
    		}
    		catch (IOException e) {
    			response = "Oops, an error occurred during the connection to Cumul.io: " + e.getMessage();
    			t.sendResponseHeaders(500, response.length());
    		}
        	
			OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
