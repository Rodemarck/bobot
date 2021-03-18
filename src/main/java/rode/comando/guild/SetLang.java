package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

@EcomandoGeral
public class SetLang extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(SetLang.class);
    public SetLang() {
        super("lang", Permission.ADMINISTRATOR, "idioma","lingua","lang");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        log.info("mudando idioma");
        hm.reply(new EmbedBuilder().setColor(Color.decode("#C8A2C8")).setTitle("**carregando...**"), message -> {
            hm.mensagem(message);
            EventLoop2.addReacao(new ConversaLingua(hm));
            return null;
        });
    }

    public static class ConversaLingua extends MensagemReacao {
        private static Logger log = LoggerFactory.getLogger(ConversaLingua.class);
        private ResourceBundle rb;
        public ConversaLingua(Helper hr) {
            super(hr, null, System.currentTimeMillis()+120000, Permission.ADMINISTRATOR, new HashMap<>());
            src(new HashMap<>(){{
                put(Constantes.emote("br"), x->mudaIdioma(guildId(),"pt","BR"));
                put(Constantes.emote("en"),x->mudaIdioma(guildId(), "en","US"));
            }});
            mensagem().addReaction(Constantes.emote("br")).submit();
            mensagem().addReaction(Constantes.emote("en")).submit();
            rb = hr.bundle();
            rerender(rb);
        }

        @Override
        public void acao() {
            log.info("acao");
        }

        @Override
        public void rerender(ResourceBundle bundle) {
            var eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"));
            eb.setTitle(rb.getString("lingua.titulo"));
            eb.appendDescription(rb.getString("lingua.br"));
            eb.appendDescription(rb.getString("lingua.us"));
            mensagem().editMessage(eb.build()).submit();
        }
        private void mudaIdioma(String id, String lingua, String pais){
            Memoria.usandoConfig(id,cg->{
                if(cg.lingua().equals(lingua) && cg.pais().equals(pais))
                    return;
                cg.lingua(lingua);
                cg.pais(pais);
                var l = new Locale(lingua, pais);
                rb = ResourceBundle.getBundle("messages", l);
                Constantes.loc(guildId(),l);
            });
        }
    }
}
