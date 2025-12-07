package planner;

import model.Grid;
import model.Node;
import model.Robot;
import algorithm.AStar;
import algorithm.Dijkstra;
import java.util.List;

/**
 * High-level path planning system that coordinates pathfinding algorithms.
 * Provides a unified interface for robot navigation.
 */
public class PathPlanner {
    private final Grid grid;
    private AStar astar;
    private Dijkstra dijkstra;
    private Algorithm currentAlgorithm;
    
    /**
     * Available pathfinding algorithms.
     */
    public enum Algorithm {
        A_STAR,
        DIJKSTRA
    }
    
    /**
     * Creates a new PathPlanner for the given grid.
     * 
     * @param grid The warehouse grid
     */
    public PathPlanner(Grid grid) {
        this.grid = grid;
        this.astar = new AStar(grid);
        this.dijkstra = new Dijkstra(grid);
        this.currentAlgorithm = Algorithm.A_STAR; // Default to A*
    }
    
    /**
     * Sets the pathfinding algorithm to use.
     * 
     * @param algorithm The algorithm to use
     */
    public void setAlgorithm(Algorithm algorithm) {
        this.currentAlgorithm = algorithm;
        System.out.println("[PathPlanner] Algorithm set to: " + algorithm);
    }
    
    /**
     * Sets whether diagonal movement is allowed for both algorithms.
     * 
     * @param allowDiagonal true to enable diagonal movement
     */
    public void setAllowDiagonal(boolean allowDiagonal) {
        astar.setAllowDiagonal(allowDiagonal);
        dijkstra.setAllowDiagonal(allowDiagonal);
        System.out.println("[PathPlanner] Diagonal movement: " + 
                         (allowDiagonal ? "ENABLED" : "DISABLED"));
    }
    
    /**
     * Plans a path from start to goal using the current algorithm.
     * 
     * @param start Starting node
     * @param goal Goal node
     * @return List of nodes representing the path
     */
    public List<Node> planPath(Node start, Node goal) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PATH PLANNING REQUEST");
        System.out.println("=".repeat(60));
        
        List<Node> path;
        long startTime = System.nanoTime();
        
        switch (currentAlgorithm) {
            case A_STAR:
                path = astar.findPath(start, goal);
                break;
            case DIJKSTRA:
                path = dijkstra.findPath(start, goal);
                break;
            default:
                path = astar.findPath(start, goal);
        }
        
        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to ms
        
        System.out.println("  Execution time: " + String.format("%.3f", executionTime) + " ms");
        System.out.println("=".repeat(60));
        
        return path;
    }
    
    /**
     * Plans a path using the grid's start and goal positions.
     * 
     * @return List of nodes representing the path
     */
    public List<Node> planPath() {
        if (grid.getStartNode() == null || grid.getGoalNode() == null) {
            System.out.println("[PathPlanner] Error: Start or goal not set!");
            return List.of();
        }
        return planPath(grid.getStartNode(), grid.getGoalNode());
    }
    
    /**
     * Plans a path and assigns it to a robot.
     * 
     * @param robot The robot to assign the path to
     * @return true if path was found and assigned, false otherwise
     */
    public boolean planPathForRobot(Robot robot) {
        Node start = robot.getCurrentNode();
        Node goal = grid.getGoalNode();
        
        if (goal == null) {
            System.out.println("[PathPlanner] Error: No goal set for robot " + 
                             robot.getRobotId());
            return false;
        }
        
        List<Node> path = planPath(start, goal);
        
        if (path.isEmpty()) {
            System.out.println("[PathPlanner] Error: No path found for robot " + 
                             robot.getRobotId());
            return false;
        }
        
        robot.setPath(path);
        System.out.println("[PathPlanner] Path assigned to robot " + robot.getRobotId());
        return true;
    }
    
    /**
     * Recalculates path for a robot from its current position.
     * Useful for dynamic obstacle avoidance.
     * 
     * @param robot The robot to replan for
     * @return true if new path was found, false otherwise
     */
    public boolean replanPath(Robot robot) {
        System.out.println("\n[PathPlanner] Replanning path for robot " + 
                         robot.getRobotId());
        return planPathForRobot(robot);
    }
    
    /**
     * Visualizes the planned path on the grid.
     * 
     * @param path The path to visualize
     */
    public void visualizePath(List<Node> path) {
        // Clear existing path markers
        grid.clearPath();
        
        // Mark the new path
        for (Node node : path) {
            grid.setPath(node.getX(), node.getY());
        }
        
        // Print the grid
        grid.printGrid();
    }
    
    /**
     * Compares both algorithms on the current grid configuration.
     */
    public void compareAlgorithms() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("ALGORITHM COMPARISON");
        System.out.println("=".repeat(60));
        
        if (grid.getStartNode() == null || grid.getGoalNode() == null) {
            System.out.println("Error: Start or goal not set!");
            return;
        }
        
        // Test A*
        System.out.println("\nTesting A*...");
        long astarStart = System.nanoTime();
        List<Node> astarPath = astar.findPath(grid.getStartNode(), grid.getGoalNode());
        long astarEnd = System.nanoTime();
        double astarTime = (astarEnd - astarStart) / 1_000_000.0;
        
        // Test Dijkstra
        System.out.println("\nTesting Dijkstra...");
        long dijkstraStart = System.nanoTime();
        List<Node> dijkstraPath = dijkstra.findPath(grid.getStartNode(), grid.getGoalNode());
        long dijkstraEnd = System.nanoTime();
        double dijkstraTime = (dijkstraEnd - dijkstraStart) / 1_000_000.0;
        
        // Print comparison
        System.out.println("\n" + "-".repeat(60));
        System.out.println("RESULTS:");
        System.out.println("-".repeat(60));
        System.out.printf("%-20s | %-15s | %-15s | %-10s\n", 
                         "Algorithm", "Path Length", "Nodes Explored", "Time (ms)");
        System.out.println("-".repeat(60));
        System.out.printf("%-20s | %-15d | %-15d | %-10.3f\n", 
                         "A*", astarPath.size(), astar.getNodesExplored(), astarTime);
        System.out.printf("%-20s | %-15d | %-15d | %-10.3f\n", 
                         "Dijkstra", dijkstraPath.size(), dijkstra.getNodesExplored(), dijkstraTime);
        System.out.println("-".repeat(60));
        
        // Analysis
        System.out.println("\nANALYSIS:");
        if (astarPath.size() == dijkstraPath.size()) {
            System.out.println("✓ Both algorithms found paths of equal length (optimal)");
        }
        
        double speedup = dijkstraTime / astarTime;
        if (speedup > 1.1) {
            System.out.printf("✓ A* was %.2fx faster than Dijkstra\n", speedup);
        } else if (speedup < 0.9) {
            System.out.printf("✓ Dijkstra was %.2fx faster than A*\n", 1/speedup);
        } else {
            System.out.println("○ Both algorithms had similar performance");
        }
        
        double efficiency = (double) astar.getNodesExplored() / dijkstra.getNodesExplored();
        System.out.printf("✓ A* explored %.1f%% of nodes compared to Dijkstra\n", 
                         efficiency * 100);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Prints statistics about the last pathfinding operation.
     */
    public void printStatistics() {
        switch (currentAlgorithm) {
            case A_STAR:
                astar.printStatistics();
                break;
            case DIJKSTRA:
                dijkstra.printStatistics();
                break;
        }
    }
    
    /**
     * Gets the current algorithm being used.
     * 
     * @return Current algorithm
     */
    public Algorithm getCurrentAlgorithm() {
        return currentAlgorithm;
    }
    
    /**
     * Gets the underlying grid.
     * 
     * @return The grid
     */
    public Grid getGrid() {
        return grid;
    }
}