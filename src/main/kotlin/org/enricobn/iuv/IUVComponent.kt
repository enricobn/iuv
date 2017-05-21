package org.enricobn.iuv

import kotlinx.html.DIV

abstract class IUVComponent<MODEL> : IUV<MODEL>() {

    fun render(parent: DIV, messageBus: MessageBus, model: MODEL) {
        view(messageBus, model).invoke(parent)
    }

}