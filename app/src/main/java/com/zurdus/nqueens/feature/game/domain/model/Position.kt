package com.zurdus.nqueens.feature.game.domain.model

import com.zurdus.nqueens.domain.NQueensRules

internal data class Position(
    val boardSize: Int,
    val queenSquares: Set<BoardSquare> = emptySet(),
) {
    init {
        require(boardSize in NQueensRules.supportedBoardSizes) {
            "Board size must be between ${NQueensRules.MINIMUM_BOARD_SIZE} " +
                "and ${NQueensRules.MAXIMUM_BOARD_SIZE}."
        }
        require(queenSquares.size <= boardSize) {
            "A position cannot contain more queens than its board size."
        }
        require(queenSquares.all { square -> square.isOnBoard(boardSize) }) {
            "Every queen must be placed on the board."
        }
    }

    val conflictingQueenSquares: Set<BoardSquare>
        get() = queenSquares
            .filter { queenSquare ->
                queenSquares.any { otherSquare ->
                    queenSquare != otherSquare && queenSquare.attacks(otherSquare)
                }
            }
            .toSet()

    val queensLeft: Int
        get() = boardSize - queenSquares.size

    val isSolved: Boolean
        get() = queensLeft == 0 && conflictingQueenSquares.isEmpty()
}
