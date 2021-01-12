package rode.utilitarios;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rode.model.ModelGuild;

public class Memoria {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    public static MongoCollection<Document> guilds;

    static {
        try{
            mongoClient = MongoClients.create(Constantes.env.get("mongo"));
            database = mongoClient.getDatabase("bot");
            guilds  = database.getCollection("guild");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void update(Document query, ModelGuild guild){
        guilds.updateOne(query, new Document("$set",guild.toMongo()));
    }
}
