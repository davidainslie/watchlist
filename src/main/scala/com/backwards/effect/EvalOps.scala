package com.backwards.effect

import cats.{Eval, Monad, MonadError}
import cats.implicits._

/**
  * Copied from [[https://github.com/atnos-org/eff/blob/master/shared/src/main/scala/org/atnos/eff/EvalEffect.scala#L73]]
  */
trait EvalOps {
  implicit final val monadErrorEval: MonadError[Eval, Throwable] = new MonadError[Eval, Throwable] {
    private val m: Monad[Eval] = Eval.catsBimonadForEval

    def pure[A](x: A): Eval[A] =
      m.pure(x)

    def flatMap[A, B](fa: Eval[A])(f: A => Eval[B]): Eval[B] =
      m.flatMap(fa)(f)

    def tailRecM[A, B](a: A)(f: A => Eval[Either[A, B]]): Eval[B] =
      m.tailRecM(a)(f)

    def raiseError[A](e: Throwable): Eval[A] =
      Eval.later(throw e)

    def handleErrorWith[A](fa: Eval[A])(f: Throwable => Eval[A]): Eval[A] =
      Eval.later {
        try Eval.now(fa.value)
        catch { case t: Throwable => f(t) }
      }.flatten
  }
}

object EvalOps extends EvalOps