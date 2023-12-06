package com.android.adsfdf

class Bank {
    private var amount: Int = 0
    private val histories = arrayListOf<String>()

    init {
        val initAmount = (100000..500000).random()
        inBalance(initAmount, "init")
    }

    override fun toString(): String {
        return "$amount"
    }

    fun outBalance(money: Int, type: String): Boolean {
        val tempAmount = amount - money
        return if (tempAmount < 0) {
            false
        } else {
            amount -= money
            saveHistory("out", money, type)
            true
        }
    }

    fun inBalance(money: Int, type: String): Boolean {
        amount += money
        saveHistory("in", money, type)
        return true
    }

    private fun saveHistory(oper: String, money: Int, type: String) {
        var history = ""
        when (oper) {
            "in" -> {
                when (type) {
                    "init" -> {
                        history = "초기 금액으로 $money 원 입금되었습니다."
                    }
                    "refund" -> {
                        history = "환불금으로 $money 원 입금되었습니다."
                    }
                    "modify" -> {
                        history = "예약 변경으로 $money 원 입금되었습니다."
                    }
                }
            }

            "out" -> {
                when (type) {
                    "reserve" -> {
                        history = "예약금으로 $money 원 출금되었습니다."
                    }
                    "modify" -> {
                        history = "예약 변경으로 $money 원 출금되었습니다."
                    }
                }
            }
        }
        this.histories.add(history)
    }

    fun printHistory() {
        for (i in 0 until histories.size) {
            println("${i + 1}. ${histories[i]}")
        }
    }
}

