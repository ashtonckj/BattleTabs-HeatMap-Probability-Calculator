# BattleTabs HeatMap Probability Calculator

A probability calculator and heatmap visualizer for BattleTabs - a modern twist on the classic Battleship game. This tool helps players make strategic decisions by calculating and displaying the probability of ship locations on an 8×8 grid using colorful ASCII visualization.

## About BattleTabs

BattleTabs is an enhanced version of the classic Battleship game with several unique features:

- **8×8 Grid**: Smaller, faster-paced gameplay compared to traditional 10×10 Battleship
- **4 Ships Per Game**: Players select 4 ships from 30+ available options
- **Unique Ship Shapes**: Beyond standard rectangles - includes L-shapes, plus signs, X-shapes, and more
- **Ship Abilities**: Each ship has unique powerups with cooldown mechanics
- **Dynamic Rock Obstacles**: Pre-placed rocks that prevent ship placement and affect strategy
- **Daily Rock Patterns**: Rock formations change daily with 8 possible variations (4 rotations × 2 mirror states)

## Features

- **Probability Heatmap**: RGB color-coded ASCII visualization showing the likelihood of ship presence on each tile
- **Rock Pattern Database**: Pre-saved rock patterns with binary search functionality
- **Real-time Updates**: Probability recalculation after each hit, miss, or reveal
- **Smart Ship Placement Analysis**: Considers valid placement positions for all possible ship combinations
- **Multiple Input Types**: Supports hits, misses, reveals, and sonar data
- **Gradient Visualization**: White → Yellow → Orange → Red color gradient (low to high probability)

## Ship Shapes

The calculator supports 16 different ship shape types:

### Rectangle Shapes
```
1×1:    1×2:     1×3:        1×4:           1×5:
■       ■ ■      ■ ■ ■       ■ ■ ■ ■        ■ ■ ■ ■ ■ 
```

### Square Shape
```
2×2:
■ ■
■ ■
```

### Special Shapes
```
+ Shape:     x Shape:     o Shape:      O Shape:
  ■           ■   ■          ■           ■ ■ ■
■ ■ ■           ■          ■   ■         ■   ■
  ■           ■   ■          ■           ■ ■ ■
```

### L-Family Shapes
```
l Shape:     L Shape:     Arch Shape:     T Shape:
■            ■            ■ ■ ■           ■ ■ ■
■ ■          ■            ■   ■             ■
             ■ ■
```

### Advanced Shapes
```
S Shape:      Zig-Zag Shape:
  ■ ■         ■ ■
■ ■             ■ ■
                  ■ ■
```

## Available Ships

The game features 30+ unique ships, each with different shapes, abilities, and cooldowns:

| Ship Name | Shape | Cooldown | Ability Type |
|-----------|-------|----------|--------------|
| Catamaran | O Shape | 8 | Selective Hits |
| Coracle | 1×1 | 6 | One Shot Hits |
| Crafty Crab | S Shape | Passive | Random Reveal (passive) |
| Crustacean | x Shape | 4 | Reveal & Attack in X pattern |
| Dolphin | l Shape | 4 | Sonar (strength based on adjacent ships) |
| Electric Eels | o Shape | 3 | Full Reveal Target Ship |
| Kayak | 1×2 | 2 | Attack 1×2 area |
| Longboat | 1×4 | 6 | Attack 5 times until miss |
| Octopus | 2×2 | 3 | 2 Random Sonar |
| Raft | 2×2 | 5 | Attack 2×2 area |
| Sailboat | 1×3 | 4 | Attack 1×3 area |
| Sea Monster | Arch Shape | 2 | 4 Random Hits |
| Sea Slug | Zig-Zag | 3 | 5 Reveals until something found |
| Killer Squid | l Shape | 2 | Attack/Reveal with adjacent reveal |
| Long Sub | 1×4 | 1 | 1 Sonar, reveals itself |
| Mini Sub | 1×2 | 2 | 1 Sonar |
| Sea Turtle | T Shape | 2 | Attack with adjacent reveal, cooldown reduction |
| Whale | + Shape | 4 | 1 Attack with adjacent reveal |
| Blunderbuster | 2×2 | 5 | 4 Random attacks in 3×3, bonus at low cooldown |
| Conniving Cannoneer | + Shape | 5 | Attack + Shape pattern |
| Cartographer | 2×2 | 5 | Choose reveal tile, extends 1 tile |
| Chaos Slug | L Shape | 1 | 3 Random reveals, 1 reveal on own board |
| Chaos Sub | 1×5 | 1 | Sonar with cooldown increase |
| Chaotic Machinist | + Shape | 3 | 5 Attacks, hits itself on each hit |
| Medium Galleon | 1×3 | 3 | 2 Attacks |
| Magic Mirror | 2×2 | 4 | Copy opponent's last ability |
| Mutineers | 1×3 | 2 | 2 Attacks, reveals itself on hit |
| Pirate Thief | 1×3 | 1 | 1 Attack, cooldown manipulation |
| Skull Cove | Arch Shape | Passive | 4 Random attacks when hit (passive) |
| Slug Squad | 1×3 | 1 | 2 Reveal tiles |
| Valkyrie | 1×4 | 5 | 2 Attacks, bonus per destroyed adjacent ship |

*Note: More ships are being added to the game regularly*

## Rock Patterns

### Pattern Format
Rock patterns are stored as 64-character binary strings (8×8 grid):
- `1` = Rock present
- `0` = Empty water tile
- Pattern reads left-to-right, top-to-bottom (position 0 = top-left, position 63 = bottom-right)

### Daily Variations
Each day features a unique rock pattern with 8 possible orientations:
- 4 rotations: 0°, 90°, 180°, 270°
- 2 mirror states: normal and mirrored
- Both players receive the same pattern but with different orientations

### Pattern Storage
Patterns are saved in `RockPatterns.txt` and support:
- Pattern selection by number
- Binary string search (e.g., searching "11011" finds patterns containing that sequence)

## How It Works

### Probability Calculation
1. **Initial Setup**: Load rock pattern and create 8×8 grid
2. **Ship Placement Simulation**: Test all possible placements for selected ship shapes
3. **Valid Placement Counting**: For each tile, count how many valid ship configurations include it
4. **Normalization**: Highest count = red (255,0,0), lowest count = white (255,255,255)
5. **Gradient Mapping**: Calculate gradient colors for intermediate probabilities

### Update Mechanics
- **Miss**: Probability for that tile and adjacent tiles decreases
- **Hit**: Probability for surrounding tiles increases (ship must extend from hit location)
- **Reveal**: Shows if tile contains ship (yellow border) or water (light blue)
- **Sonar**: Reveals diamond-shaped area up to specified ring, excludes ship-containing ring

### Sonar Mechanics
When a sonar is used at a coordinate:
1. Expands in diamond shape (ring 0, 1, 2, 3...)
2. Continues until a ring contains part of a ship
3. Returns the ring number (e.g., "2" means rings 0 and 1 are empty, ring 2+ has a ship)
4. All tiles in rings 0 through (n-1) are revealed as empty
5. Updates probability calculations accordingly

## Installation & Setup

### Requirements
- Java Development Kit (JDK) 8 or higher
- Any Java-compatible IDE (VS Code recommended)

### No External Dependencies
This project uses only standard Java libraries:
- `java.util.Scanner`
- Standard Java I/O and utilities

### Running the Program

**Using VS Code:**
1. Install Java Extension Pack for VS Code
2. Open project folder
3. Run `src/Battleship/BattleshipHeatMap.java`

**Using Command Line:**
```bash
# Compile
javac src/Battleship/BattleshipHeatMap.java

# Run
java -cp src Battleship/BattleshipHeatMap
```

**Using any IDE:**
1. Import project
2. Run `BattleshipHeatMap.java` as Java Application

## Usage

### Basic Workflow
1. **Configure Opponent Ships**: Select the 4 ship shapes your opponent is using by typing 1 - 16
2. **Select Rock Pattern**: Type "rock" and press "enter" key, and choose from saved patterns or search by binary string
3. **Input Game State**: Enter coordinates and action types
4. **View Heatmap**: Analyze the color-coded probability grid
5. **Update & Repeat**: Continue inputting new information as the game progresses

### Input Format
```
Coordinate: <row> <col>
Action: <action>
```

**Examples:**
- `7 7 --(enter)-> hit` - Bottom-right tile was a hit
- `0 0 --(enter)-> miss` - Top-left tile was a miss
- `3 4 --(enter)-> reveal` - Tile (3,4) was revealed (game shows if ship is present)
- `4 4 --(enter)-> sonar --(enter)-> 2` - Sonar at (4,4) detected ship at ring 2

### Coordinate System
- Rows: 0 (top) to 7 (bottom)
- Columns: 0 (left) to 7 (right)
- Example: `7 7` = bottom-right corner

## Visualization

### ASCII Board Design
The board uses box-drawing characters for a clean appearance:
```
╔═══╦═══╦═══╦═══╦═══╦═══╦═══╦═══╗
║   ║   ║   ║   ║   ║   ║   ║   ║
╠═══╬═══╬═══╬═══╬═══╬═══╬═══╬═══╣
║   ║   ║   ║   ║   ║   ║   ║   ║
...
╚═══╩═══╩═══╩═══╩═══╩═══╩═══╩═══╝
```

### Color Coding
- **Base Grid**: Blue ASCII borders
- **Probability Numbers**: RGB color-coded
  - Red (255, 0, 0): Highest probability
  - Orange: High probability
  - Yellow: Medium probability
  - White (255, 255, 255): Lowest probability
- **Rocks**: Grey tiles (cannot place ships)
- **Hit Tiles**: Yellow tiles
- **Sunken Tiles**: Red tiles
- **Miss Tiles**: Light blue (water shown)

### Status Indicators
- ` ■■■■ ` - Rock obstacle (grey)
- ` >--< ` - Hit (yellow)
- ` ???? ` - Miss (light blue/grey)
- `|>--<|` - Reveal (yellow / blue)
- Numbers - Probability values (colored by gradient)

## Future Enhancements

### Planned Features
- [ ] Ship ability probability calculations
- [ ] Multi-tile attack pattern analysis (2×2, 1×3, etc.)
- [ ] Cooldown tracking system
- [ ] Reveal tile probability adjustments
- [ ] Player's own ship powerup predictions
- [ ] Advanced sonar diamond-shape analysis (Adding probability)
- [ ] Historical game data analysis
- [ ] Convert Console-Line Interface (CLI) to Graphical User Interface (GUI)
- [ ] Detecting if some ships are a subset of other ships

## Technical Details

### Algorithm Complexity
- Pattern matching: O(n × m) where n = possible positions, m = ship rotations
- Probability updates: O(64) per tile update (constant time for 8×8 grid)
- Heatmap rendering: O(64) for full grid display

### Memory Usage
- Rock patterns: Stored as 64-bit binary strings
- Probability grid: 8×8 integer array
- Ship configurations: Cached rotation/position combinations

### Color Calculation
```
For each tile probability P:
1. Normalize: N = (P - min) / (max - min)
2. Map to gradient:
   - Red channel: 255
   - Green channel: 255 × (1 - N)
   - Blue channel: 255 × (1 - N)
Result: Smooth white → yellow → orange → red gradient
```

## Contributing

This is a personal project for BattleTabs strategy optimization. Suggestions and improvements are welcome!

### Areas for Contribution
- Additional ship shape patterns
- Improved probability algorithms
- Enhanced visualization options
- Performance optimizations
- Documentation improvements

## Acknowledgments

- Original BattleTabs game which I found from Discord community
- Inspired by classic Battleship probability calculators
- Built for strategic gameplay enhancement

---

**Note**: This calculator is designed for educational and strategic analysis purposes. It enhances gameplay by providing statistical insights based on game mechanics and probability theory.
