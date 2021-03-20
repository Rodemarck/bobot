package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public abstract class MensagemReacao extends ModelLoop{
    private static Logger log = LoggerFactory.getLogger(MensagemReacao.class);
    private Message mensagem;
    private HashMap<String, Consumer<Helper.Reacao>> src;
    private String guildId;
    private String pic;
    private String nome;

    public MensagemReacao(Helper hr,Message msg, String membro, long fim, Permission permissao, HashMap<String, Consumer<Helper.Reacao>> src) {
        super(TipoLoop.G_MENSAGEM_REACAO, EventLoop2.geraId(), membro, System.currentTimeMillis(), fim, 20000,permissao);
        log.info("MensagemReacao<Init>");
        this.mensagem =msg;//hr.event().getChannel().sendMessage(Constantes.builder().setTitle(hr.text("embed.load")).build()).complete();
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
            helper.mensagem().removeReaction(helper.emoji(),helper.membro().getUser()).queue();
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
            fim(System.currentTimeMillis()+delay());
            rerender(rb);
        }else
            finaliza();
    }

    public static boolean expirado(MensagemReacao mensagemReacao) {
        if(!mensagemReacao.ativo())
            return true;
        var b = System.currentTimeMillis() > mensagemReacao.fim();
        if(b) {
            mensagemReacao.mensagem().delete().queue();
        }
        return b;
    }
}
