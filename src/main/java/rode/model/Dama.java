package rode.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rode.utilitarios.Constantes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private static BufferedImage dama_preta;
    private static BufferedImage dama_branca;
    private static BufferedImage tabuleiro;
    private static BufferedImage tabuleiro_flip;

    static {
        try {
            preta = ImageIO.read(new File("imgs/preta.png"));
            branca = ImageIO.read(new File("imgs/branca.png"));
            dama_branca = ImageIO.read(new File("imgs/dama_branca.png"));
            dama_preta = ImageIO.read(new File("imgs/dama_preta.png"));
            tabuleiro  = ImageIO.read(new File("imgs/tab.png"));
            tabuleiro_flip = ImageIO.read(new File("imgs/tabf.png"));
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
        //  Stream.concat(pecas_brancas.stream(),pecas_pretas.stream()).forEach(p->p.dama=true);
        //this.historico = new Ponto[10][2];
    }
    public File plot()  {
        try{
            var tab = copy(tabuleiro);
            var ctx = tab.getGraphics();

            for(var d:pecas_brancas)
                ctx.drawImage(d.dama ? dama_branca : branca,x+(adder * d.p.x),y+(adder * d.p.y),null);
            for(var d:pecas_pretas)
                ctx.drawImage(d.dama ? dama_preta : preta,x+(adder * d.p.x),y+(adder * d.p.y),null);
            var f = new File("lixo/" + System.currentTimeMillis() + ".png");
            ImageIO.write(tab, "png", f);
            return f;
        }catch (IOException e){
            return null;
        }
    }
    private BufferedImage copy(BufferedImage img){
        var clone = new BufferedImage(img.getWidth(),img.getHeight(),img.getType());
        var g = clone.createGraphics();
        g.drawImage(img,0,0,null);
        return clone;
    }
    public File plotFlipped()  {
        try{
            var tab = copy(tabuleiro_flip);
            var ctx = tab.getGraphics();

            for(var d:pecas_brancas)
                ctx.drawImage(d.dama ? dama_branca : branca,x+(adder * (7 - d.p.x)),y+(adder * (7 - d.p.y)),null);
            for(var d:pecas_pretas)
                ctx.drawImage(d.dama ? dama_preta : preta,x+(adder * (7 - d.p.x)),y+(adder * (7 - d.p.y)),null);
            var f = new File("lixo/" + System.currentTimeMillis() + ".png");
            ImageIO.write(tab, "png", f);
            return f;
        }catch (IOException e){
            return null;
        }
    }
    private Peca get(Ponto p){
        return Stream.concat(pecas_brancas.stream(),pecas_pretas.stream())
                .filter(ponto -> p.equals(p))
                .findFirst()
                .orElse(null);

    }
    public boolean play(boolean vezBranco, Ponto ... pontos){
        log.debug("jogando com o {}" , vezBranco?"banco":"preto" );
        var cadaveres = new LinkedList<Peca>();
        Peca peca = null;
        var erro = false;
        log.debug("pontos {");
        var str = Arrays.stream(pontos)
                .map(Ponto::toString)
                .collect(Collectors.joining(","))
        ;
        log.debug(str);
        log.debug("}");

        for (int i = 0; i < pontos.length; i++) {
            log.debug(pontos[i].toString());
        }
        peca = get(pontos[0]);
        for(var i=1;i<pontos.length;i++){
            log.debug(peca + " -> " + pontos[i] );
            if(peca.checaMovimento(pontos[i],cadaveres) == -1) {/*peca.valido(pontos[i],cadaveres)*/
                erro = true;
                log.error("movimento inválido de " + pontos[i-1] + " >>> " + pontos[i] );
                break;
            }
            peca.p = pontos[i];
        }
        if(erro)
            return false;
        System.out.println("pedras");
        cadaveres.forEach(System.out::println);
        if(vezBranco)
            pecas_pretas.removeAll(cadaveres);
        else
            pecas_brancas.removeAll(cadaveres);
        if(!peca.dama && (peca.branca && peca.p.y == 0 || !peca.branca && peca.p.y == 7)){
            peca.dama = true;
        }
        return true;
    }
    public boolean ocupado(Ponto p){
        return ocupado(p.x,p.y);
    }
    public boolean ocupado(int x, int y){
        return Stream.concat(pecas_brancas.stream(),pecas_pretas.stream())
                .map(Peca::getP)
                .anyMatch(ponto->ponto.equals(x,y));
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
            log.info((branca ? "branca" : "preta") + ", ponto=" + ponto );
            if(ponto.y > p.y){
                log.info("y>" );
                if(branca && !dama) {
                    log.error("caso 1" );
                    return -1;
                }
                if(ponto.x > p.x){
                    log.info("x>" );
                    if(ponto.equals(p.x+1,p.y+1)) {
                        log.error("caso 2" );
                        return ocupado(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x+2,p.y+2)) {
                        log.error("caso 3" );
                        var t = get(new Ponto(p.x+1, p.y+1));
                        if(t != null && t.branca!=branca && ocupado(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 4" );
                    return -1;
                }
                if(ponto.x < p.x){
                    log.info("x<" );
                    if(ponto.equals(p.x-1,p.y+1)) {
                        log.error("caso 6" );
                        return ocupado(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x-2,p.y+2)) {
                        log.error("caso 7" );
                        var t = get(new Ponto(p.x-1,p.y+1));
                        System.out.println(t == null? "peca achada" :"vacuo");
                        if(t != null && t.branca!=branca && ocupado(ponto)){
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 8" );
                    return -1;
                }
                log.error("caso 9" );
                return -1;
            }
            if(ponto.y < p.y){
                log.info("y<" );
                if(!branca && !dama) {
                    log.error("caso 9" );
                    return -1;
                }
                if(ponto.x > p.x){
                    log.info("x>" );
                    if(ponto.equals(p.x+1,p.y-1)) {
                        log.debug("caso 10" );
                        return ocupado(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x+2,p.y-2)) {
                        log.debug("caso 11" );
                        var t = get(new Ponto(p.x+1,p.y-1));
                        System.out.println((t != null? "peca achada" :"vacuo" )+ ocupado(ponto) );
                        if(t != null && t.branca!=branca  && ocupado(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }
                    log.error("caso 12" );
                    return -1;
                }
                if(ponto.x < p.x){
                    log.info("x<" );
                    if(ponto.equals(p.x-1,p.y-1)) {
                        log.debug("caso 14" );
                        return ocupado(ponto)? 0: -1;
                    }
                    if(ponto.equals(p.x-2,p.y-2)) {
                        log.debug("caso 15" );
                        var t = get(new Ponto(p.x-1, p.y-1));
                        System.out.println(t == null? "peca achada" :"vacuo" );
                        if(t != null && t.branca!=branca && ocupado(ponto)) {
                            cadaver.add(t);
                            return 1;
                        }
                    }log.error("caso 16" );
                    return -1;
                }
                log.error("caso 17" );
                return -1;
            }
            log.error("caso 18" );
            return -1;
        }
        public int checaMovimento(Ponto ponto, LinkedList<Peca> cadaveres){
            int addX = ponto.x > p.x ? 1:-1;
            int addY = ponto.y > p.y ? 1:-1;
            int X = p.x+addX;
            int Y = p.y+addY;
            log.debug("add = " + new Ponto(addX,addY) );
            if(dama){
                log.debug("dama");
                boolean check = false,ocupado;
                for(; X>=0 && X<8 && Y>=0 && Y<8 ;X +=addX,Y+=addY){
                    ocupado = ocupado(X,Y);
                    log.debug("olhando " + new Ponto(X,Y) + " -> " + ocupado);
                    if(ponto.equals(X,Y) && !ocupado)
                        return check? 1:0;
                    if(ocupado && check)
                        return -1;
                    if(ocupado) {
                        log.debug("pedrinha em " + new Ponto(X,Y));
                        check = true;
                        cadaveres.add(get(new Ponto(X,Y)));
                    }
                }
                return -1;
            }
            log.debug("não é dama" );
            log.debug("branca " + true );
            if(branca) {
                if (addY == 1){
                    log.debug("branca subindo" );
                    return -1;
                }
            }else if (addY == -1){
                log.debug("preta descendo" );
                return -1;
            }
            var ocupado = ocupado(X,Y);
            if(ponto.equals(X,Y))
                return ocupado ? -1:0;
            if(ponto.equals(X+addX,Y+addY))
                return ocupado && !ocupado(ponto) ? 1 : -1;
            return -1;
        }
        public Ponto getP(){
            return p;
        }
        @Override
        public String toString() {
            return (branca?"B":"P") +p + (dama?"D":"");
        }
    }
}
