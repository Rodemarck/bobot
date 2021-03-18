package rode.comando.guild.mina;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.ComandoGuild;
import rode.core.EventLoop2;
import rode.core.Helper;
import rode.model.CampoMinado;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class CriaCampoMinado extends ComandoGuild {
    public CriaCampoMinado() {
        super("mine", null, "campo");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void executa(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        EventLoop2.addReacao(new ConversaCampo(hm));
    }
    public static class ConversaCampo extends MensagemReacao{
        private static Logger log = LoggerFactory.getLogger(ConversaCampo.class);
        private CampoMinado campo;
        private EmbedBuilder eb;
        private int x;
        private boolean check = false;
        private boolean end = false;
        public ConversaCampo(Helper hr) {
            super(hr, hr.membro().getUser().getId(), System.currentTimeMillis()+20000,Permission.ADMINISTRATOR,new HashMap<>());
            delay(60000);
            this.campo = new CampoMinado();
            src(new HashMap<>(){{
                for(int i=0;i<10;i++) {
                    final var ii = i;
                    put(Constantes.emote("" + i), r -> click(ii, r));
                }
            }});
            mensagem().clearReactions().submit().thenCompose(q->{
                for(int i=0;i<10;i++)
                    mensagem().addReaction(Constantes.emote(""+i)).submit();
                return null;
            });
            eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"))
                    .setTitle("Campo minado")
                    .setFooter("jogo de " + nome())
                    .clearFields();
            rerender(hr.bundle());
        }

        @Override
        public void acao() {

        }

        @Override
        public void rerender(ResourceBundle rb) {
            log.info("rerender");
            eb.setDescription("```c\n" + campo + "```")
                    .clearFields();
            if(campo.finalizado())
                eb.addField(rb.getString("campo.state"),rb.getString(end ? "campo.over":"campo.clear"),false);
            else
                eb.addField(rb.getString("campo.type"),check?"y":"x",false);
            mensagem().editMessage(eb.build()).submit();
        }

        private void click(int n, Helper.Reacao r){
            if(check){
                check=false;
                end = campo.joga(x,n);
                if(campo.finalizado()){
                    rerender(r.bundle());
                    log.info("end!!!");
                    finaliza();
                }
                return;
            }
            x=n;
            check = true;
        }
    }

}
