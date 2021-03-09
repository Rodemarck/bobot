package rode.utilitarios;

import net.dv8tion.jda.api.entities.Guild;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.Main;
import rode.model.Poll;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Grafico {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    public static File poll(Poll poll, Guild guild) throws IOException {
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        HashMap<String, Integer> votos = poll.getNumeroVotos();
        votos.entrySet().forEach(entry->
            pieDataset.setValue(entry.getKey(), entry.getValue())
        );


        JFreeChart chart = ChartFactory.createRingChart(
                poll.titulo(), pieDataset, false, false, false);

        File f = new File(System.currentTimeMillis() + ".png");
        ChartUtilities.saveChartAsPNG(f, chart, 500, 350);

        return f;
    }
}
