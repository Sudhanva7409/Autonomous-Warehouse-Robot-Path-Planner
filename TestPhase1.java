import model.Grid;
import model.Node;
import model.Robot;

/**
 * Test class for Phase 1 components.
 * Demonstrates Grid, Node, and Robot functionality.
 */
public class TestPhase1 {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("    WAREHOUSE ROBOT PATH PLANNER - PHASE 1 TEST");
        System.out.println("=".repeat(60));
        
        // Test 1: Grid Creation
        testGridCreation();
        
        // Test 2: Node Operations
        testNodeOperations();
        
        // Test 3: Robot Operations
        testRobotOperations();
        
        // Test 4: Complete Scenario
        testCompleteScenario();
    }
    
    /**
     * Tests basic grid creation and manipulation.
     */
    private static void testGridCreation() {
        System.out.println("\n[TEST 1] Grid Creation and Manipulation");
        System.out.println("-".repeat(60));
        
        Grid grid = new Grid(15, 10);
        System.out.println("✓ Created " + grid.getWidth() + "x" + grid.getHeight() + " grid");
        
        // Set start and goal
        grid.setStart(1, 1);
        grid.setGoal(13, 8);
        System.out.println("✓ Set start position: (1, 1)");
        System.out.println("✓ Set goal position: (13, 8)");
        
        // Add obstacles
        grid.createSampleLayout();
        System.out.println("✓ Added sample warehouse layout");
        
        // Print the grid
        grid.printGrid();
        
        // Test validation
        System.out.println("Testing position validation:");
        System.out.println("  - Is (5, 5) valid? " + grid.isValid(5, 5));
        System.out.println("  - Is (20, 20) valid? " + grid.isValid(20, 20));
        System.out.println("  - Is (5, 5) walkable? " + grid.isWalkable(5, 5));
    }
    
    /**
     * Tests Node class functionality.
     */
    private static void testNodeOperations() {
        System.out.println("\n[TEST 2] Node Operations");
        System.out.println("-".repeat(60));
        
        Node node1 = new Node(5, 5);
        Node node2 = new Node(10, 8);
        
        System.out.println("Created nodes:");
        System.out.println("  - " + node1);
        System.out.println("  - " + node2);
        
        // Test distance calculations
        double manhattan = node1.manhattanDistance(node2);
        double euclidean = node1.euclideanDistance(node2);
        
        System.out.println("\nDistance calculations:");
        System.out.println("  - Manhattan distance: " + manhattan);
        System.out.println("  - Euclidean distance: " + String.format("%.2f", euclidean));
        
        // Test score setting
        node1.setGScore(10.0);
        node1.setHScore(manhattan);
        
        System.out.println("\nAfter setting scores:");
        System.out.println("  - " + node1);
        System.out.println("  - F-score automatically calculated: " + node1.getFScore());
        
        // Test comparison
        node2.setGScore(5.0);
        node2.setHScore(3.0);
        
        System.out.println("\nNode comparison (for priority queue):");
        System.out.println("  - node1.compareTo(node2) = " + node1.compareTo(node2));
        System.out.println("  - " + (node1.compareTo(node2) > 0 ? "node2 has lower priority" : "node1 has lower priority"));
    }
    
    /**
     * Tests Robot class functionality.
     */
    private static void testRobotOperations() {
        System.out.println("\n[TEST 3] Robot Operations");
        System.out.println("-".repeat(60));
        
        Robot robot = new Robot(1, 1, "ROBOT-001");
        System.out.println("Created robot: " + robot);
        
        // Create a sample path
        java.util.List<Node> samplePath = new java.util.ArrayList<>();
        samplePath.add(new Node(1, 1));
        samplePath.add(new Node(2, 1));
        samplePath.add(new Node(3, 1));
        samplePath.add(new Node(4, 1));
        samplePath.add(new Node(5, 1));
        
        robot.setPath(samplePath);
        System.out.println("✓ Set path with " + samplePath.size() + " nodes");
        
        // Simulate movement
        System.out.println("\nSimulating robot movement:");
        int step = 1;
        while (robot.moveNext()) {
            System.out.println("  Step " + step + ": Robot at (" + 
                             robot.getCurrentX() + ", " + robot.getCurrentY() + ")");
            step++;
        }
        
        System.out.println("  Final position: (" + robot.getCurrentX() + ", " + 
                         robot.getCurrentY() + ")");
        System.out.println("  Has reached goal: " + robot.hasReachedGoal());
        
        robot.printStatus();
    }
    
    /**
     * Tests a complete scenario with grid, robot, and visualization.
     */
    private static void testCompleteScenario() {
        System.out.println("\n[TEST 4] Complete Scenario");
        System.out.println("-".repeat(60));
        
        // Create warehouse environment
        Grid grid = new Grid(20, 12);
        grid.setStart(2, 2);
        grid.setGoal(17, 9);
        
        // Add custom obstacles (warehouse shelves)
        System.out.println("Setting up warehouse layout...");
        for (int y = 3; y < 10; y++) {
            grid.setObstacle(7, y);
            grid.setObstacle(13, y);
        }
        // Create passages
        grid.clearObstacle(7, 6);
        grid.clearObstacle(13, 5);
        
        // Create robot
        Robot robot = new Robot(2, 2, "FORKLIFT-42");
        System.out.println("✓ Created robot: " + robot.getRobotId());
        
        // For now, create a simple manual path (Phase 2 will generate this automatically)
        System.out.println("✓ Creating manual path for demonstration...");
        java.util.List<Node> manualPath = new java.util.ArrayList<>();
        
        // Simple path avoiding obstacles
        for (int x = 2; x <= 6; x++) {
            manualPath.add(new Node(x, 2));
        }
        for (int y = 2; y <= 6; y++) {
            manualPath.add(new Node(6, y));
        }
        for (int x = 6; x <= 12; x++) {
            manualPath.add(new Node(x, 6));
        }
        for (int y = 6; y >= 5; y--) {
            manualPath.add(new Node(12, y));
        }
        for (int x = 12; x <= 17; x++) {
            manualPath.add(new Node(x, 5));
        }
        for (int y = 5; y <= 9; y++) {
            manualPath.add(new Node(17, y));
        }
        
        // Mark path on grid
        for (Node node : manualPath) {
            grid.setPath(node.getX(), node.getY());
        }
        
        robot.setPath(manualPath);
        
        System.out.println("✓ Path set with " + manualPath.size() + " waypoints");
        
        // Display the complete scenario
        grid.printGrid();
        robot.printStatus();
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PHASE 1 TESTING COMPLETE!");
        System.out.println("All core components (Grid, Node, Robot) are working correctly.");
        System.out.println("Ready for Phase 2: Pathfinding algorithm implementation.");
        System.out.println("=".repeat(60) + "\n");
    }
}