package org.enricobn.iuv

import kotlinx.html.HtmlBlockTag

abstract class IUVComponent<MODEL> : IUV<MODEL>() {

    fun render(parent: HtmlBlockTag, messageBus: MessageBus, model: MODEL) {
        view(messageBus, model).invoke(parent)
    }

}