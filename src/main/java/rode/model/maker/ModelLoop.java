package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import rode.core.EventLoop2;


public abstract class ModelLoop {
    private TipoLoop tipo;
    private long id;
    private String membro;
    private long inicio;
    private long fim;
    private long delay;
    private boolean ativo = true;
    private Permission permissao;

    public ModelLoop(TipoLoop tipo, long id, String membro, long inicio, long fim, long delay, Permission permissao) {
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

    public String membro() {
        return membro;
    }

    public void membro(String membro) {
        this.membro = membro;
    }

    public void finaliza(){
        ativo(false);
        //EventLoop2.checa();
    }
}
