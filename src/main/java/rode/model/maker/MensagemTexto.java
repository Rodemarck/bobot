package rode.model.maker;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.TextChannel;
import rode.core.EventLoop2;
import rode.core.Helper;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class MensagemTexto extends ModelLoop{
    private HashMap<Pattern, Consumer<Helper.Mensagem>> src;
    private TextChannel canal;

    public MensagemTexto(String membro, long fim, long delay, Permission permissao) {
        super(TipoLoop.G_MENSAGEM_TEXTO, EventLoop2.geraId(), membro, System.currentTimeMillis(), fim, delay, permissao);
    }

    public void run(Helper.Mensagem hm){
        synchronized (this){
            if(ativo()){
                for(var e: src.entrySet())
                    if(e.getKey().matcher(hm.getMessage().getContentRaw()).find())
                        e.getValue().accept(hm);
            }
        }
    }

    public static boolean expirado(MensagemTexto mensagemTexto) {
        boolean b = System.currentTimeMillis() > mensagemTexto.fim();
        return b;
    }
}
