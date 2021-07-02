package br.com.zup.pix.cadastrachavepix


import br.com.zup.pix.compartilhado.TipoDeChave
import br.com.zup.pix.compartilhado.TipoDeConta
import br.com.zup.pix.compartilhado.validacao.ValidUUID
import br.com.zup.pix.compartilhado.validacao.ValidaChavePix
import br.com.zup.pix.entidades.ChavePix
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ValidaChavePix
data class ChavePixDto(
    @field:ValidUUID
    @field:NotBlank
    val codigoInterno: String,
    @field:NotNull
    val tipoDeChave: TipoDeChave?,
    @field:Size(max = 77)
    val valorDaChave: String,
    @field:NotNull
    val tipoDeConta: TipoDeConta?
) {

    fun toModel(): ChavePix {
        return ChavePix(
            codigoCliente = UUID.fromString(this.codigoInterno),
            tipoDeChave = tipoDeChave!!,
            chave = if (this.tipoDeChave!!.equals(TipoDeChave.ALEATORIA)) {
                UUID.randomUUID().toString()
            } else {
                this.valorDaChave
            },
            tipoDeConta = tipoDeConta!!
        )


    }

}
