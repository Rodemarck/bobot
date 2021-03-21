package rode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.core.Helper;
import rode.utilitarios.Constantes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Dama {
    private static final Logger log = LoggerFactory.getLogger(Dama.class);
    private List<Peca> pecas_pretas;
    private List<Peca> pecas_brancas;
    private Ponto[][] historico;
    private static int x = 60;
    private static int y = 60;
    private static int adder = 61;
    private static BufferedImage preta;
    private static BufferedImage branca;

    static {
        try {
            preta = ImageIO.read(new File("imgs/dama_preta.png"));
            branca = ImageIO.read(new File("imgs/dama_branca.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
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

    public boolean joga(boolean vezBranco, Ponto ... pontos){
        log.debug("jogando com o {}" , vezBranco?"banco":"preto");
        if(vezBranco){
            for(var peca:pecas_brancas)
                if(peca.p.equals(pontos[0])){
                    log.info("achei peca");
                    if(peca.valido(pontos[1])){
                        peca.p = pontos[1];
                        return true;
                    }
                }
        }
        else{
            for(var peca:pecas_pretas)
                if(peca.p.equals(pontos[0])){
                    log.info("achei peca");
                    if(peca.valido(pontos[1])){
                        peca.p = pontos[1];
                        return true;
                    }
                }
        }
        return false;
    }
    public boolean vago(Ponto p){
        for(var v:pecas_brancas)
            if(v.p.equals(p))
                return false;
        for(var b:pecas_pretas)
            if(b.p.equals(p))
                return false;
        return true;
    }
    public Peca contains(Ponto p, boolean branco){
        if(branco)
            for(var b:pecas_brancas)
                if(b.p.equals(p))
                    return b;
        else
            for(var v:pecas_pretas)
                if(v.p.equals(p))
                    return v;
        return null;
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
        public boolean valido(Ponto ponto){
            log.info("branca = " +branca);
            if(ponto.y > p.y){
                log.info("y>");
                if(branca && !dama) {
                    log.error("caso 1");
                    return false;
                }
                if(ponto.x > p.x){
                    log.info("x>");
                    if(ponto.equals(p.x+1,p.y+1)) {
                        log.error("caso 2");
                        return vago(ponto);
                    }
                    if(ponto.equals(p.x+2,p.y+2)) {
                        log.error("caso 3");
                        var t = contains(ponto, branca);
                        if(t != null && vago(ponto)) {
                            if (branca)
                                pecas_pretas.remove(t);
                            else
                                pecas_brancas.remove(t);
                        }
                    }
                    log.error("caso 4");
                    return false;
                }
                if(ponto.x < p.x){
                    log.info("x<");
                    if(ponto.equals(p.x-1,p.y+1)) {
                        log.error("caso 6");
                        return vago(ponto);
                    }
                    if(ponto.equals(p.x-2,p.y+2)) {
                        log.error("caso 7");
                        var t = contains(ponto, !branca);
                        if(t != null && vago(ponto)){
                            if(branca)
                                pecas_pretas.remove(t);
                            else
                                pecas_brancas.remove(t);
                            return true;
                        }
                    }
                    log.error("caso 8");
                    return false;
                }log.error("caso 9");
                return false;
            }
            if(ponto.y < p.y){
                log.info("y<");
                if(!branca && !dama) {
                    log.error("caso 9");
                    return false;
                }
                if(ponto.x > p.x){
                    log.info("x>");
                    if(ponto.equals(p.x+1,p.y-1)) {
                        log.error("caso 10");
                        return vago(ponto);
                    }
                    if(ponto.equals(p.x+2,p.y-2)) {
                        log.error("caso 11");
                        var t = contains(ponto, branca);
                        if(t != null && vago(ponto)) {
                            if (branca)
                                pecas_pretas.remove(t);
                            else
                                pecas_brancas.remove(t);
                            return true;
                        }
                    }
                    log.error("caso 12");
                    return false;
                }
                if(ponto.x < p.x){
                    log.info("x<");
                    if(ponto.equals(p.x-1,p.y-1)) {
                        log.error("caso 14");
                        return vago(ponto);
                    }
                    if(ponto.equals(p.x-2,p.y-2)) {
                        log.error("caso 15");
                        var t = contains(ponto, branca);
                        if(t != null && vago(ponto)) {
                            if (branca)
                                pecas_pretas.remove(t);
                            else
                                pecas_brancas.remove(t);
                            return true;
                        }
                    }log.error("caso 16");
                    return false;
                }
                log.error("caso 17");
                return false;
            }
            log.error("caso 18");
            return false;
        }
    }
}
