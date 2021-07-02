package br.com.zup.pix.chavepix.remove

import br.com.zup.pix.chavepix.entidades.repositorios.ChavePixRepository
import br.com.zup.pix.compartilhado.handlers.errors.ChavePixNaoEncontradaException
import br.com.zup.pix.compartilhado.validacao.ValidUUID
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.constraints.NotBlank

@Validated
@Singleton
class RemoveChaveService(@Inject val chavePixRepository: ChavePixRepository) {

    fun remove(
        @NotBlank @ValidUUID clientId: String?,
        @NotBlank @ValidUUID chaveId: String?,
    ) {

        val clientIdUUID = UUID.fromString(clientId)
        val chaveIdUUID = UUID.fromString(chaveId)

        val optionalChave = chavePixRepository.findByIdAndCodigoCliente( chaveIdUUID, clientIdUUID)
        if(optionalChave.isEmpty){
            print("chave nao existe")
            throw ChavePixNaoEncontradaException("Chave nao encontrada")

        }
        chavePixRepository.delete(optionalChave.get())
    }

}