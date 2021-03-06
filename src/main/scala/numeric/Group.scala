package numeric

trait Group[@specialized A] extends Loop[A] 
                               with Monoid[A] 
                               with Multiplicative[A,Group[A],Group.Multiplication[A]]
                               with Additive[A,Group[A],Group.Addition[A]] {
  def inverse(a: A): A
  def over(a: A, b: A): A = this(a, inverse(b))
  def under(a: A, b: A): A = this(inverse(a), b)
  override def dual: Group[A] = new Group.Dual[A] {
    override def dual: Group[A] = Group.this
  }
  def *[B](that: Group[B]) = new Group.Product[A,B] {
    def _1: Group[A] = Group.this
    def _2: Group[B] = that
  }
  implicit override def multiplication(a: A): Group.Multiplication[A] = new Group.Multiplication[A] {
    def lhs: A = a
    def numeric: Group[A] = Group.this
  }
  override def addition(a: A): Group.Addition[A] = new Group.Addition[A] {
    def lhs: A = a
    def numeric: Group[A] = Group.this
  }
}

object Group {
  def apply[A](
    f:(A,A) => A,
    inv: A => A,
    id: A
  ) : Group[A] = new Group[A] {
    def apply(a: A, b: A): A = f(a,b)
    def inverse(a: A) = inv(a)
    def e: A = id
  }
  trait Multiplication[@specialized A] extends Loop.Multiplication[A] 
                                          with Ops[A,Group[A]] {
    def inverse: A = numeric.inverse(lhs)
  }
  trait Addition[@specialized A] extends Loop.Addition[A] 
                                    with Ops[A,Group[A]] {
    def unary_-(): A = numeric.inverse(lhs)
    def negate: A = numeric.inverse(lhs)
  }
  trait Dual[@specialized A] extends Loop[A] with Monoid.Dual[A] with Group[A] {
    def inverse(a: A) = dual.inverse(a)
  }
  trait Product[A,B] extends Loop.Product[A,B] 
                        with Monoid.Product[A,B] 
                        with Group[(A,B)] 
                        with ProductLike[Group,A,B] {
    def inverse(a: (A,B)): (A,B) = (_1.inverse(a._1),_2.inverse(a._2))
    override def under(a: (A,B), b: (A,B)): (A,B) =  (_1.under(a._1,b._1), _2.under(a._2,b._2))
    override def over(a: (A,B), b: (A,B)): (A,B) = (_1.over(a._1,b._1), _2.over(a._2,b._2))
    override def dual: Product[A,B] = new Product.Dual[A,B] {
      override def dual: Product[A,B] = Product.this
    }
  }
  object Product {
    trait Dual[A,B] extends Product[A,B] {
      def _1: Group[A] = dual._1.dual
      def _2: Group[B] = dual._2.dual
    }
  }
}
