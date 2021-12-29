package rode.comando.guild;

import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EcomandoGeral;
import rode.model.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

@EcomandoGeral
public class SetLang extends ComandoGuild {
    private static Logger log = LoggerFactory.getLogger(SetLang.class);
    public SetLang() {
        super("lingua", Permission.ADMINISTRATOR, "idioma","lingua","lang");
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        log.debug("mudando idioma");
        hm.getEvent()
                .getChannel()
                .sendMessageEmbeds(Constantes.builder(hm.getBundle()).build()).queue(msg->{
            log.debug("alterando mensagem");
            hm.setMensagem(msg);
            log.debug("add evento");
            EventLoop.addReacao(new ConversaLingua(hm));
        },err->{
            log.error(err.getMessage());
        });
    }

    public static class ConversaLingua extends MensagemReacao {
        private static Logger log = LoggerFactory.getLogger(ConversaLingua.class);
        private ResourceBundle rb;
        public ConversaLingua(Helper hr) {
            super(hr,hr.getMensagem(),null,System.currentTimeMillis()+120000,Permission.ADMINISTRATOR,new HashMap<>());
            log.debug("ConversaLingua<Init>");
            setComandos(new HashMap<>(){{
                put(Constantes.emote("br"), x->mudaIdioma(getGuildId(),"pt","BR"));
                put(Constantes.emote("en"),x->mudaIdioma(getGuildId(), "en","US"));
            }});
            getMensagem().addReaction(Constantes.emote("br")).queue();
            getMensagem().addReaction(Constantes.emote("en")).queue();
            rb = hr.getBundle();
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
            getMensagem().editMessageEmbeds(eb.build()).queue();
        }
        private void mudaIdioma(String id, String lingua, String pais){
            Memoria.usandoConfig(id,cg->{
                if(cg.lingua().equals(lingua) && cg.pais().equals(pais))
                    return;
                cg.lingua(lingua);
                cg.pais(pais);
                var l = new Locale(lingua, pais);
                rb = ResourceBundle.getBundle("messages", l);
                Constantes.loc(getGuildId(),l);
            });
        }
    }
}
