package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop;
import rode.core.Helper;

import java.util.List;


public abstract class ModelLoop {
    private static Logger log = LoggerFactory.getLogger(ModelLoop.class);
    private getTipo tipo;
    private long id;
    private List<String> membros;
    private long inicio;
    private long fim;
    private long delay;
    private boolean ativo = true;
    private Permission permissao;

    public ModelLoop(getTipo tipo, long id, List<String> membros, long inicio, long fim, long delay, Permission permissao) {
        log.info("ModelLoop<Init>");
        this.tipo = tipo;
        this.id = id;
        this.membros = membros;
        this.inicio = inicio;
        this.fim = fim;
        this.delay = delay;
        this.ativo = true;
        this.permissao = permissao;
    }

    public Permission getPermissao() {
        return permissao;
    }

    public void setPermissao(Permission permissao) {
        this.permissao = permissao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInicio() {
        return inicio;
    }

    public void setInicio(long inicio) {
        this.inicio = inicio;
    }

    public long getFim() {
        return fim;
    }

    public void setFim(long fim) {
        this.fim = fim;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public getTipo getTipos() {
        return tipo;
    }

    public void setTipo(getTipo tipo) {
        this.tipo = tipo;
    }

    public List<String> getMembros() {
        return membros;
    }

    public void setMembros(List<String> membro) {
        this.membros = membro;
    }

    public void finaliza(){
        setAtivo(false);
        EventLoop.checa();
    }
}
