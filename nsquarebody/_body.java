public class _body{

double mass;
double x;
double y;
double velocityX;
double velocityY;
int ID;


public _body(int ID, double mass, double x, double y, double velocityX, double velocityY){
    this.ID = ID;
    this.mass = mass;
    this.x = x;
    this.y = y;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
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