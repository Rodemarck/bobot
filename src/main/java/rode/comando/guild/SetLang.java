package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.model.ConfigGuid;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;
import java.util.ResourceBundle;

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
                put(Constantes.POOL_EMOTES.get(0), x->mudaIdioma(new ConfigGuid(guildId(),"pt","BR")));
                put(Constantes.POOL_EMOTES.get(1),x->mudaIdioma(new ConfigGuid(guildId(), "en","US")));
            }});
            mensagem().addReaction(Constantes.POOL_EMOTES.get(0)).submit();
            mensagem().addReaction(Constantes.POOL_EMOTES.get(1)).submit();
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
            eb.appendDescription(String.format("%s : %s",Constantes.POOL_EMOTES.get(0) ,rb.getString("lingua.br")));
            eb.appendDescription(String.format("%s : %s",Constantes.POOL_EMOTES.get(1),rb.getString("lingua.us")));
            mensagem().editMessage(eb.build()).submit();
        }
        private void mudaIdioma(ConfigGuid cg){
            var l = new Locale(cg.lingua(), cg.pais());
            rb = ResourceBundle.getBundle("messages", l);
            Constantes.loc(guildId(),l);
            Memoria.update(cg);
        }
    }
}
