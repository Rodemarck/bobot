package rode.aviso;

import net.dv8tion.jda.api.entities.TextChannel;

import java.time.LocalDateTime;
import java.util.function.Consumer;

public abstract class Aviso {
    private TextChannel canal;
    private LocalDateTime horario;

    public Aviso(TextChannel canal, LocalDateTime horario) {
        this.canal = canal;
        this.horario = horario;
    }

    public TextChannel canal() {
        return canal;
    }

    public void canal(TextChannel canal) {
        this.canal = canal;
    }

    public LocalDateTime horario() {
        return horario;
    }

    public void horario(LocalDateTime horario) {
        this.horario = horario;
    }

    public abstract void acao();

    public static boolean expirado(Aviso aviso) {
        if(LocalDateTime.now().isAfter(aviso.horario)){
            aviso.acao();
            return true;
        }
        return false;
    }
}
