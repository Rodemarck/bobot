package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.util.HashMap;
import java.util.function.Consumer;

public abstract class MensagemReacao extends ModelLoop{
    private Message mensagem;
    private HashMap<String, Consumer<Helper.Reacao>> src;

    public MensagemReacao(Message mensagem, String membro, long fim, Permission permissao, HashMap<String, Consumer<Helper.Reacao>> src) {
        super(TipoLoop.G_MENSAGEM_REACAO, EventLoop2.geraId(), membro, System.currentTimeMillis(), fim, 50,permissao);
        this.mensagem = mensagem;
        this.src = src;
    }

    public void run( Helper.Reacao helper) {
        synchronized (this){
            if(ativo()){
                for(var e : src.entrySet()) {
                    if (e.getKey().equals(helper.emoji())) {
                        e.getValue().accept(helper);
                        break;
                    }
                }
            }
            helper.getMessage().removeReaction(helper.emoji(),helper.getEvent().getUser()).submit();
            acao();
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

    public static boolean expirado(MensagemReacao mensagemReacao) {
        return System.currentTimeMillis() > mensagemReacao.fim();
    }
}
