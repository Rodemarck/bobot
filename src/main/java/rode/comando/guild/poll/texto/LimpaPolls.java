package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.EventLoop;
import rode.core.Helper;
import rode.model.ModelGuild;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

@EComandoPoll
public class LimpaPolls extends AbrePoll {
    public LimpaPolls() {
        super("clean", Permission.ADMINISTRATOR, false,"limpa","limpar","clear","clean");
        setPath("limpar");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(), bundle.getString(getHelp()));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        var guild = Memoria.guild(hm.guildId());
        if(!guild.getPolls().isEmpty()){
            hm.reply(Constantes.builder(hm.getBundle()), msg->{
                hm.setMensagem(msg);
                EventLoop.addReacao(new ConversaLimpar(hm));
            });
        }
        else
            hm.reply(hm.getText("limpar.exec"));
    }

    public static class ConversaLimpar extends MensagemReacao {
        private static Logger log = LoggerFactory.getLogger(ConversaLimpar.class);

        private int n=0;


        public ConversaLimpar(Helper hr) {
            super(hr,hr.getMensagem(), Arrays.asList(hr.getId()), System.currentTimeMillis()+20000,Permission.ADMINISTRATOR,new HashMap<>());
            log.debug("membro id =[" + getMembros() + "]");
            var guild = Memoria.guild(getGuildId());
            atualiza(guild);
            rerender(hr.getBundle());
        }


        @Override
        public void acao() {}


        public void pagina(String pp){
            log.debug("deletando poll de titul [+" + pp + "]");
            var guild = Memoria.guild(getGuildId());
            for(var p:guild.getPolls())
                if(p.getTitulo().equals(pp)){
                    guild.getPolls().remove(p);
                    Memoria.update(new Document("id", getGuildId()),guild);
                    return;
                }
        }

        public void rerender(ResourceBundle rb){
            var guild = Memoria.guild(getGuildId());
            var eb = Constantes.builder()
                    .setTitle(rb.getString("limpar.delete"));
            for(int i=0;i<guild.getPolls().size();i++){
                var p = guild.getPolls().get(i);
                eb.appendDescription(Constantes.emotePoll(i) + " : **" + p.getTitulo()+"** , criada por <@"+p.getCriadorId()+">.\n\n");
            }
            eb.setFooter(String.format(rb.getString("limpar.exclusive"), getNome()), getPic());
            getMensagem().editMessageEmbeds(eb.build()).queue(q->atualiza(guild));

        }

        public void atualiza(ModelGuild guild){
            getMensagem().clearReactions().queue(x->{
                setComandos(new HashMap<>(){{
                    for (int i = 0; i < guild.getPolls().size(); i++) {
                        final int finalI = i;
                        getMensagem().addReaction(Constantes.emotePoll(i)).queue();
                        put(Constantes.emotePoll(i), r -> pagina(guild.getPolls().get(finalI).getTitulo()));
                    }
                }});
            });
        }
    }
}
