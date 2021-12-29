package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.EventLoop;
import rode.core.Helper;

import java.util.*;
import java.util.function.Consumer;


public abstract class MensagemReacao extends ModelLoop{
    private static Logger log = LoggerFactory.getLogger(MensagemReacao.class);
    private Message mensagem;
    private HashMap<String, Consumer<Helper.Reacao>> comandos;
    private String guildId;
    private String pic;
    private String nome;

    public MensagemReacao(Helper hr, Message msg, List<String> membro, long fim, Permission permissao, HashMap<String, Consumer<Helper.Reacao>> comandos) {
        super(getTipo.G_MENSAGEM_REACAO, EventLoop.geraId(), membro, System.currentTimeMillis(), fim, 20000,permissao);
        log.info("MensagemReacao<Init>");
        this.mensagem =msg;//hr.event().getChannel().sendMessage(Constantes.builder().setTitle(hr.text("embed.load")).build()).complete();
        this.comandos = comandos;
        this.guildId = hr.guildId();
        this.pic = hr.getMembro().getUser().getAvatarUrl();
        this.nome = hr.getMembro().getUser().getAsTag();
    }

    public void run( Helper.Reacao helper) {
        synchronized (this){
            if(getAtivo()){
                for(var e : comandos.entrySet()) {
                    if (e.getKey().equals(helper.emoji())) {
                        e.getValue().accept(helper);
                        render(helper.getBundle());
                        break;
                    }
                }

            }
            helper.getMensagem().removeReaction(helper.emoji(),helper.getMembro().getUser()).queue();
        }
    }

    public abstract void acao();

    public abstract void rerender(ResourceBundle rb);

    private void render(ResourceBundle rb){
        acao();
        if(getAtivo()){
            setFim(System.currentTimeMillis()+ getDelay());
            rerender(rb);
        }else
            finaliza();
    }

    public static boolean expirado(MensagemReacao mensagemReacao) {
        if(!mensagemReacao.getAtivo())
            return true;
        var b = System.currentTimeMillis() > mensagemReacao.getFim();
        if(b) {
            mensagemReacao.getMensagem().delete().queue();
        }
        return b;
    }

    public Message getMensagem() {
        return mensagem;
    }

    public void setMensagem(Message mensagem) {
        this.mensagem = mensagem;
    }

    public HashMap<String, Consumer<Helper.Reacao>> getComandos() {
        return comandos;
    }

    public void setComandos(HashMap<String, Consumer<Helper.Reacao>> comandos) {
        this.comandos = comandos;
    }

    public String getGuildId() {
        return guildId;
    }

    public void setGuildId(String guildId) {
        this.guildId = guildId;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
