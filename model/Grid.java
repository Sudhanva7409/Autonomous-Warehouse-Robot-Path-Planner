package model;

/**
 * Represents the warehouse grid/map with obstacles, start, and goal positions.
 * Uses a 2D array to store cell types.
 */
public class Grid {
    // Cell type constants
    public static final int EMPTY = 0;
    public static final int OBSTACLE = 1;
    public static final int START = 2;
    public static final int GOAL = 3;
    public static final int PATH = 4;
    
    private final int width;
    private final int height;
    private final int[][] grid;
    private Node startNode;
    private Node goalNode;
    
    /**
     * Creates a new grid with specified dimensions.
     * 
     * @param width Number of columns
     * @param height Number of rows
     */
    public Grid(int width, int height) {
        this.width = width;
        this.height = height;
        this.grid = new int[height][width];
        initializeGrid();
    }
    
    /**
     * Initializes all cells to EMPTY.
     */
    private void initializeGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                grid[y][x] = EMPTY;
            }
        }
    }
    
    /**
     * Sets the start position for the robot.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void setStart(int x, int y) {
        if (isValid(x, y)) {
            if (startNode != null) {
                grid[startNode.getY()][startNode.getX()] = EMPTY;
            }
            grid[y][x] = START;
            startNode = new Node(x, y);
        }
    }
    
    /**
     * Sets the goal position for the robot.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void setGoal(int x, int y) {
        if (isValid(x, y)) {
            if (goalNode != null) {
                grid[goalNode.getY()][goalNode.getX()] = EMPTY;
            }
            grid[y][x] = GOAL;
            goalNode = new Node(x, y);
        }
    }
    
    /**
     * Sets an obstacle at the specified position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void setObstacle(int x, int y) {
        if (isValid(x, y)) {
            grid[y][x] = OBSTACLE;
        }
    }
    
    /**
     * Removes an obstacle at the specified position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void clearObstacle(int x, int y) {
        if (isValid(x, y) && grid[y][x] == OBSTACLE) {
            grid[y][x] = EMPTY;
        }
    }
    
    /**
     * Marks a cell as part of the path.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     */
    public void setPath(int x, int y) {
        if (isValid(x, y) && grid[y][x] == EMPTY) {
            grid[y][x] = PATH;
        }
    }
    
    /**
     * Clears all path markings from the grid.
     */
    public void clearPath() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (grid[y][x] == PATH) {
                    grid[y][x] = EMPTY;
                }
            }
        }
    }
    
    /**
     * Checks if a position is valid (within bounds).
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return true if position is within grid bounds
     */
    public boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * Checks if a position is walkable (not an obstacle).
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return true if the cell can be traversed
     */
    public boolean isWalkable(int x, int y) {
        return isValid(x, y) && grid[y][x] != OBSTACLE;
    }
    
    /**
     * Gets the cell type at a position.
     * 
     * @param x X-coordinate
     * @param y Y-coordinate
     * @return Cell type constant
     */
    public int getCellType(int x, int y) {
        if (isValid(x, y)) {
            return grid[y][x];
        }
        return -1;
    }
    
    // Getters
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public Node getStartNode() {
        return startNode;
    }
    
    public Node getGoalNode() {
        return goalNode;
    }
    
    /**
     * Prints a text representation of the grid to console.
     */
    public void printGrid() {
        System.out.println("\nWarehouse Grid:");
        System.out.println("═".repeat(width * 2 + 1));
        
        for (int y = 0; y < height; y++) {
            System.out.print("║");
            for (int x = 0; x < width; x++) {
                char symbol = switch (grid[y][x]) {
                    case EMPTY -> '.';
                    case OBSTACLE -> '█';
                    case START -> 'S';
                    case GOAL -> 'G';
                    case PATH -> '*';
                    default -> '?';
                };
                System.out.print(symbol + " ");
            }
            System.out.println("║");
        }
        
        System.out.println("═".repeat(width * 2 + 1));
        System.out.println("Legend: S=Start, G=Goal, █=Obstacle, *=Path, .=Empty\n");
    }
    
    /**
     * Creates a simple warehouse layout with some obstacles.
     */
    public void createSampleLayout() {
        // Add some obstacles to simulate warehouse shelves
        for (int y = 2; y < height - 2; y++) {
            setObstacle(width / 4, y);
            setObstacle(width / 2, y);
            setObstacle(3 * width / 4, y);
        }
        
        // Create some gaps for navigation
        clearObstacle(width / 4, height / 3);
        clearObstacle(width / 2, 2 * height / 3);
        clearObstacle(3 * width / 4, height / 2);
    }
}