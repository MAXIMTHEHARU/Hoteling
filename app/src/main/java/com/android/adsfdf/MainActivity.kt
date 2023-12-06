package com.android.adsfdf

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.lang.NullPointerException
import java.time.Duration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

//예외 처리의 활용
//
fun String.isNumeric(): Boolean {
    return try {
        this.toInt()
        true
    } catch (e: Exception) {
        false
    }
}

@SuppressLint("NewApi")
fun withInDate(stx: LocalDate, etx: LocalDate, check: LocalDate): Boolean {
    return !check.isBefore(stx) && check.isBefore(etx)
}

@SuppressLint("NewApi")
fun main() {
    val dateFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
    val peoples = arrayListOf<People>()
    val resvHistory = arrayListOf<ResvHistory>()
    while (true) {
        println("호텔예약 프로그램 입니다.")
        println(
            "[메뉴]\n1. 방예약, 2. 예약목록 출력, 3. 예약목록 (정렬) 출력, 4. 시스템 종료, 5. 금액 입금-출금 내역 목록 출력 6. 예약 변경/취소"
        )
        val menu = readln()
        if (!menu.isNumeric()) {
            System.err.println("메뉴 입력은 숫자만 가능합니다")
            continue
        }
        when (menu.toInt()) {
            1 -> {
                val randomResvMoney = (10000..50000).random()
                println("예약자분의 성함을 입력해주세요")
                val name = readln()
                var roomNum: Int
                while (true) {
                    println("예약할 방번호를 입력해주세요")
                    val room = readln()
                    if (!room.isNumeric()) {
                        System.err.println("방번호 입력은 숫자만 가능합니다")
                        continue
                    }
                    if (room.toInt() in 100..999) {
                        roomNum = room.toInt()
                        break
                    } else {
                        System.err.println("올바르지 않은 방번호 입니다. 방번호는 100~999 영역 이내입니다.")
                        continue
                    }
                }
                var checkIn: LocalDate? = null
                while (true) {
                    println("체크인 날짜를 입력해주세요 표기형식. 20230631")
                    val checkDate = readln()
                    try {
                        val temp = LocalDate.from(dateFormat.parse(checkDate))
                        if (temp.isBefore(LocalDate.now())) {
                            println("체크인은 지난날은 선택할 수 없습니다.")
                            continue
                        }
                        val existRooms = resvHistory.filter { it.reserveRoom == roomNum }
                        if (existRooms.isNotEmpty()) {
                            for (room in existRooms) {
                                if (!withInDate(room.checkIn, room.checkOut, temp)) {
                                    checkIn = temp
                                } else {
                                    println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
                                    break
                                }
                            }
                        } else {
                            checkIn = temp
                        }

                        if (checkIn == null) {
                            continue
                        }
                        break
                    } catch (e: DateTimeParseException) {
                        System.err.println("올바르지 않은 포맷입니다 다시 입력해주세요")
                        continue
                    }
                }

                var checkOut: LocalDate? = null
                while (true) {
                    println("체크아웃 날짜를 입력해주세요 표기형식. 20230631")
                    val checkDate = readln()
                    try {
                        val temp = LocalDate.from(dateFormat.parse(checkDate))
                        if (temp.isBefore(checkIn) || temp.isEqual(checkIn)) {
                            println("체크인 날짜보다 이전이거나 같을 수는 없습니다.")
                            continue
                        }
                        val existRooms = resvHistory.filter { it.reserveRoom == roomNum }
                        if (existRooms.isNotEmpty()) {
                            for (room in existRooms) {
                                if (!withInDate(room.checkIn, room.checkOut, temp)) {
                                    checkOut = temp
                                } else {
                                    println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
                                    break
                                }
                            }
                        } else {
                            checkOut = temp
                        }

                        if (checkOut == null) {
                            continue
                        } else {
                            break
                        }
                    } catch (e: DateTimeParseException) {
                        System.err.println("올바르지 않은 포맷입니다 다시 입력해주세요")
                        continue
                    }
                }
                var people = peoples.find { it.name == name }
                if (people == null) {
                    people = People(name = name)
                    peoples.add(people)
                }

                if (people.money.outBalance(randomResvMoney, "reserve")) {
                    ResvHistory(
                        people = people,
                        checkIn = checkIn!!,
                        checkOut = checkOut!!,
                        reserveRoom = roomNum,
                        resvMoney = randomResvMoney
                    ).run {
                        resvHistory.add(this)
                    }

                    println("호텔 예약이 완료되었습니다.")
                } else {
                    println("잔액이 부족하여 예약에 실패했습니다.")
                }
            }

            2 -> {
                println("호텔 예약자 목록입니다.")
                for (i in 0 until resvHistory.size) {
                    val item = resvHistory[i]
                    println("${i + 1}. 사용자: ${item.people.name}, 방번호: ${item.reserveRoom}, 체크인: ${item.checkIn}, 체크아웃: ${item.checkOut}")
                }
            }

            3 -> {
                println("호텔 예약자 목록입니다. (정렬완료)")
                val sortedArray = resvHistory.sortedBy { item -> item.people.name }
                for (i in sortedArray.indices) {
                    val item = sortedArray[i]
                    println("${i + 1}. 사용자: ${item.people.name}, 방번호: ${item.reserveRoom}, 체크인: ${item.checkIn}, 체크아웃: ${item.checkOut}")
                }
            }

            4 -> {
                println("호텔 영업을 종료 합니다.")
                break
            }

            5 -> {
                println("조회하실 사용자 이름을 입력하세요")
                val name = readln()
                with(peoples.find { it.name == name }) {
                    if (this == null) {
                        println("예약된 사용자를 찾을수 없습니다.")
                    } else {
                        this.money.printHistory()
                    }
                }
            }

            6 -> {
                println("예약을 변경할 사용자 이름을 입력하세요")
                val name = readln()
                with(resvHistory.filter { it.people.name == name }) {
                    if (this.isEmpty()) {
                        println("사용자 이름으로 예약된 목록을 찾을수 없습니다.")
                    } else {
                        var isComplete = false
                        while (!isComplete) {
                            println("$name 님이 예약한 목록입니다. 변경하실 예약번호를 입력해주세요 (탈출은 exit입력)")
                            for (i in 0 until this.size) {
                                val item = this[i]
                                println("${i + 1}. 방번호: ${item.reserveRoom}, 체크인: ${item.checkIn}, 체크아웃: ${item.checkOut}")
                            }
                            val num = readln()
                            if (!num.isNumeric()) {
                                if (num == "exit") {
                                    break
                                } else {
                                    System.err.println("입력은 숫자만 가능합니다")
                                    continue
                                }
                            } else {
                                if (num.toInt() in 1 until this.size + 1) {
                                    val item = this[num.toInt() - 1]
                                    println("해당 예약을 어떻게 하시겠어요 1. 변경 2. 취소 / 이외 번호. 메뉴로 돌아가기")
                                    while (true) {
                                        val modiMenu = readln()
                                        if (!modiMenu.isNumeric()) {
                                            System.err.println("입력은 숫자만 가능합니다")
                                            continue
                                        } else {
                                            when (modiMenu.toInt()) {
                                                1 -> {
                                                    var checkIn: LocalDate? = null
                                                    while (true) {
                                                        println("변경할 체크인 날짜를 입력해주세요 표기형식. 20230631")
                                                        val checkDate = readln()
                                                        try {
                                                            val temp = LocalDate.from(dateFormat.parse(checkDate))
                                                            if (temp.isBefore(LocalDate.now())) {
                                                                println("체크인은 지난날은 선택할 수 없습니다.")
                                                                continue
                                                            }
                                                            val existRooms =
                                                                resvHistory.filter { it.reserveRoom == item.reserveRoom }
                                                            if (existRooms.isNotEmpty()) {
                                                                for (room in existRooms) {
                                                                    if (!withInDate(
                                                                            room.checkIn,
                                                                            room.checkOut,
                                                                            temp
                                                                        )
                                                                    ) {
                                                                        checkIn = temp
                                                                    } else {
                                                                        println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
                                                                        break
                                                                    }
                                                                }
                                                            } else {
                                                                checkIn = temp
                                                            }

                                                            if (checkIn == null) {
                                                                continue
                                                            }
                                                            break
                                                        } catch (e: DateTimeParseException) {
                                                            System.err.println("올바르지 않은 포맷입니다 다시 입력해주세요")
                                                            continue
                                                        }
                                                    }

                                                    var checkOut: LocalDate? = null
                                                    while (true) {
                                                        println("변경할 체크아웃 날짜를 입력해주세요 표기형식. 20230631")
                                                        val checkDate = readln()
                                                        try {
                                                            val temp = LocalDate.from(dateFormat.parse(checkDate))
                                                            if (temp.isBefore(checkIn) || temp.isEqual(checkIn)) {
                                                                println("체크인 날짜보다 이전이거나 같을 수는 없습니다.")
                                                                continue
                                                            }
                                                            val existRooms =
                                                                resvHistory.filter { it.reserveRoom == item.reserveRoom }
                                                            if (existRooms.isNotEmpty()) {
                                                                for (room in existRooms) {
                                                                    if (!withInDate(
                                                                            room.checkIn,
                                                                            room.checkOut,
                                                                            temp
                                                                        )
                                                                    ) {
                                                                        checkOut = temp
                                                                    } else {
                                                                        println("해당 날짜에 이미 방을 사용중입니다. 다른날짜를 입력해주세요")
                                                                        break
                                                                    }
                                                                }
                                                            } else {
                                                                checkOut = temp
                                                            }

                                                            if (checkOut == null) {
                                                                continue
                                                            } else {
                                                                break
                                                            }
                                                        } catch (e: DateTimeParseException) {
                                                            System.err.println("올바르지 않은 포맷입니다 다시 입력해주세요")
                                                            continue
                                                        }
                                                    }

                                                    item.checkIn = checkIn!!
                                                    item.checkOut = checkOut!!
                                                    isComplete = true
                                                    println("예약 변경이 완료 되었습니다.")
                                                    break
                                                }

                                                2 -> {
                                                    println("[취소 유의사항]")
                                                    println("체크인 3일 이전 취소 예약금 환불 불가")
                                                    println("체크인 5일 이전 취소 예약금의 30% 환불")
                                                    println("체크인 7일 이전 취소 예약금의 50% 환불")
                                                    println("체크인 14일 이전 취소 예약금의 80% 환불")
                                                    println("체크인 30일 이전 취소 예약금의 100% 환불")

                                                    val diff = Duration.between(
                                                        LocalDate.now().atStartOfDay(),
                                                        item.checkIn.atStartOfDay()
                                                    )
                                                    val diffDays = diff.toDays().toInt()
                                                    val refundMoney = if (diffDays <= 3) {
                                                        (item.resvMoney * 0)
                                                    } else if (diffDays <= 5) {
                                                        (item.resvMoney * 0.30).toInt()
                                                    } else if (diffDays <= 7) {
                                                        (item.resvMoney * 0.50).toInt()
                                                    } else if (diffDays <= 14) {
                                                        (item.resvMoney * 0.80).toInt()
                                                    } else {
                                                        item.resvMoney
                                                    }
                                                    item.people.money.inBalance(refundMoney, "refund")
                                                    resvHistory.remove(item)
                                                    isComplete = true
                                                    println("취소가 완료되었습니다.")
                                                    break
                                                }

                                                else -> {
                                                    break
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    println("범위에 없는 예약번호 입니다.")
                                }
                            }
                        }
                    }
                }
            }

            else -> {
                println("올바른 메뉴 번호를 입력해주세요")
            }
        }
    }
}