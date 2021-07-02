package br.com.zup.pix.entidades

import br.com.zup.pix.compartilhado.TipoDeChave
import br.com.zup.pix.compartilhado.TipoDeConta
import org.junit.jupiter.api.Test
import java.util.*

internal class ChavePixTest {





    private fun novaChavePix(
        tipoDeChave: TipoDeChave,
        chave: String = UUID.randomUUID().toString(),
        clienteId: UUID =  UUID.randomUUID()
    ) : ChavePix{
        return ChavePix(
            codigoCliente = clienteId,
            tipoDeChave = tipoDeChave,
            chave = chave,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE
        )
    }
}