package Pente.Controller;

import Pente.Model.PenteBoard;
import Pente.Model.PenteTile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;

@Controller
public class PenteController {

    @Autowired
    PenteBoard penteBoard = new PenteBoard();
    int numMoves = 0;
    boolean humanWon = false;
    boolean computerWon = false;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";

    @RequestMapping("/")
    public String displayHome(ModelMap modelMap){
        modelMap = cycleModelMaps(modelMap);
        return "pente";
    }

    @RequestMapping("/startNewGame")
    public String startNewGame(ModelMap modelMap){
        penteBoard.newBoard();
        humanWon = false;
        computerWon = false;
        numMoves = 0;
        modelMap = cycleModelMaps(modelMap);
        return "pente";
    }

    @RequestMapping("human/{tileID}")
    public String makeHumanMove(@PathVariable String tileID, ModelMap modelMap){

        //TODO you will let the game keep going if someone won
        //if(penteBoard.checkTileFree(tileID) && !computerWon){
        if(penteBoard.checkTileFree(tileID)){
            penteBoard.applyHumanMove(tileID);
            humanWon = penteBoard.checkForWinner("H");
            numMoves++;
            //if(numMoves<360 && !humanWon){
            if(numMoves<360){
                penteBoard.makeComputerMove(tileID);
                computerWon = penteBoard.checkForWinner("C");
                numMoves++;
            }
        }

        modelMap = cycleModelMaps(modelMap);
        return "pente";
    }

    @RequestMapping("/outputBoard")
    public String outputBoard(ModelMap modelMap){


        ArrayList<ArrayList<PenteTile>> theBoard = penteBoard.getTheBoard();

        System.out.println(ANSI_RED + "test red text" + ANSI_RESET);

        for(ArrayList<PenteTile> row: theBoard) {
            for (PenteTile tile : row) {
                if(tile.isFreeSpace()){
                    System.out.print(ANSI_GREEN + tile.getColorTile() + ANSI_RESET);
                } else {
                    System.out.print(ANSI_RED + tile.getColorTile() + ANSI_RESET);
                }

            }
            System.out.println(" ");
        }


        modelMap = cycleModelMaps(modelMap);
        return "pente";
    }


    public ModelMap cycleModelMaps(ModelMap modelMap){

        if(penteBoard.getTheBoard().size() <19){
            penteBoard.newBoard();
        }
        modelMap.put("theBoard", penteBoard.getTheBoard());
        modelMap.put("humanWon", humanWon);
        modelMap.put("computerWon", computerWon);

        return modelMap;
    }


}