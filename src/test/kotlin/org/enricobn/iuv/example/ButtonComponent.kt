package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
import org.enricobn.iuv.GetAsync
import org.enricobn.iuv.HTML
import org.enricobn.iuv.UV

// MODEL

data class ButtonModel(val selectedButtonModel: SelectedButtonModel)

// MESSAGES
interface ButtonComponentMessage

data class SelectedButtonMessageWrapper(val selectedButtonMessage: SelectedButtonMessage) : ButtonComponentMessage

data class ButtonCountry(val alpha3_code: String) : ButtonComponentMessage

// SERVICE
data class Country(val name: String, val alpha2_code: String, val alpha3_code: String)

data class CountryResponse(val messages: List<String>, val result: Country)

data class CountryRestResponse(val RestResponse: CountryResponse)

object ButtonComponent : UV<ButtonModel, ButtonComponentMessage> {

    fun init(text: String) : ButtonModel {
        return ButtonModel(SelectedButton.init(text))
    }

    override fun update(message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, Cmd<ButtonComponentMessage>?> {
        if (message is SelectedButtonMessageWrapper) {
            val selectedButtonUpdateResult = SelectedButton.update(message.selectedButtonMessage, model.selectedButtonModel)

            val selectedButtonCmd = selectedButtonUpdateResult.second?.map(::SelectedButtonMessageWrapper)

            val cmd =
                if (model.selectedButtonModel.selected) {
                    val getAsync = GetAsync<CountryRestResponse,ButtonComponentMessage>(
                                "http://services.groupkt.com/country/get/iso2code/IT")
                            { response ->
                                ButtonCountry(response.RestResponse.result.alpha3_code)
                            }
                    Cmd.cmdOf(getAsync, selectedButtonCmd)
                } else {
                    selectedButtonCmd
                }
            return Pair(ButtonModel(selectedButtonUpdateResult.first), cmd)
        } else if (message is ButtonCountry) {
            val text = model.selectedButtonModel.text + " " + message.alpha3_code
            return Pair(ButtonModel(SelectedButtonModel(text, model.selectedButtonModel.selected)), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(model: ButtonModel): HTML<ButtonComponentMessage>.() -> Unit = {
        map(SelectedButton, model.selectedButtonModel, ::SelectedButtonMessageWrapper)
    }

}