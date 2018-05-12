require.config({
    waitSeconds: 5
});

//
//var snabbdom = require('snabbdom');
//
//var patch = snabbdom.init([ // Init patch function with chosen modules
//  require('snabbdom-class').default, // makes it easy to toggle classes
//  require('snabbdom-props').default, // for setting properties on DOM elements
//  require('snabbdom-style').default, // handles styling on elements with support for animations
//  require('snabbdom-eventlisteners').default, // attaches event listeners
//]);
//var h = require('h').default; // helper function for creating vnodes


require([
            'snabbdom',
            'h',
            'snabbdom-style',
            'snabbdom-class',
            'snabbdom-props',
            'snabbdom-attributes',
            'snabbdom-eventlisteners'
        ],function(snabbdom, h, style, _class, props, attributes, eventlisteners) {

    patch = snabbdom.init([
        style.default,
        _class.default,
        props.default,
        attributes.default,
        eventlisteners.default
    ]);

    var vnode = snabbdom.h('span', 'foobar');
    var container = document.getElementById('app');

    patch(container, vnode);

    setTimeout(function() {
      //var newVnode = h('span', {style: {fontWeight: 'bold', fontStyle: 'italic'}}, 'This is now bold italics');
      //var newVnode = h('a', {class: {active: true, selected: false}}, 'Toggle');
      //var newVnode = h('a', {props: {href: '/foo'}}, 'Go to Foo');
      //var newVnode = h('a', {attrs: {href: '/foo'}}, 'Go to Foo');

      function clickHandler(number) { console.log('button ' + number + ' was clicked!'); }

      var newVnode = snabbdom.h('div', [
        snabbdom.h('a', {on: {click: [clickHandler, 1]}, attrs: {href: '#'}}, '1'),
        snabbdom.h('a', {on: {click: [clickHandler, 2]}, attrs: {href: '#'}}, '2'),
        snabbdom.h('a', {on: {click: [clickHandler, 3]}, attrs: {href: '#'}}, '3'),
      ]);

      patch(vnode, newVnode);
    }, 1500);
});