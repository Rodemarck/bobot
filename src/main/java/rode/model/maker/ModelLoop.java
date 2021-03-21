package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop;

import java.util.List;


public abstract class ModelLoop {
    private static Logger log = LoggerFactory.getLogger(ModelLoop.class);
    private TipoLoop tipo;
    private long id;
    private List<String> membro;
    private long inicio;
    private long fim;
    private long delay;
    private boolean ativo = true;
    private Permission permissao;

    public ModelLoop(TipoLoop tipo, long id, List<String> membro, long inicio, long fim, long delay, Permission permissao) {
        log.info("ModelLoop<Init>");
        this.tipo = tipo;
        this.id = id;
        this.membro = membro;
        this.inicio = inicio;
        this.fim = fim;
        this.delay = delay;
        this.ativo = true;
        this.permissao = permissao;
    }

    public Permission permissao() {
        return permissao;
    }

    public void permissao(Permission permissao) {
        this.permissao = permissao;
    }

    public long id() {
        return id;
    }

    public void id(long id) {
        this.id = id;
    }

    public long inicio() {
        return inicio;
    }

    public void inicio(long inicio) {
        this.inicio = inicio;
    }

    public long fim() {
        return fim;
    }

    public void fim(long fim) {
        this.fim = fim;
    }

    public long delay() {
        return delay;
    }

    public void delay(long delay) {
        this.delay = delay;
    }

    public boolean ativo() {
        return ativo;
    }

    public void ativo(boolean ativo) {
        this.ativo = ativo;
    }

    public TipoLoop tipo() {
        return tipo;
    }

    public void tipo(TipoLoop tipo) {
        this.tipo = tipo;
    }

    public List<String> membro() {
        return membro;
    }

    public void membro(List<String> membro) {
        this.membro = membro;
    }

    public void finaliza(){
        ativo(false);
        EventLoop.checa();
    }
}
