package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.ComandoGuild;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.ModelGuild;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;

@EComandoPoll
public class LimpaPolls extends ComandoGuild {
    public LimpaPolls() {
        super("limpar", Permission.ADMINISTRATOR, "limpa","limpar","lpoll");
    }

    @Override
    public void execute(LinkedList<String> args, Helper.Mensagem hm) throws Exception {
        var guild = Memoria.guild(hm.guildId());
        if(!guild.getPolls().isEmpty()){
            hm.reply(Constantes.builder(hm.bundle()),msg->{
                hm.mensagem(msg);
                EventLoop.addReacao(new ConversaLimpar(hm));
            });
        }
        else
            hm.reply(hm.text("limpar.exec"));
    }

    public static class ConversaLimpar extends MensagemReacao {
        private static Logger log = LoggerFactory.getLogger(ConversaLimpar.class);

        private int n=0;


        public ConversaLimpar(Helper hr) {
            super(hr,hr.mensagem(), Arrays.asList(hr.id()), System.currentTimeMillis()+20000,Permission.ADMINISTRATOR,new HashMap<>());
            log.debug("membro id =[" + membro() + "]");
            var guild = Memoria.guild(guildId());
            atualiza(guild);
            rerender(hr.bundle());
        }


        @Override
        public void acao() {}


        public void pagina(String pp){
            log.debug("deletando poll de titul [+" + pp + "]");
            var guild = Memoria.guild(guildId());
            for(var p:guild.getPolls())
                if(p.titulo().equals(pp)){
                    guild.getPolls().remove(p);
                    Memoria.update(new Document("id",guildId()),guild);
                    return;
                }
        }

        public void rerender(ResourceBundle rb){
            var guild = Memoria.guild(guildId());
            var eb = Constantes.builder()
                    .setTitle(rb.getString("limpar.delete"));
            for(int i=0;i<guild.getPolls().size();i++){
                var p = guild.getPolls().get(i);
                eb.appendDescription(Constantes.emotePoll(i) + " : **" + p.titulo()+"** , criada por <@"+p.criadorId()+">.\n\n");
            }
            eb.setFooter(String.format(rb.getString("limpar.exclusive"),nome()),pic());
            mensagem().editMessage(eb.build()).queue(q->atualiza(guild));

        }

        public void atualiza(ModelGuild guild){
            mensagem().clearReactions().queue(x->{
                src( new HashMap<>(){{
                    for (int i = 0; i < guild.getPolls().size(); i++) {
                        final int finalI = i;
                        mensagem().addReaction(Constantes.emotePoll(i)).queue();
                        put(Constantes.emotePoll(i), r -> pagina(guild.getPolls().get(finalI).titulo()));
                    }
                }});
            });
        }
    }
}
