package ru.tbank.education.school.lesson2

//класс 1, композиция: содержит Seat
class Cinema {
    private val seats = mutableListOf<Seat>()
    init {
        for (i in 1..50) {
            seats.add(Seat(i, false))
        }
    }

    fun getSeat(seatId: Int): Seat? {
        return seats.find { it.id == seatId }
    }

    fun showStatistics() {
        println("number of sold tickets: ${seats.size}")
    }
}

//класс 2 - базовый, абстрактный, внутри модификатор доступа protected, open функция, ссылка на другой класс (Seat)
abstract class Ticket {
    protected var basePrice: Int = 100
    val id: Int
    val seat: Seat
    val type: String
    var isBought: Boolean = false
    //основной конструктор
    constructor(id: Int, seat: Seat, price: Int) {
        this.id = id
        this.seat = seat
        this.basePrice = price
        this.type = "standard"
    }
    //дополнительный конструктор
    constructor(id: Int, seat: Seat, price: Int, type: String) {
        this.id = id
        this.seat = seat
        this.basePrice = price
        this.type = type
    }

    open val finalPrice: Int
        get() = basePrice
    open fun buyTicket(cinema: Cinema): Boolean {
        if (seat.isBusy) {
            println("seat ${seat.id} is already occupied")
            return false
        }
        if (isBought) {
            println("ticket $id is already bought")
            return false
        }
        isBought = true
        println("you bought ticket $id")
        return true
    }

    open fun getTicketInfo(): String {
        return "ticket $id, type: $type, your seat: ${seat.id}"
    }
}

//класс 3 - наследник базового, super, override
class StandardTicket : Ticket {
    constructor(id: Int, seat: Seat, price: Int) : super(id, seat, price)
    override fun buyTicket(cinema: Cinema): Boolean {
        val result = super.buyTicket(cinema)
        if (result) {
            println("standard ticket")
        }
        return result
    }
    override fun getTicketInfo(): String {
        val baseInfo = super.getTicketInfo()
        return "$baseInfo standard ticket"
    }
}

//класс 4 - наследник базового, super, override, внутри private var
class VipTicket : Ticket {
    private var vipLevel: Int = 1
    override val finalPrice: Int
        get() = (basePrice * 2 * vipLevel).also {
            println("price for vip: $basePrice * 2 * $vipLevel = $it")
        }

    constructor(id: Int, seat: Seat, price: Int) : super(id, seat, price, "vip")
    override fun buyTicket(cinema: Cinema): Boolean {
        val result = super.buyTicket(cinema)
        if (result) {
            println("vip ticket")
        }
        return result
    }
    override fun getTicketInfo(): String {
        val baseInfo = super.getTicketInfo()
        return "$baseInfo vip ticket, vip level = $vipLevel"
    }
}

//класс 5 - наследник базового, super, override
class GroupTicket : Ticket {
    val groupSize: Int
    override val finalPrice: Int
        get() = basePrice * groupSize
    constructor(id: Int, seat: Seat, price: Int, groupSize: Int) : super(id, seat, price, "group") {
        this.groupSize = groupSize
    }
    override fun buyTicket(cinema: Cinema): Boolean {
        val result = super.buyTicket(cinema) // SUPER вызов родительского метода
        if (result) {
            println("group ticket")
        }
        return result
    }
    override fun getTicketInfo(): String {
        val baseInfo = super.getTicketInfo()
        return "$baseInfo group ticket for $groupSize people"
    }
}


//класс 6 - data class, внутри геттер и сеттер
data class Seat(
    val id: Int,
    private var _isBusy: Boolean
) {
    val isBusy: Boolean
        get() {
            println("seat $id: $_isBusy")
            return _isBusy
        }
    var status: String = "free"
        set(value) {
            println("seat $id: $field => $value")
            field = value
            _isBusy = value == "occupied"
        }

    val description: String
        get() = "seat $id (${if (_isBusy) "occupied" else "free"})"
}

fun main() {
    val cinema = Cinema()
    println("cinema\n")

    val seat1 = cinema.getSeat(1)!!
    val seat2 = cinema.getSeat(2)!!
    val seat3 = cinema.getSeat(3)!!

    val standard = StandardTicket(1, seat1, 100)
    val vip = VipTicket(2, seat2, 200)
    val group = GroupTicket(3, seat3, 100, 4)

    standard.buyTicket(cinema)
    vip.buyTicket(cinema)
    group.buyTicket(cinema)

    println(standard.getTicketInfo())
    println(vip.getTicketInfo())
    println(group.getTicketInfo())

    println("standard final price: ${standard.finalPrice}")
    println("vip final price: ${vip.finalPrice}")
    println("group final price: ${group.finalPrice}")

    println(seat1.description)
    seat1.status = "occupied"
    println(seat1.description)

    cinema.showStatistics()
}