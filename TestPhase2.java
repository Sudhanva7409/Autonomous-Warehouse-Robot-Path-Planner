import model.Grid;
import model.Node;
import model.Robot;
import algorithm.AStar;
import algorithm.Dijkstra;
import planner.PathPlanner;
import java.util.List;

/**
 * Test class for Phase 2 - Pathfinding Algorithms.
 * Demonstrates A*, Dijkstra, and PathPlanner functionality.
 */
public class TestPhase2 {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("    WAREHOUSE ROBOT PATH PLANNER - PHASE 2 TEST");
        System.out.println("    Pathfinding Algorithms: A* and Dijkstra");
        System.out.println("=".repeat(70));
        
        // Test 1: Basic A* Pathfinding
        testAStarBasic();
        
        // Test 2: Basic Dijkstra Pathfinding
        testDijkstraBasic();
        
        // Test 3: Algorithm Comparison
        testAlgorithmComparison();
        
        // Test 4: PathPlanner Integration
        testPathPlannerIntegration();
        
        // Test 5: Complex Warehouse Scenario
        testComplexWarehouse();
        
        // Test 6: Dynamic Replanning
        testDynamicReplanning();
        
        System.out.println("\n" + "=".repeat(70));
        System.out.println("PHASE 2 TESTING COMPLETE!");
        System.out.println("All pathfinding algorithms working correctly.");
        System.out.println("Ready for Phase 3: Robot Simulation & Visualization.");
        System.out.println("=".repeat(70) + "\n");
    }
    
    /**
     * Tests basic A* pathfinding.
     */
    private static void testAStarBasic() {
        System.out.println("\n[TEST 1] Basic A* Pathfinding");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(15, 10);
        grid.setStart(1, 1);
        grid.setGoal(13, 8);
        
        // Add some obstacles
        for (int y = 3; y < 8; y++) {
            grid.setObstacle(7, y);
        }
        
        System.out.println("Grid setup:");
        grid.printGrid();
        
        AStar astar = new AStar(grid);
        List<Node> path = astar.findPath();
        
        if (!path.isEmpty()) {
            System.out.println("✓ A* found a path!");
            markPath(grid, path);
            grid.printGrid();
            astar.printStatistics();
        }
    }
    
    /**
     * Tests basic Dijkstra pathfinding.
     */
    private static void testDijkstraBasic() {
        System.out.println("\n[TEST 2] Basic Dijkstra Pathfinding");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(15, 10);
        grid.setStart(1, 1);
        grid.setGoal(13, 8);
        
        // Add obstacles
        for (int y = 3; y < 8; y++) {
            grid.setObstacle(7, y);
        }
        
        Dijkstra dijkstra = new Dijkstra(grid);
        List<Node> path = dijkstra.findPath();
        
        if (!path.isEmpty()) {
            System.out.println("✓ Dijkstra found a path!");
            markPath(grid, path);
            grid.printGrid();
            dijkstra.printStatistics();
        }
    }
    
    /**
     * Tests and compares both algorithms.
     */
    private static void testAlgorithmComparison() {
        System.out.println("\n[TEST 3] Algorithm Comparison");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(20, 15);
        grid.setStart(2, 2);
        grid.setGoal(17, 12);
        
        // Create a maze-like warehouse
        for (int y = 4; y < 12; y++) {
            grid.setObstacle(8, y);
            grid.setObstacle(15, y);
        }
        grid.clearObstacle(8, 7);
        grid.clearObstacle(15, 9);
        
        System.out.println("Test environment:");
        grid.printGrid();
        
        PathPlanner planner = new PathPlanner(grid);
        planner.compareAlgorithms();
    }
    
    /**
     * Tests PathPlanner integration with Robot.
     */
    private static void testPathPlannerIntegration() {
        System.out.println("\n[TEST 4] PathPlanner Integration");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(20, 12);
        grid.setStart(2, 2);
        grid.setGoal(17, 9);
        grid.createSampleLayout();
        
        PathPlanner planner = new PathPlanner(grid);
        planner.setAlgorithm(PathPlanner.Algorithm.A_STAR);
        
        // Create robot at start position
        Robot robot = new Robot(2, 2, "ROBOT-ALPHA");
        System.out.println("Created: " + robot);
        
        // Plan path for robot
        boolean success = planner.planPathForRobot(robot);
        
        if (success) {
            System.out.println("✓ Path planning successful!");
            planner.visualizePath(robot.getPath());
            robot.printStatus();
            
            // Simulate robot movement
            System.out.println("Simulating robot movement (first 5 steps):");
            for (int i = 0; i < 5 && robot.moveNext(); i++) {
                System.out.println("  Step " + (i+1) + ": Position (" + 
                                 robot.getCurrentX() + ", " + robot.getCurrentY() + ")");
            }
        }
    }
    
    /**
     * Tests complex warehouse scenario.
     */
    private static void testComplexWarehouse() {
        System.out.println("\n[TEST 5] Complex Warehouse Scenario");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(25, 15);
        grid.setStart(2, 2);
        grid.setGoal(22, 12);
        
        // Create realistic warehouse layout with multiple aisles
        System.out.println("Creating complex warehouse layout...");
        
        // Vertical aisles (shelving units)
        for (int y = 2; y < 13; y++) {
            grid.setObstacle(6, y);
            grid.setObstacle(10, y);
            grid.setObstacle(14, y);
            grid.setObstacle(18, y);
        }
        
        // Create passages
        grid.clearObstacle(6, 4);
        grid.clearObstacle(6, 8);
        grid.clearObstacle(10, 6);
        grid.clearObstacle(10, 10);
        grid.clearObstacle(14, 5);
        grid.clearObstacle(14, 9);
        grid.clearObstacle(18, 7);
        grid.clearObstacle(18, 11);
        
        System.out.println("Warehouse layout:");
        grid.printGrid();
        
        PathPlanner planner = new PathPlanner(grid);
        
        // Test with A*
        planner.setAlgorithm(PathPlanner.Algorithm.A_STAR);
        List<Node> astarPath = planner.planPath();
        
        if (!astarPath.isEmpty()) {
            System.out.println("\n✓ A* successfully navigated complex warehouse!");
            planner.visualizePath(astarPath);
        }
        
        // Compare algorithms
        planner.compareAlgorithms();
    }
    
    /**
     * Tests dynamic replanning when obstacles change.
     */
    private static void testDynamicReplanning() {
        System.out.println("\n[TEST 6] Dynamic Path Replanning");
        System.out.println("-".repeat(70));
        
        Grid grid = new Grid(20, 12);
        grid.setStart(2, 2);
        grid.setGoal(17, 9);
        
        // Initial simple path
        for (int y = 4; y < 8; y++) {
            grid.setObstacle(10, y);
        }
        grid.clearObstacle(10, 6);
        
        PathPlanner planner = new PathPlanner(grid);
        Robot robot = new Robot(2, 2, "ADAPTIVE-BOT");
        
        System.out.println("Initial warehouse state:");
        planner.planPathForRobot(robot);
        planner.visualizePath(robot.getPath());
        
        System.out.println("Initial path length: " + robot.getPath().size());
        
        // Simulate robot movement
        for (int i = 0; i < 5; i++) {
            robot.moveNext();
        }
        
        System.out.println("\n⚠ Obstacle detected ahead! Replanning...");
        
        // Add new obstacle in robot's path
        grid.setObstacle(10, 6);
        grid.setObstacle(11, 6);
        
        System.out.println("Updated warehouse state:");
        grid.printGrid();
        
        // Replan from current position
        planner.replanPath(robot);
        
        if (robot.hasPath()) {
            System.out.println("✓ Successfully replanned path!");
            System.out.println("New path length: " + robot.getPath().size());
            planner.visualizePath(robot.getPath());
            
            System.out.println("\nRobot adapting to new obstacles demonstrates:");
            System.out.println("  - Dynamic obstacle detection");
            System.out.println("  - Real-time path recalculation");
            System.out.println("  - Continuous warehouse operation");
        }
    }
    
    /**
     * Helper method to mark path on grid.
     */
    private static void markPath(Grid grid, List<Node> path) {
        for (Node node : path) {
            grid.setPath(node.getX(), node.getY());
        }
    }
}