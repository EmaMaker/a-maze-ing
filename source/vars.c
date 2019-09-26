#include "vars.h"

void initVars(){
    //First setup of player-related variables. This has to be done only on first game load
    for(int i = 0; i < 4; i++){
        pScores[i] = 0;
        pPlaying[0] = false;
        pPos[i][0] = 0;
        pPos[i][1] = 0;
    }
}