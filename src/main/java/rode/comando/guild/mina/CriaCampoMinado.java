package rode.comando.guild.mina;

import net.dv8tion.jda.api.EmbedBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoJogo;
import rode.model.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.CampoMinado;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

@EComandoJogo
public class CriaCampoMinado extends ComandoGuild {
    private static final Logger log = LoggerFactory.getLogger(CriaCampoMinado.class);
    public CriaCampoMinado() {
        super("campo", null, "campo");
    }


    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        hm.reply(Constantes.builder(hm.getBundle()), msg->{
            hm.setMensagem(msg);
            EventLoop.addReacao(new ConversaCampo(hm));
            log.trace("msg");
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
            super(hr,hr.getMensagem(), Arrays.asList(hr.getId()), System.currentTimeMillis()+20000,null ,new HashMap<>());
            setDelay(60000);
            this.campo = new CampoMinado();
            setComandos(new HashMap<>(){{
                for(int i=0;i<10;i++) {
                    var ii = i;
                    put(Constantes.emote("" + i), r -> click(ii, r));
                }
            }});
            getMensagem().clearReactions().queue(q->{
                for(int i=0;i<10;i++)
                    getMensagem().addReaction(Constantes.emote(""+i)).queue();
            });
            eb = Constantes.builder()
                    .setTitle("Campo minado")
                    .setFooter("jogo de " + getNome());
            rerender(hr.getBundle());
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
            getMensagem().editMessageEmbeds(eb.build()).queue();
        }

        private void click(int n, Helper.Reacao r){
            log.debug("click");
            if(check){
                check=false;
                end = campo.joga(x,n);
                if(campo.finalizado()){
                    rerender(r.getBundle());
                    log.info("end!!!");
                    finaliza();
                }
                return;
            }
            x=n;
            check = true;
        }

        @Override
        public String toString() {
            return "ConversaCampo{" +
                    "campo={\n\t" + campo +
                    "\n}, eb=" + eb +
                    ", x=" + x +
                    ", check=" + check +
                    ", end=" + end +
                    '}';
        }
    }

}
