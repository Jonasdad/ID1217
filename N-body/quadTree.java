import java.util.*;

public class quadTree {
    body root;
    int mass_sum;
    quadTree NW;
    quadTree NE;
    quadTree SW;
    quadTree SE;
    float[] topleft = new float[2];
    float[] botright = new float[2];

    public quadTree(float botrightX, float botrightY, float topleftX, float topleftY) {
        this.root = null;
        quadTree NW = null;
        quadTree NE = null;
        quadTree SW = null;
        quadTree SE = null;
        this.topleft[0] = topleftX;
        this.topleft[1] = topleftY;
        this.botright[0] = botrightX;
        this.botright[1] = botrightY;
    }

    public void insert(body b) {
        if(b == null){
            return;
        }
        if (Math.abs(topleft[0] - botright[0]) <= 1 && Math.abs(topleft[1] - botright[1]) <= 1) {
            // We are at a quad of unit area; cannot subdivide further    
            return;
        }
        if (root == null){
            System.out.println("Setting body to root");
            
            root = b;
            System.out.println(b.getMass());
            this.mass_sum += b.getMass();
            return;
        }
        // If root is not null and we are inserting a new body, push down the root
        body temp = root;
        root = null; // Set the new body as root
        push(temp);  // Push down the old root
        insert(b);   // Push down the new body
    }

    public void push(body b) {
        float centerX = Math.abs(this.topleft[0] - this.botright[0]) / 2;
        float centerY = Math.abs(this.topleft[1] - this.botright[1]) / 2;
        System.out.println("Center X: " + centerX + ", Center Y: " + centerY);
        if (b.getX() <= centerX) { // if the body is in the left half
            System.out.println("Body is in the left half");
            if (b.getY() >= centerY) { // if the body is in the top half
                System.out.println("Body is in the top half");
                if (this.NW == null) {
                    System.out.println("Creating new NW quadTree");
                    this.NW = new quadTree(centerX, centerY, this.topleft[0], this.topleft[1]);
                }
                System.out.println("Inserting body into NW quadTree");
                this.NW.insert(b);
            } else {
                System.out.println("Body is in the bottom half");
                if (this.SW == null) {
                    System.out.println("Creating new SW quadTree");
                    this.SW = new quadTree(this.topleft[0], centerY, centerX, this.botright[1]);
                }
                System.out.println("Inserting body into SW quadTree");
                this.SW.insert(b);
            }
        } else {
            System.out.println("Body is in the right half");
            if (b.getY() >= centerY) {
                System.out.println("Body is in the top half");
                if (this.NE == null) {
                    System.out.println("Creating new NE quadTree");
                    this.NE = new quadTree(this.botright[0], centerY, centerX, this.topleft[1]);
                }
                System.out.println("Inserting body into NE quadTree");
                this.NE.insert(b);
            } else {
                System.out.println("Body is in the bottom half");
                if (this.SE == null) {
                    System.out.println("Creating new SE quadTree");
                    this.SE = new quadTree(this.botright[0], this.botright[1], centerX, centerY);
                }
                System.out.println("Inserting body into SE quadTree");
                this.SE.insert(b);
            }
        }
    }


    public double[] center_of_mass() {
        double totalMass = 0;
        double[] centerOfMass = new double[3]; // [center_of_mass_x, center_of_mass_y, mass]
        if (root != null) {
            totalMass += root.getMass();
            centerOfMass[0] += root.getMass() * root.getX();
            centerOfMass[1] += root.getMass() * root.getY();
        }
        if (NW != null) {
            double[] NWCenterOfMass = NW.center_of_mass();
            totalMass += NWCenterOfMass[2];
            centerOfMass[0] += NWCenterOfMass[0] * NWCenterOfMass[2];
            centerOfMass[1] += NWCenterOfMass[1] * NWCenterOfMass[2];
        }
        if (NE != null) {
            double[] NECenterOfMass = NE.center_of_mass();
            totalMass += NECenterOfMass[2];
            centerOfMass[0] += NECenterOfMass[0] * NECenterOfMass[2];
            centerOfMass[1] += NECenterOfMass[1] * NECenterOfMass[2];
        }
        if (SW != null) {
            double[] SWCenterOfMass = SW.center_of_mass();
            totalMass += SWCenterOfMass[2];
            centerOfMass[0] += SWCenterOfMass[0] * SWCenterOfMass[2];
            centerOfMass[1] += SWCenterOfMass[1] * SWCenterOfMass[2];
        }
        if (SE != null) {
            double[] SECenterOfMass = SE.center_of_mass();
            totalMass += SECenterOfMass[2];
            centerOfMass[0] += SECenterOfMass[0] * SECenterOfMass[2];
            centerOfMass[1] += SECenterOfMass[1] * SECenterOfMass[2];
        }
        if (totalMass > 0) {
            centerOfMass[0] /= totalMass;
            centerOfMass[1] /= totalMass;
            centerOfMass[2] = totalMass;
        }
        return centerOfMass;
    }

    public static void main(String[] args) {
        quadTree q = new quadTree(100, 0, 0, 100);
        Random r = new Random();
        for(int i = 0; i < 200; i++){
            body b = new body(i+1, 1, r.nextInt(1000), r.nextInt(1000), 0,0);
            q.insert(b);
        }

        /*
        body b1 = new body(1, 10, 100, 100, 0, 0); // NE
        q.push(b1);
      
        body b2 = new body(2, 10, 0, 100, 0, 0); // NW
        q.push(b2);
        
        body b3 = new body(3, 10, 0, 0, 0, 0); // SW
        q.push(b3);

        body b4 = new body(4, 10, 100, 0, 0, 0); // SE
        q.push(b4);

        body b5 = new body(5, 10, 50, 50, 0 ,0);
        q.push(b5);

        body b6 = new body(6, 10, 25, 25, 0 ,0);
        q.push(b6);

        body b7 = new body(7, 10, 75, 75, 0 ,0);
        q.push(b7);

        body b8 = new body(8, 10, 25, 75, 0 ,0);
        q.push(b8);*/

        double[] COM = q.center_of_mass();
        double[] COM2 = q.NE.center_of_mass();
        System.out.println("Coordinates: " + "("+COM[0] +","+ COM[1]+")" + " Mass: " + COM[2]);
        System.out.println("Coordinates: " + "("+COM2[0] +","+ COM2[1]+")" + " Mass: " + COM2[2]);
        
    }
}
