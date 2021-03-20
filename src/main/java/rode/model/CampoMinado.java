package rode.model;

import java.util.LinkedList;
import java.util.Random;
import java.util.function.Consumer;

public class CampoMinado {
    private final short[][] campo;
    private final boolean[][] campoVisivel;
    private static final Random rng = new Random();
    private boolean finalizado;

    public CampoMinado() {
        this.finalizado = false;
        this.campo = new short[10][10];
        var campo2 = new short[10][10];
        this.campoVisivel = new boolean[10][10];
        int x,y;
        for(int i=0;i<10;i++){
            do{
                x = rng.nextInt(10);
                y = rng.nextInt(10);
            }while (campo[x][y] != 0);
            campo[x][y]=-1;
            loop(new Ponto(x,y),ponto -> ++campo2[ponto.x][ponto.y]);
        }
        for(x=0;x<10;x++)
            for(y=0;y<10;y++){
                if(campo[x][y] == 0)
                    campo[x][y] = campo2[x][y];
                this.campoVisivel[x][y] = false;

            }

    }
    @Override
    public String toString(){
        var sb = new StringBuilder();
        sb.append(" ");
        for(int x=0;x<10;x++){
            sb.append("  ");
            sb.append(x);
        }
        sb.append("\n ╔");
        sb.append("═══".repeat(10));
        sb.append("\n");
        for(int y=0;y<10;y++) {
            sb.append(y);
            sb.append("║ ");
            for (int x = 0; x < 10; x++) {
                if(campoVisivel[x][y]){
                    if(campo[x][y] == 0)
                        sb.append(" ");
                    else{
                        if(campo[x][y] == -1)
                            sb.append("X");
                        else
                            sb.append(campo[x][y]);
                    }
                }else
                    sb.append("?");
                sb.append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public boolean joga(int x, int y){
        if(campo[x][y]==-1){
            campoVisivel[x][y]=true;
            finalizado = true;
            return true;
        }
        traca(new Ponto(x,y));
        finalizado = true;
        for(int yy=0;yy<10;yy++)
            for(int xx=0;xx<10;xx++)
                if(campoVisivel[xx][yy] && campo[x][y]!=-1)
                    finalizado = false;
        return false;
    }

    private void loop(Ponto p, Consumer<Ponto> acao){
        int x0=round(p.x-1),
                xf = round(p.x+1),
                y0 = round(p.y-1),
                yf = round(p.y+1);
        for(int y=y0;y<=yf;y++)
            for(int x=x0;x<=xf;x++)
                acao.accept(new Ponto(x,y));
    }
    private void traca(Ponto pqp){
        var pontos = new LinkedList<Ponto>();
        var todosPontos = new LinkedList<Ponto>();
        Ponto p;
        pontos.add(pqp);
        while (!pontos.isEmpty()){

            System.out.println("*");
            p = pontos.pop();
            campoVisivel[p.x][p.y] = true;
            System.out.println(p);
            todosPontos.add(p);
            if(campo[p.x][p.y] == 0){
                loop(p,ponto->{
                    if(!todosPontos.contains(ponto) && !campoVisivel[ponto.x][ponto.y])
                        pontos.add(ponto);
                });
            }
        }
    }

    public boolean finalizado() {
        return finalizado;
    }

    private int round(int i) {
        return Math.min(Math.max(0,i), 9);
    }
}
