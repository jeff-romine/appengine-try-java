package myapp;
// Imports the Google Cloud client library


import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.OrderBy;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class ListFulfillmentKeys {
    public static void main(String... args) throws Exception {
        // Instantiates a client
        final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        if (!datastore.getOptions().getProjectId().equals("search-engine-252715")) {
            System.exit(-1);
        }

        final DateTime start = DateTime.now().withTimeAtStartOfDay().minusWeeks(2);
        final Timestamp ts = Timestamp.ofTimeMicroseconds(start.getMillis() * 1000L);

        com.google.cloud.datastore.KeyQuery query = Query.newKeyQueryBuilder()
            .setKind("Fulfillment")
            .setOrderBy(OrderBy.asc("created"))
            .setOffset(2490000)
            .build();

        QueryResults<Key> fulfillments = datastore.run(query);
        int counter=0;

        PrintWriter printWriter = new PrintWriter(new FileWriter(new File("fulfillment-keys.txt")));
        try {
            while (fulfillments.hasNext()) {
                final Key key = fulfillments.next();
                printWriter.println(key.toUrlSafe());

                if ((++counter % 1000) == 0) {
                    printWriter.flush();
                    System.out.println("count: " + counter);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        printWriter.flush();
        printWriter.close();
//
//        // The kind for the new entity
//        String kind = "Task";
//        // The name/ID for the new entity
//        String name = "sampletask1";
//        // The Cloud Datastore key for the new entity
//        Key taskKey = datastore.newKeyFactory().setKind(kind).newKey(name);
//
//        // Prepares the new entity
//        Entity task = Entity.newBuilder(taskKey)
//            .set("description", "Buy milk")
//            .build();
//
//        // Saves the entity
//        datastore.put(task);
//
//        System.out.printf("Saved %s: %s%n", task.getKey().getName(), task.getString("description"));
//
//        //Retrieve entity
//        Entity retrieved = datastore.get(taskKey);
//
//        System.out.printf("Retrieved %s: %s%n", taskKey.getName(), retrieved.getString("description"));

    }
}
