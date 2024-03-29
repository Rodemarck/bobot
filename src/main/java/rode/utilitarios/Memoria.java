package rode.utilitarios;


import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import rode.core.PollHelper;
import rode.model.ConfigGuid;
import rode.model.ModelGuild;

import java.util.function.Consumer;

public class Memoria {
    private static MongoClient mongoClient;
    private static MongoDatabase database;
    public static MongoCollection<Document> guilds;
    public static MongoCollection<Document> configs;

    static {
        try{
            mongoClient = MongoClients.create(Constantes.env("mongo"));
            database = mongoClient.getDatabase("domo");
            guilds  = database.getCollection("dispositivo");
            configs = database.getCollection("config");
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

    public static void update(ConfigGuid cg){
        configs.updateOne(new Document("id",cg.id()),new Document("$set",cg.toMongo()));
    }
    public static void eachPoll(Consumer<ModelGuild> action) {
        guilds.find().forEach(d->action.accept(ModelGuild.fromMongo(d)));
    }

    public static ModelGuild guild(String guildId) {
        return  ModelGuild.fromMongo(guilds.find(new Document("id",guildId)).first());
    }
    public static void usandoConfig(String id, Consumer<ConfigGuid> f){
        var c = config(id);
        f.accept(c);
        update(c);
    }
    public static ConfigGuid config(String id) {
        return ConfigGuid.fromMongo(configs.find(new Document("id",id)).first());
    }

    public static void insert(ConfigGuid config) {
        configs.insertOne(config.toMongo());
    }

    public static void eachConfig(Consumer<ConfigGuid> action) {
        guilds.find().forEach(d->action.accept(ConfigGuid.fromMongo(d)));
    }
}
