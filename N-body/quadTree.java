import java.util.*;

public class QuadTree {
    body root;
    int mass_sum;
    double centerOfMassX;
    double centerOfMassY;
    QuadTree NW;
    QuadTree NE;
    QuadTree SW;
    QuadTree SE;
    static int numSubTrees = 0;
    int[] topleft = new int[2];
    int[] botright = new int[2];
    body bodies[] = new body[240];
    static int debug = 0;

    double Theta;
    public QuadTree(int botrightX, int botrightY, int topleftX, int topleftY) {
        this.root = null;
        this.NW = null;
        this.NE = null;
        this.SW = null;
        this.SE = null;
        this.topleft[0] = topleftX;
        this.topleft[1] = topleftY;
        this.botright[0] = botrightX;
        this.botright[1] = botrightY;
    }

    public boolean isLeaf(){
        return NW == null && NE == null && SW == null && SE == null; //If all children are null, then it is a leaf
    }

    

    public void push(body b) {
        mass_sum += b.mass;
        // Update the center of mass
        centerOfMassX = (centerOfMassX * (mass_sum - b.mass) + b.x * b.mass) / mass_sum;
        centerOfMassY = (centerOfMassY * (mass_sum - b.mass) + b.y * b.mass) / mass_sum;
        if (root == null && isLeaf()) {
            root = b;
            return;
        }
        if(!isLeaf()){
            pushToChild(b);
        }
        else {
            if (Math.abs(topleft[0] - botright[0]) <= 5 && Math.abs(topleft[1]- botright[1]) <= 5) {
                if(root == null){
                    root = b;
                    return;
                }
                root.mass += b.mass;
                b = null;
                return;
            }
            // Create the child nodes
            int centerX = (topleft[0] + botright[0]) / 2;
            int centerY = (topleft[1] + botright[1]) / 2;
            NW = new QuadTree(centerX, centerY, topleft[0], topleft[1]);
            SW = new QuadTree(topleft[0], centerY, centerX, botright[1]);
            NE = new QuadTree(botright[0], centerY, centerX, topleft[1]);
            SE = new QuadTree(botright[0], botright[1], centerX, centerY);
            numSubTrees += 4;
            // Push the existing body and the new body into the appropriate child nodes
            
            pushToChild(root);
            pushToChild(b);
            // Set the root of the current node to null
            root = null;
        }
    }
    
    private void pushToChild(body b) {
        if (((botright[0] + topleft[0]) / 2) >= b.x) { // if x is less than the middle
            if (((botright[1] + topleft[1]) / 2) <= b.y) { // if y is greater than the middle
                // push to NW
                NW.push(b);
            } else {
                // push to SW
                SW.push(b);
            }
        } else { // if x is greater than the middle
            if (((botright[1] + topleft[1]) / 2) <= b.y) { // if y is greater than the middle
                // push to NE
                NE.push(b);
            } else { // if y is less than the middle
                // push to SE
                SE.push(b);
            }
        }
    }

    public void updateForce(body b){
        if(b == null || b == root){
            return;
        }
        if(isLeaf()){
            b.updateVelocity(this);
        }
        else{
            double d = b.distanceTo(this);
            double r = Math.abs(this.botright[0] - this.topleft[0]); //this.botright[0] - this.topleft[0])/2;
            if((r/d) < Theta){
                b.updateVelocity(this);
            }
            else{
                if(NW!=null){
                    NW.updateForce(b);
                }
                if(NE!=null){
                    NE.updateForce(b);
                }
                if(SW!=null){
                    SW.updateForce(b);
                }
                if(SE!=null){
                    SE.updateForce(b);
                }
                return;
            }
        }
    }
   /* 
   public static void main(String[] args) {
       QuadTree q = new QuadTree(1000, 0, 0, 1000);
       Random r = new Random();
       long t1 = System.nanoTime();
       double[][] original = new double[240][2];
       double[][] updated = new double[240][2];
       for (int i = 0; i < 240; i++) {
           body b = new body(i + 1, 1, r.nextInt(1000),r.nextInt(1000), 0, 0);
           q.bodies[i] = b;
           original[i][0] = b.x;
           original[i][1] = b.y;
           q.push(b);
        }
        
        for(int i = 0; i < 240; i++){
            q.updateForce(q.bodies[i]);
            updated[i][0] = q.bodies[i].x;
            updated[i][1] = q.bodies[i].y;
        }

        int counter=0;
        for(int i = 0; i < 240; i++){
            if(original[i][0] != updated[i][0] && original[i][1] != updated[i][1]){
                counter++;
            }
        }
        
        System.out.println("Number of bodies that moved: " + counter);
        System.out.println("Time: " + (System.nanoTime() - t1) / 1000000.0 + " ms");      
    }
    */ 
}
