package org.iuv.core.html.groups

import org.iuv.core.HTMLElement
import org.iuv.core.html.elements.*

interface CommonPhrasingElements<MESSAGE> : HTMLElement<MESSAGE>
 
 {
    fun abbr(init: Abbr<MESSAGE>.() -> Unit) {
        element(Abbr(), init)
    }
    fun area(init: Area<MESSAGE>.() -> Unit) {
        element(Area(), init)
    }
    fun b(init: B<MESSAGE>.() -> Unit) {
        element(B(), init)
    }
    fun bdi(init: Bdi<MESSAGE>.() -> Unit) {
        element(Bdi(), init)
    }
    fun bdo(init: Bdo<MESSAGE>.() -> Unit) {
        element(Bdo(), init)
    }
    fun br(init: Br<MESSAGE>.() -> Unit) {
        element(Br(), init)
    }
    fun button(init: Button<MESSAGE>.() -> Unit) {
        element(Button(), init)
    }
    fun cite(init: Cite<MESSAGE>.() -> Unit) {
        element(Cite(), init)
    }
    fun code(init: Code<MESSAGE>.() -> Unit) {
        element(Code(), init)
    }
    fun command(init: Command<MESSAGE>.() -> Unit) {
        element(Command(), init)
    }
    fun datalist(init: Datalist<MESSAGE>.() -> Unit) {
        element(Datalist(), init)
    }
    fun dfn(init: Dfn<MESSAGE>.() -> Unit) {
        element(Dfn(), init)
    }
    fun em(init: Em<MESSAGE>.() -> Unit) {
        element(Em(), init)
    }
    fun embed(init: Embed<MESSAGE>.() -> Unit) {
        element(Embed(), init)
    }
    fun i(init: I<MESSAGE>.() -> Unit) {
        element(I(), init)
    }
    fun iframe(init: Iframe<MESSAGE>.() -> Unit) {
        element(Iframe(), init)
    }
    fun img(init: Img<MESSAGE>.() -> Unit) {
        element(Img(), init)
    }
    fun input(init: Input<MESSAGE>.() -> Unit) {
        element(Input(), init)
    }
    fun kbd(init: Kbd<MESSAGE>.() -> Unit) {
        element(Kbd(), init)
    }
    fun keygen(init: Keygen<MESSAGE>.() -> Unit) {
        element(Keygen(), init)
    }
    fun label(init: Label<MESSAGE>.() -> Unit) {
        element(Label(), init)
    }
    fun mark(init: Mark<MESSAGE>.() -> Unit) {
        element(Mark(), init)
    }
    fun meter(init: Meter<MESSAGE>.() -> Unit) {
        element(Meter(), init)
    }
    fun output(init: Output<MESSAGE>.() -> Unit) {
        element(Output(), init)
    }
    fun progress(init: Progress<MESSAGE>.() -> Unit) {
        element(Progress(), init)
    }
    fun q(init: Q<MESSAGE>.() -> Unit) {
        element(Q(), init)
    }
    fun ruby(init: Ruby<MESSAGE>.() -> Unit) {
        element(Ruby(), init)
    }
    fun s(init: S<MESSAGE>.() -> Unit) {
        element(S(), init)
    }
    fun samp(init: Samp<MESSAGE>.() -> Unit) {
        element(Samp(), init)
    }
    fun script(init: Script<MESSAGE>.() -> Unit) {
        element(Script(), init)
    }
    fun select(init: Select<MESSAGE>.() -> Unit) {
        element(Select(), init)
    }
    fun small(init: Small<MESSAGE>.() -> Unit) {
        element(Small(), init)
    }
    fun span(init: Span<MESSAGE>.() -> Unit) {
        element(Span(), init)
    }
    fun strong(init: Strong<MESSAGE>.() -> Unit) {
        element(Strong(), init)
    }
    fun sub(init: Sub<MESSAGE>.() -> Unit) {
        element(Sub(), init)
    }
    fun sup(init: Sup<MESSAGE>.() -> Unit) {
        element(Sup(), init)
    }
    fun textarea(init: Textarea<MESSAGE>.() -> Unit) {
        element(Textarea(), init)
    }
    fun time(init: Time<MESSAGE>.() -> Unit) {
        element(Time(), init)
    }
    fun u(init: U<MESSAGE>.() -> Unit) {
        element(U(), init)
    }
    fun var_(init: Var_<MESSAGE>.() -> Unit) {
        element(Var_(), init)
    }
    fun wbr(init: Wbr<MESSAGE>.() -> Unit) {
        element(Wbr(), init)
    }
}