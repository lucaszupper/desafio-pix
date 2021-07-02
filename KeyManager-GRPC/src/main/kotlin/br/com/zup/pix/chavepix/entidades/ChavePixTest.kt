package br.com.zup.pix.chavepix.entidades

import br.com.zup.pix.chavepix.TipoDeChave
import br.com.zup.pix.chavepix.TipoDeConta
import java.util.*

internal class ChavePixTest {





    private fun novaChavePix(
        tipoDeChave: TipoDeChave,
        chave: String = UUID.randomUUID().toString(),
        clienteId: UUID =  UUID.randomUUID()
    ) : ChavePix {
        return ChavePix(
            codigoCliente = clienteId,
            tipoDeChave = tipoDeChave,
            chave = chave,
            tipoDeConta = TipoDeConta.CONTA_CORRENTE
        )
    }
}