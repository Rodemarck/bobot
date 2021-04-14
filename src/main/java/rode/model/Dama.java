package rode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static int x = 15;
    private static int y = 15;
    private static int adder = 45;
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
    public File plotFlipped()  {
        try{
            var tab = ImageIO.read(new File("imgs/tabf.png"));
            var ctx = tab.getGraphics();

            for(var d:pecas_brancas)
                ctx.drawImage(branca,x+(adder * (7 - d.p.x)),y+(adder * (7 - d.p.y)),null);
            for(var d:pecas_pretas)
                ctx.drawImage(preta,x+(adder * (7 - d.p.x)),y+(adder * (7 - d.p.y)),null);
            var f = new File("lixo/" + System.currentTimeMillis() + ".png");
            ImageIO.write(tab, "png", f);
            return f;
        }catch (IOException e){
            return null;
        }
    }
    private Peca get(Ponto p){
        for(var peca:pecas_pretas)
            if(peca.p.equals(p))
                return peca;
        for(var peca:pecas_brancas)
            if(peca.p.equals(p))
                return peca;
        return null;
    }
    public boolean play(boolean vezBranco, Ponto ... pontos){
        log.debug("jogando com o {}" , vezBranco?"banco":"preto");
        var cadaveres = new LinkedList<Peca>();
        Peca peca = null;
        var fim = false;
        for(var i=1;i<pontos.length;i++){
            peca = get(pontos[i-1]);
            if(peca.valido(pontos[i],cadaveres) == -1) {
                fim = true;
                break;
            }
            peca.p = pontos[i];
        }
        if(fim){
            peca.p=pontos[0];
        }
        System.out.println("pedras");
        cadaveres.forEach(System.out::println);
        if(vezBranco)
            pecas_pretas.removeAll(cadaveres);
        else
            pecas_brancas.removeAll(cadaveres);
        return true;
    }
    public boolean vago(Ponto p){
        log.info("branco?");
        for(var v:pecas_brancas)
            if(v.p.equals(p))
                return false;
        log.info("preto?");
        for(var b:pecas_pretas)
            if(b.p.equals(p))
                return false;
        log.info("nada?");
        return true;
    }
    public Peca contains(Ponto p, boolean branco){
        if(!branco)
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
        public int valido(Ponto ponto,LinkedList<Peca> cadaver){
            log.info("branca = " +branca + ", ponto=" + ponto);
            if(ponto.y > p.y){
                log.info("y>");
                if(branca && !dama) {
                    log.error("caso 1");
                    return -1;
                }
                if(ponto.x > p.x){
                    log.info("x>");
                    if(ponto.equals(p.x+1,p.y+1)) {
                        log.error("caso 2");
                        return vago(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x+2,p.y+2)) {
                        log.error("caso 3");
                        var t = get(new Ponto(p.x+1, p.y+1));
                        System.out.println(t == null? "peca achada" :"vacuo");
                        if(t != null && t.branca!=branca && vago(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 4");
                    return -1;
                }
                if(ponto.x < p.x){
                    log.info("x<");
                    if(ponto.equals(p.x-1,p.y+1)) {
                        log.error("caso 6");
                        return vago(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x-2,p.y+2)) {
                        log.error("caso 7");
                        var t = get(new Ponto(p.x-1,p.y+1));
                        System.out.println(t == null? "peca achada" :"vacuo");
                        if(t != null && t.branca!=branca && vago(ponto)){
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 8");
                    return -1;
                }
                log.error("caso 9");
                return -1;
            }
            if(ponto.y < p.y){
                log.info("y<");
                if(!branca && !dama) {
                    log.error("caso 9");
                    return -1;
                }
                if(ponto.x > p.x){
                    log.info("x>");
                    if(ponto.equals(p.x+1,p.y-1)) {
                        log.error("caso 10");
                        return vago(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x+2,p.y-2)) {
                        log.error("caso 11");
                        var t = get(new Ponto(p.x+1,p.y-1));
                        System.out.println((t != null? "peca achada" :"vacuo" )+ vago(ponto) );
                        if(t != null && t.branca!=branca  && vago(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 12");
                    return -1;
                }
                if(ponto.x < p.x){
                    log.info("x<");
                    if(ponto.equals(p.x-1,p.y-1)) {
                        log.error("caso 14");
                        return vago(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x-2,p.y-2)) {
                        log.error("caso 15");
                        var t = get(new Ponto(p.x-1, p.y-1));
                        System.out.println(t == null? "peca achada" :"vacuo");
                        if(t != null && t.branca!=branca && vago(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }log.error("caso 16");
                    return -1;
                }
                log.error("caso 17");
                return -1;
            }
            log.error("caso 18");
            return -1;
        }

        @Override
        public String toString() {
            return (branca?"B":"P") +p + (dama?"D":"");
        }
    }
}
