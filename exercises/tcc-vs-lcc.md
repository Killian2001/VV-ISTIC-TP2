# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

*Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) are metrics calculated from a graph of a class, where nodes correspond to the methods of the class, and the edges between two methods indicates thes two methds both use a same instance variable. In this graph, TCC corresponds to the number of pairs of node connected over the number of possible pairs of node in this graph ; on the other hand, LCC is equal to the number of pairs of node (indirect or direct) over the total number of pairs of the graph. It is possible to obtain the same TCC or LCC under the following circumstances :

- There is no methods which share variables : therefore, there is no edges, i.e. no pairs of methods bound in the graph. Thus, the numerator is 0 in both TCC and LCC formulas, and we have TCC = LCC = 0. An example of such class :
  ```java
  public class Point {
    private final int x;
    private final int y;
  
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
  
    public int getX() {
      return x;
    }
  
    public int getY() {
      return y;
    }
  }
  ```
- There is no more than two methods which share the same instance variable : in this case, each pairs linked by an edge is isolated from the other pairs ; therefore, there is no indirect pair, and the numerator of the LCC counts only direct pairs. Thus, it is equal to the numerator of the TCC, and therefore we have TCC = LCC. An example :
  ```java
  public class Point {
    private final int x;
    private final int y;
  
    public Point(int x, int y) {
      this.x = x;
      this.y = y;
    }
  
    public int getX() {
      return x;
    }
  
    public int getY() {
      return y;
    }

    public int setX(int x) {
      this.x = x;
    }

    public int setY(int y) {
      this.y = y;
    }
  }
  ```
- All variables are shared by all methods : we obtain therefore a connected graph, which means that all nodes are directly connected : numerators of TCC and LCC are equals to the number of pairs, and therefore, TCC = LCC = 1. An example :
  ```java
  public class Vector {
    private final int x;
    private final int y;
  
    public Vector(int x, int y) {
      this.x = x;
      this.y = y;
    }
  
    public double norm() {
      return Math.sqrt((x * x) + (y * y));
    }

    public Vector opposite() {
      return new Vector(-x, -y);
    }
  }
  ```
For any class, $LCC \geq TCC$ ; first the denominator in the formula of TCC is the same that the one of LCC formula. Secondly, the numerator of LCC formula is the number of direct and indirect pairs ; as the set of direct pairs is a subset of the set of direct and indrect pairs, we can be sure the numerator of LCC will be superior or equal to the one of the TCC formula ; therefore, we will always have $LCC \geq TCC$.
