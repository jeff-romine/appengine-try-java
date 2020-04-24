package myapp;
// Imports the Google Cloud client library

import com.google.appengine.repackaged.org.joda.time.DateTime;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Entity.Builder;
import com.google.cloud.datastore.FullEntity;
import com.google.cloud.datastore.IncompleteKey;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;
import com.google.cloud.datastore.StructuredQuery.PropertyFilter;
import com.google.cloud.datastore.Transaction;

public class SimpleUpdater {
    public static void main(String... args) throws Exception {
        // Instantiates a client
        final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

        if (!datastore.getOptions().getProjectId().equals("search-engine-252715")) {
            System.exit(-1);
        }

        final DateTime start = DateTime.now().withTimeAtStartOfDay().minusWeeks(2);
        final Timestamp ts = Timestamp.ofTimeMicroseconds(start.getMillis() * 1000L);

        Query<Entity> query = Query.newEntityQueryBuilder()
            .setKind("Fulfillment")
            .setFilter(PropertyFilter.ge("created", ts))
            .build();

        final QueryResults<Entity> fulfillments = datastore.run(query);
        int counter=0;
        while (fulfillments.hasNext()) {
            final Entity fulfillment = fulfillments.next();
            final Transaction txn = datastore.newTransaction();
            final Builder fulfillmentBuilder = Entity.newBuilder(fulfillment);
            final FullEntity<IncompleteKey> shipping = fulfillment.getEntity("shipping");
            final FullEntity.Builder<IncompleteKey> shippingBuilder = FullEntity.newBuilder(shipping);

            if ((++counter % 1000) == 0) {
                System.out.println("count: " + counter);
                System.out.printf("Key(`Order`,%d,`Fulfillment`,%d)\n", fulfillment.getKey().getParent().getId(),fulfillment.getKey().getId());
            }

            shippingBuilder.set("name", "Joe Customer");

            fulfillmentBuilder.set("shipping", shippingBuilder.build());
            txn.put(fulfillmentBuilder.build());
            txn.commit();
        }
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
