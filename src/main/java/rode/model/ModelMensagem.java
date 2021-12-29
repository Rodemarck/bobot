package rode.model;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop;
import rode.core.Helper;

import java.time.LocalDateTime;
import java.util.LinkedList;

@Data
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

    public void update() {
        this.tempoLimite.plusSeconds(EventLoop.delay/1000);
    }

    public boolean tick(LocalDateTime agr){
        return agr.isBefore(tempoLimite);
    }

    public abstract void execute(String[] args, Helper.Mensagem hm);
}
