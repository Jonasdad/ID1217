import java.util.*;

public class quadtree_try {
    body root;
    int mass_sum;
    double centerOfMassX;
    double centerOfMassY;
    quadtree_try NW;
    quadtree_try NE;
    quadtree_try SW;
    quadtree_try SE;
    int[] topleft = new int[2];
    int[] botright = new int[2];

    public quadtree_try(int botrightX, int botrightY, int topleftX, int topleftY) {
        this.root = null;
        NW = null;
        NE = null;
        SW = null;
        SE = null;
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
            System.out.println("CenterX: " + centerX + " CenterY: " + centerY);
            NW = new quadtree_try(centerX, centerY, topleft[0], topleft[1]);
            SW = new quadtree_try(topleft[0], centerY, centerX, botright[1]);
            NE = new quadtree_try(botright[0], centerY, centerX, topleft[1]);
            SE = new quadtree_try(botright[0], botright[1], centerX, centerY);

            // Push the existing body and the new body into the appropriate child nodes
            
            if (root.x == b.x && root.y == b.y) {
                // Slightly adjust the coordinates of the new body
                b.x += 1;
                b.y += 1;
            } 

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

    
    public static void main(String[] args) {
        quadtree_try q = new quadtree_try(10000, 0, 0, 10000);
        Random r = new Random();
        Set<Integer> set = new HashSet<>();
        int numbers[][] = new int[10000][2];
        
        while(set.size() < 10000){
            int number = r.nextInt(10000);
            set.add(number);
        }
        
        for(int number : set){
        //    System.out.println(number);
        }

        long t1 = System.nanoTime();
      

        for (int i = 0; i < 100; i++) {
          body b = new body(i + 1, 1, r.nextInt(10000),r.nextInt(10000), 0, 0);
          q.push(b);
          System.out.println("Inserted: " + b.ID + " at " + b.getX() + ", " + b.getY());
        }
        System.out.println(q.mass_sum);
        
    }
}
