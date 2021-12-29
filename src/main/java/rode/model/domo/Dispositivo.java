package rode.model.domo;

import org.bson.Document;

public class Dispositivo {
    private String nome;
    private String id;
    private int estado;

    public static Dispositivo de(Document dispositivo) {
        return null;
    }


    public int getEstado() {
        return estado;
    }

    public Dispositivo(String nome, String id, int estado) {
        this.nome = nome;
        this.id = id;
        this.estado = estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
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

    public Document toMongo() {
        return new Document()
                .append("id",id)
                .append("nome",nome)
                .append("estado",estado)

                ;
    }
}
