'use strict';

(function(window) {

    if(window.ClearVideoViewBackground) {
        console.log("ALREADY FIXED")
        return
    }

  const callback = function(list, observer) {
        list.forEach((mutation) => {

            switch(mutation.type) {
                case 'childList':
                    mutation.addedNodes.forEach((node) => {
                        if(node.id && node.id==='videoPlayerView') {
                            node.style.background = 'none'
                            return;
                        }
                    });
                break;
            }
        });
    };

    const observer = new MutationObserver(callback);
    observer.observe(document.documentElement, {
        childList: true, subtree: true
    });

    window.ClearVideoViewBackground = {}

    document.querySelectorAll("body").forEach( (e)=> {
        e.style.background="none";
    });

    console.log("FIXED!")
})(this);