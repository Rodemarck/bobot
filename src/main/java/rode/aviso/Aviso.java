package rode.aviso;

import net.dv8tion.jda.api.JDA;
import org.bson.Document;

import java.time.LocalDateTime;

public abstract class Aviso {
    private String guildId;
    private String id;
    private String canal;
    private LocalDateTime horario;
    private String mensagem;
    private String titulo;


    public Aviso(String canal, LocalDateTime horario) {
        this.canal = canal;
        this.horario = horario;
    }

    public Aviso(String guildId, String id, String canal, LocalDateTime horario, String mensagem, String titulo) {
        this.guildId = guildId;
        this.id = id;
        this.canal = canal;
        this.horario = horario;
        this.mensagem = mensagem;
        if(titulo == null || titulo.isEmpty())
            this.titulo = "Alarme programado";
        else
            this.titulo = titulo;
    }

    public String canal() {
        return canal;
    }

    public void canal(String canal) {
        this.canal = canal;
    }

    public LocalDateTime horario() {
        return horario;
    }

    public void horario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String mensagem() {
        return mensagem;
    }

    public void mensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String titulo() {
        return titulo;
    }

    public void titulo(String titulo) {
        this.titulo = titulo;
    }

    public String guildId() {
        return guildId;
    }

    public void guildId(String guildId) {
        this.guildId = guildId;
    }

    public String id() {
        return id;
    }

    public void id(String id) {
        this.id = id;
    }

    public abstract boolean acao(JDA jda);

    public Document toMongo(){
        this.guildId = guildId;
        this.id = id;
        this.canal = canal;
        this.horario = horario;
        this.mensagem = mensagem;
        if(titulo == null || titulo.isEmpty())
            this.titulo = "Alarme programado";
        else
            this.titulo = titulo;
        return new Document("guildId",guildId)
                .append("id",id)
                .append("canal",canal)
                .append("mensagem",mensagem)
                .append("titulo",titulo);
    }
}
