package br.com.zup.pix.compartilhado.handlers.errors

import br.com.zup.pix.compartilhado.handlers.ExceptionHandler
import br.com.zup.pix.compartilhado.handlers.StatusWithDetails
import io.grpc.Status

class ChavePixNaoEncontradaExceptionHandler : ExceptionHandler<ChavePixNaoEncontradaException> {
    override fun handle(e: ChavePixNaoEncontradaException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixNaoEncontradaException
    }
}