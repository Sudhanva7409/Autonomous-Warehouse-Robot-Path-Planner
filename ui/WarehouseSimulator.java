package ui;

import model.Grid;
import model.Node;
import model.Robot;
import planner.PathPlanner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Interactive GUI for warehouse robot path planning and simulation.
 * Provides real-time visualization of pathfinding and robot movement.
 */
public class WarehouseSimulator extends JFrame {
    private static final int CELL_SIZE = 40;
    private static final int CONTROL_PANEL_WIDTH = 300;
    
    private Grid grid;
    private PathPlanner planner;
    private List<Robot> robots;
    private GridPanel gridPanel;
    private ControlPanel controlPanel;
    private Timer animationTimer;
    private boolean isAnimating;
    private EditMode editMode;
    
    /**
     * Edit modes for grid interaction.
     */
    public enum EditMode {
        SET_START("Set Start"),
        SET_GOAL("Set Goal"),
        ADD_OBSTACLE("Add Obstacle"),
        REMOVE_OBSTACLE("Remove Obstacle"),
        NONE("Select");
        
        private final String label;
        
        EditMode(String label) {
            this.label = label;
        }
        
        public String getLabel() {
            return label;
        }
    }
    
    /**
     * Creates a new warehouse simulator.
     * 
     * @param width Grid width
     * @param height Grid height
     */
    public WarehouseSimulator(int width, int height) {
        super("Warehouse Robot Path Planner");
        
        this.grid = new Grid(width, height);
        this.planner = new PathPlanner(grid);
        this.robots = new ArrayList<>();
        this.editMode = EditMode.NONE;
        this.isAnimating = false;
        
        initializeComponents();
        setupFrame();
        createDefaultScenario();
    }
    
    /**
     * Initializes GUI components.
     */
    private void initializeComponents() {
        setLayout(new BorderLayout());
        
        // Create grid panel
        gridPanel = new GridPanel();
        add(gridPanel, BorderLayout.CENTER);
        
        // Create control panel
        controlPanel = new ControlPanel();
        add(controlPanel, BorderLayout.EAST);
        
        // Setup animation timer
        animationTimer = new Timer(200, e -> stepAnimation());
    }
    
    /**
     * Sets up the main frame.
     */
    private void setupFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    /**
     * Creates a default warehouse scenario.
     */
    private void createDefaultScenario() {
        grid.setStart(2, 2);
        grid.setGoal(grid.getWidth() - 3, grid.getHeight() - 3);
        grid.createSampleLayout();
        gridPanel.repaint();
    }
    
    /**
     * Grid visualization panel.
     */
    private class GridPanel extends JPanel {
        public GridPanel() {
            setPreferredSize(new Dimension(
                grid.getWidth() * CELL_SIZE,
                grid.getHeight() * CELL_SIZE
            ));
            setBackground(Color.WHITE);
            
            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    handleGridClick(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
                }
            });
            
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (editMode == EditMode.ADD_OBSTACLE || 
                        editMode == EditMode.REMOVE_OBSTACLE) {
                        handleGridClick(e.getX() / CELL_SIZE, e.getY() / CELL_SIZE);
                    }
                }
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            drawGrid(g2d);
            drawCells(g2d);
            drawRobots(g2d);
        }
        
        /**
         * Draws the grid lines.
         */
        private void drawGrid(Graphics2D g2d) {
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(1));
            
            for (int x = 0; x <= grid.getWidth(); x++) {
                g2d.drawLine(x * CELL_SIZE, 0, x * CELL_SIZE, 
                           grid.getHeight() * CELL_SIZE);
            }
            
            for (int y = 0; y <= grid.getHeight(); y++) {
                g2d.drawLine(0, y * CELL_SIZE, grid.getWidth() * CELL_SIZE, 
                           y * CELL_SIZE);
            }
        }
        
        /**
         * Draws all cells with their types.
         */
        private void drawCells(Graphics2D g2d) {
            for (int y = 0; y < grid.getHeight(); y++) {
                for (int x = 0; x < grid.getWidth(); x++) {
                    int cellType = grid.getCellType(x, y);
                    Color color = getCellColor(cellType);
                    
                    if (color != null) {
                        g2d.setColor(color);
                        g2d.fillRect(x * CELL_SIZE + 2, y * CELL_SIZE + 2, 
                                   CELL_SIZE - 4, CELL_SIZE - 4);
                    }
                    
                    // Draw labels for start and goal
                    if (cellType == Grid.START || cellType == Grid.GOAL) {
                        g2d.setColor(Color.WHITE);
                        g2d.setFont(new Font("Arial", Font.BOLD, 16));
                        String label = cellType == Grid.START ? "S" : "G";
                        FontMetrics fm = g2d.getFontMetrics();
                        int labelX = x * CELL_SIZE + (CELL_SIZE - fm.stringWidth(label)) / 2;
                        int labelY = y * CELL_SIZE + (CELL_SIZE + fm.getAscent()) / 2;
                        g2d.drawString(label, labelX, labelY);
                    }
                }
            }
        }
        
        /**
         * Draws all robots.
         */
        private void drawRobots(Graphics2D g2d) {
            for (Robot robot : robots) {
                int x = robot.getCurrentX() * CELL_SIZE;
                int y = robot.getCurrentY() * CELL_SIZE;
                
                // Draw robot body
                g2d.setColor(new Color(255, 140, 0)); // Orange
                g2d.fillOval(x + 8, y + 8, CELL_SIZE - 16, CELL_SIZE - 16);
                
                // Draw robot outline
                g2d.setColor(new Color(200, 100, 0));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawOval(x + 8, y + 8, CELL_SIZE - 16, CELL_SIZE - 16);
                
                // Draw robot ID
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 10));
                String id = robot.getRobotId().substring(0, 
                           Math.min(2, robot.getRobotId().length()));
                FontMetrics fm = g2d.getFontMetrics();
                int labelX = x + (CELL_SIZE - fm.stringWidth(id)) / 2;
                int labelY = y + (CELL_SIZE + fm.getAscent()) / 2 - 2;
                g2d.drawString(id, labelX, labelY);
            }
        }
        
        /**
         * Gets color for cell type.
         */
        private Color getCellColor(int cellType) {
            return switch (cellType) {
                case Grid.EMPTY -> null;
                case Grid.OBSTACLE -> new Color(60, 60, 60);
                case Grid.START -> new Color(34, 139, 34);
                case Grid.GOAL -> new Color(220, 20, 60);
                case Grid.PATH -> new Color(100, 149, 237, 100);
                default -> null;
            };
        }
    }
    
    /**
     * Control panel for user interactions.
     */
    private class ControlPanel extends JPanel {
        private JLabel algorithmLabel;
        private JLabel statusLabel;
        private JTextArea statsArea;
        
        public ControlPanel() {
            setPreferredSize(new Dimension(CONTROL_PANEL_WIDTH, 0));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setBackground(new Color(240, 240, 240));
            
            addTitle();
            addSeparator();
            addEditingControls();
            addSeparator();
            addAlgorithmControls();
            addSeparator();
            addSimulationControls();
            addSeparator();
            addUtilityControls();
            addSeparator();
            addStatusDisplay();
        }
        
        private void addTitle() {
            JLabel title = new JLabel("Control Panel");
            title.setFont(new Font("Arial", Font.BOLD, 18));
            title.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(title);
            add(Box.createVerticalStrut(10));
        }
        
        private void addSeparator() {
            add(Box.createVerticalStrut(5));
            JSeparator separator = new JSeparator();
            separator.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 1));
            add(separator);
            add(Box.createVerticalStrut(5));
        }
        
        private void addEditingControls() {
            JLabel label = new JLabel("Edit Mode:");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(label);
            add(Box.createVerticalStrut(5));
            
            JComboBox<String> modeCombo = new JComboBox<>();
            for (EditMode mode : EditMode.values()) {
                modeCombo.addItem(mode.getLabel());
            }
            modeCombo.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 30));
            modeCombo.addActionListener(e -> {
                editMode = EditMode.values()[modeCombo.getSelectedIndex()];
                updateStatus("Mode: " + editMode.getLabel());
            });
            add(modeCombo);
        }
        
        private void addAlgorithmControls() {
            JLabel label = new JLabel("Algorithm:");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(label);
            add(Box.createVerticalStrut(5));
            
            JComboBox<String> algoCombo = new JComboBox<>(
                new String[]{"A* (Fast)", "Dijkstra (Optimal)"}
            );
            algoCombo.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 30));
            algoCombo.addActionListener(e -> {
                PathPlanner.Algorithm algo = algoCombo.getSelectedIndex() == 0 ?
                    PathPlanner.Algorithm.A_STAR : PathPlanner.Algorithm.DIJKSTRA;
                planner.setAlgorithm(algo);
                updateStatus("Algorithm: " + algo);
            });
            add(algoCombo);
            add(Box.createVerticalStrut(5));
            
            JCheckBox diagonalCheck = new JCheckBox("Allow Diagonal");
            diagonalCheck.addActionListener(e -> {
                planner.setAllowDiagonal(diagonalCheck.isSelected());
                updateStatus("Diagonal: " + (diagonalCheck.isSelected() ? "ON" : "OFF"));
            });
            add(diagonalCheck);
        }
        
        private void addSimulationControls() {
            JButton planButton = createButton("Plan Path");
            planButton.addActionListener(e -> planPath());
            add(planButton);
            
            JButton addRobotButton = createButton("Add Robot");
            addRobotButton.addActionListener(e -> addRobot());
            add(addRobotButton);
            
            JButton animateButton = createButton("Animate");
            animateButton.addActionListener(e -> toggleAnimation());
            add(animateButton);
            
            JButton stepButton = createButton("Step");
            stepButton.addActionListener(e -> stepAllRobots());
            add(stepButton);
            
            JButton resetButton = createButton("Reset Robots");
            resetButton.addActionListener(e -> resetRobots());
            add(resetButton);
        }
        
        private void addUtilityControls() {
            JButton clearPathButton = createButton("Clear Path");
            clearPathButton.addActionListener(e -> clearPath());
            add(clearPathButton);
            
            JButton compareButton = createButton("Compare Algorithms");
            compareButton.addActionListener(e -> compareAlgorithms());
            add(compareButton);
            
            JButton clearGridButton = createButton("Clear Grid");
            clearGridButton.addActionListener(e -> clearGrid());
            add(clearGridButton);
        }
        
        private void addStatusDisplay() {
            statusLabel = new JLabel("Ready");
            statusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(statusLabel);
            add(Box.createVerticalStrut(10));
            
            JLabel statsTitle = new JLabel("Statistics:");
            statsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
            add(statsTitle);
            
            statsArea = new JTextArea(8, 20);
            statsArea.setEditable(false);
            statsArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
            JScrollPane scrollPane = new JScrollPane(statsArea);
            scrollPane.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 150));
            add(scrollPane);
        }
        
        private JButton createButton(String text) {
            JButton button = new JButton(text);
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(CONTROL_PANEL_WIDTH - 20, 35));
            return button;
        }
        
        private void updateStatus(String message) {
            statusLabel.setText(message);
        }
        
        private void updateStats(String stats) {
            statsArea.setText(stats);
        }
    }
    
    /**
     * Handles grid click events.
     */
    private void handleGridClick(int x, int y) {
        if (!grid.isValid(x, y)) return;
        
        switch (editMode) {
            case SET_START:
                grid.setStart(x, y);
                controlPanel.updateStatus("Start set at (" + x + ", " + y + ")");
                break;
            case SET_GOAL:
                grid.setGoal(x, y);
                controlPanel.updateStatus("Goal set at (" + x + ", " + y + ")");
                break;
            case ADD_OBSTACLE:
                if (grid.getCellType(x, y) == Grid.EMPTY) {
                    grid.setObstacle(x, y);
                }
                break;
            case REMOVE_OBSTACLE:
                if (grid.getCellType(x, y) == Grid.OBSTACLE) {
                    grid.clearObstacle(x, y);
                }
                break;
            case NONE:
                // Do nothing
                break;
        }
        
        gridPanel.repaint();
    }
    
    /**
     * Plans a path using current algorithm.
     */
    private void planPath() {
        if (grid.getStartNode() == null || grid.getGoalNode() == null) {
            controlPanel.updateStatus("Error: Set start and goal first!");
            return;
        }
        
        List<Node> path = planner.planPath();
        
        if (!path.isEmpty()) {
            planner.visualizePath(path);
            controlPanel.updateStatus("Path found: " + path.size() + " steps");
            updateStatistics(path);
            gridPanel.repaint();
        } else {
            controlPanel.updateStatus("No path found!");
        }
    }
    
    /**
     * Adds a robot at the start position.
     */
    private void addRobot() {
        if (grid.getStartNode() == null) {
            controlPanel.updateStatus("Error: Set start position first!");
            return;
        }
        
        String robotId = "R" + (robots.size() + 1);
        Robot robot = new Robot(grid.getStartNode().getX(), 
                               grid.getStartNode().getY(), robotId);
        robots.add(robot);
        
        controlPanel.updateStatus("Added robot: " + robotId);
        gridPanel.repaint();
    }
    
    /**
     * Toggles animation on/off.
     */
    private void toggleAnimation() {
        if (robots.isEmpty()) {
            controlPanel.updateStatus("Add a robot first!");
            return;
        }
        
        if (!isAnimating) {
            // Plan paths for all robots
            for (Robot robot : robots) {
                if (!robot.hasPath()) {
                    planner.planPathForRobot(robot);
                }
            }
            animationTimer.start();
            isAnimating = true;
            controlPanel.updateStatus("Animation started");
        } else {
            animationTimer.stop();
            isAnimating = false;
            controlPanel.updateStatus("Animation stopped");
        }
    }
    
    /**
     * Steps animation forward.
     */
    private void stepAnimation() {
        boolean anyMoving = false;
        
        for (Robot robot : robots) {
            if (robot.moveNext()) {
                anyMoving = true;
            }
        }
        
        gridPanel.repaint();
        
        if (!anyMoving) {
            animationTimer.stop();
            isAnimating = false;
            controlPanel.updateStatus("All robots reached goals");
        }
    }
    
    /**
     * Steps all robots once.
     */
    private void stepAllRobots() {
        if (robots.isEmpty()) {
            controlPanel.updateStatus("Add a robot first!");
            return;
        }
        
        for (Robot robot : robots) {
            if (!robot.hasPath()) {
                planner.planPathForRobot(robot);
            }
            robot.moveNext();
        }
        
        gridPanel.repaint();
        controlPanel.updateStatus("Robots stepped forward");
    }
    
    /**
     * Resets all robots to start.
     */
    private void resetRobots() {
        for (Robot robot : robots) {
            robot.resetPath();
        }
        gridPanel.repaint();
        controlPanel.updateStatus("Robots reset");
    }
    
    /**
     * Clears the path visualization.
     */
    private void clearPath() {
        grid.clearPath();
        gridPanel.repaint();
        controlPanel.updateStatus("Path cleared");
    }
    
    /**
     * Compares algorithms and shows results.
     */
    private void compareAlgorithms() {
        if (grid.getStartNode() == null || grid.getGoalNode() == null) {
            controlPanel.updateStatus("Error: Set start and goal first!");
            return;
        }
        
        planner.compareAlgorithms();
        controlPanel.updateStatus("Algorithm comparison complete");
    }
    
    /**
     * Clears the entire grid.
     */
    private void clearGrid() {
        grid = new Grid(grid.getWidth(), grid.getHeight());
        planner = new PathPlanner(grid);
        robots.clear();
        gridPanel.repaint();
        controlPanel.updateStatus("Grid cleared");
    }
    
    /**
     * Updates statistics display.
     */
    private void updateStatistics(List<Node> path) {
        StringBuilder stats = new StringBuilder();
        stats.append("Path Length: ").append(path.size()).append("\n");
        stats.append("Algorithm: ").append(planner.getCurrentAlgorithm()).append("\n");
        stats.append("Robots: ").append(robots.size()).append("\n");
        stats.append("\nRobot Status:\n");
        
        for (Robot robot : robots) {
            stats.append(robot.getRobotId()).append(": ");
            stats.append("(").append(robot.getCurrentX()).append(",");
            stats.append(robot.getCurrentY()).append(") - ");
            stats.append(robot.getState()).append("\n");
        }
        
        controlPanel.updateStats(stats.toString());
    }
    
    /**
     * Main method to launch the simulator.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WarehouseSimulator simulator = new WarehouseSimulator(20, 15);
            simulator.setVisible(true);
        });
    }
}