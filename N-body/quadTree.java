import java.util.*;

public class quadTree {
    body root;
    int mass_sum;
    double centerOfMassX;
    double centerOfMassY;
    quadTree NW;
    quadTree NE;
    quadTree SW;
    quadTree SE;
    int[] topleft = new int[2];
    int[] botright = new int[2];

    public quadTree(int botrightX, int botrightY, int topleftX, int topleftY) {
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

    public void push(body b) {
        // Update the mass and center of mass of the current node
        // this.centerOfMassX = center_of_mass()[0];
        // this.centerOfMassY = center_of_mass()[1];
        
        if(this.root == null){
            this.root = b;
            return;
        }
        else{
            
        }
        int centerX = this.topleft[0] + (this.botright[0] - this.topleft[0]) / 2;
        int centerY = this.topleft[1] + (this.botright[1] - this.topleft[1]) / 2;

        if (b.getX() <= centerX) { // if the body is in the left half
            if (b.getY() >= centerY) { // if the body is in the top half
                if (this.NW == null) {
                    this.NW = new quadTree(centerX, centerY, this.topleft[0], this.topleft[1]);
                    this.NW.push(b);
                }
            } else {
                if (this.SW == null) {
                    this.SW = new quadTree(this.topleft[0], centerY, centerX, this.botright[1]);
                    this.SW.push(b);
                }
            }
        } else {
            if (b.getY() >= centerY) {
                if (this.NE == null) {
                    this.NE = new quadTree(this.botright[0], centerY, centerX, this.topleft[1]);
                    this.NE.push(b);
                }
            } else {
                if (this.SE == null) {
                    this.SE = new quadTree(this.botright[0], this.botright[1], centerX, centerY);
                    this.SE.push(b);
                }
            }
        }

        // If the current node is a leaf node, store the body

    }

    public boolean inBoundaries(body b) {
        if (b.x >= this.topleft[0] && b.x <= this.botright[0] && b.y >= this.topleft[1] && b.y <= this.botright[1]) {
            return true;
        }
        return false;
    }

    public boolean isLeaf(quadTree tree) {
        if (tree.NW == null && tree.NE == null && tree.SW == null && tree.SE == null) {
            return true;
        }
        return false;
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
        quadTree q = new quadTree(10000, 0, 0, 10000);
        Random r = new Random();

        long t1 = System.nanoTime();
        for (int i = 0; i < 10; i++) {
            body b = new body(i + 1, 1, r.nextInt(10000), r.nextInt(10000), 0, 0);
            q.push(b);
            System.out.println("Inserted: " + b.ID + " at " + b.getX() + ", " + b.getY());
        }
        System.out.println("hej");
        // System.out.println("Main Root: " + q.NE.root.ID);
        // System.out.println("Time: " + ((System.nanoTime() - t1)/1_000) + " micro
        // seconds");
        // System.out.println("Center of Mass: " + centerOfMass[0] + ", " +
        // centerOfMass[1] + ", Mass: " + centerOfMass[2]);
    }
}
