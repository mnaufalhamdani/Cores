package com.duakelinci.core.data.model

import com.duakelinci.core.helper.NoDataFoundException

class ValidationModelBuilder {

    private var listValidation = mutableListOf<ValidationModel>()

    fun add(validation: ValidationModel){
        this.listValidation.find { it.nameOfField == validation.nameOfField }
            ?.let {
                throw Throwable("Name Field is Exist. Try Change the Name Field")
            } ?: this.listValidation.add(validation)
    }

    fun add(vararg validations: ValidationModel){
        validations.forEach { validation ->
            this.listValidation.find { it.nameOfField == validation.nameOfField }
                ?.let {
                    throw Throwable("Name Field is Exist. Try Change the Name Field")
                } ?: this.listValidation.add(validation)
        }
    }

    fun get(nameOfField: String): ValidationModel {
        return this.listValidation.find { it.nameOfField == nameOfField }
            ?: throw NoDataFoundException("Name Field Doesn't Exists")
    }
    fun get() : List<ValidationModel> = this.listValidation
}

data class ValidationModel(
    var nameOfField: String,
    var message: String?,
    var messageId: Int = 0,
    var formErrors: FormErrors?
) {
    //    constructor() : this("", false,null, FormErrors.EMPTY)
    constructor(nameOfField: String) : this(nameOfField, null, 0, FormErrors.VALID)

    fun setFormError(formErrors: FormErrors, message: String?=null){
        this.message = message
        this.formErrors = formErrors
        if (formErrors == FormErrors.EMPTY && this.message.isNullOrEmpty()){
            this.message = "Isian ini tidak boleh kosong."
        }
    }

    fun setFieldMessage(value:String){
        message = value
    }
    fun setFieldMessage(value:Int){
        messageId = value
    }
}

data class Validation(var isValid: Boolean, var validationFields: MutableList<ValidationModel>, var message:String?=null)

enum class FormErrors {
    EMPTY, INVALID_FORMAT, PASSWORD_NOT_MATCHING, CUSTOM_ERROR, VALID
}