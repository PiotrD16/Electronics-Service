import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.bson.types.Binary;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import java.util.List;
import java.util.ArrayList;

public class MongoOrderRepository {

    private final MongoCollection<Document> collection;

    public MongoOrderRepository(String connectionString, String dbName, String collectionName) {
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(dbName);
        collection = database.getCollection(collectionName);
    }

    /**
     * Zapisuje nowe zlecenie do MongoDB
     *
     * @param userId       identyfikator użytkownika
     * @param repairId     identyfikator naprawy (0 jeśli nieustalony)
     * @param employeeID   identyfikator pracownika
     * @param description  opis problemu
     * @param imageFile    plik obrazu (np. zdjęcie urządzenia)
     * @throws IOException jeśli odczyt pliku się nie powiedzie
     *
     * @public MongoResults zwraca id uzytkownika, naprawy, opis i zdjecie z bazy dla wybranego employee_id
     */
    public void insertOrder(int userId, int repairId, int employeeID, String description, File imageFile) throws IOException
    {
        byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
        Binary imageBinary = new Binary(imageBytes);

        Document order = new Document("user_id", userId)
                .append("repair_id", repairId)
                .append("employee_id", employeeID)
                .append("description", description)
                .append("image", imageBinary);

        collection.insertOne(order);
    }

    public List<MongoResults> get_mongo_data(int employee_ID)
    {
        List<MongoResults> mongo_results = new ArrayList<>();

        FindIterable<Document> iterable = collection.find(eq("employee_id", employee_ID));

        for (Document document : iterable)
        {
            int userID = document.getInteger("user_id");
            int repairID = document.getInteger("repair_id");
            String description = document.getString("description");
            Binary image = (Binary) document.get("image", Binary.class);

            byte[] imageBytes = null;
            if (image != null)
                imageBytes = image.getData();
            mongo_results.add(new MongoResults(userID, repairID, description, imageBytes));
        }

        return mongo_results;
    }
}
