import Pente.Model.PenteBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PenteBoardTest {

    @Test
    void shouldMakeNewBoard(){
        PenteBoard penteBoard = new PenteBoard();
        penteBoard.newBoard();
        int testSize = penteBoard.getTheBoard().size();
        assertEquals(19, testSize);
    }


    @Test
    void doesMoveSetupComputerForCaptureInLine() {
        PenteBoard penteBoard = new PenteBoard();

        penteBoard.newBoard();
        penteBoard.setATile("H",9,9);
        penteBoard.setATile("C",10,9);


        boolean result = penteBoard.doesMoveSetupComputerForCapture(11,9);
        assertEquals(true, result);
    }

    @Test
    void doesMoveSetupComputerForCaptureInPocket() {
        PenteBoard penteBoard = new PenteBoard();

        penteBoard.newBoard();
        penteBoard.setATile("H",9,9);
        penteBoard.setATile("C",11,9);


        boolean result = penteBoard.doesMoveSetupComputerForCapture(10,9);
        assertEquals(true, result);
    }
}