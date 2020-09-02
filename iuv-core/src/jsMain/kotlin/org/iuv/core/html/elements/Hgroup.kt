package org.iuv.core.html.elements
import org.iuv.core.html.attributegroups.GlobalAttributeGroup

class Hgroup<MESSAGE> : org.iuv.core.HTML<MESSAGE>("hgroup")
 ,GlobalAttributeGroup<MESSAGE>
 
 {

    fun h1(init: H1<MESSAGE>.() -> Unit) {
        element(H1(), init)
    }
    fun h2(init: H2<MESSAGE>.() -> Unit) {
        element(H2(), init)
    }
    fun h3(init: H3<MESSAGE>.() -> Unit) {
        element(H3(), init)
    }
    fun h4(init: H4<MESSAGE>.() -> Unit) {
        element(H4(), init)
    }
    fun h5(init: H5<MESSAGE>.() -> Unit) {
        element(H5(), init)
    }
    fun h6(init: H6<MESSAGE>.() -> Unit) {
        element(H6(), init)
    }

}