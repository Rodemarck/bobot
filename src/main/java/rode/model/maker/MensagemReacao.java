package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public abstract class MensagemReacao extends ModelLoop{
    private Message mensagem;
    private HashMap<String, Consumer<Helper.Reacao>> src;
    private String guildId;
    private String pic;
    private String nome;
    public MensagemReacao(Helper hr, String membro, long fim, Permission permissao, HashMap<String, Consumer<Helper.Reacao>> src) {
        super(TipoLoop.G_MENSAGEM_REACAO, EventLoop2.geraId(), membro, System.currentTimeMillis(), fim, 50,permissao);
        this.mensagem = hr.mensagem();
        this.src = src;
        this.guildId = hr.guildId();
        this.pic = hr.membro().getUser().getAvatarUrl();
        this.nome = hr.membro().getUser().getAsTag();
    }

    public void run( Helper.Reacao helper) {
        synchronized (this){
            if(ativo()){
                for(var e : src.entrySet()) {
                    if (e.getKey().equals(helper.emoji())) {
                        e.getValue().accept(helper);
                        render(helper.bundle());
                        break;
                    }
                }

            }
            helper.mensagem().removeReaction(helper.emoji(),helper.getEvent().getUser()).submit();
        }
    }

    public abstract void acao();

    public Message mensagem() {
        return mensagem;
    }

    public void mensagem(Message mensagem) {
        this.mensagem = mensagem;
    }

    public HashMap<String, Consumer<Helper.Reacao>> src() {
        return src;
    }

    public void src(HashMap<String, Consumer<Helper.Reacao>> src) {
        this.src = src;
    }

    public String guildId() {
        return guildId;
    }

    public void guildId(String guildId) {
        this.guildId = guildId;
    }

    public String pic() {
        return pic;
    }

    public void pic(String pic) {
        this.pic = pic;
    }

    public String nome() {
        return nome;
    }

    public void nome(String nome) {
        this.nome = nome;
    }

    public abstract void rerender(ResourceBundle rb);

    private void render(ResourceBundle rb){
        acao();
        if(ativo()){
            fim(System.currentTimeMillis()+20000);
            rerender(rb);
        }else
            finaliza();
    }

    public static boolean expirado(MensagemReacao mensagemReacao) {
        var b = System.currentTimeMillis() > mensagemReacao.fim();
        if(b)
            mensagemReacao.mensagem().delete().submit();
        return b;
    }
}
