package org.iuv.xhtml.impl

import org.iuv.xhtml.XHTMLResourceProvider
import java.io.File
import java.net.URL

class XHTMLFileResourceProvider : XHTMLResourceProvider {

    override fun getUrl(resource: String): URL {
        return File("iuv-xhtml/src/jvm8Main/resources$resource").toURI().toURL()
    }

}