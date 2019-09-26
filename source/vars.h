#include "maze.h"
#include <grrlib.h>
#include <wiiuse/wpad.h>

// RGBA Colors
#define GRRLIB_BLACK   0x000000FF
#define GRRLIB_MAROON  0x800000FF
#define GRRLIB_GREEN   0x008000FF
#define GRRLIB_OLIVE   0x808000FF
#define GRRLIB_NAVY    0x000080FF
#define GRRLIB_PURPLE  0x800080FF
#define GRRLIB_TEAL    0x008080FF
#define GRRLIB_GRAY    0x808080FF
#define GRRLIB_SILVER  0xC0C0C0FF
#define GRRLIB_RED     0xFF0000FF
#define GRRLIB_LIME    0x00FF00FF
#define GRRLIB_YELLOW  0xFFFF00FF
#define GRRLIB_BLUE    0x0000FFFF
#define GRRLIB_FUCHSIA 0xFF00FFFF
#define GRRLIB_AQUA    0x00FFFFFF
#define GRRLIB_WHITE   0xFFFFFFFF

#define SWIDTH rmode->viWidth
#define SHEIGHT rmode->viHeight

#define MHEIGHT SHEIGHT
#define MWIDTH MHEIGHT
#define M_XOFF ((SWIDTH - MHEIGHT) / 2)

#define HUD_P_XOFF (M_XOFF / 2)
#define HUD_P_YOFF (MHEIGHT / 4)

#define W_GRID 20
#define H_GRID 20
#define w_GRID (W_GRID*2 +1)
#define h_GRID (W_GRID*2 +1)

#define sizeW (MWIDTH / W_GRID)
#define sizeH (MHEIGHT / H_GRID)
#define rSizeW (MWIDTH / w_GRID)
#define rSizeH (MHEIGHT / h_GRID)

#define P1_COL GRRLIB_BLUE
#define P2_COL GRRLIB_RED
#define P3_COL GRRLIB_GREEN
#define P4_COL GRRLIB_YELLOW

#define MAZE_CORRIDOR_COL GRRLIB_WHITE
#define MAZE_WALL_COL GRRLIB_BLACK
#define MAZE_EP_COL GRRLIB_PURPLE

#define L_OFFSETV 1
#define L_OFFSETH 2

#define MOVE_TOFFSET 150
#define EP_DIST (W_GRID*4/5)

//drawing offset
#define D_OFFSET 0
#define PHD_OFFSET 15

#define LEAVE_MENU_WIDTH (SWIDTH / 2)
#define LEAVE_MENU_HEIGHT (SHEIGHT / 4)


void initVars();

//arrays with the cells to be drawn
int todraw[w_GRID][h_GRID];
// Storing wpadHeld and wpadDown for 4 remotes
//Index 0 is for held, 1 is for down
u32 wpadHD[4][2];
//Storing last time a remote has clicked a button, so the player is not too fast
u32 times[4][4];
//Array with the position of the four players. it is initialized in initVars;
//First dimension is the player. Second dimension is the coordinate. 
//Second dimension, first index is x position, second index is y position
int pPos[4][2];
//Table of counters to add a bit of delay to the player movement
//First dimension is the player. Second dimension is the direction in order up, right, down, left
u32 pMoveCounter[4];
//Array saying if a player is playing or not. it is initialized in initVars;
int pPlaying[4];
//Scores
int pScores[4];
//font for HUD
GRRLIB_texImg *tex_BMfont5;
GRRLIB_texImg *tex_BMfont3;
