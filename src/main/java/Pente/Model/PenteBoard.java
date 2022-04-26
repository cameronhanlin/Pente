package Pente.Model;

import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

@Component
public class PenteBoard {

    ArrayList<ArrayList<PenteTile>> theBoard = new ArrayList<>();
    Random rand = new Random();
    int humanCaptures;
    int computerCaptures;
    int lastComputerMove;


    public void newBoard(){
        theBoard.clear();
        humanCaptures = 0;
        computerCaptures =0;
        lastComputerMove = 180;
        int cellID = 0;
        for(int i=0; i<19; i++){
            ArrayList<PenteTile> tempRow = new ArrayList<>();
            for(int j=0; j<19; j++){
                tempRow.add(new PenteTile(cellID));
                cellID++;
            }
            theBoard.add(tempRow);
        }
    }

    public void applyHumanMove(String tileID){
        int id = Integer.valueOf(tileID);

        for(ArrayList<PenteTile> row: theBoard){
            for(PenteTile tile : row){
                if(tile.getId() == id){
                    tile.setColorTile("H");
                    tile.setFreeSpace(false);
                }
            }
        }
    }

    public void makeComputerMove(){

        /*Priority of computer moves
        if have 4 in a row, play the 5th to win
        if have a 5 but missing one pocket to win
        --if have 4 captured, capture a 5th to win
        if opponent has 4 in a row, block
        --if oppenent has 4 captures, block the capture
        check for if oppenent has a pocket 5 in row HHFHH
        if have open 3, make open 4 prioritize an area that it could have 5 in a row to win
        look for a pocket to make 4 in a row CFCC
        if oppenent has open 3 in a row, block
        if can make a capture, do it.
        if can block a capture, block it
        --if have open 2, make open 3, prioritize an area that it could have 5 in a row
        --else play a random piece near where the human last played?

         */

        boolean moveMade = false;

        // lowercase f means a freespace that will not have a tile placed on it, can only be used on the ends.
        ArrayList<String> priority = new ArrayList<String>(  //this Generates a priority for paterns it could play on.
                Arrays.asList("CCCCF","FCCCC", "CFCCC","CCFCC","CCCFC", // add a fifth to win                     #1
                        "FHHHH","HHHHF", "HFHHH", "HHFHH", "HHHFH",// block a fifth that human could win
                        "fFCCCf", "fCCCFf", "fCFCCf", "fCCFCf", // make open ended 4
                        "FHHHF", "fHFHHf", "fHHFHf", //block an open ended 4 from being made
                        "FHHC", "CHHF", // capture                                                              #if computer capture is 4, this should be #2
                        "FCCH","HCCF", //block capture                                                          #if human capture is 4, this should be # 2/3
                        "FHHF", //tries to start a capture
                        "fCFFCf", //tries to connect its own pieces better
                        "FCFCFf", "fFCFCF", //place a random tile if its got two going for it
                        "fFFCCFFf")); //places something around if its got two in a row
        ArrayList<String> stringBoard = theBoardAsStrings();

        //TODO rearrange priorities if human captures are at 4 or if computer captures are at 4? if statements for first part and then add the rest to all after?
        //TODO Do you want to try and add in patern logic?
        //TODO website title tab

        for(String checkFor : priority){
            ArrayList<Object> someReturn = doesBoardContain(checkFor, stringBoard);
            boolean doesContain = (boolean)someReturn.get(0);
            if(doesContain){
                String line = (String)someReturn.get(1);
                String lineKey = line.substring(0,line.length()-5);
                String lineCode = line.substring(line.length()-5,line.length());
                System.out.println(lineKey+" "+lineCode);
                knownComputerMove(checkFor, lineKey, lineCode);
                moveMade = true;
                break;
            }
        }

        if(!moveMade){
            makeRandoComputerMove();
        }
    }

    public void knownComputerMove(String checkFor, String lineKey, String lineCode){
        checkFor = checkFor.replaceAll("f","");
        int place = lineKey.indexOf(checkFor);
        int placePlus = 0;
        int count = checkFor.length() - checkFor.replaceAll("F","").length();
        int line = Integer.valueOf(lineCode.substring(0,2))-10;
        String lineDirection = lineCode.substring(2);
        System.out.println("looking for "+checkFor+" which is size "+checkFor.length());
        System.out.println("In Known Computer Moves, Line num "+line);
        System.out.println("In Known Computer Moves, Direction "+lineDirection);

        if(count == 1){         //if there was only one Capital F in the key string, it goes to that place
            placePlus = checkFor.indexOf("F");
        } else {                //if there were two or more Capital F in the key String, it randomly picks one.
            int somePlace;
            do{
                somePlace = rand.nextInt(checkFor.length());
            } while (!checkFor.substring(somePlace,somePlace+1).equals("F"));
            placePlus = somePlace;
        }

        place = place + placePlus;
        System.out.println("at place "+place);

        // row
        if(lineDirection.equals("row")){     //if the key string was in a row, it finds the location here.
            theBoard.get(line).get(place).setColorTile("C");
            theBoard.get(line).get(place).setFreeSpace(false);
            checkForCaptures("C", line, place);
        } else if (lineDirection.equals("col")) {  //if the key string was in a column, it finds the location here.
            theBoard.get(place).get(line).setColorTile("C");
            theBoard.get(place).get(line).setFreeSpace(false);
            checkForCaptures("C", place, line);
        } else { // in here is where it checks all the diagonals
            int i = 0;
            int j = 0;
            if (lineDirection.equals("drl")){    //drl: Diagonal to the Right on the Left side.
                i = line;
                //j = 0;
                for(int x = 0; x<place;x++){
                    i++;
                    j++;
                }
            } else if (lineDirection.equals("dlr")){ //dlr: Diagonal to the Left on the Right side.
                i = line;
                j = 18;
                for(int x = 0; x<place;x++){
                    i++;
                    j--;
                }
            } else if (lineDirection.equals("drt")){ //drt: Diagonal to the Right along the Top
                //i=0;
                j=line;
                for(int x = 0; x<place;x++){
                    i++;
                    j++;
                }
            } else if (lineDirection.equals("dlt")){ //dlt: Diagonal to the Left along the Top
                //i=0;
                j=line;
                for(int x = 0; x<place;x++){
                    i++;
                    j--;
                }
            }
            theBoard.get(i).get(j).setColorTile("C");
            theBoard.get(i).get(j).setFreeSpace(false);
            checkForCaptures("C", i, j);
        }

    }

    public ArrayList<String> theBoardAsStrings(){
        ArrayList<String> stringBoard = new ArrayList<>();

        int rowNum = 10;
        int colNum = 10;
        int diaNum;


        for(int i=0; i<theBoard.size(); i++){ //adds all rows
            String row = "";
            for(int j=0; j<theBoard.get(i).size();j++){
                row = row.concat(theBoard.get(i).get(j).getColorTile());
            }
            row = row+rowNum;
            row = row.concat("row");
            stringBoard.add(row);
            rowNum++;
        }

        for(int j=0; j<theBoard.get(0).size(); j++){ //adds all columns
            String column = "";
            for(int i=0; i<theBoard.get(j).size();i++){
                column = column.concat(theBoard.get(i).get(j).getColorTile());
            }
            column = column+colNum;
            column = column.concat("col");
            stringBoard.add(column);
            colNum++;
        }

        diaNum = 10;
        for(int i=0; i<theBoard.size();i++){ //adds diagonals to the right on the left side. top to bottom
            int j=0;
            int tempI = i;
            String diagonal = "";
            while(tempI<=18 && j<=18){
                diagonal = diagonal.concat(theBoard.get(tempI).get(j).getColorTile());
                tempI++;
                j++;
            }
            diagonal = diagonal+diaNum;
            diagonal = diagonal.concat("drl");
            stringBoard.add(diagonal);
            diaNum++;
        }

        diaNum = 10;
        for(int i=0; i<theBoard.size();i++){ //adds diagonals to the left on the right side.  dlr top to bottom
            int j=18;
            int tempI = i;
            String diagonal = "";
            while(tempI<=18 && j>=0){
                diagonal = diagonal.concat(theBoard.get(tempI).get(j).getColorTile());
                tempI++;
                j--;
            }
            diagonal = diagonal+diaNum;
            diagonal = diagonal.concat("dlr");
            stringBoard.add(diagonal);
            diaNum++;
        }

        diaNum = 10;
        for(int j=0; j<theBoard.get(0).size();j++){ //adds diagonals to the right along top left to right along top
            int i=0;
            int tempJ = j;
            String diagonal = "";
            while(tempJ<=18 && i<=18){
                diagonal = diagonal.concat(theBoard.get(i).get(tempJ).getColorTile());
                tempJ++;
                i++;
            }
            diagonal = diagonal+diaNum;
            diagonal = diagonal.concat("drt");
            stringBoard.add(diagonal);
            diaNum++;
        }

        diaNum = 10;
        for(int j=0; j<theBoard.get(0).size();j++){ //adds diagonals to the right along top dlt left to right
            int i=0;
            int tempJ = j;
            String diagonal = "";
            while(tempJ>=0 && i<=18){
                diagonal = diagonal.concat(theBoard.get(i).get(tempJ).getColorTile());
                tempJ--;
                i++;
            }
            diagonal = diagonal+diaNum;
            diagonal = diagonal.concat("dlt");
            stringBoard.add(diagonal);
            diaNum++;
        }

        return stringBoard;
    }

    public void makeRandoComputerMove(){    //TODO it will sometimes randomly set the human up for a capture.
        ArrayList<Integer> humanMove = returnIJ(String.valueOf(lastComputerMove)); //TODO, maybe prioritize being closer to other pieces?
        int humanI = humanMove.get(0);
        int humanJ = humanMove.get(1);

        int maxRange = 2; //a range for distance from human guess for the computer
        int maxGuess = 1; //a counter for when the computer should increase the range of guesses

        int offSetI = 0;
        int offSetJ = 0;

        do{
            offSetI = rand.nextInt(maxRange*2)-maxRange;
            offSetJ = rand.nextInt(maxRange*2)-maxRange;
            offSetI = offSetI+humanI;
            offSetJ = offSetJ+humanJ;
            if(offSetI<0)
                offSetI=0;
            if(offSetI>18)
                offSetI=18;
            if(offSetJ<0)
                offSetJ=0;
            if(offSetJ>18)
                offSetJ=18;

            if(maxGuess%15 == 0){
                maxRange++;
            }
            maxGuess++;

        } while (!theBoard.get(offSetI).get(offSetJ).isFreeSpace());

        theBoard.get(offSetI).get(offSetJ).setColorTile("C");
        theBoard.get(offSetI).get(offSetJ).setFreeSpace(false);

        checkForCaptures("C", offSetI, offSetJ);
    }

    public void checkForCaptures(String mark, int placeI, int placeJ){

        if(mark.equals("C")){
            lastComputerMove = returnTileID(placeI, placeJ);
        }


        int[] cycleI = {0,0,-3,3,-3,-3,3,3};
        int[] cycleJ = {-3,3,0,0,-3,3,-3,3};

        for(int k=0;k<cycleI.length;k++) {
            if(placeI + cycleI[k] >= 0 && placeI + cycleI[k] <= 18 && placeJ + cycleJ[k] >= 0 && placeJ + cycleJ[k] <= 18){
                if (getTileColor(placeI + cycleI[k], placeJ + cycleJ[k]).equals(mark)){
                    int stepI = cycleI[k] / 3;
                    int stepJ = cycleJ[k] / 3;
                    if (!getTileColor(placeI + stepI, placeJ + stepJ).equals("F") && !getTileColor(placeI + stepI, placeJ + stepJ).equals(mark) &&
                            !getTileColor(placeI + (2 * stepI), placeJ + (2 * stepJ)).equals("F") && !getTileColor(placeI + (2 * stepI), placeJ + (2 * stepJ)).equals(mark)) {
                        theBoard.get(placeI + stepI).get(placeJ + stepJ).setColorTile("F");
                        theBoard.get(placeI + stepI).get(placeJ + stepJ).setFreeSpace(true);
                        theBoard.get(placeI + (2 * stepI)).get(placeJ + (2 * stepJ)).setColorTile("F");
                        theBoard.get(placeI + (2 * stepI)).get(placeJ + (2 * stepJ)).setFreeSpace(true);
                        if (mark.equals("H")) {
                            humanCaptures++;
                        }
                        if (mark.equals("C")) {
                            computerCaptures++;
                        }
                    }
                }
            }
        }
    }

    public void checkForCaptures(String mark, String tileID){
        ArrayList<Integer> place = returnIJ(tileID);  //i is vertical, j is horizontal
        int placeI = place.get(0);
        int placeJ = place.get(1);

        checkForCaptures(mark, placeI, placeJ);
    }

    public ArrayList<Object> doesBoardContain(String marks, ArrayList<String> stringBoard){

        marks = marks.toUpperCase();

        ArrayList<Object> toReturn = new ArrayList<>();

        for(String line : stringBoard){
            if(line.contains(marks)){
                toReturn.add(true);
                toReturn.add(line);
                return toReturn;
            }
        }

        toReturn.add(false);
        return toReturn;
    }

    public boolean checkForWinner(String mark){
        String marks = "";
        ArrayList<String> stringBoard = theBoardAsStrings();

        for(int i=0; i<5;i++){
            marks = marks + mark;
        }

        for(String line : stringBoard){
            if(line.contains(marks)){
                return true;
            }
        }

        if (mark.equals("H") && humanCaptures>=5) {
            return true;
        }
        if (mark.equals("C") && computerCaptures>=5) {
            return true;
        }

        return false;
    }

    public boolean checkTileFree(String tileID){
        int id = Integer.valueOf(tileID);

        for(ArrayList<PenteTile> row: theBoard){
            for(PenteTile tile : row){
                if(tile.getId() == id){
                    return tile.isFreeSpace();
                }
            }
        }

        return false;
    }

    public boolean checkTileAvailable(){   //only returns false if all tiles are full
        for(ArrayList<PenteTile> row: theBoard) {
            for (PenteTile tile : row) {
                if (tile.isFreeSpace()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void setATile(String mark, int i, int j){ //should only be used for first move stuff
        theBoard.get(i).get(j).setColorTile(mark);
        theBoard.get(i).get(j).setFreeSpace(false);
    }

    public ArrayList<Integer> returnIJ(String tileID){ //i is vertical, j is horizontal
        ArrayList<Integer> defaultReturn = new ArrayList<>();

        for(int i=0; i<theBoard.size(); i++){
            for(int j=0; j<theBoard.size(); j++){
                if(theBoard.get(i).get(j).getId() == Integer.valueOf(tileID)){
                    defaultReturn.add(i);
                    defaultReturn.add(j);
                    return defaultReturn;
                }
            }
        }

        defaultReturn.add(0);
        defaultReturn.add(0);
        return defaultReturn;
    }

    public int returnTileID(int i, int j){
        return theBoard.get(i).get(j).getId();
    }

    public String getTileColor(int i, int j){
        return theBoard.get(i).get(j).getColorTile();
    }


    public ArrayList<ArrayList<PenteTile>> getTheBoard() {
        return theBoard;
    }

    public void setTheBoard(ArrayList<ArrayList<PenteTile>> theBoard) {
        this.theBoard = theBoard;
    }

    public int getHumanCaptures() {
        return humanCaptures;
    }

    public void setHumanCaptures(int humanCaptures) {
        this.humanCaptures = humanCaptures;
    }

    public int getComputerCaptures() {
        return computerCaptures;
    }

    public void setComputerCaptures(int computerCaptures) {
        this.computerCaptures = computerCaptures;
    }
}
