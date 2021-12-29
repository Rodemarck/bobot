package rode.comando.guild.poll.texto;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import rode.core.Anotacoes.EComandoPoll;
import rode.core.Helper;
import rode.core.PollHelper;
import rode.model.Poll;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.ResourceBundle;

@EComandoPoll
public class GraficoPoll extends AbrePoll {
    public GraficoPoll() {
        super("grafico", null, false,"grafico","gráfico","graphic","gpoll");
        setPath("grafico");
    }

    @Override
    public void subscribeSlash(CommandData commandData, ResourceBundle bundle) {
        var subCommand = new SubcommandData(getCommand(),bundle.getString(getHelp()))
                .addOptions(new OptionData(OptionType.STRING,"titulo","titulo da poll a ser deletada").setRequired(true));
        commandData.addSubcommands(subCommand);
    }

    @Override
    public void execute(String[] args, Helper.Mensagem hm) throws Exception {
        PollHelper.getPoll(args, hm, dp -> {
            if(dp.guild() != null){
                Poll poll = dp.guild().getPoll(dp.titulo());
                File arq = plot(poll, hm.getEvent().getGuild());
                hm.reply(arq);
                return;
            }
            hm.reply(String.format(hm.getText("grafico.exec"),dp.titulo()));
        });
    }

    @Override
    public void helpExtensive(EmbedBuilder me, ResourceBundle rb) {
        help(me,rb);
    }

    public File plot(Poll poll, Guild guild) throws IOException {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        HashMap<String, Integer> votos = poll.getVotesCount();
        votos.entrySet().forEach(entry->
                pieDataset.setValue(entry.getKey(), entry.getValue())
        );


        JFreeChart chart = ChartFactory.createRingChart(
                poll.getTitulo(), pieDataset, false, false, false);

        File f = new File("lixo/"+System.currentTimeMillis() + ".png");
        ChartUtilities.saveChartAsPNG(f, chart, 500, 350);

        return f;
    }
}
