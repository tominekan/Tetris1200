# Tetris 1200 v1.0

I built this for my final project in CIS 1200, but polished it a bit more over the 
following winter break.
## Installation
- Because I have a Mac, I have a MacOS .dmg file for use. Once I get to
the computer labs in school I'll create Windows and Linux binaries.


## How to Play
- Basic Game Mechanics:
  - We want to fit falling blocks together like a puzzle
  - Left/Right (A and D) to shift the blocks left or right
  - Up (W) to rotate the block
  - We clear entire rows by pressing the filling them completely with blocks.
  - The goal is to get the highest score possible
  - Once the block is touching the ground or other blocks, we can't move it
- There are 3 Tetris1200 modes
  - **Normal**: Blocks are selected randomly and fall pretty randomly
  - **Random Start**: Gray blocks are put randomly at the start
  - **Predicable**: Blocks fall in a predictable pattern
- How scoring works:
  - Scoring is pretty generous
  - For every "fall" we have: +1 point
  - For every placed block: +10 points
  - For every cleared row: +100 points


### TODOS: (not in any particular order)
- [ ] We use the MVC design pattern, so definitely add some tests for the MVC
- [ ] Formatting, figure out some formatting tricks immediately
- [ ] Add more game modes (Randomized Starting Position, Rainbow Mode??)
- [ ] Implement a preview of the next block
- [ ] Give some more time to shift blocks once on a surface.
- [ ] Make the Info screen a lot nicer/integrate into beginning screen
- [ ] ADD SOME MORE IMAGES for less bland UI
- [ ] Smooth out some gameplay
- [ ] Iron out some little bugs in the Pause/Play button (relating to when info button is pressed)
- [ ] More consistent formatting
- [ ] Do deeper level refactoring (already fixed glaring inconsistencies)
- [ ] Create exe for windows apps
- [ ] Work on better sizing for the app icon
- [x] Fix FileIO resistance on MacOS
- [x] Fully switched from File -> getResource()
