package org.enricobn.iuv.example

import org.enricobn.iuv.Cmd
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

class ButtonComponent<CONTAINER_MESSAGE> : UV<ButtonModel, ButtonComponentMessage, CONTAINER_MESSAGE> {

    private val selectedButton = SelectedButton<CONTAINER_MESSAGE>()

    fun init(text: String) : ButtonModel {
        return ButtonModel(selectedButton.init(text))
    }

    override fun update(map: (ButtonComponentMessage) -> CONTAINER_MESSAGE, message: ButtonComponentMessage,
                        model: ButtonModel): Pair<ButtonModel, Cmd<CONTAINER_MESSAGE>?> {
        if (message is SelectedButtonMessageWrapper) {
            val selectedButtonUpdateResult = selectedButton.update(selectedButtonMap(map), message.selectedButtonMessage,
                    model.selectedButtonModel)

            if (model.selectedButtonModel.selected) {
                return Pair(ButtonModel(selectedButtonUpdateResult.first), { messageBus ->
                    getAsync<CountryRestResponse>("http://services.groupkt.com/country/get/iso2code/IT", messageBus, map)
                        { response ->
                            ButtonCountry(response.RestResponse.result.alpha3_code)
                        }

                    if (selectedButtonUpdateResult.second != null) {
                        selectedButtonUpdateResult.second!!(messageBus)
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

    override fun view(map: (ButtonComponentMessage) -> CONTAINER_MESSAGE, model: ButtonModel): HTML<CONTAINER_MESSAGE>.() -> Unit = {
        selectedButton(model.selectedButtonModel, selectedButtonMap(map))
    }

    private fun selectedButtonMap(map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) = { selectedButtonMessage: SelectedButtonMessage ->
        map(SelectedButtonMessageWrapper(selectedButtonMessage))
    }

}

fun <CONTAINER_MESSAGE> HTML<CONTAINER_MESSAGE>.buttonComponent(model: ButtonModel, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) {
    ButtonComponent<CONTAINER_MESSAGE>().render(this, map, model)
}