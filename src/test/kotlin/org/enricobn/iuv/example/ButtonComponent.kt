package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
import org.enricobn.iuv.HTML
import org.enricobn.iuv.UV
import org.enricobn.iuv.mapCmd

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

class ButtonComponent<CONTAINER_MESSAGE> : UV<ButtonModel, ButtonComponentMessage> {

    private val selectedButton = SelectedButton<CONTAINER_MESSAGE>()

    fun init(text: String) : ButtonModel {
        return ButtonModel(selectedButton.init(text))
    }

    override fun update(message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, Cmd<ButtonComponentMessage>?> {
        if (message is SelectedButtonMessageWrapper) {
            val selectedButtonUpdateResult = selectedButton.update(message.selectedButtonMessage, model.selectedButtonModel)

            val selectedButtonCmd = mapCmd(selectedButtonUpdateResult.second, ::SelectedButtonMessageWrapper)

            if (model.selectedButtonModel.selected) {
                return Pair(ButtonModel(selectedButtonUpdateResult.first), { messageBus ->
                    getAsync<CountryRestResponse>("http://services.groupkt.com/country/get/iso2code/IT", messageBus)
                        { response ->
                            ButtonCountry(response.RestResponse.result.alpha3_code)
                        }

                    if (selectedButtonCmd != null) {
                        selectedButtonCmd(messageBus)
                    }
                })
            } else {
                return Pair(ButtonModel(selectedButtonUpdateResult.first), selectedButtonCmd)
            }
        } else if (message is ButtonCountry) {
            val text = model.selectedButtonModel.text + " " + message.alpha3_code
            return Pair(ButtonModel(SelectedButtonModel(text, model.selectedButtonModel.selected)), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(model: ButtonModel): HTML<ButtonComponentMessage>.() -> Unit = {
        map(::SelectedButtonMessageWrapper) {
            selectedButton.render(this, model.selectedButtonModel)
        }
    }

}

fun <CONTAINER_MESSAGE> HTML<ButtonComponentMessage>.buttonComponent(model: ButtonModel, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) {
    ButtonComponent<CONTAINER_MESSAGE>().render(this, model)
}