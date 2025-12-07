package model;

/**
 * Represents a node in the warehouse grid for pathfinding algorithms.
 * Each node tracks its position, costs, and parent for path reconstruction.
 */
public class Node implements Comparable<Node> {
    private final int x;
    private final int y;
    private double gScore; // Cost from start to this node
    private double hScore; // Heuristic cost from this node to goal
    private double fScore; // Total cost (g + h)
    private Node parent;   // Parent node for path reconstruction
    
    /**
     * Creates a new Node at the specified position.
     * 
     * @param x The x-coordinate (column)
     * @param y The y-coordinate (row)
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.gScore = Double.POSITIVE_INFINITY;
        this.hScore = 0;
        this.fScore = Double.POSITIVE_INFINITY;
        this.parent = null;
    }
    
    // Getters
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public double getGScore() {
        return gScore;
    }
    
    public double getHScore() {
        return hScore;
    }
    
    public double getFScore() {
        return fScore;
    }
    
    public Node getParent() {
        return parent;
    }
    
    // Setters
    public void setGScore(double gScore) {
        this.gScore = gScore;
        updateFScore();
    }
    
    public void setHScore(double hScore) {
        this.hScore = hScore;
        updateFScore();
    }
    
    public void setParent(Node parent) {
        this.parent = parent;
    }
    
    /**
     * Updates the f-score (total cost) based on current g and h scores.
     */
    private void updateFScore() {
        this.fScore = this.gScore + this.hScore;
    }
    
    /**
     * Calculates Manhattan distance heuristic to target node.
     * 
     * @param target The target node
     * @return Manhattan distance
     */
    public double manhattanDistance(Node target) {
        return Math.abs(this.x - target.x) + Math.abs(this.y - target.y);
    }
    
    /**
     * Calculates Euclidean distance heuristic to target node.
     * 
     * @param target The target node
     * @return Euclidean distance
     */
    public double euclideanDistance(Node target) {
        int dx = this.x - target.x;
        int dy = this.y - target.y;
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    /**
     * Compares nodes based on f-score for priority queue ordering.
     */
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.fScore, other.fScore);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Node node = (Node) obj;
        return x == node.x && y == node.y;
    }
    
    @Override
    public int hashCode() {
        return 31 * x + y;
    }
    
    @Override
    public String toString() {
        return String.format("Node(%d, %d) [g=%.2f, h=%.2f, f=%.2f]", 
                           x, y, gScore, hScore, fScore);
    }
}