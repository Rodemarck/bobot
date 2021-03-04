package rode.utilitarios;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rode.core.PollHelper;
import rode.model.ModelGuild;

import java.util.function.Consumer;

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

    public static void update(PollHelper.DadosPoll dp) {
        guilds.updateOne(dp.query(), new Document("$set",dp.guild().toMongo()));
    }

    public static void each(Consumer<ModelGuild> action) {
        guilds.find().forEach(d->action.accept(ModelGuild.fromMongo(d)));
    }

    public static ModelGuild guild(String guildId) {
        return  ModelGuild.fromMongo(guilds.find(new Document("id",guildId)).first());
    }
}
