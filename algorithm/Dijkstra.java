package algorithm;

import model.Grid;
import model.Node;
import java.util.*;

/**
 * Dijkstra's shortest path algorithm implementation.
 * Guarantees the shortest path without using heuristics.
 */
public class Dijkstra {
    private final Grid grid;
    private PriorityQueue<Node> openSet;
    private Set<String> closedSet;
    private Map<String, Node> allNodes;
    
    // Movement directions: up, down, left, right
    private static final int[][] DIRECTIONS = {
        {0, -1},  // Up
        {0, 1},   // Down
        {-1, 0},  // Left
        {1, 0}    // Right
    };
    
    // Diagonal directions
    private static final int[][] DIAGONAL_DIRECTIONS = {
        {0, -1}, {0, 1}, {-1, 0}, {1, 0},
        {-1, -1}, {-1, 1}, {1, -1}, {1, 1}
    };
    
    private boolean allowDiagonal;
    private int nodesExplored;
    
    /**
     * Creates a new Dijkstra pathfinder for the given grid.
     * 
     * @param grid The warehouse grid to navigate
     */
    public Dijkstra(Grid grid) {
        this.grid = grid;
        this.allowDiagonal = false;
        this.nodesExplored = 0;
    }
    
    /**
     * Sets whether diagonal movement is allowed.
     * 
     * @param allowDiagonal true to enable diagonal movement
     */
    public void setAllowDiagonal(boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
    }
    
    /**
     * Finds the shortest path from start to goal using Dijkstra's algorithm.
     * 
     * @param start Starting node
     * @param goal Goal node
     * @return List of nodes representing the path, or empty list if no path found
     */
    public List<Node> findPath(Node start, Node goal) {
        // Initialize data structures
        openSet = new PriorityQueue<>();
        closedSet = new HashSet<>();
        allNodes = new HashMap<>();
        nodesExplored = 0;
        
        // Create fresh start node
        Node startNode = getOrCreateNode(start.getX(), start.getY());
        startNode.setGScore(0);
        // Dijkstra doesn't use heuristic, so we set h-score to 0
        startNode.setHScore(0);
        
        openSet.add(startNode);
        
        System.out.println("\n[Dijkstra] Starting pathfinding...");
        System.out.println("  Start: (" + start.getX() + ", " + start.getY() + ")");
        System.out.println("  Goal: (" + goal.getX() + ", " + goal.getY() + ")");
        
        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            nodesExplored++;
            
            // Check if we reached the goal
            if (current.getX() == goal.getX() && current.getY() == goal.getY()) {
                System.out.println("  ✓ Path found!");
                System.out.println("  Nodes explored: " + nodesExplored);
                return reconstructPath(current);
            }
            
            closedSet.add(getNodeKey(current.getX(), current.getY()));
            
            // Explore all neighbors
            for (Node neighbor : getNeighbors(current)) {
                String neighborKey = getNodeKey(neighbor.getX(), neighbor.getY());
                
                if (closedSet.contains(neighborKey)) {
                    continue;
                }
                
                // Calculate tentative distance
                double tentativeGScore = current.getGScore() + getMovementCost(current, neighbor);
                
                if (tentativeGScore < neighbor.getGScore()) {
                    // Found a better path to this neighbor
                    neighbor.setParent(current);
                    neighbor.setGScore(tentativeGScore);
                    neighbor.setHScore(0); // No heuristic in Dijkstra
                    
                    if (!openSet.contains(neighbor)) {
                        openSet.add(neighbor);
                    }
                }
            }
        }
        
        System.out.println("  ✗ No path found!");
        System.out.println("  Nodes explored: " + nodesExplored);
        return new ArrayList<>();
    }
    
    /**
     * Finds a path using the grid's start and goal nodes.
     * 
     * @return List of nodes representing the path, or empty list if no path found
     */
    public List<Node> findPath() {
        if (grid.getStartNode() == null || grid.getGoalNode() == null) {
            System.out.println("  ✗ Start or goal node not set!");
            return new ArrayList<>();
        }
        return findPath(grid.getStartNode(), grid.getGoalNode());
    }
    
    /**
     * Gets all valid neighbors of a node.
     * 
     * @param node The current node
     * @return List of neighboring nodes
     */
    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();
        int[][] directions = allowDiagonal ? DIAGONAL_DIRECTIONS : DIRECTIONS;
        
        for (int[] dir : directions) {
            int newX = node.getX() + dir[0];
            int newY = node.getY() + dir[1];
            
            if (grid.isWalkable(newX, newY)) {
                // For diagonal movement, check if path is not blocked
                if (allowDiagonal && Math.abs(dir[0]) == 1 && Math.abs(dir[1]) == 1) {
                    if (!grid.isWalkable(node.getX() + dir[0], node.getY()) ||
                        !grid.isWalkable(node.getX(), node.getY() + dir[1])) {
                        continue;
                    }
                }
                
                neighbors.add(getOrCreateNode(newX, newY));
            }
        }
        
        return neighbors;
    }
    
    /**
     * Calculates movement cost between two adjacent nodes.
     * 
     * @param from Source node
     * @param to Destination node
     * @return Movement cost
     */
    private double getMovementCost(Node from, Node to) {
        int dx = Math.abs(from.getX() - to.getX());
        int dy = Math.abs(from.getY() - to.getY());
        
        if (dx == 1 && dy == 1) {
            return Math.sqrt(2); // Diagonal movement
        }
        return 1.0; // Cardinal movement
    }
    
    /**
     * Reconstructs the path from goal to start.
     * 
     * @param goalNode The goal node with parent chain
     * @return List of nodes from start to goal
     */
    private List<Node> reconstructPath(Node goalNode) {
        List<Node> path = new ArrayList<>();
        Node current = goalNode;
        
        while (current != null) {
            path.add(current);
            current = current.getParent();
        }
        
        Collections.reverse(path);
        System.out.println("  Path length: " + path.size() + " steps");
        return path;
    }
    
    /**
     * Gets or creates a node at the specified position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Node at the position
     */
    private Node getOrCreateNode(int x, int y) {
        String key = getNodeKey(x, y);
        return allNodes.computeIfAbsent(key, k -> new Node(x, y));
    }
    
    /**
     * Generates a unique key for a node position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return String key
     */
    private String getNodeKey(int x, int y) {
        return x + "," + y;
    }
    
    /**
     * Gets the number of nodes explored in the last search.
     * 
     * @return Number of nodes explored
     */
    public int getNodesExplored() {
        return nodesExplored;
    }
    
    /**
     * Prints statistics about the last pathfinding operation.
     */
    public void printStatistics() {
        System.out.println("\n[Dijkstra Statistics]");
        System.out.println("  Algorithm: Dijkstra");
        System.out.println("  Movement: " + (allowDiagonal ? "8-directional" : "4-directional"));
        System.out.println("  Nodes explored: " + nodesExplored);
        System.out.println("  Guarantee: Always finds shortest path");
    }
}