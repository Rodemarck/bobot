package rode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop;
import rode.core.Helper;

import java.time.LocalDateTime;
import java.util.LinkedList;

public abstract class ModelMensagem {
    public static Logger log = LoggerFactory.getLogger(ModelMensagem.class);
    private LocalDateTime tempoLimite;
    private long usuarioId;
    private long mensagemId;
    private ModelGuild guild;

    public ModelMensagem(long usuarioId, long mensagemId, ModelGuild guild) {
        this.tempoLimite = LocalDateTime.now().plusSeconds(EventLoop.delayS);
        this.usuarioId = usuarioId;
        this.guild = guild;
        this.mensagemId = mensagemId;
    }

    public LocalDateTime getDeadLine() {
        return tempoLimite;
    }

    public void setDeadLine(LocalDateTime tempoLimite) {
        this.tempoLimite = tempoLimite;
    }

    public long getUsersId() {
        return usuarioId;
    }

    public void setUsersId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getMessageId() {
        return mensagemId;
    }

    public void setMessageId(long mensagemId) {
        this.mensagemId = mensagemId;
    }

    public ModelGuild getGuild() {
        return guild;
    }

    public void setGuild(ModelGuild guild) {
        this.guild = guild;
    }

    public void update() {
        this.tempoLimite.plusSeconds(EventLoop.delay/1000);
    }

    public boolean tick(LocalDateTime agr){
        return agr.isBefore(tempoLimite);
    }

    public abstract void execute(String[] args, Helper.Mensagem hm);
}
