package rode.model;

public class Ponto {
    public int x;
    public int y;
    public Ponto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Ponto p) {
        return p.x == x && p.y == y;
    }
    public boolean equals(int x, int y){
        return this.x == x && this.y == y;
    }

    @Override
    public String toString() {
        return "(" + x +',' + y +')';
    }
}
