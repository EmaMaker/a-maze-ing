#include <stdbool.h>

#ifndef CELL
#define CELL
typedef struct c{
    bool visited;
    bool up, down, right, left;
    int x, y;
} cell;
#endif

void startNewLevel();
void generateMaze();
void prepareShow();
void showMaze();
void initMazeGrid();
bool allCellVisited();
void setupEndPoint();
bool epInCross(int, int);

//stack stuff
void push();
void pop();
int getX();
int getY();
