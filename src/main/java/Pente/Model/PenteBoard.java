package Pente.Model;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

@Component
public class PenteBoard {

    ArrayList<ArrayList<PenteTile>> theBoard = new ArrayList<>();
    Random rand = new Random();


    public void newBoard(){
        theBoard.clear();
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

    public void makeComputerMove(String humanTile){

        /*Priority of computer moves
        --if have 4 in a row, play the 5th to win
        --if have a 5 but missing one pocket to win
        --if have 4 captured, capture a 5th to win
        --if opponent has 4 in a row, block
        --if oppenent has 4 captures, block the capture
        --check for if oppenent has a pocket 5 in row HHFHH
        --if have open 3, make open 4 prioritize an area that it could have 5 in a row to win
        --look for a pocket to make 4 in a row CFCC
        --if oppenent has open 3 in a row, block

        --if can make a capture, do it.
        --if can block a capture, block it
        --if have open 2, make open 3, prioritize an area that it could have 5 in a row
        --else play a random piece near where the human last played?

         */

        boolean moveMade = false;




        ArrayList<String> priority = new ArrayList<String>(
                Arrays.asList("CCCCF","FCCCC", "CFCCC","CCFCC","CCCFC", // add a fifth to win
                        "FHHHH","HHHHF", "HFHHH", "HHFHH", "HHHFH",// block a fifth
                        "FCCCF", // make open ended 4 ?? add on a 6th?
                        "FHHHF")); //block an open ended 4 from being made
        ArrayList<String> stringBoard = theBoardAsStrings();


        for(String checkFor : priority){
            ArrayList<Object> someReturn = doesBoardContain(checkFor, stringBoard);
            boolean doesContain = (boolean)someReturn.get(0);
            if(doesContain){
                String line = (String)someReturn.get(1);
                String lineKey = line.substring(0,line.length()-6);
                String lineCode = line.substring(line.length()-5,line.length());
                System.out.println(lineKey+" "+lineCode);
                knownComputerMove(checkFor, lineKey, lineCode);
                moveMade = true;
                break;
            }
        }



        if(!moveMade){
            makeRandoComputerMove(humanTile);
        }

    }

    public void knownComputerMove(String checkFor, String lineKey, String lineCode){

        int place = lineKey.indexOf(checkFor);
        int placePlus = 0;
        int count = checkFor.length() - checkFor.replaceAll("F","").length();
        int line = Integer.valueOf(lineCode.substring(0,2))-10;
        String lineDirection = lineCode.substring(2);
        System.out.println("In Known Computer Moves, Line num "+line);
        System.out.println("In Known Computer Moves, Direction "+lineDirection);


        if(count == 1){
            placePlus = checkFor.indexOf("F");
        } else {
            int somePlace;
            do{
                somePlace = rand.nextInt(checkFor.length());
            } while (!checkFor.substring(somePlace).equals("F")); ///TODO here its just random, do you want to prioritize a freespace next to another free space?
            placePlus = somePlace;
        }

        place = place + placePlus;

        // row
        if(lineDirection.equals("row")){
            theBoard.get(line).get(place).setColorTile("C");
            theBoard.get(line).get(place).setFreeSpace(false);
        } else if (lineDirection.equals("col")) {
            theBoard.get(place).get(line).setColorTile("C");
            theBoard.get(place).get(line).setFreeSpace(false);
        }


        //TODO diagonal




    }

    public ArrayList<String> theBoardAsStrings(){
        ArrayList<String> stringBoard = new ArrayList<>();

        int rowNum = 10;
        int colNum = 10;

        for(int i=0; i<theBoard.size(); i++){
            String row = "";
            for(int j=0; j<theBoard.get(i).size();j++){
                row = row.concat(theBoard.get(i).get(j).getColorTile());
            }
            row = row+rowNum;
            row = row.concat("row");
            stringBoard.add(row);
            rowNum++;
        }

        for(int i=0; i<theBoard.size(); i++){
            String column = "";
            for(int j=0; j<theBoard.get(i).size();j++){
                column = column.concat(theBoard.get(j).get(i).getColorTile());
            }
            column = column+colNum;
            column = column.concat("col");
            stringBoard.add(column);
            colNum++;
        }

        //TODO all the diagonals have to be added too.

        return stringBoard;
    }

    public void makeRandoComputerMove(String humanTile){
        ArrayList<Integer> humanMove = returnIJ(humanTile);
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
    }

    public ArrayList<Object> doesBoardContain(String marks, ArrayList<String> stringBoard){

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


    public ArrayList<ArrayList<PenteTile>> getTheBoard() {
        return theBoard;
    }

    public void setTheBoard(ArrayList<ArrayList<PenteTile>> theBoard) {
        this.theBoard = theBoard;
    }
}
