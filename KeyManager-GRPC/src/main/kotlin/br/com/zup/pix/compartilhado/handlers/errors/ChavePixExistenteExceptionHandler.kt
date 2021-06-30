package br.com.zup.pix.compartilhado.handlers.errors


import br.com.zup.pix.compartilhado.handlers.ExceptionHandler
import br.com.zup.pix.compartilhado.handlers.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixExistenteExceptionHandler: ExceptionHandler<ChavePixExistenteException> {
    override fun handle(e: ChavePixExistenteException): StatusWithDetails {
        return StatusWithDetails(Status.ALREADY_EXISTS
            .withDescription(e.message)
            .withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixExistenteException
    }
}

