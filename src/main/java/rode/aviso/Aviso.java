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

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract boolean acao(JDA jda);

    public Document toMongo(){
        if(titulo == null || titulo.isEmpty())
            this.titulo = "Alarme programado";
        return new Document("guildId",guildId)
                .append("id",id)
                .append("canal",canal)
                .append("mensagem",mensagem)
                .append("titulo",titulo);
    }
}
