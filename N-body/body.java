public class body{

double mass;
double x;
double y;
volatile double velocityX;
volatile double velocityY;
int ID;
final private double G = 6.674 * Math.pow(10, -11);

public body(int ID, int mass, double x, double y, double velocityX, double velocityY){
    this.ID = ID;
    this.mass = mass;
    this.x = x;
    this.y = y;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
}


public void updateVelocity(QuadTree b){
    body a = this;
    double dx;
    double dy; 
    double distance;
    double f;
    if(b.root == null){
        dx = b.centerOfMassX - a.x;
        dy = b.centerOfMassY - a.y;
        distance = Math.sqrt(dx*dx + dy*dy);
        f  = G * a.mass * b.mass_sum / (distance * distance);
        a.velocityX += f * dx / distance;
        a.velocityY += f * dy / distance;
        return;
    }
    dx = b.root.x - a.x;
    dy = b.root.y - a.y;
    distance = Math.sqrt(dx*dx + dy*dy);
    f  = (1 * a.mass * b.root.mass) / (distance * distance);
    a.velocityX += f * dx / distance;
    a.velocityY += f * dy / distance;   
}

public double distanceTo(QuadTree tree) {

    double dx = x - tree.centerOfMassX;
    double dy = y - tree.centerOfMassY;
    return Math.sqrt(dx*dx + dy*dy);
}

public double getX(){
    return x;
}

public double getY(){
    return y;
}

public void setX(double x){
    this.x = x;
}

public void setY(double y){
    this.y = y;
}

public double getMass(){
    return mass;
}

public void setMass(double mass){
    this.mass = mass;
}

public double getVelocityX(){
    return velocityX;}
public double getVelocityY(){
    return velocityY;}
public void setVelocityX(double velocityX){
    this.velocityX = velocityX;}
public void setVelocityY(double velocityY){
    this.velocityY = velocityY;}

}