package com.github.j5ik2o.spetstore.domain.model.purchase

import com.github.j5ik2o.spetstore.domain.infrastructure.support.Identifier

case class OrderItemId(value: Long)
  extends Identifier[Long]
