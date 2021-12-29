package rode.model.domo;

import org.bson.Document;

import java.util.List;

public class Rede {
    private String nome;
    private String id;
    private String gerenciador;
    private List<Dispositivo> dispositivos;

    public Rede(String nome, String id,String gerenciador, List<Dispositivo> dispositivos) {
        this.nome = nome;
        this.id = id;
        this.gerenciador = gerenciador;
        this.dispositivos = dispositivos;
    }

    public static Rede de(Document document) {
        return new Rede(document.getString("nome"),document.getString("id"), document.getString("gerenciador"),
                ((List<Document>)document.get("dispositivos")).stream()
                        .map(Dispositivo::de)
                        .toList());
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Dispositivo> getDispositivos() {
        return dispositivos;
    }

    public void setDispositivos(List<Dispositivo> dispositivos) {
        this.dispositivos = dispositivos;
    }

    public Document toMongo() {
        return new Document()
                .append("id",id)
                .append("nome",nome)
                .append("gerenciador",gerenciador)
                .append("dispositivos",
                        dispositivos.stream()
                                .map(d-> d.toMongo())
                                .toList()
                )

                ;
    }
}
