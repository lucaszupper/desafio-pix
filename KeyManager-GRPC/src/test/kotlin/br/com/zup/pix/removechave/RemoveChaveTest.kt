package br.com.zup.pix.removechave

import br.com.zup.pix.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.pix.RemoveChaveRequest
import br.com.zup.pix.chavepix.TipoDeChave
import br.com.zup.pix.chavepix.TipoDeConta
import br.com.zup.pix.chavepix.entidades.ChavePix
import br.com.zup.pix.chavepix.entidades.repositorios.ChavePixRepository
import br.com.zup.pix.compartilhado.violations
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

@MicronautTest(transactional = false)
internal class RemoveChaveTest (
    val chavePixRepository: ChavePixRepository,
    val grpcClient: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub
    ){
    lateinit var CHAVE_PIX: ChavePix

    @BeforeEach
    fun setup(){
        CHAVE_PIX = chavePixRepository.save(chave(
            tipoDeChave = TipoDeChave.EMAIL,
            chave = "email@email.com",
            codigoCliente = UUID.randomUUID()
        ))
    }
    @AfterEach
    fun limpeza() {
        chavePixRepository.deleteAll()
    }



    @Test
    fun `deve remover uma chave`() {
        val response = grpcClient.removeChave(RemoveChaveRequest.newBuilder()
            .setChaveId(CHAVE_PIX.id.toString())
            .setClientId(CHAVE_PIX.codigoCliente.toString())
            .build())

        with(response){
            assertEquals("Chave excluida com sucesso", responseMessage )
        }
    }

    @Test
    fun `nao deve remover quando chave for inexistente`(){
        val uuid = UUID.randomUUID().toString()

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(RemoveChaveRequest.newBuilder()
                .setChaveId(uuid)
                .setClientId(CHAVE_PIX.codigoCliente.toString())
                .build())
        }

        with(erro){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave nao encontrada", status.description)
        }
    }

    @Test
    fun `nao deve remover chave pertencente a outro cliente`(){
        val uuid = UUID.randomUUID().toString()

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(RemoveChaveRequest.newBuilder()
                .setChaveId(CHAVE_PIX.id.toString())
                .setClientId(uuid.toString())
                .build())
        }

        with(erro){
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Chave nao encontrada", status.description)
        }
    }

    @Test
    fun `nao deve remover chave com request vazia`() {
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.removeChave(RemoveChaveRequest.newBuilder().build())
        }

        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat(violations(), containsInAnyOrder(
                Pair("chaveId", "não deve estar em branco"),
                Pair("clientId", "não deve estar em branco"),
                Pair("chaveId", "UUID não possui um formato válido"),
                Pair("clientId", "UUID não possui um formato válido"),
            ))
        }
    }


    @Factory
    class Clients {
        @Bean
        fun clientGrpc(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
            return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(tipoDeChave: TipoDeChave, chave: String, codigoCliente: UUID): ChavePix {
        return ChavePix(codigoCliente,tipoDeChave,chave,tipoDeConta = TipoDeConta.CONTA_CORRENTE)
    }
}