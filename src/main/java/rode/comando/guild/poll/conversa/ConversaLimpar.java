package rode.comando.guild.poll.conversa;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.model.ModelGuild;
import rode.model.maker.MensagemReacao;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.awt.*;
import java.util.HashMap;

public class ConversaLimpar extends MensagemReacao {
    private static Logger log = LoggerFactory.getLogger(ConversaLimpar.class);

    private int n=0;
    private String guildId;
    private String pic;
    private String nome;

    public ConversaLimpar(Message mensagem, Member membro, long fim, String guildId) {
        super(mensagem, membro.getUser().getId(), fim,Permission.ADMINISTRATOR,new HashMap<>());
        log.debug("membro id =[" + membro() + "]");
        pic = membro.getUser().getAvatarUrl();
        nome = membro.getUser().getAsTag();
        var guild = Memoria.guild(guildId);
        atualiza(guild);
        this.guildId = guildId;
        rerender();
    }


    @Override
    public void acao() {
        fim(System.currentTimeMillis()+20000);
        rerender();
    }


    public void pagina(String pp){
        log.debug("deletando poll de titul [+" + pp + "]");
        var guild = Memoria.guild(guildId);
        for(var p:guild.getPolls())
            if(p.getTitulo().equals(pp)){
                guild.getPolls().remove(p);
                Memoria.update(new Document("id",guildId),guild);
                return;
            }
    }

    private void rerender(){
        var guild = Memoria.guild(guildId);
        var eb = new EmbedBuilder().setColor(Color.decode("#C8A2C8"))
                .setTitle("Deletar a poll");
        for(int i=0;i<guild.getPolls().size();i++){
            var p = guild.getPolls().get(i);
            eb.appendDescription(Constantes.POOL_EMOTES.get(i) + " : **" + p.getTitulo()+"** , criada por <@"+p.getCriadorId()+">.\n\n");
        }
        eb.setFooter("uso exclusivo de " + nome,pic);
        mensagem().editMessage(eb.build()).submit()
        .thenRun(()->atualiza(guild));

    }

    public void atualiza(ModelGuild guild){
        mensagem().clearReactions().queue(x->{
            src( new HashMap<>(){{
                for (int i = 0; i < guild.getPolls().size(); i++) {
                    final int finalI = i;
                    mensagem().addReaction(Constantes.POOL_EMOTES.get(i)).submit();
                    put(Constantes.POOL_EMOTES.get(i), r -> pagina(guild.getPolls().get(finalI).getTitulo()));
                }
            }});
        });
    }
}
