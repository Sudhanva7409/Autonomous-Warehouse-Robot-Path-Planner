package model;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a warehouse robot that can navigate through the grid.
 * Tracks position, path, and movement state.
 */
public class Robot {
    private int currentX;
    private int currentY;
    private List<Node> path;
    private int currentPathIndex;
    private final String robotId;
    private RobotState state;
    
    /**
     * Possible states of the robot.
     */
    public enum RobotState {
        IDLE,           // Robot is not moving
        MOVING,         // Robot is following a path
        REACHED_GOAL,   // Robot has reached its destination
        NO_PATH         // No valid path available
    }
    
    /**
     * Creates a new robot at the specified position.
     * 
     * @param startX Initial x-coordinate
     * @param startY Initial y-coordinate
     * @param robotId Unique identifier for the robot
     */
    public Robot(int startX, int startY, String robotId) {
        this.currentX = startX;
        this.currentY = startY;
        this.robotId = robotId;
        this.path = new ArrayList<>();
        this.currentPathIndex = 0;
        this.state = RobotState.IDLE;
    }
    
    /**
     * Sets a new path for the robot to follow.
     * 
     * @param path List of nodes representing the path
     */
    public void setPath(List<Node> path) {
        if (path == null || path.isEmpty()) {
            this.path = new ArrayList<>();
            this.state = RobotState.NO_PATH;
            return;
        }
        
        this.path = new ArrayList<>(path);
        this.currentPathIndex = 0;
        this.state = RobotState.MOVING;
    }
    
    /**
     * Moves the robot to the next position in the path.
     * 
     * @return true if the robot moved, false if at end of path
     */
    public boolean moveNext() {
        if (path.isEmpty() || currentPathIndex >= path.size()) {
            state = RobotState.IDLE;
            return false;
        }
        
        Node nextNode = path.get(currentPathIndex);
        currentX = nextNode.getX();
        currentY = nextNode.getY();
        currentPathIndex++;
        
        if (currentPathIndex >= path.size()) {
            state = RobotState.REACHED_GOAL;
            return false;
        }
        
        state = RobotState.MOVING;
        return true;
    }
    
    /**
     * Moves the robot directly to a specific position (for initialization).
     * 
     * @param x Target x-coordinate
     * @param y Target y-coordinate
     */
    public void setPosition(int x, int y) {
        this.currentX = x;
        this.currentY = y;
    }
    
    /**
     * Resets the robot to the start of its current path.
     */
    public void resetPath() {
        currentPathIndex = 0;
        if (!path.isEmpty()) {
            Node start = path.get(0);
            currentX = start.getX();
            currentY = start.getY();
            state = RobotState.MOVING;
        }
    }
    
    /**
     * Checks if the robot has reached its goal.
     * 
     * @return true if robot is at the end of its path
     */
    public boolean hasReachedGoal() {
        return state == RobotState.REACHED_GOAL || 
               (!path.isEmpty() && currentPathIndex >= path.size());
    }
    
    /**
     * Gets the remaining path from current position.
     * 
     * @return List of remaining nodes in the path
     */
    public List<Node> getRemainingPath() {
        if (path.isEmpty() || currentPathIndex >= path.size()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(path.subList(currentPathIndex, path.size()));
    }
    
    /**
     * Gets the distance to the goal.
     * 
     * @return Number of steps remaining, or -1 if no path
     */
    public int getDistanceToGoal() {
        if (path.isEmpty()) {
            return -1;
        }
        return path.size() - currentPathIndex;
    }
    
    // Getters
    public int getCurrentX() {
        return currentX;
    }
    
    public int getCurrentY() {
        return currentY;
    }
    
    public String getRobotId() {
        return robotId;
    }
    
    public RobotState getState() {
        return state;
    }
    
    public List<Node> getPath() {
        return new ArrayList<>(path);
    }
    
    public int getCurrentPathIndex() {
        return currentPathIndex;
    }
    
    public boolean hasPath() {
        return !path.isEmpty();
    }
    
    /**
     * Gets the current position as a Node.
     * 
     * @return Node representing current position
     */
    public Node getCurrentNode() {
        return new Node(currentX, currentY);
    }
    
    @Override
    public String toString() {
        return String.format("Robot[%s] at (%d, %d) - State: %s, Steps to goal: %d", 
                           robotId, currentX, currentY, state, getDistanceToGoal());
    }
    
    /**
     * Prints detailed robot information.
     */
    public void printStatus() {
        System.out.println("\n" + "─".repeat(50));
        System.out.println("Robot Status: " + robotId);
        System.out.println("─".repeat(50));
        System.out.println("Position: (" + currentX + ", " + currentY + ")");
        System.out.println("State: " + state);
        System.out.println("Path length: " + path.size());
        System.out.println("Current step: " + currentPathIndex + " / " + path.size());
        System.out.println("Distance to goal: " + getDistanceToGoal());
        System.out.println("─".repeat(50) + "\n");
    }
}