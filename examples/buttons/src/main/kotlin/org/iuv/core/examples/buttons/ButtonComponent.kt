package org.iuv.core.examples.buttons

import org.iuv.core.Cmd
import org.iuv.core.Cmd.Companion.cmdOf
import org.iuv.core.GetAsync
import org.iuv.core.HTML
import org.iuv.core.UV

// MODEL

data class ButtonModel(val country: String, val selectedButtonModel: SelectedButtonModel)

// MESSAGES
interface ButtonComponentMessage

data class SelectedButtonMessageWrapper(val selectedButtonMessage: SelectedButtonMessage) : ButtonComponentMessage

data class ButtonCountry(val alpha3_code: String) : ButtonComponentMessage

// SERVICE
data class Country(val name: String, val alpha2_code: String, val alpha3_code: String)

data class  CountryResponse(val messages: List<String>, val result: Country)

data class CountryRestResponse(val RestResponse: CountryResponse)

object ButtonComponent : UV<ButtonModel, ButtonComponentMessage> {

    fun init(text: String, country: String) : ButtonModel {
        return ButtonModel(country, SelectedButton.init(text))
    }

    override fun update(message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, Cmd<ButtonComponentMessage>?> {
        when (message) {
            is SelectedButtonMessageWrapper -> {
                val (updatedModel, updateCmd) = SelectedButton.update(message.selectedButtonMessage, model.selectedButtonModel)

                val updateCmdMapped = updateCmd?.map(::SelectedButtonMessageWrapper)

                val cmd =
                        if (model.selectedButtonModel.selected) {
                            val getAsync = GetAsync<CountryRestResponse,ButtonComponentMessage>(
                                    "http://services.groupkt.com/country/get/iso2code/${model.country}")
                            { response ->
                                ButtonCountry(response.RestResponse.result.alpha3_code)
                            }
                            cmdOf(getAsync, updateCmdMapped)
                        } else {
                            updateCmdMapped
                        }
                return Pair(model.copy(selectedButtonModel = updatedModel), cmd)
            }
            is ButtonCountry -> {
                val text = model.selectedButtonModel.text + " " + message.alpha3_code
                return Pair(model.copy(selectedButtonModel = model.selectedButtonModel.copy(text = text)), null)
            }
            else -> return Pair(model, null)
        }
    }

    override fun view(model: ButtonModel): HTML<ButtonComponentMessage>.() -> Unit = {
        map(SelectedButton, model.selectedButtonModel, ::SelectedButtonMessageWrapper)
    }

}