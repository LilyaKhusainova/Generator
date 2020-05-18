package ru.hlil

import java.sql.*
import java.util.*


fun main() {
    var columnCount: Int//столбцы
    var recordCount: Int//строки

    val random = Random()
    val sc = Scanner(System.`in`)

    println("Введите название таблицы")
    val name: String = sc.nextLine()

    println("Введите количество столбцов")
    columnCount = sc.nextInt()//вводим с клавиатуры размерность
    //columnCount = random.nextInt(20000) + 100//заполнение рандомно

    println("Введите количество строк")
    recordCount = sc.nextInt()
    //recordCount =  random.nextInt(20000) + 100
    println("Стобцов: $columnCount")
    println("Строк: $recordCount")


    val dt1 = "drop table if exists `$name`"
    val ins1 = "INSERT INTO `$name` VALUES (?, ?, ?)"
    val ct1 = "CREATE TABLE `$name` " +
            "(value int," +
            "record_index int," +
            "column_index int)"

    /*

       * внутренний по номеру столбца. Метод setInt(i, v) устанавливает на место знака вопроса
       * i-го по счету значение v.
       * Поскольку строк может быть и 10^8, не рационально (по врермени) для каждой строчки делать отдельный запрос
       * на вставку и коммитить этот запрос. Вместо этого они складываются вместе методом addBatch() и затем
       * вставляются все вместе методом executeBatch().
       * После этого происходит "commit" - изменения сохраняются в бд.
       * */

    val c: Connection = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/Client_Server?serverTimezone=UTC",
        "Liliya",
        "12345"
    )
    val ins: PreparedStatement = c.prepareStatement(ins1)
    val s: Statement = c.createStatement()
    s.execute(dt1)
    s.execute(ct1)
    c.autoCommit = false
    val total = columnCount*recordCount
    var progress: Long = 0

    for (recordIndex in 1..recordCount) {
        ins.clearBatch()
        ins.setInt(2, recordIndex)
        for (columnIndex in 1..columnCount) {
            ins.setInt(3, columnIndex)
            ins.setInt(1, random.nextInt(1000) - 500)
            ins.addBatch()
        }
        ins.executeBatch()
        c.commit()
        //вывод прогресса генерации
        val currentProgress =
            Math.round((recordIndex * columnCount).toDouble() / total.toDouble() * 100)
        if (currentProgress > progress) {
            progress = currentProgress
            print("$progress%")
        }
    }
    val result1: ResultSet = s.executeQuery("SELECT COUNT(*) FROM `$name`")
    result1.next()
    println("\nВставлено элементов: " + result1.getInt(1))



}


