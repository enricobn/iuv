package org.iuv.xhtml

import java.net.URL

interface XHTMLResourceProvider {

    fun getUrl(resource: String) : URL

}