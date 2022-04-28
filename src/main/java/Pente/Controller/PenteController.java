package Pente.Controller;

import Pente.Model.PenteBoard;
import Pente.Model.PenteTile;
import Pente.Service.ColorScale;
import Pente.Service.GrayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Random;

@Controller
public class PenteController {

    @Autowired
    PenteBoard penteBoard = new PenteBoard();
    GrayService grayService = new GrayService();
    ColorScale colorScale = grayService.fetchGrayColorData();
    ColorScale offsetScale = grayService.fetchOffsetColor(colorScale.getNumericValue());

    Random rand = new Random();
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

    @RequestMapping("/rules")
    public String displayRules(ModelMap modelMap){
        modelMap = cycleModelMaps(modelMap);
        return "rules";
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

        //TODO make sure the tileID is good enough


        if(penteBoard.getTheBoard().size() < 19){
            penteBoard.newBoard();
        } else {
            if(numMoves == 0){
                if(rand.nextInt(2) == 0){
                    penteBoard.setATile("H",9,9);
                    numMoves++;
                    penteBoard.makeComputerMove();
                    numMoves++;
                } else {
                    penteBoard.setATile("C",9,9);
                    numMoves++;
                }
            } else {
                if(penteBoard.checkTileFree(tileID) && !computerWon && !humanWon &&penteBoard.checkTileAvailable()){
                    //TODO make your tileID convert to a int, and then only used as a int elsewhere.
                    penteBoard.applyHumanMove(tileID);
                    penteBoard.checkForCaptures("H", tileID);
                    humanWon = penteBoard.checkForWinner("H");
                    numMoves++;
                    if(penteBoard.checkTileAvailable() && !humanWon){
                        penteBoard.makeComputerMove();
                        computerWon = penteBoard.checkForWinner("C");
                        numMoves++;
                    }
                }
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
        modelMap.put("humanCaptures", penteBoard.getHumanCaptures());
        modelMap.put("computerCaptures", penteBoard.getComputerCaptures());

        modelMap.put("colorScale",colorScale);
        modelMap.put("offsetScale",offsetScale);



        return modelMap;
    }


}
