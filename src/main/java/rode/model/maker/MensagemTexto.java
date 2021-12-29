package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import rode.core.EventLoop;
import rode.core.Helper;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public abstract class MensagemTexto extends ModelLoop{
    private HashMap<Pattern, Consumer<Helper.Mensagem>> comandos;
    private TextChannel canal;
    private String mensagem;

    public MensagemTexto(TextChannel canal, List<String> menbros,String mensagem, long fim, long delay, Permission permissao) {
        super(getTipo.G_MENSAGEM_TEXTO, EventLoop.geraId(), menbros, System.currentTimeMillis(), fim, delay, permissao);
        this.canal = canal;
        this.mensagem = mensagem;
    }
    public MensagemTexto(Helper.Mensagem hm, List<String> menbros, String mensagem){
        super(getTipo.G_MENSAGEM_TEXTO, EventLoop.geraId(),menbros,System.currentTimeMillis(),System.currentTimeMillis()+120000,120000,null);
        this.canal = hm.getEvent().getChannel();
        this.mensagem = mensagem;
    }

    public void run(Helper.Mensagem hm){
        var check = false;
        synchronized (this){
            if(getAtivo()){
                for(var e: comandos.entrySet())
                    if(e.getKey().matcher(hm.getMensagem().getContentRaw()).find()) {
                        e.getValue().accept(hm);
                        acao(hm);
                        check = true;
                    }
                if(check)
                    rerender(hm);
            }
        }
    }

    public static boolean expirado(MensagemTexto mensagemTexto) {
        boolean b = !mensagemTexto.getAtivo() || System.currentTimeMillis() > mensagemTexto.getFim() ;
        if(b)
            mensagemTexto.canal.sendMessage(mensagemTexto.mensagem).queue();
        return b;
    }

    public HashMap<Pattern, Consumer<Helper.Mensagem>> getComandos() {
        return comandos;
    }

    public void setComandos(HashMap<Pattern, Consumer<Helper.Mensagem>> src) {
        this.comandos = src;
    }

    public TextChannel getCanal() {
        return canal;
    }

    public void setCanal(TextChannel canal) {
        this.canal = canal;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
    public abstract void acao(Helper.Mensagem hm);
    public abstract void rerender(Helper.Mensagem hm);
}
