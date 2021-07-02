package br.com.zup.pix.cadastrachavepix

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("\${api.consulta.itau}")

interface ConsultaCliente {

    @Get("/api/v1/clientes/{idCliente}/contas{?tipo}")
    fun consultaConta(@PathVariable idCliente: String, @QueryValue tipo: String): HttpResponse<ConsultaContaResponse>
}

data class ConsultaContaResponse(
    val tipo: String,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {

}

data class Titular(
    val id: String,
    val nome: String,
    val cpf: String
) {

}

data class Instituicao (val nome: String, val ispb: String) {

}
