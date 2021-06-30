package br.com.zup.pix.cadastrachavepix

import br.com.zup.pix.ChavePixRequest
import br.com.zup.pix.ChavePixResponse
import br.com.zup.pix.DesafioPixServiceGrpc
import br.com.zup.pix.compartilhado.handlers.ErrorHandler
import br.com.zup.pix.services.ChavePixService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import org.slf4j.LoggerFactory

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ChaveController(@Inject val chavePixService: ChavePixService): DesafioPixServiceGrpc.DesafioPixServiceImplBase() {

    private val logger = LoggerFactory.getLogger(ChaveController::class.java)

    override fun cadastraChave(request: ChavePixRequest?, responseObserver: StreamObserver<ChavePixResponse>?) {

        val chaveDto = request?.toModel()

        val chavePix = chavePixService.cadastraChavePix(chaveDto!!)

        responseObserver?.onNext(ChavePixResponse.newBuilder()
            .setClientId(chavePix.codigoCliente.toString())
            .setChaveId(chavePix.id.toString())
            .build())
        responseObserver?.onCompleted()
    }

}
