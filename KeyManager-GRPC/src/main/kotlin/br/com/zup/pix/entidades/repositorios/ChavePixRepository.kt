package br.com.zup.pix.entidades.repositorios

import br.com.zup.pix.entidades.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID>{

    fun findByChave(chave: String): Optional<ChavePix>


}