public class body{

int mass;
int x;
int y;
int velocityX;
int velocityY;
int ID;


public body(int ID, int mass, int x, int y, int velocityX, int velocityY){
    this.ID = ID;
    this.mass = mass;
    this.x = x;
    this.y = y;
    this.velocityX = velocityX;
    this.velocityY = velocityY;
}


public int getX(){
    return x;
}

public int getY(){
    return y;
}

public void setX(int x){
    this.x = x;
}

public void setY(int y){
    this.y = y;
}

public int getMass(){
    return mass;
}

public void setMass(int mass){
    this.mass = mass;
}

public int getVelocityX(){
    return velocityX;}
public int getVelocityY(){
    return velocityY;}
public void setVelocityX(int velocityX){
    this.velocityX = velocityX;}
public void setVelocityY(int velocityY){
    this.velocityY = velocityY;}

}