package br.com.zup.pix.compartilhado.validacao

import br.com.zup.pix.cadastrachavepix.ChavePixDto
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.ConstraintValidator
import javax.validation.ConstraintValidatorContext
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidaChavePixValidator::class])
annotation class ValidaChavePix(
    val message: String = "Chave tipo Inválida (\${validatedValue.tipo})",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidaChavePixValidator: javax.validation.ConstraintValidator<ValidaChavePix, ChavePixDto> {

    override fun isValid(value: ChavePixDto?, context: javax.validation.ConstraintValidatorContext): Boolean {


        if (value?.tipoDeChave == null) {
            return true
        }
        println("antes do valid")
        println(value)
        val valid = value.tipoDeChave.valida(value.valorDaChave)
        println("apos valid")
        if (!valid) {

            context.disableDefaultConstraintViolation()
            context
                .buildConstraintViolationWithTemplate(context.defaultConstraintMessageTemplate)
                .addPropertyNode("chave").addConstraintViolation()
        }

        return valid
    }
}