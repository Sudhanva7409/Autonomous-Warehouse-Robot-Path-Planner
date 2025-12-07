import ui.WarehouseSimulator;
import javax.swing.SwingUtilities;

/**
 * Test class for Phase 3 - GUI and Simulation.
 * Launches the interactive warehouse simulator.
 */
public class TestPhase3 {
    
    public static void main(String[] args) {
        printWelcomeMessage();
        launchSimulator();
    }
    
    /**
     * Prints welcome message and instructions.
     */
    private static void printWelcomeMessage() {
        System.out.println("=".repeat(70));
        System.out.println("    WAREHOUSE ROBOT PATH PLANNER - PHASE 3");
        System.out.println("    Interactive GUI & Robot Simulation");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("INSTRUCTIONS:");
        System.out.println("-".repeat(70));
        System.out.println();
        
        System.out.println("1. SETUP:");
        System.out.println("   - Select 'Set Start' mode and click on grid to place start");
        System.out.println("   - Select 'Set Goal' mode and click on grid to place goal");
        System.out.println("   - Use 'Add Obstacle' to draw obstacles on the grid");
        System.out.println("   - Use 'Remove Obstacle' to erase obstacles");
        System.out.println();
        
        System.out.println("2. PATHFINDING:");
        System.out.println("   - Choose algorithm: A* (faster) or Dijkstra (guaranteed optimal)");
        System.out.println("   - Enable/disable diagonal movement");
        System.out.println("   - Click 'Plan Path' to calculate the route");
        System.out.println("   - Path appears as light blue trail on the grid");
        System.out.println();
        
        System.out.println("3. ROBOT SIMULATION:");
        System.out.println("   - Click 'Add Robot' to place a robot at start position");
        System.out.println("   - Click 'Step' to move robot one step at a time");
        System.out.println("   - Click 'Animate' to watch robot follow path automatically");
        System.out.println("   - Click 'Reset Robots' to return robots to start");
        System.out.println();
        
        System.out.println("4. UTILITIES:");
        System.out.println("   - 'Clear Path' - Removes path visualization");
        System.out.println("   - 'Compare Algorithms' - Tests both A* and Dijkstra");
        System.out.println("   - 'Clear Grid' - Resets entire warehouse");
        System.out.println();
        
        System.out.println("VISUAL GUIDE:");
        System.out.println("-".repeat(70));
        System.out.println("   [S] Green  = Start Position");
        System.out.println("   [G] Red    = Goal Position");
        System.out.println("   █   Gray   = Obstacle (Warehouse Shelf)");
        System.out.println("   ~   Blue   = Calculated Path");
        System.out.println("   ●   Orange = Robot");
        System.out.println();
        
        System.out.println("TIPS:");
        System.out.println("-".repeat(70));
        System.out.println("   • Try creating complex mazes to test the algorithms");
        System.out.println("   • Compare A* vs Dijkstra on the same layout");
        System.out.println("   • A* is usually faster but Dijkstra guarantees shortest path");
        System.out.println("   • Diagonal movement creates more realistic paths");
        System.out.println("   • Add multiple robots to simulate warehouse traffic");
        System.out.println();
        
        System.out.println("EXAMPLE SCENARIOS TO TRY:");
        System.out.println("-".repeat(70));
        System.out.println("   1. Simple Path:");
        System.out.println("      - Set start at top-left, goal at bottom-right");
        System.out.println("      - Add a few obstacles in between");
        System.out.println("      - Watch the algorithm find the shortest route");
        System.out.println();
        
        System.out.println("   2. Warehouse Aisles:");
        System.out.println("      - Create vertical columns of obstacles (shelves)");
        System.out.println("      - Leave gaps for navigation");
        System.out.println("      - Test with and without diagonal movement");
        System.out.println();
        
        System.out.println("   3. Maze Challenge:");
        System.out.println("      - Build a complex maze with many turns");
        System.out.println("      - Compare algorithm performance");
        System.out.println("      - Watch robot navigate through tight spaces");
        System.out.println();
        
        System.out.println("   4. Multi-Robot Coordination:");
        System.out.println("      - Add multiple robots");
        System.out.println("      - Animate them simultaneously");
        System.out.println("      - Simulate real warehouse operations");
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("LAUNCHING SIMULATOR...");
        System.out.println("=".repeat(70));
        System.out.println();
    }
    
    /**
     * Launches the GUI simulator.
     */
    private static void launchSimulator() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Set system look and feel for better appearance
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName()
                );
            } catch (Exception e) {
                System.out.println("Could not set system look and feel");
            }
            
            // Create and show simulator
            WarehouseSimulator simulator = new WarehouseSimulator(20, 15);
            simulator.setVisible(true);
            
            System.out.println("✓ Simulator window opened successfully!");
            System.out.println("✓ Default warehouse layout loaded");
            System.out.println("✓ Ready for interaction");
            System.out.println();
            System.out.println("Start by:");
            System.out.println("  1. Clicking 'Plan Path' to see the default route");
            System.out.println("  2. Clicking 'Add Robot' to place a robot");
            System.out.println("  3. Clicking 'Animate' to watch it move");
            System.out.println();
            System.out.println("Have fun exploring autonomous warehouse navigation!");
            System.out.println("=".repeat(70));
        });
    }
    
    /**
     * Demonstrates programmatic usage (for testing without GUI).
     */
    private static void demonstrateProgrammaticUsage() {
        System.out.println("\n[BONUS] Programmatic Usage Example:");
        System.out.println("-".repeat(70));
        
        System.out.println("```java");
        System.out.println("// Create simulator");
        System.out.println("WarehouseSimulator sim = new WarehouseSimulator(20, 15);");
        System.out.println();
        System.out.println("// The simulator provides a complete GUI with:");
        System.out.println("// - Interactive grid editing");
        System.out.println("// - Real-time pathfinding visualization");
        System.out.println("// - Robot animation controls");
        System.out.println("// - Algorithm comparison tools");
        System.out.println();
        System.out.println("// Show the window");
        System.out.println("sim.setVisible(true);");
        System.out.println("```");
        System.out.println("-".repeat(70));
    }
}