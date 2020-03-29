package com.emamaker.amazeing.maze;

import java.util.ArrayList;
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
		w = w_+(1-w%2);
		h = h_+(1-h%2);
		W = (w-1) / 2;
		H = (h-1) / 2;
//		W = w_;
//		H = h_;
//
//		w = 2 * W + 1;
//		h = 2 * H + 1;
		
		/*
		 * w-1 = 2*W
		 * W = w-1)
		 */
		
		cellsGrid = new Cell[W][H];
		todraw = new int[w][h];
		System.out.println(W + "*" + H + " --- " + w + "*" + h);
	}

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
	
	//Setup end point in a random location. At a distance of EP_DIST from every player
	public void setupEndPoint(){
	    //Randomly spawns all the players
	    //For a spawn location to be valid, it has to be free from both walls and players
		int x = 0, y = 0;
	    
		do {
			x = (Math.abs(rand.nextInt()) % (w));
		    y = (Math.abs(rand.nextInt()) % (h));
	    //while there's a wall in current location pick new location
		}while(main.gameManager.areTherePlayersNearby(x, y, EP_DIST) || occupiedSpot(x, y));
		WINX = x;
		WINZ = y;
		todraw[x][y] = 2;
		main.world.worldManager.setCell(WINX, 0, WINZ, CellId.ID_WOOD);
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

		//setupEndPoint();
		for (int j = 0; j < h; j++) {
			for (int i = 0; i < w; i++) {
				main.world.worldManager.setCell(i, 0, j, CellId.ID_GRASS);
				if (todraw[i][j] == 1)
					main.world.worldManager.setCell(i, 1, j, CellId.ID_LEAVES);
//				if (todraw[i][j] == 2)
//					main.world.worldManager.setCell(i, 0, j, CellId.ID_WOOD);
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
		return todraw[x][y];
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