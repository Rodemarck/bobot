package rode.model;

import rode.core.Helper;
import rode.utilitarios.Constantes;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Dama {

    private List<Peca> pecas_pretas;
    private List<Peca> pecas_brancas;
    private Ponto[][] historico;
    private static int x = 60;
    private static int y = 60;
    private static int adder = 61;

    public Dama(){
        pecas_pretas = new LinkedList<>(Arrays.asList(
                new Peca(1,0,false),new Peca(3,0,false),
                new Peca(5,0,false),new Peca(7,0,false),
                new Peca(0,1,false),new Peca(2,1,false),
                new Peca(4,1,false),new Peca(6,1,false),
                new Peca(1,2,false),new Peca(3,2,false),
                new Peca(5,2,false),new Peca(7,2,false)
        ));
        pecas_brancas = new LinkedList<>(Arrays.asList(
                new Peca(0,5,true),new Peca(2,5,true),
                new Peca(4,5,true),new Peca(6,5,true),
                new Peca(1,6,true),new Peca(3,6,true),
                new Peca(5,6,true),new Peca(7,6,true),
                new Peca(0,7,true),new Peca(2,7,true),
                new Peca(4,7,true),new Peca(6,7,true)
        ));
        this.historico = new Ponto[10][2];
    }

    public File plot()  {
        try{
            var tab = ImageIO.read(new File("imgs/tab.png"));

            var preta = ImageIO.read(new File("imgs/dama_preta.png"));
            var branca = ImageIO.read(new File("imgs/dama_branca.png"));
            var ctx = tab.getGraphics();

            for(var d:pecas_brancas)
                ctx.drawImage(branca,x+(adder * d.p.x),y+(adder * d.p.y),null);
            for(var d:pecas_pretas)
                ctx.drawImage(preta,x+(adder * d.p.x),y+(adder * d.p.y),null);
            var f = new File("lixo/" + System.currentTimeMillis() + ".png");
            ImageIO.write(tab, "png", f);


            return f;
        }catch (IOException e){
            return null;
        }
    }

    public void sendPlot(Helper.Mensagem event) {
        var p = plot();
        event.getEvent().getChannel().sendFile(p,"a.png").embed(Constantes.builder().setImage("attachment://a.png").build()).queue();
        /*event.jda().getTextChannelById(822709716601929758l).sendFile(plot()).queue(m->{
            event.reply(Constantes.builder());
        });*/
    }

    private class Peca{
        private Ponto p;
        private boolean dama;
        private final boolean branca;
        public Peca(int x, int y, boolean branca){
            this.p = new Ponto(x,y);
            this.dama = false;
            this.branca = branca;
        }
    }
}
