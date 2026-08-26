package com.zurdus.nqueens.feature.game.domain.model

internal data class NQueensGame(
    val boardSize: Int,
    val queens: Set<BoardPosition> = emptySet(),
) {
    init {
        require(boardSize >= MINIMUM_BOARD_SIZE) {
            "Board size must be at least $MINIMUM_BOARD_SIZE."
        }
        require(queens.size <= boardSize) {
            "A game cannot contain more queens than its board size."
        }
        require(queens.all { position -> position.isOnBoard(boardSize) }) {
            "Every queen must be placed on the board."
        }
    }

    val conflictingQueens: Set<BoardPosition>
        get() = queens
            .filter { queen ->
                queens.any { other ->
                    queen != other && queen.attacks(other)
                }
            }
            .toSet()

    val queensLeft: Int
        get() = boardSize - queens.size

    val isSolved: Boolean
        get() = queensLeft == 0 && conflictingQueens.isEmpty()
}

private const val MINIMUM_BOARD_SIZE = 4
