package br.com.zup.pix.services

import br.com.zup.pix.cadastrachavepix.ChavePixDto
import br.com.zup.pix.cadastrachavepix.ConsultaCliente
import br.com.zup.pix.compartilhado.handlers.errors.ChavePixExistenteException
import br.com.zup.pix.entidades.ChavePix
import br.com.zup.pix.entidades.repositorios.ChavePixRepository
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class ChavePixService (@Inject val chavePixRepository: ChavePixRepository,
                       @Inject val consultaCliente: ConsultaCliente ) {

    val logger = LoggerFactory.getLogger(ChavePixService::class.java)

    @Transactional
    fun cadastraChavePix(@Valid chavePixDto: ChavePixDto): ChavePix {

        val optionalChave = chavePixRepository.findByChave(chavePixDto.valorDaChave)
        if (optionalChave.isPresent){
            throw ChavePixExistenteException("Chave ${chavePixDto.valorDaChave} ja existe na base de dados")

        }

        val responseItau = consultaCliente.consultaConta(chavePixDto.codigoInterno, chavePixDto.tipoDeConta!!.name)
        //val responseItau = consultaCliente.consulta(chavePixDto.codigoInterno)
        logger.info(responseItau.status.toString())
        if (responseItau.status.equals(HttpStatus.NOT_FOUND)){
            throw IllegalStateException("Cliente nao encontrado no banco de dados")
        }
        println("antes de gravar")
        val chavePix = chavePixRepository.save(chavePixDto.toModel())
        print("apos gravar")

        return chavePix

    }
}