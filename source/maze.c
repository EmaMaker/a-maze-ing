#include <grrlib.h>
#include <wiiuse/wpad.h>

#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <time.h>

#include "maze.h"
#include "vars.h"
#include "player.h"

/** An Implementation of the Depth-First Search Recursive Backtracker Maze generator algorithm based on my work at:
 *  https://github.com/EmaMaker/recursive-backtracker/tree/c-implementation **/

cell* current;
cell* grid[W_GRID][H_GRID];

int stackX[W_GRID*H_GRID];
int stackY[W_GRID*H_GRID];
int cv = 0;

// int todraw[w_GRID][h_GRID];

void startNewLevel(){
    srand(time(NULL));
    generateMaze();
    prepareShow();
    setupPlayers();
    setupEndPoint();
}

void initMazeGrid(){
    for(int i = 0; i < W_GRID; i++){
        for(int j = 0; j < H_GRID; j++){
            cell* c = malloc(sizeof(cell));
            grid[i][j] = c;
        }
    }
}

void generateMaze(){
    //Init random

    //Reset the counter for the stack when generating new maze or it will go over the array size!!!!
    cv = 0;

    for(int i = 0; i < W_GRID; i++){
        for(int j = 0; j < H_GRID; j++){
            grid[i][j]->visited=false;
            grid[i][j]->up=true;
            grid[i][j]->down=true;
            grid[i][j]->right=true;
            grid[i][j]->left=true;
            grid[i][j]->x = i;
            grid[i][j]->y = j;
        }
    }

    //Pick a current cell
    current = grid[0][0];

    //while there are unvisited cells. This is gonna change when adding a stack
    while(!allCellVisited()){
        current->visited=true;

        //Get unvisited neighbours
        int un = 0;
        int x = current->x;
        int y = current->y;
        if(x-1 >= 0 && !grid[x-1][y]->visited) un++;
        if(x+1 < W_GRID && !grid[x+1][y]->visited) un++;
        if(y-1 >= 0 && !grid[x][y-1]->visited) un++;
        if(y+1 < H_GRID && !grid[x][y+1]->visited) un++;
        
        //If there are any
        if(un > 0){
            int xs[un], ys[un];
            
            int tun = 0;
            if(x-1 >= 0 && !grid[x-1][y]->visited){
                xs[tun] = x-1;
                ys[tun] = y;
                tun++;
            }
            if(x+1 < W_GRID && !grid[x+1][y]->visited){
                xs[tun] = x+1;
                ys[tun] = y;
                tun++;
            }
            if(y-1 >= 0 && !grid[x][y-1]->visited){
                xs[tun] = x;
                ys[tun] = y-1;
                tun++;
            }
            if(y+1 < H_GRID && !grid[x][y+1]->visited) {
                xs[tun] = x;
                ys[tun] = y+1;
                tun++;
            }

            //Randomly choose one
            int c = (int)(rand()) % un;

            //Delete walls
            //Right
            if(grid[xs[c]][ys[c]]->x == current->x+1){
                current->right=false;
                grid[xs[c]][ys[c]]->left=false;
            }
            //Left
            if(grid[xs[c]][ys[c]]->x == current->x-1){
                current->left=false;
                grid[xs[c]][ys[c]]->right=false;
            }
            //Up
            if(grid[xs[c]][ys[c]]->y == current->y-1){
                current->up=false;
                grid[xs[c]][ys[c]]->down=false;
            }
            //Up
            if(grid[xs[c]][ys[c]]->y == current->y+1){
                current->down=false;
                grid[xs[c]][ys[c]]->up=false;
            }

            //Push current cell to the stack
            push(current->x, current->y);

            current = grid[xs[c]][ys[c]];
        }else if(cv > 0){
            //Else if stack is not empty
            current = grid[getX()][getY()];
            pop();
        }
    }
}

//Setup end point in a random location. At a distance of EP_DIST from every player
void setupEndPoint(){
    //Randomly spawns all the players
    //For a spawn location to be valid, it has to be free from both walls and players
    int x = (rand() % (w_GRID));
    int y = (rand() % (h_GRID));

    //while there's a wall in current location pick new location
    while(thereIsPlayerInPos(x, y) || 
        ((todraw[(x-1+w_GRID)%w_GRID][y] == 1 && todraw[(x+1+w_GRID)%w_GRID][y] == 1) || 
        (todraw[x][(y+1+h_GRID)%h_GRID] == 1 && todraw[x][(y-1+h_GRID)%h_GRID] == 1)) || areTherePlayersNearby(x, y, EP_DIST)) {
        x = (rand() % (w_GRID));
        y = (rand() % (h_GRID));
    }
    todraw[x][y] = 2;
}

void prepareShow(){
  for(int i = 0; i < w_GRID; i++){
    for(int j = 0; j < h_GRID; j++){
      todraw[i][j] = 1;
    }
  }
  
  for(int i = 0; i < W_GRID; i++){
    for(int j = 0; j < H_GRID; j++){
      
      int x = 2*i+1;
      int y = 2*j+1;
      
      //current cell
      todraw[x][y] = 0;
      if(!grid[i][j]->up)todraw[x][y-1]=0;
      if(!grid[i][j]->right)todraw[x+1][y]=0;
      if(!grid[i][j]->down)todraw[x][y+1]=0;
      if(!grid[i][j]->left)todraw[x-1][y]=0;
    }
  }

}

void showMaze(){
  for(int j = 0; j < h_GRID; j++){
    for(int i = 0; i < w_GRID; i++){
        if(todraw[i][j] == 1){
            //wall
            GRRLIB_Rectangle(M_XOFF + D_OFFSET + i*rSizeW, D_OFFSET + j*rSizeH, rSizeW, rSizeH, MAZE_WALL_COL, 1);
        }else if(todraw[i][j] == 0){
            //corridor
            GRRLIB_Rectangle(M_XOFF + D_OFFSET + i*rSizeW, D_OFFSET + j*rSizeH, rSizeW, rSizeH, MAZE_CORRIDOR_COL, 1);
        }else if(todraw[i][j] == 2){
            //end point
            GRRLIB_Rectangle(M_XOFF + D_OFFSET + i*rSizeW, D_OFFSET + j*rSizeH, rSizeW, rSizeH, MAZE_EP_COL, 1);
        }
    }

  }
}

bool allCellVisited() {
  for(int i = 0; i < W_GRID; i++){
    for(int j = 0; j < H_GRID; j++){
      if(!grid[i][j]->visited) return false;
    }
  }
  return true;
}

/*STUCK STUFF*/
//Pushes to stackX and stackY
void push(int x, int y){
    if(cv < W_GRID*H_GRID && cv >= 0){
        stackX[cv] = x;
        stackY[cv] = y;
        cv++;
    }
}

void pop(){
    if(cv < W_GRID*H_GRID && cv >= 0){
        cv--;
        stackX[cv] = -1;
        stackY[cv] = -1;
    }
}

int getX(){
    return stackX[cv-1];
}

int getY(){
    return stackY[cv-1];
}
