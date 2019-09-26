#include "control.h"
#include "maze.h"
#include "vars.h"
#include "math.h"
#include "time.h"
#include "player.h"

#include <stdbool.h>
#include <grrlib.h>
#include <wiiuse/wpad.h>


//Only at game loaded first time: setups the number of players that are altrady playing and don't have to press + to enter the game
void firstSetupPlayers(int n){
    //Init random
    srand(time(NULL));    

    //By default player one is always playing
    if(n < 1) n = 1;
    if(n > 4) n = 4;

    for(int i = 0; i < 4;i ++){
        pPlaying[i] = (i < n);
    }

}

void setupPlayers(){

    for(int i = 0; i < 4; i++){
        //By default all players start at (0, 0), out of playable map. so thereIsPlayerInLocation() can operate freely
        pPos[i][0] = 0;
        pPos[i][1] = 0;
    }

    //Randomly spawns all the players
    //For a spawn location to be valid, it has to be free from both walls and players
    for(int i = 0; i < 4; i++){
        int x = 0;
        int y = 0;
        pPos[i][0] = x;
        pPos[i][1] = y;

        //while there's a wall in current location pick new location
        while(todraw[x][y] == 1 || thereIsPlayerInPos(x, y)) {
            x = (rand() % (w_GRID));
            y = (rand() % (h_GRID));
        }
        pPos[i][0] = x;
        pPos[i][1] = y;
    }
}

// Checks if there is any player in the given location
bool thereIsPlayerInPos(int x, int y){
    for(int i = 0; i < 4; i++){
        if(pPlaying[i] && pPos[i][0] == x && pPos[i][1] == y) return true;
    }
    return false;
}

// Checks if there is any player in the given range fom the given location
// Using pythagorean distance formula without sqrt
bool areTherePlayersNearby(int x, int y, int range){
    for(int i = 0; i < 4; i++){
        if(pPlaying[i] && ( ((x - pPos[i][0])*(x - pPos[i][0])) + ((y - pPos[i][1])*(y - pPos[i][1])) < (EP_DIST*EP_DIST) ) ) return true;
    }
    return false;
}

void updatePlayers(){
    /*LEVEL WINNING*/
    for(int i = 0; i < 4; i++){
        /*SOMEONE HAS REACHED THE ENDPOINT, GENERATE NEW LEVEL!*/
        if(todraw[ pPos[i][0] ][ pPos[i][1] ] == 2) {
            pScores[i]++;
            startNewLevel(1);
        }
    }

}

void showPlayer(){
    if(pPlaying[0]) GRRLIB_Rectangle(M_XOFF + D_OFFSET + pPos[0][0]*rSizeW, D_OFFSET + pPos[0][1]*rSizeH, rSizeW, rSizeH, P1_COL, 1);
    if(pPlaying[1]) GRRLIB_Rectangle(M_XOFF + D_OFFSET + pPos[1][0]*rSizeW, D_OFFSET + pPos[1][1]*rSizeH, rSizeW, rSizeH, P2_COL, 1);
    if(pPlaying[2]) GRRLIB_Rectangle(M_XOFF + D_OFFSET + pPos[2][0]*rSizeW, D_OFFSET + pPos[2][1]*rSizeH, rSizeW, rSizeH, P3_COL, 1);
    if(pPlaying[3]) GRRLIB_Rectangle(M_XOFF + D_OFFSET + pPos[3][0]*rSizeW, D_OFFSET + pPos[3][1]*rSizeH, rSizeW, rSizeH, P4_COL, 1);
}

int nPlayersPlaying(){
    int n = 0;
    for(int i = 0; i < 4; i++){
        if(pPlaying[i]) n++;
    }
    return n;
}