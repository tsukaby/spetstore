package com.github.j5ik2o.spetstore.domain.purchase

import com.github.j5ik2o.spetstore.domain.customer._
import com.github.j5ik2o.spetstore.domain.basic._
import com.github.j5ik2o.spetstore.domain.pet._
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import com.github.j5ik2o.spetstore.infrastructure.support.EntityIOContextOnMemory
import com.github.j5ik2o.spetstore.domain.customer.CustomerConfig
import com.github.j5ik2o.spetstore.domain.pet.PetTypeId
import com.github.j5ik2o.spetstore.domain.customer.CustomerId
import com.github.j5ik2o.spetstore.domain.pet.Pet
import com.github.j5ik2o.spetstore.domain.customer.CustomerProfile
import com.github.j5ik2o.spetstore.domain.basic.PostalAddress
import com.github.j5ik2o.spetstore.domain.pet.PetId
import com.github.j5ik2o.spetstore.domain.customer.Customer
import com.github.j5ik2o.spetstore.domain.basic.Contact

class OrderSpec extends Specification {

  "order" should {
    val pet = Pet(
      id = PetId(),
      petTypeId = PetTypeId(),
      sexType = SexType.Male,
      name = "ぽち",
      description = None,
      price = BigDecimal(100),
      supplierId = SupplierId()
    )
    "add orderItem" in {
      val order = Order(
        status = OrderStatus.Pending,
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List.empty
      )
      val orderItem = OrderItem(pet, 1)
      val newOrder = order.addOrderItem(orderItem)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beTrue
      newOrder.sizeOfOrderItems must_== 1
    }
    "remove orderItem" in {
      val orderItem = OrderItem(pet, 1)
      val order = Order(
        status = OrderStatus.Pending,
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      val newOrder = order.removeOrderItem(orderItem)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beFalse
      newOrder.sizeOfOrderItems must_== 0
    }
    "remove orderItem by index" in {
      val orderItem = OrderItem(pet, 1)
      val order = Order(
        status = OrderStatus.Pending,
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      val newOrder = order.removeOrderItemByIndex(0)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beFalse
      newOrder.sizeOfOrderItems must_== 0
    }
    "get totalPrice" in {
      val orderItem = OrderItem(pet, 1)
      val order = Order(
        status = OrderStatus.Pending,
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      order.totalPrice must_== BigDecimal(100)
    }
    "apply from cart" in {
      val customer = Customer(
        id = CustomerId(),
        status = CustomerStatus.Enabled,
        name = "Junichi Kato",
        profile = CustomerProfile(
          postalAddress = PostalAddress(
            ZipCode("100", "1000"),
            Pref.東京都,
            "目黒区下目黒",
            "1-1-1"
          ),
          contact = Contact("hoge@hoge.com", "00-0000-0000")
        ),
        config = CustomerConfig(
          loginName = "fugafuga",
          password = "hogehoge",
          favoriteCategoryId = None
        )
      )
      val cart = Cart(
        id = CartId(),
        customerId = customer.id,
        cartItems = List(
          CartItem(pet, 1, false)
        )
      )
      implicit val ar = CustomerRepository.ofMemory(Map(customer.id -> customer))
      implicit val ctx = EntityIOContextOnMemory
      val order = Order.fromCart(cart).get
      order.orderItems.exists(e => e.pet == pet && e.quantity == 1) must beTrue
    }

  }

}
