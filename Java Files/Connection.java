public class Connection {
    int from = -1, to = -1;
    double weight = 0;
    long id = -1;
    Connection(int f, int t, double w)
    {
        this.from = f;
        this.to = t;
        this.weight = w;
        this.id = this.hashCode();
    }
    void display()
    {
        System.out.println(from+" -> "+to+" : "+weight);
    }
}
