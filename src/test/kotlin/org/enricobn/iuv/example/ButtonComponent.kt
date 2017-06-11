package org.enricobn.iuv.example

import org.enricobn.iuv.HTML
import org.enricobn.iuv.IUV
import org.enricobn.iuv.MessageBus
import org.w3c.xhr.XMLHttpRequest

// MODEL

data class ButtonModel(val text: String, val selected: Boolean)

// MESSAGES
interface ButtonComponentMessage

class ButtonClick : ButtonComponentMessage

class ButtonSend(val alpha3_code: String) : ButtonComponentMessage

// SERVICE
data class Country(val name: String, val alpha2_code: String, val alpha3_code: String)

data class CountryResponse(val messages: List<String>, val result: Country)

data class CountryRestResponse(val RestResponse: CountryResponse)

class ButtonComponent<CONTAINER_MESSAGE> : IUV<ButtonModel, ButtonComponentMessage, CONTAINER_MESSAGE>() {

    override fun update(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE, message: ButtonComponentMessage, model: ButtonModel): Pair<ButtonModel, (() -> Unit)?> {
        if (message is ButtonClick) {
            if (model.selected) {
                return Pair(model, { ->
                    val url = "http://services.groupkt.com/country/get/iso2code/IT"
                    val request = XMLHttpRequest()
                    request.onreadystatechange = { event ->
                        if (request.readyState.toInt() == 4 && request.status.toInt() == 200) {
                            val response = JSON.parse<CountryRestResponse>(request.responseText)
                            messageBus.send(map(ButtonSend(response.RestResponse.result.alpha3_code)))
                        }
                    }
                    request.open("get", url, true)
                    request.send()
                })
            } else {
                return Pair(ButtonModel(model.text, !model.selected), null)
            }
        } else if (message is ButtonSend) {
            return Pair(ButtonModel(model.text + " " + message.alpha3_code, model.selected), null)
        } else {
            return Pair(model, null)
        }
    }

    override fun view(messageBus: MessageBus<CONTAINER_MESSAGE>, map: (ButtonComponentMessage) -> CONTAINER_MESSAGE, model: ButtonModel): HTML.() -> Unit = {
        button {
            +model.text

            onClick { _ -> messageBus.send(map(ButtonClick())) }

            if (model.selected) {
                classes = "ButtonComponentSelected"
            }
        }
    }

}

fun <CONTAINER_MESSAGE> HTML.buttonComponent(messageBus: MessageBus<CONTAINER_MESSAGE>, model: ButtonModel,
                                             map: (ButtonComponentMessage) -> CONTAINER_MESSAGE) {
    ButtonComponent<CONTAINER_MESSAGE>().render(this, messageBus, map, model)
}