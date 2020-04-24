package com.emamaker.amazeing.maze;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import com.emamaker.amazeing.AMazeIng;
import com.emamaker.voxelengine.block.CellId;

public class MazeGenerator {

	AMazeIng main;
	Random rand = new Random();

	Cell currentCell;
	Cell[][] cellsGrid;
	ArrayList<Cell> stack = new ArrayList<Cell>();
	public int[][] todraw;

	public int w, h, W, H;
	public int EP_DIST = 5;
	public int WINX, WINZ;

	public MazeGenerator(AMazeIng game) {
		this(game, 20, 20);
	}

	public MazeGenerator(AMazeIng game, int dimx, int dimy) {
		main = game;
		setMazeSize(dimx, dimy);
	}

	public void setMazeSize(int w_, int h_) {
		w = w_ - 1;
		h = h_ - 1;
		W = w / 2;
		H = h / 2;

		cellsGrid = new Cell[W][H];
		todraw = new int[w][h];
		System.out.println(W + "*" + H + " --- " + w + "*" + h);
	}

	/*
	 * Implementation of a recursive backtracker to generated the maze. Mazes
	 * generated in this way can always be solved.
	 * https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_backtracker
	 */
	public void generateMaze() {
		// init cells
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				cellsGrid[i][j] = new Cell(i, j, this);
			}
		}

		currentCell = cellsGrid[0][0];

		// while there are unvisited cells
		while (!allCellsVisited()) {
			// mark current as visisted
			currentCell.visited = true;

			// check for unvisited neighbours
			Cell[] neighbours = currentCell.unvisitedNeighbours();

			// if the cell has unvisited neighbours
			if (neighbours.length > 0) {
				// Randomly choose one
				Cell n = neighbours[rand.nextInt(neighbours.length)];

				// Push current cell to the stack
				stack.add(currentCell);

				// Delete the walls
				if (n.x == currentCell.x + 1) {
					// right
					currentCell.walls[1] = false;
					n.walls[3] = false;
				}
				if (n.x == currentCell.x - 1) {
					// left
					currentCell.walls[3] = false;
					n.walls[1] = false;
				}
				if (n.y == currentCell.y + 1) {
					// up
					currentCell.walls[2] = false;
					n.walls[0] = false;
				}
				if (n.y == currentCell.y - 1) {
					// down
					currentCell.walls[0] = false;
					n.walls[2] = false;
				}
				currentCell.current = false;
				currentCell = n;
				currentCell.current = true;
			} else if (!stack.isEmpty()) {
				currentCell.current = false;
				// pop a cell from the custom and make it the current one
				currentCell = stack.get(stack.size() - 1);
				stack.remove(stack.size() - 1);
				currentCell.current = true;
			}
		}
		prepareShow();
	}

	/*
	 * Setup end point in a random location. At a distance of EP_DIST from every
	 * player
	 */
	public void setupEndPoint() {
		// Randomly places the end point
		int x = 0, y = 0;

		do {
			x = (Math.abs(rand.nextInt()) % (w));
			y = (Math.abs(rand.nextInt()) % (h));
			// while there's a wall in current location pick new location
		} while (main.gameManager.areTherePlayersNearby(x, y, EP_DIST) || occupiedSpot(x, y));
		WINX = x;
		WINZ = y;
		todraw[x][y] = 2;
		show(todraw);
	}

	/*
	 * Run-lenght encodes ( https://en.wikipedia.org/wiki/Run-length_encoding) the
	 * maze todraw configuration, so it can be easily passed to server clients. This
	 * should be done once the game is been set up and the end point has been
	 * placed. We'll normally use a number for the count of equal blocks next to
	 * each other We'll use letters instead for the todraw[][] numbers to represent
	 * the different block, starting from A (Ascii 65) and adding the todraw[x][y]
	 * index to the ascii value of A. To even simplify decoding, the count number is
	 * encoded in a letter too, starting from a (Ascii 97), so that every count
	 * takes up just to characters.
	 * 
	 */
	public String runLenghtEncode() {
		// todraw[x][y], where row number is x and the index of the block in that row is
		// y
		int count = 0;
		String s = "";
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				// https://www.geeksforgeeks.org/run-length-encoding/
				count = 1;
				while (j < h - 1 && todraw[i][j] == todraw[i][j + 1]) {
					count++;
					j++;
				}
				s += String.valueOf((char)(count + 97));
				s += String.valueOf((char)(todraw[i][j] + 65));
//				System.out.println("Got block " + todraw[i][j] + " for " + count + "times");
			}
//			System.out.println("Going next column");
			s += "-";
		}
		System.out.println(s);
		return s;
	}

	/*
	 * Run length decodes the maze received from the server. We know that the block
	 * types start from A (Ascii 65), so we can simply subtract 65 We know that the
	 * block count start from a (Ascii 97), so we can simply subtract 97 from the
	 * current index and get the block type, repeated for how many times the count
	 * number says
	 */
	public int[][] runLenghtDecode(String s) {
		int[][] todraw_ = null;
		int count, type, totalcount = 0, width = 0;

		// Split the various rows
		String[] rows = s.split("-");
		System.out.println(Arrays.deepToString(rows));

		count = ((int) (rows[0].charAt(0))) - 97;
		width += count;

		// Mazes are always squares
		setMazeSize(width + 1, rows.length + 1);
		// Temporarely patch to the calculation errors in setMazeSize
		todraw_ = new int[w][h];

		for (int i = 0; i < width; i++) {
			totalcount = 0;
			for (int j = 0; j < rows[i].length(); j += 2) {
				count = ((int) (rows[i].charAt(j))) - 97;
				type = ((int) (rows[i].charAt(j + 1))) - 65;

				for (int k = totalcount; k < totalcount + count; k++) {
					todraw_[i][k] = type;
				}
				totalcount += count;
			}
		}
		return todraw_;
	}

	public void prepareShow() {
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				todraw[i][j] = 1;
			}
		}

		// constructs an array so that the walls of the cells are black tiles and the
		// free cells are white tiles
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				int x = 2 * i + 1;
				int y = 2 * j + 1;

				// current cell
				todraw[x][y] = 0;

				// up wall
				if (!cellsGrid[i][j].walls[0])
					todraw[x][y - 1] = 0;
				// down wall
				if (!cellsGrid[i][j].walls[2])
					todraw[x][y + 1] = 0;
				// left wall
				if (!cellsGrid[i][j].walls[3])
					todraw[x - 1][y] = 0;
				// right all
				if (!cellsGrid[i][j].walls[1])
					todraw[x + 1][y] = 0;
			}
		}

		show(todraw);
	}

	public void show(int[][] todraw_) {
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				todraw[i][j] = todraw_[i][j];
				main.world.worldManager.setCell(i, 1, j, CellId.ID_AIR);

				main.world.worldManager.setCell(i, 0, j, CellId.ID_GRASS);

//				if (todraw[i][j] == 1)
//					main.world.worldManager.setCell(i, 1, j, CellId.ID_LEAVES);
				if (todraw[i][j] == 2) {
					WINX = i;
					WINZ = j;
					
					System.out.println("Win position in: " + i + ", " + j);
					main.world.worldManager.setCell(i, 0, j, CellId.ID_WOOD);
				}
			}
		}
	}

	boolean allCellsVisited() {
		for (int i = 0; i < W; i++) {
			for (int j = 0; j < H; j++) {
				if (!cellsGrid[i][j].visited)
					return false;
			}
		}
		return true;
	}

	int cellAt(int x, int y) {
		if (x < w && y < h)
			return todraw[x][y];
		else
			return 1;
	}

	public boolean occupiedSpot(int x, int y) {
		return cellAt(x, y) != 0;
	}
}

class Cell {

	public boolean visited = false;
	public boolean current = false;
	public boolean[] walls = { true, true, true, true };

	// top, right, bottom, left,
	// true means wall present

	int x, y;
	MazeGenerator gen;

	public Cell(int x_, int y_, MazeGenerator generator_) {
		this.x = x_;
		this.y = y_;
		gen = generator_;
	}

	// returning an array of unvisited neighbours, so that they can be easily
	// choosed later on
	Cell[] unvisitedNeighbours() {
		int un = 0;
		if (x - 1 >= 0 && !gen.cellsGrid[x - 1][y].visited)
			un++;
		if (x + 1 < gen.W && !gen.cellsGrid[x + 1][y].visited)
			un++;
		if (y - 1 >= 0 && !gen.cellsGrid[x][y - 1].visited)
			un++;
		if (y + 1 < gen.H && !gen.cellsGrid[x][y + 1].visited)
			un++;

		Cell[] c = new Cell[un];

		int tun = 0;
		if (x - 1 >= 0 && !gen.cellsGrid[x - 1][y].visited) {
			c[tun] = gen.cellsGrid[x - 1][y];
			tun++;
		}
		if (x + 1 < gen.W && !gen.cellsGrid[x + 1][y].visited) {
			c[tun] = gen.cellsGrid[x + 1][y];
			tun++;
		}
		if (y - 1 >= 0 && !gen.cellsGrid[x][y - 1].visited) {
			c[tun] = gen.cellsGrid[x][y - 1];
			tun++;
		}
		if (y + 1 < gen.H && !gen.cellsGrid[x][y + 1].visited) {
			c[tun] = gen.cellsGrid[x][y + 1];
			tun++;
		}
		return c;
	}

}