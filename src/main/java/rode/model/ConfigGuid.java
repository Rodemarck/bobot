package rode.model;

import org.bson.Document;
import rode.aviso.AvisoAlarme;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class ConfigGuid {
    private String id;
    private String lingua;
    private String pais;
    private String canalPoll;
    private boolean mencao;
    private List<AvisoAlarme> alarmes;

    public ConfigGuid(String id, String lingua, String pais) {
        this.id = id;
        this.lingua = lingua;
        this.pais = pais;
        this.canalPoll = null;
        this.mencao = false;
        this.alarmes = new LinkedList<>();
    }

    public ConfigGuid(String id, String lingua, String pais, String canalPoll, boolean mencao, List<AvisoAlarme> alarmes) {
        this.id = id;
        this.lingua = lingua;
        this.pais = pais;
        this.canalPoll = canalPoll;
        this.mencao = mencao;
        this.alarmes = alarmes;
    }

    public Document toMongo(){
        return new Document("id",id)
                .append("lingua",lingua)
                .append("pais",pais)
                .append("canalPoll",canalPoll)
                .append("mencao",mencao)
                .append("avisos", alarmes);
    }
    public static ConfigGuid fromMongo(Document doc){
        return new ConfigGuid(doc.getString("id")
                ,doc.getString("lingua")
                ,doc.getString("pais")
                ,doc.getString("canalPoll")
                ,doc.getBoolean("mencao",false)
                ,(((List<Document>)doc.get("polls",new LinkedList<Document>())).stream().map(d->AvisoAlarme.fromMongo(d)).collect(Collectors.toList())));
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

    public String canalPoll() {
        return canalPoll;
    }

    public void canalPoll(String canalPoll) {
        this.canalPoll = canalPoll;
    }

    public boolean mencao() {
        return mencao;
    }

    public void mencao(boolean mencao) {
        this.mencao = mencao;
    }

    public List<AvisoAlarme> avisos() {
        return alarmes;
    }

    public void avisos(List<AvisoAlarme> avisos) {
        this.alarmes = avisos;
    }
}
