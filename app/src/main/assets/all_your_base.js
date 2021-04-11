(function() {
    function ownIt(e) {
        const elementPrototype = e.__proto__;
        if(elementPrototype.modded) {
            console.log("already modded")
            return;
        }

       Object.defineProperty(elementPrototype,'src', {
            configurable: true,
            set: function(val) {   exobridge.setSrc(val);    },
            get: function()    {   return exobridge.getSrc();}
        });

        elementPrototype.load = function() { exobridge.load(); }

        elementPrototype.play = function()  {  exobridge.play();  }
        elementPrototype.pause = function() {  exobridge.pause(); }

        Object.defineProperty(elementPrototype,'autoplay', {
            configurable: true,
            set: function(val) { exobridge.setAutoplay(val);     },
            get: function()   { return exobridge.getAutoplay();  }
        });

        Object.defineProperty(elementPrototype,'duration', {
            configurable: true,
            get: function()   { return exobridge.getDuration();  }
        });



        Object.defineProperty(elementPrototype,'currentTime', {
            configurable: true,
            get: function()   { return exobridge.getCurrentTime();  },
            set: function(val)   { exobridge.setCurrentTime(val); }
        });

        Object.defineProperty(elementPrototype,'seeking', {
            configurable: true,
            get: function()   { return exobridge.seeking()  }
        });

        const _removeAttributeFucntion = elementPrototype.removeAttribute;
        elementPrototype.removeAttribute = function() {
            if (arguments[0]==='src') {
                exobridge.tearDownExoplayer();
            }  else {
                _removeAttributeFucntion.apply(this,arguments);
            }
        }

        const _setAttributeFunction = elementPrototype.setAttribute;
        elementPrototype.setAttribute = function() {
            if (arguments[0]==='src') {
                exobridge.setSrc(arguments[1]);
            }  else {
                _setAttributeFunction.apply(this,arguments);
            }
        }

        Object.defineProperty(elementPrototype,'ended', {
            configurable: true,
            get: function()   { return exobridge.ended()  }
        });

        Object.defineProperty(elementPrototype,'error', {
            configurable: true,
            get: function()   { return JSON.parse(exobridge.getMediaError())  }
        });

        elementPrototype.modded = true;
    }

    ownIt(document.createElement('video'));
    ownIt(document.createElement('source'));

    if(!window.VideoDomBridge) {
        window.VideoDomBridge = {
            raiseEvent : function(event) {
                document.querySelectorAll('video').forEach((vid) => {
                    console.log("DISPATCH " + event)
                    vid.dispatchEvent( new Event(event) );
                });
             }
        }
    }

    // ITV specific tweak to hide video element when it is added to the dom, NOT WELL WRITTEN
    function callback(mutationList, observer) {
      mutationList.forEach( (mutation) => {
        switch(mutation.type) {
          case 'childList':
             document.querySelectorAll("video").forEach( (e)=>{e.style.display="none";} )
            break;
        }
      });
    }
    const targetNode = document.querySelector("body");
    const observerOptions = {
      childList: true,
      attributes: true,

      // Omit (or set to false) to observe only changes to the parent node
      subtree: true
    }
    const observer = new MutationObserver(callback);
    observer.observe(targetNode, observerOptions);

    console.log("LOADED>.....");
})();
