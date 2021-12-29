package rode.model.domo;

import org.bson.Document;

public class Usuario {
    private String id;
    private String senha;

    public Usuario(String id, String senha) {
        this.id = id;
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Document toMongo(){
        return new Document("id",id)
                .append("senha", senha);
    }

    public static Usuario de(Document document){
        return new Usuario(document.getString("id"),document.getString("senha"));
    }
}
