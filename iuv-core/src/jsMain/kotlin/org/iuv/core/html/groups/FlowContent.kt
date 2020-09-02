package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.*

interface FlowContent<MESSAGE> : HTMLElement<MESSAGE>
 ,CommonPhrasingElements<MESSAGE>
 {
    fun a(init: FlowContentA<MESSAGE>.() -> Unit) {
        element(FlowContentA(), init)
    }
    fun p(init: P<MESSAGE>.() -> Unit) {
        element(P(), init)
    }
    fun hr(init: Hr<MESSAGE>.() -> Unit) {
        element(Hr(), init)
    }
    fun pre(init: Pre<MESSAGE>.() -> Unit) {
        element(Pre(), init)
    }
    fun ul(init: Ul<MESSAGE>.() -> Unit) {
        element(Ul(), init)
    }
    fun ol(init: Ol<MESSAGE>.() -> Unit) {
        element(Ol(), init)
    }
    fun dl(init: Dl<MESSAGE>.() -> Unit) {
        element(Dl(), init)
    }
    fun div(init: Div<MESSAGE>.() -> Unit) {
        element(Div(), init)
    }
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
    fun hgroup(init: Hgroup<MESSAGE>.() -> Unit) {
        element(Hgroup(), init)
    }
    fun blockquote(init: Blockquote<MESSAGE>.() -> Unit) {
        element(Blockquote(), init)
    }
    fun address(init: Address<MESSAGE>.() -> Unit) {
        element(Address(), init)
    }
    fun ins(init: FlowContentIns<MESSAGE>.() -> Unit) {
        element(FlowContentIns(), init)
    }
    fun del(init: FlowContentDel<MESSAGE>.() -> Unit) {
        element(FlowContentDel(), init)
    }
    fun object_(init: FlowContentObject<MESSAGE>.() -> Unit) {
        element(FlowContentObject(), init)
    }
    fun map_(init: FlowContentMap<MESSAGE>.() -> Unit) {
        element(FlowContentMap(), init)
    }
    fun section(init: Section<MESSAGE>.() -> Unit) {
        element(Section(), init)
    }
    fun nav(init: Nav<MESSAGE>.() -> Unit) {
        element(Nav(), init)
    }
    fun article(init: Article<MESSAGE>.() -> Unit) {
        element(Article(), init)
    }
    fun aside(init: Aside<MESSAGE>.() -> Unit) {
        element(Aside(), init)
    }
    fun header(init: Header<MESSAGE>.() -> Unit) {
        element(Header(), init)
    }
    fun footer(init: Footer<MESSAGE>.() -> Unit) {
        element(Footer(), init)
    }
    fun video(init: FlowContentVideo<MESSAGE>.() -> Unit) {
        element(FlowContentVideo(), init)
    }
    fun audio(init: FlowContentAudio<MESSAGE>.() -> Unit) {
        element(FlowContentAudio(), init)
    }
    fun figure(init: Figure<MESSAGE>.() -> Unit) {
        element(Figure(), init)
    }
    fun table(init: Table<MESSAGE>.() -> Unit) {
        element(Table(), init)
    }
    fun form(init: Form<MESSAGE>.() -> Unit) {
        element(Form(), init)
    }
    fun fieldset(init: Fieldset<MESSAGE>.() -> Unit) {
        element(Fieldset(), init)
    }
    fun menu(init: Menu<MESSAGE>.() -> Unit) {
        element(Menu(), init)
    }
    fun canvas(init: FlowContentCanvas<MESSAGE>.() -> Unit) {
        element(FlowContentCanvas(), init)
    }
    fun details(init: Details<MESSAGE>.() -> Unit) {
        element(Details(), init)
    }
}