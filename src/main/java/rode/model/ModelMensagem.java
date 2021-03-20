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

    public long mensagemId() {
        return mensagemId;
    }

    public void mensagemId(long mensagemId) {
        this.mensagemId = mensagemId;
    }

    public void tempoLimite(LocalDateTime tempoLimite) {
        this.tempoLimite = tempoLimite;
    }

    public long usuarioId() {
        return usuarioId;
    }

    public void usuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public ModelGuild guild() {
        return guild;
    }

    public void guild(ModelGuild guild) {
        this.guild = guild;
    }

    public LocalDateTime tempoLimite() {
        return tempoLimite;
    }


    public void atualiza() {
        this.tempoLimite.plusSeconds(EventLoop.delay/1000);
    }

    public boolean tick(LocalDateTime agr){
        return agr.isBefore(tempoLimite);
    }

    public abstract void executa(LinkedList<String> args, Helper.Mensagem hm);
}
