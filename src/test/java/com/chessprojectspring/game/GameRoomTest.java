package com.chessprojectspring.game;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import org.junit.jupiter.api.Test;
import java.util.StringTokenizer;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class GameRoomTest {

    Board board = new Board();

    @Test
    public void moveTest() {
        System.out.println("--------------------------");
        System.out.println(board);

        board.doMove(new Move(Square.G2, Square.G4));

        System.out.println("--------------------------");
        System.out.println(board);

        System.out.println("--------------------------");
        board.doMove(new Move(Square.G2, Square.G4));
    }

    @Test
    public void doMoveTest() {
        System.out.println("--------------------------");
        System.out.println(board);

        System.out.println("--------------------------");
        System.out.println(board.doMove("g2g4"));

        System.out.println("--------------------------");
        System.out.println(board);

        System.out.println("--------------------------");
        System.out.println(board.doMove("h7h5"));
    }

    // 단위테스트
    @Test
    public void unitTest() {
        // String input = "h2h4 h7h5 g2g4 g7g5 h4g5 h5h4 h1h3 h8h7 h3a3 h4h3 a3a4 h3h2 a4a5 h2h1=R"
        //             + " a5a4 h1g1 f1h3"; //a3a4 f1e1"; // 프로모션 // e1f1 g1f1

        String input = "e2e4 f7f5 d1h5";

        StringTokenizer tokenizer = new StringTokenizer(input, " ");

        int i = 0;

        while(tokenizer.hasMoreTokens()) {
            System.out.println("--------------------------");
            System.out.println("Move " + ++i + " : " + board.doMove(tokenizer.nextToken()));
            System.out.println("--------------------------");
            System.out.println(board);
        }

        System.out.println("--------------------------");
//        System.out.println(board.isMoveLegal(new Move(Square.A3, Square.A4), false));
        System.out.println(board.isMated());
        System.out.println(board.isStaleMate());
        //이게 뭐죠
        // 왕이 안전해질수 있는 상태가 없으면 상대 승리지 왜 무승부예요
        // 그럼 체크메이트도 내 왕이 안전해질수 있는 상태가 없는거잖아요 
    }

}
