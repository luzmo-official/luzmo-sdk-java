package io.cumul.sdk;

import java.io.IOException;
import org.json.JSONObject;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CumulioExample {

	public static void main(String[] args) {
		
		try {
			// Setup connection
			Cumulio client = new Cumulio("< Your API key >","< Your API token >");
			
			// Example 1: create a new dataset
			JSONObject securable = client.create("securable",
				ImmutableMap.of(
					"type", "dataset",
					"name", ImmutableMap.of(
						"nl", "Burrito-statistieken",
						"en", "Burrito statistics"
					)
				)
			);
				
			// Example 2: update a dataset
			client.update("securable", securable.getString("id"), ImmutableMap.of(
				"description", ImmutableMap.of(
					"nl", "Het aantal geconsumeerde burrito\'s per type"
				)
			));
	
			// Example 3: create 2 columns
			client.create("column",
				ImmutableMap.of(
					"type", "hierarchy",
					"format", "",
					"informat", "hierarchy",
					"order", 0,
					"name", ImmutableMap.of(
						"nl", "Type burrito"
					)
				),
				ImmutableList.of(
					ImmutableMap.of(
						"role", "Securable",
						"id", securable.getString("id")
					)
				)
			);
			client.create("column",
				ImmutableMap.of(
					"type", "numeric",
					"format", ",.0f",
					"informat", "numeric",
					"order", 1,
					"name", ImmutableMap.of(
						"nl", "Burrito-gewicht"
					)
				),
				ImmutableList.of(
					ImmutableMap.of(
						"role", "Securable",
						"id", securable.getString("id")
					)
				)
			);
	
	
			// Example 4: push 2 data points to a (pre-existing) dataset
			client.create("data", ImmutableMap.of(
				"securable_id", securable.getString("id"),
				"data", ImmutableList.of(
					ImmutableList.of("sweet", 126),
					ImmutableList.of("sour", 352)
				)
			));
			
			System.out.println("Datasets & columns were created and data was pushed to the set!");
		
		}
		catch (IOException e) {
			System.out.println("Oops, an error occurred during the connection to Cumul.io: " + e.getMessage());
		}
	}
}
