package rode.comando.guild;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EcomandoGeral;
import rode.core.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

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
        hm.getEvent().getChannel().sendMessage(Constantes.builder(hm.bundle()).build()).queue(msg->{
            log.info("alterando mensagem");
            hm.mensagem(msg);
            log.info("add evento");
            EventLoop.addReacao(new ConversaLingua(hm));
        },err->{
            log.error(err.getMessage());
        });
    }

    public static class ConversaLingua extends MensagemReacao {
        private static Logger log = LoggerFactory.getLogger(ConversaLingua.class);
        private ResourceBundle rb;
        public ConversaLingua(Helper hr) {
            super(hr,hr.mensagem(),null,System.currentTimeMillis()+120000,Permission.ADMINISTRATOR,new HashMap<>());
            log.info("ConversaLingua<Init>");
            src(new HashMap<>(){{
                put(Constantes.emote("br"), x->mudaIdioma(guildId(),"pt","BR"));
                put(Constantes.emote("en"),x->mudaIdioma(guildId(), "en","US"));
            }});
            mensagem().addReaction(Constantes.emote("br")).queue();
            mensagem().addReaction(Constantes.emote("en")).queue();
            rb = hr.bundle();
            rerender(rb);
        }

        @Override
        public void acao() {
            log.info("acao");
        }

        @Override
        public void rerender(ResourceBundle bundle) {
            var eb = Constantes.builder();
            eb.setTitle(rb.getString("lingua.titulo"));
            eb.appendDescription(rb.getString("lingua.br"));
            eb.appendDescription(rb.getString("lingua.us"));
            mensagem().editMessage(eb.build()).queue();
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
