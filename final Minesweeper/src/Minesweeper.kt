class Cell (var content: Char = '.', var display: Char = '.', var revealed: Boolean = false)
class Ground {
    var field = arrayOf<Array<Cell>>()
    var mines = 0
    var gameContinue = true
    var out = false
    init {
        for (i in 0..8) {
            var array = arrayOf<Cell>()
            for (j in 0..8) {
                array += Cell()
            }
            field += array
        }
        print("How many mines do you want on the field?")
        mines = readLine()!!.toInt()
        plant()
        for (i in 0..8) {
            for (j in 0..8) count(i, j)
        }
    }
    private fun count(r: Int, c: Int) {
        var temp = 48
        for(i in -1..1) {
            for (j in -1..1) {
               if (r + i >= 0 && c + j >= 0 && r + i <= 8 && c + j <= 8) {
                   if (field[r + i][c + j].content == 'X') temp++
               }
            }
        }
        if (field[r][c].content != 'X') {
            if (temp == 48) field[r][c].content = '.'
            else field[r][c].content = temp.toChar()
        }
    }
    fun showField(notOver: Boolean) {
        if (!notOver) {
            for (i in 0..8) {
                for (j in 0..8) {
                    if (field[i][j].content == 'X') field[i][j].display = field[i][j].content
                }
            }
        }
        println("")
        println(" │123456789│")
        println("—│—————————│")
        for (i in 0..8) {
            print("${i + 1}|")
            for (j in 0..8) {
                print(field[i][j].display)
            }
            println("|")
        }
        println("—│—————————│")
    }
}
fun Ground.plant() {
    val list = mutableListOf<Int>()
    for (i in 0..80) list.add(i)
    repeat (mines) {
        val temp = list.random()
        val i = temp / 9
        val j = temp % 9
        field[i][j].content = 'X'
        list.remove(temp)
    }
}
fun Ground.marker(take: String) {
    val x = take.first().toInt() - 49
    val y = take[2].toInt() - 49
    val func = take.substringAfterLast(" ")
    if (func.toLowerCase() == "mine") {
        if (field[y][x].display == '.') {
            field[y][x].display = '*'
        }
        else if (field[y][x].display == '*') {
            field[y][x].display = '.'
        }
    } else if (func.toLowerCase() == "free") {
        if (!field[y][x].revealed) {
            when (field[y][x].content) {
                'X' -> {
                    gameContinue = false
                    out = true
                }
                '.' -> floodOut(y, x)
                else -> {
                    field[y][x].display = field[y][x].content
                    field[y][x].revealed = true
                }
            }
        }
    }
}
fun Ground.floodOut (y: Int, x: Int) {
    if (x > 8 || x < 0 || y > 8 || y < 0) return
    if (field[y][x].content.isDigit()) {
        field[y][x].display = field[y][x].content
        field[y][x].revealed = true
    }
    if (field[y][x].content == '.' && !field[y][x].revealed) {
        field[y][x].display = '/'
        field[y][x].revealed = true
        for(i in -1..1) {
            for (j in -1..1) {
                if ((y + i >= 0 && x + j >= 0 && y + i <= 8 && x + j <= 8) && !(y + i == y && x + j == x)) {
                    floodOut(y + i, x + j)
                }
            }
        }
    }
}
fun Ground.checker() {
    for (i in 0..8) {
        for (j in 0..8) {
            if (!field[i][j].revealed && field[i][j].content != 'X') return
        }
    }
    gameContinue = false
}
fun main () {
    val game = Ground()
    game.showField(true)
    while(game.gameContinue) {
        print("Set/unset mines marks or claim a cell as free:")
        val take: String = readLine()!!
        game.marker(take)
        game.checker()
        game.showField(game.gameContinue)
    }
    if (game.out) println("You stepped on a mine and failed!") else print("Congratulations! You found all mines!")
}