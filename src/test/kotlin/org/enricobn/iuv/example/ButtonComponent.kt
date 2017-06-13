package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.MessageBus
import org.enricobn.iuv.UV

// MODEL

data class ButtonModel(val selectedButtonModel: SelectedButtonModel)

// MESSAGES
interface ButtonComponentMessage

class SelectedButtonMessageWrapper(val selectedButtonMessage: SelectedButtonMessage) : ButtonComponentMessage

class ButtonCountry(val alpha3_code: String) : ButtonComponentMessage

// SERVICE
data class Country(val name: String, val alpha2_code: String, val alpha3_code: String)

data class CountryResponse(val messages: List<String>, val result: Country)

data class CountryRestResponse(val RestResponse: CountryResponse)

class ButtonComponent<CONTAINER_MESSAGE> : UV<ButtonModel, ButtonComponentMessage, CONTAINER_MESSAGE> {

    private val selectedButton = SelectedButton<CONTAINER_MESSAGE>()

    fun init(text: String) : ButtonModel {
        return ButtonModel(selectedButton.init(text))
    }

    override fun update(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE,
                        message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, (() -> Unit)?> {
        if (message is SelectedButtonMessageWrapper) {
            val selectedButtonUpdateResult = selectedButton.update(messageBus, selectedButtonMap(map),
                    message.selectedButtonMessage, model.selectedButtonModel)

            if (model.selectedButtonModel.selected) {
                return Pair(ButtonModel(selectedButtonUpdateResult.first), { ->
                    getAsync<CountryRestResponse>("http://services.groupkt.com/country/get/iso2code/IT", messageBus, map)
                        { response ->
                            ButtonCountry(response.RestResponse.result.alpha3_code)
                        }

                    if (selectedButtonUpdateResult.second != null) {
                        selectedButtonUpdateResult.second!!()
                    }
                })
            } else {
                return Pair(ButtonModel(selectedButtonUpdateResult.first), selectedButtonUpdateResult.second)
            }
        } else if (message is ButtonCountry) {
            val text = model.selectedButtonModel.text + " " + message.alpha3_code
            return Pair(ButtonModel(SelectedButtonModel(text, model.selectedButtonModel.selected)), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE,
                      model: ButtonModel): HTML.() -> Unit = {
        selectedButton(messageBus, model.selectedButtonModel, selectedButtonMap(map))
    }

    private fun selectedButtonMap(map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) = { selectedButtonMessage: SelectedButtonMessage ->
        map.invoke(SelectedButtonMessageWrapper(selectedButtonMessage))
    }

}

fun <CONTAINER_MESSAGE> HTML.buttonComponent(messageBus: MessageBus<CONTAINER_MESSAGE>, model: ButtonModel,
                                             map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) {
    ButtonComponent<CONTAINER_MESSAGE>().render(this, messageBus, map, model)
}