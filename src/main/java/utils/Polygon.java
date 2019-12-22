package utils;

import static java.lang.Math.abs;

public class Polygon {
    
    private int n_points;
    private double[] x_points;
    private double[] y_points;
    
    public Polygon(double[] x, double[] y) throws Exception{
        if (x.length == y.length) {
            n_points = x.length;
            x_points = new double[n_points];
            y_points = new double[n_points];
            System.arraycopy(x, 0, this.x_points, 0, n_points);
            System.arraycopy(y, 0, this.y_points, 0, n_points);
        } else
            throw new Exception("Different arrays size");
    }
    
    double area() {
        double area = 0;

        int j = n_points - 1; 
        for (int i = 0; i < n_points; i++) { 
            area += (x_points[j] + x_points[i]) * (y_points[j] - y_points[i]); 
            j = i;
        } 

        return abs(area / 2); 
    } 

    public static void main (String[] args) throws Exception{
        Polygon p = new Polygon(new double[] {16.273587482483578, 16.273587482483578, 17.466052517516424, 17.466052517516424, 16.273587482483578}, new double[] {40.67150236146425, 41.56981763853575, 41.56981763853575, 40.67150236146425, 40.67150236146425});
        System.out.printf("Area: %.15f", p.area());
    }
}
