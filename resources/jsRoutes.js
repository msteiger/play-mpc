var jsRoutes = {}; (function(_root){
var _nS = function(c,f,b){var e=c.split(f||"."),g=b||_root,d,a;for(d=0,a=e.length;d<a;d++){g=g[e[d]]=g[e[d]]||{}}return g}
var _qS = function(items){var qs = ''; for(var i=0;i<items.length;i++) {if(items[i]) qs += (qs ? '&' : '') + items[i]}; return qs ? ('?' + qs) : ''}
var _s = function(p,s){return p+((s===true||(s&&s.secure))?'s':'')+'://'}
var _wA = function(r){return {ajax:function(c){c=c||{};c.url=r.url;c.type=r.method;return jQuery.ajax(c)}, method:r.method,url:r.url,absoluteURL: function(s){return _s('http',s)+'cubieboard:9000'+r.url},webSocketURL: function(s){return _s('ws',s)+'cubieboard:9000'+r.url}}}
_nS('controllers.Application'); _root.controllers.Application.setVolume = 
      function(amount) {
      return _wA({method:"POST", url:"/" + "volume/" + (function(k,v) {return v})("amount", amount)})
      }
   
_nS('controllers.Application'); _root.controllers.Application.selectSong = 
      function(pos) {
      return _wA({method:"POST", url:"/" + "selectsong/" + (function(k,v) {return v})("pos", pos)})
      }
   
_nS('controllers.Application'); _root.controllers.Application.setSongPos = 
      function(pos) {
      return _wA({method:"POST", url:"/" + "setsongpos/" + (function(k,v) {return v})("pos", pos)})
      }
   
_nS('controllers.Application'); _root.controllers.Application.addDbEntry = 
      function(url) {
      return _wA({method:"GET", url:"/" + "addDbEntry" + _qS([(function(k,v) {return encodeURIComponent(k)+'='+encodeURIComponent(v)})("url", url)])})
      }
   
_nS('controllers.Application'); _root.controllers.Application.remove = 
      function(id) {
      return _wA({method:"GET", url:"/" + "remove/" + (function(k,v) {return v})("id", id)})
      }
   
})(jsRoutes)
          