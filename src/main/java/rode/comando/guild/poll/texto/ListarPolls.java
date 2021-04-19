package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.requests.restaction.CommandUpdateAction;
import org.bson.Document;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.model.ModelGuild;
import rode.model.Poll;
import rode.utilitarios.Constantes;
import rode.utilitarios.Memoria;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.function.BiFunction;

@EComandoPoll
public class ListarPolls extends AbrePoll {

    public ListarPolls() {
        super("list", null, false, "lpoll","list","listar","listpoll","listarpoll");
        setPath("list");
    }

    @Override
    public void subscribeSlash(CommandUpdateAction.CommandData commandData, ResourceBundle bundle) {
        var subCommand = new CommandUpdateAction.SubcommandData(getCommand(), bundle.getString(getHelp()));
        commandData.addSubcommand(subCommand);
    }

    @Override
    public void executeSlash(SlashCommandEvent slash, Helper.Slash hs) {

    }
    private void function(String[] args, Helper hm, BiFunction<String, EmbedBuilder, Message> reply){
        Document doc = Memoria.guilds.find(new Document("id", hm.guildId())).first();
        if(doc != null){
            ModelGuild g = ModelGuild.fromMongo(doc);

            EmbedBuilder eb = Constantes.builder();
            eb.setTitle("polls abertas");
            for(Poll p: g.getPolls()) {
                String t = p.getTitle();
                eb.appendDescription(String.format(hm.text("list.exec.line"), t, p.creatorId()));
            }
            reply.apply(null,eb);
            return;
        }
        reply.apply(hm.text("list.exec.empty"),null);
    }
    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws IOException, Exception {
        /*Document doc = Memoria.guilds.find(new Document("id", hm.guildId())).first();
        if(doc != null){
            ModelGuild g = ModelGuild.fromMongo(doc);

            EmbedBuilder eb = Constantes.builder();
            eb.setTitle("polls abertas");
            for(Poll p: g.getPolls()) {
                String t = p.getTitle();
                eb.appendDescription(String.format(hm.text("list.exec.line"), t, p.creatorId()));
            }
            hm.reply(eb);
            return;
        }
        hm.reply(hm.text("list.exec.empty"));*/
    }
}
