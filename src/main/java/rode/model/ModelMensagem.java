package rode.model;

import rode.core.EventLoop;

import java.time.LocalDateTime;

public class ModelMensagem {
    LocalDateTime tempoLimite;

    public LocalDateTime tempoLimite() {
        return tempoLimite;
    }

    public void tempoLimite(LocalDateTime tempoLimite) {
        this.tempoLimite = tempoLimite;
    }

    public void atualiza() {
        this.tempoLimite.plusSeconds(EventLoop.delay/1000);
    }
}
