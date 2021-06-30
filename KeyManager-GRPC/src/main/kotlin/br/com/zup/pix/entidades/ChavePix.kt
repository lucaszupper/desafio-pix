package br.com.zup.pix.entidades

import br.com.zup.pix.compartilhado.TipoDeChave
import br.com.zup.pix.compartilhado.TipoDeConta
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@Entity
class ChavePix(
    @Column(nullable = false)
    val codigoCliente: UUID,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeChave: TipoDeChave,
    @Column(unique = true, nullable = false)
    val chave: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoDeConta: TipoDeConta

) {
    @Id
    @GeneratedValue
    val id: UUID?= null
    val LocalDateTime = java.time.LocalDateTime.now()


}