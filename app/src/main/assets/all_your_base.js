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

    document.querySelector(".app").style.background="none";

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

    console.log("LOADED>.....");
})();
