package rode.model;

import org.bson.Document;

public class ConfigGuid {
    private String id;
    private String lingua;
    private String pais;
    private String pollChanell;
    private boolean mencao;

    public ConfigGuid(String id, String lingua, String pais) {
        this.id = id;
        this.lingua = lingua;
        this.pais = pais;
    }

    public Document toMongo(){
        return new Document("id",id)
                .append("lingua",lingua)
                .append("pais",pais);
    }
    public static ConfigGuid fromMongo(Document doc){
        return new ConfigGuid(doc.getString("id"),doc.getString("lingua"),doc.getString("pais"));
    }

    public void id(String id) {
        this.id = id;
    }

    public void lingua(String lingua) {
        this.lingua = lingua;
    }

    public void pais(String pais) {
        this.pais = pais;
    }

    public String id() {
        return id;
    }

    public String lingua() {
        return lingua;
    }

    public String pais() {
        return pais;
    }
}
