package com.informatique.electronicmeetingsplatform.business.base

import com.informatique.electronicmeetingsplatform.business.BusinessState

abstract class BaseUseCase<in P, R> {
    abstract suspend operator fun invoke(parameters: P): BusinessState<R>
}

abstract class BaseUseCaseWithoutParams<R> {

    abstract suspend operator fun invoke(): BusinessState<R>
}

