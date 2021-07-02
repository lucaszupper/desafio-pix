package br.com.zup.pix.cadastrachave

import br.com.zup.pix.ChavePixRequest
import br.com.zup.pix.DesafioPixServiceGrpc
import br.com.zup.pix.TipoDeChave
import br.com.zup.pix.cadastrachavepix.ConsultaCliente
import br.com.zup.pix.cadastrachavepix.ConsultaContaResponse
import br.com.zup.pix.cadastrachavepix.Instituicao
import br.com.zup.pix.cadastrachavepix.Titular
import br.com.zup.pix.compartilhado.TipoDeConta
import br.com.zup.pix.compartilhado.violations
import br.com.zup.pix.entidades.ChavePix
import br.com.zup.pix.entidades.repositorios.ChavePixRepository

import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.AbstractBlockingStub
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat

import org.hamcrest.Matchers.containsInAnyOrder

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class CadastraChavePixTest(
    val chavePixRepository: ChavePixRepository,
    val grpcClient: DesafioPixServiceGrpc.DesafioPixServiceBlockingStub

) {
    @Inject
    lateinit var consultaCliente: ConsultaCliente

    companion object {
      val CLIENT_ID = UUID.randomUUID()
    }


    @BeforeEach
    fun setup() {
        chavePixRepository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma chave pix - Email`() {
        `when`(consultaCliente.consultaConta(idCliente = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(itauResponse()))

        val response = grpcClient.cadastraChave(ChavePixRequest.newBuilder()
            .setCodigoInterno(CLIENT_ID.toString())
            .setTipoDeChave(TipoDeChave.EMAIL)
            .setValorDaChave("lucas@email.com")
            .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
            .build())

        with(response) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(chaveId)
        }
    }
    @Test
    fun `deve cadastrar uma chave pix - CPF`() {
        `when`(consultaCliente.consultaConta(idCliente = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(itauResponse()))

        val response = grpcClient.cadastraChave(ChavePixRequest.newBuilder()
            .setCodigoInterno(CLIENT_ID.toString())
            .setTipoDeChave(TipoDeChave.CPF)
            .setValorDaChave("26951023050")
            .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
            .build())

        with(response) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(chaveId)
        }
    }
    @Test
    fun `deve cadastrar uma chave pix - TELEFONE`() {
        `when`(consultaCliente.consultaConta(idCliente = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(itauResponse()))

        val response = grpcClient.cadastraChave(ChavePixRequest.newBuilder()
            .setCodigoInterno(CLIENT_ID.toString())
            .setTipoDeChave(TipoDeChave.TELEFONE)
            .setValorDaChave("+5584998506034")
            .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
            .build())

        with(response) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(chaveId)
        }
    }
    @Test
    fun `deve cadastrar uma chave pix - ALEATORIA`() {
        `when`(consultaCliente.consultaConta(idCliente = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(itauResponse()))

        val response = grpcClient.cadastraChave(ChavePixRequest.newBuilder()
            .setCodigoInterno(CLIENT_ID.toString())
            .setTipoDeChave(TipoDeChave.ALEATORIA)
            .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
            .build())

        with(response) {
            assertEquals(CLIENT_ID.toString(), clientId)
            assertNotNull(chaveId)
        }
    }
    @Test
    fun `nao deve gerar chave duplicada - CPF`(){
        chavePixRepository.save(ChavePix(
            CLIENT_ID, br.com.zup.pix.compartilhado.TipoDeChave.CPF, "26951023050", TipoDeConta.CONTA_CORRENTE
        ))

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setValorDaChave("26951023050")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
                .build())
        }
        with(erro) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave 26951023050 ja existe na base de dados", status.description)
        }
    }

    @Test
    fun `nao deve gerar chave quando cliente nao for encontrado`(){
        `when`(consultaCliente.consultaConta(idCliente = CLIENT_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setValorDaChave("teste@teste.com")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        with(erro) {
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            assertEquals("Cliente nao encontrado no banco de dados", status.description)
        }
    }
    @Test
    fun `nao deve gerar chave com requisicao vazia`() {
        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder().build())
        }

        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat( violations(), containsInAnyOrder(
                Pair("codigoInterno", "UUID não possui um formato válido"),
                Pair("codigoInterno", "não deve estar em branco"),
                Pair("tipoDeConta", "não deve ser nulo"),
                Pair("tipoDeChave", "não deve ser nulo"),
            ))
        }
    }
    @Test
    fun `nao deve gerar chave com dados invalidos - CPF`(){

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.CPF)
                .setValorDaChave("60794595072")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_POUPANCA)
                .build())
        }

        // validação
        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat(violations(), containsInAnyOrder(
                Pair("chave", "Chave tipo Inválida (CPF : validatedValue.valorDaChave )"),
            ))
        }
    }
    @Test
    fun `nao deve gerar chave com dados invalidos - TELEFONE`(){

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.TELEFONE)
                .setValorDaChave("84998506287")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_POUPANCA)
                .build())
        }

        // validação
        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat(violations(), containsInAnyOrder(
                Pair("chave", "Chave tipo Inválida (TELEFONE : validatedValue.valorDaChave )"),
            ))
        }
    }
    @Test
    fun `nao deve gerar chave com dados invalidos - EMAIL`(){

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.EMAIL)
                .setValorDaChave("zup.com.br")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_POUPANCA)
                .build())
        }

        // validação
        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat(violations(), containsInAnyOrder(
                Pair("chave", "Chave tipo Inválida (EMAIL : validatedValue.valorDaChave )"),
            ))
        }
    }
    @Test
    fun `nao deve gerar chave com dados invalidos - ALEATORIA`(){

        val erro = assertThrows<StatusRuntimeException> {
            grpcClient.cadastraChave(ChavePixRequest.newBuilder()
                .setCodigoInterno(CLIENT_ID.toString())
                .setTipoDeChave(TipoDeChave.ALEATORIA)
                .setValorDaChave("zup@zup.com.br")
                .setTipoDeConta(br.com.zup.pix.TipoDeConta.CONTA_POUPANCA)
                .build())
        }

        // validação
        with(erro) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("Dados inválidos", status.description)
            assertThat(violations(), containsInAnyOrder(
                Pair("chave", "Chave tipo Inválida (ALEATORIA : validatedValue.valorDaChave )"),
            ))
        }
    }




    @MockBean(ConsultaCliente::class)
    fun consultaClienteItau(): ConsultaCliente? {
        return Mockito.mock(ConsultaCliente::class.java)
    }

    @Factory
    class Clients {
    @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): DesafioPixServiceGrpc.DesafioPixServiceBlockingStub? {
            return DesafioPixServiceGrpc.newBlockingStub(channel)

        }
    }

    private fun itauResponse (): ConsultaContaResponse {
        return ConsultaContaResponse(
        tipo = "CONTA_CORRENTE",
        instituicao = Instituicao(nome = "ITAU", ispb = "teste" ),
        agencia = "1234",
        numero = "654321",
        titular = Titular("","Lucas Rodrigues", "26951023050")
        )
    }
}