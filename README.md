Autonomous Warehouse Robot Path Planner
A warehouse navigation system that uses A* and Dijkstra algorithms to find optimal paths for robots. Includes an interactive GUI to visualize pathfinding in real-time.
Project Overview
This project simulates a warehouse robot that needs to navigate from a start position to a goal while avoiding obstacles (like shelves). It implements two pathfinding algorithms:

A Algorithm*: Fast pathfinding using heuristics
Dijkstra's Algorithm: Guarantees shortest path

The system includes:

Grid-based warehouse map
Robot navigation with obstacle avoidance
Interactive GUI with real-time visualization
Support for 4-directional and diagonal movement
Dynamic path recalculation

Test Files:
TestPhase1.java
Tests the Grid, Node, and Robot classes
Validates grid creation, obstacle placement, and robot movement
Prints warehouse layout to console

TestPhase2.java
Tests A* and Dijkstra pathfinding algorithms
Compares algorithm performance (speed, nodes explored)
Tests dynamic replanning when obstacles change
Demonstrates complex warehouse scenarios

TestPhase3.java
Launches the interactive GUI simulator
Provides instructions for using the interface
Allows visual testing of all features

How to Run
1. Compile the Project
bash# Create bin directory
mkdir bin

# Compile all files
1. Compile the Project
   
javac -d bin model/*.java algorithm/*.java planner/*.java ui/*.java TestPhase1.java TestPhase2.java TestPhase3.java

2. Run Tests
 
Test Phase 1 (Core Components):

bashjava -cp bin TestPhase1

Test Phase 2 (Pathfinding Algorithms):

bashjava -cp bin TestPhase2

Test Phase 3 (GUI Simulator):

bashjava -cp bin TestPhase3

3. Use the GUI
Once the GUI opens:

Select "Set Start" and click on the grid
Select "Set Goal" and click on the grid
Use "Add Obstacle" to draw obstacles
Click "Plan Path" to see the route
Click "Add Robot" to place a robot
Click "Animate" to watch the robot move


Built with: Java, Swing, OOP, Pathfinding Algorithms
