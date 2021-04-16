package rode.comando.guild.mina;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.model.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.CampoMinado;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class CriaCampoMinado extends ComandoGuild {
    public CriaCampoMinado() {
        super(/*"mine"*/null, null, "campo");
    }

    @Override
    public void help(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle loc) {

    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        hm.reply(Constantes.builder(hm.bundle()),msg->{
            hm.mensagem(msg);
            EventLoop.addReacao(new ConversaCampo(hm));
        });
    }
    public static class ConversaCampo extends MensagemReacao{
        private static Logger log = LoggerFactory.getLogger(ConversaCampo.class);
        private CampoMinado campo;
        private EmbedBuilder eb;
        private int x;
        private boolean check = false;
        private boolean end = false;
        public ConversaCampo(Helper hr) {
            super(hr,hr.mensagem(), Arrays.asList(hr.id()), System.currentTimeMillis()+20000,null ,new HashMap<>());
            delay(60000);
            this.campo = new CampoMinado();
            src(new HashMap<>(){{
                for(int i=0;i<10;i++) {
                    final var ii = i;
                    put(Constantes.emote("" + i), r -> click(ii, r));
                }
            }});
            mensagem().clearReactions().queue(q->{
                for(int i=0;i<10;i++)
                    mensagem().addReaction(Constantes.emote(""+i)).queue();
            });
            eb = Constantes.builder()
                    .setTitle("Campo minado")
                    .setFooter("jogo de " + nome());
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
            mensagem().editMessage(eb.build()).queue();
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
