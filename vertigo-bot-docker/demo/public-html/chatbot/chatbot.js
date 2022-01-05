let Chatbot = {};
document.addEventListener('DOMContentLoaded', function () {
    const tour = new Shepherd.Tour({
      defaultStepOptions: {
        cancelIcon: {
          enabled: true
        },
        classes: 'class-1 class-2',
        scrollTo: { behavior: 'smooth', block: 'center' }
      }
    });

    tour.addStep({
      title: 'Welcome tour',
      text: `Bienvenue sur notre site de démo. Nous allons vous guider !`,
      attachTo: {
        element: 'body',
        on: 'auto'
      },
      buttons: [
        {
          action() {
            return this.next();
          },
          text: 'Suivant'
        }
      ],
      id: 'start'
    });

    tour.addStep({
      title: 'Chatbot',
      text: `Ici c'est moi, le chatbot ! Tu peux me demander ce que tu veux, je suis la pour ça :)`,
      attachTo: {
        element: 'iframe',
        on: 'auto'
      },
      buttons: [
        {
          action() {
            return this.back();
          },
          classes: 'shepherd-button-secondary',
          text: 'Précédent'
        },
        {
          action() {
            return this.next();
          },
          text: 'Suivant'
        }
      ],
      id: 'chatbot'
    });

    tour.addStep({
      title: 'Page suivante',
      text: `Cliquez ici pour accéder à la page 2`,
      attachTo: {
        element: 'a',
        on: 'auto'
      },
      buttons: [
        {
          action() {
            return this.back();
          },
          classes: 'shepherd-button-secondary',
          text: 'Précédent'
        },
        {
          action() {
            return this.complete();
          },
          text: 'Terminer'
        }
      ],
      id: 'nextPage'
    });
    if (!String.prototype.startsWith) {
      String.prototype.startsWith = function (searchString, position) {
        position = position || 0;
        return this.substr(position, searchString.length) === searchString;
      };
    }

    function _chatbotWrapper() {

      let _initParam = null;
      let _button = null;
      let _iframe = null;

      function _createFlottingButton() {
        _button = document.createElement('div');

        _button.style.cssText = 'position: fixed;' +
                'display: flex;' +
                'align-items: center;' +
                'bottom: 140px;' +
                'right: -90px;' +
                'width: 160px;' +
                'height: 63px;' +
                'transition: right 0.3s ease 0s;' +
                'background-color: #1aab8d;' +
                'color: white;' +
                'border: solid 1px black;' +
                'border-radius: 10px 0 0 10px;' +
                'cursor: pointer;' +
                'overflow: hidden;' +
                '-webkit-user-select: none;' +
                '-ms-user-select: none;' +
                '-moz-user-select: none;' +
                'user-select: none;' +
                'font-family: Verdana, Arial, Helvetica, sans-serif;';

        _button.addEventListener('click', Chatbot.show);
        _button.addEventListener('mouseover', _buttonOver);
        _button.addEventListener('mouseout', _buttonOut);

        const img = document.createElement('img');
        img.style.cssText = 'width: 63px; height: 63px; background-color: white; color: black;';
        img.src = '/chatbot/images/avatar/avatar.png';
        img.alt = _initParam.botName;
        _button.appendChild(img);

        const text = document.createElement('div');
        text.style.cssText = 'flex-grow: 1; text-align: center; font-size: 16px; color: white;';
        text.textContent = _initParam.botName;
        _button.appendChild(text);

        document.body.appendChild(_button);
      }

      function addImageViewerModal() {
        const modal = document.createElement('div');
        modal.id = 'imageViewerModal';
        modal.className = 'modal';

        const spanClose = document.createElement('span');
        spanClose.id = 'close';
        spanClose.className = 'close';
        spanClose.innerHTML = '&times;';
        spanClose.addEventListener('click', hidePictureModal);

        const img = document.createElement('img');
        img.id = 'imgToView';
        img.className = 'modal-content';
        modal.appendChild(spanClose);
        modal.appendChild(img);

        document.body.prepend(modal);
      }

      function hidePictureModal() {
        document.getElementById('imageViewerModal').style.display = 'none';
      }

      function _buttonOver() {
        _button.style.right = '0px';
      }

      function _buttonOut() {
        _button.style.right = '-90px';
      }

      function _createIframe() {
        _iframe = document.createElement('iframe');

        _iframe.style.cssText = 'position: fixed;' +
                'bottom: 0px;' +
                'right: 5px;' +
                'z-index: 10000;' +
                'max-width: 415px;' +
                'min-width: 300px;' +
                'width: 50%; ' +
                'max-height: 750px;' +
                'min-height: 400px;' +
                'height: 95%;' +
                'box-shadow: 0px 0px 15px 5px grey;' +
                'background-color: white;' +
                'border: solid 1px black;';
        _iframe.src = `${_initParam.botIHMBaseUrl}?runnerUrl=${_initParam.runnerUrl}&botName=${_initParam.botName}&useRating=${_initParam.useRating}`;

        if (_initParam.optionalParameters) {
          _initParam.optionalParameters.forEach(function (optionalParam) {
            _iframe.src += '&' + optionalParam.key + '=' + optionalParam.value;
          });
        }

        document.body.appendChild(_iframe);
      }

      function _initIframeListener() {
        window.addEventListener(
          'message',
          function (event) {
            if (event.data === 'Chatbot.minimize') {
              Chatbot.minimize();
            }
            else if (event.data === 'Chatbot.close') {
              Chatbot.close();
            }
            else if (event.data === 'scanContext') {
              const contextPrefix = _initParam.optionalParameters.find(param => param.key === 'contextPrefix');
              const map = {};
              if (contextPrefix !== undefined) {
                document
                    .querySelectorAll(`input[type=hidden][id^=${contextPrefix.value}]`)
                    .forEach(function (input) {
                      map[input.getAttribute('name')] = input.getAttribute('value');
                    });
              }
              event.ports[0].postMessage({result : map});
            }
            else if (event.data.pictureModal) {
              Chatbot.showPictureModal(event.data.pictureModal);
            }
            else if (event.data.jsevent) {
              Chatbot.startJsEvent(event.data.jsevent);
            }
          },
          false
        );
      }

      Chatbot = {
        init(param) {
          _initParam = param;
          _createFlottingButton();
          addImageViewerModal();
          _initIframeListener();
          if (sessionStorage.showChatbot === 'true') {
            this.show();
          }
        },

        show() {
          sessionStorage.showChatbot = true;
          if (_iframe === null) {
            _createIframe();
          } else {
            _iframe.style.visibility = 'visible';
          }
        },

        showPictureModal(src) {
          const modal = document.getElementById('imageViewerModal');
          const modalImg = parent.document.getElementById('imgToView');
          modalImg.src = src;
          modal.style.display = 'block';
        },

        minimize() {
          sessionStorage.showChatbot = false;
          _iframe.style.visibility = 'hidden';
        },

        close() {
          sessionStorage.showChatbot = false;
          document.body.removeChild(_iframe);
          _iframe = null;
        },

        startJsEvent(eventName) {
          if (eventName === 'welcomeTour1') {
            tour.start();
          } else if (eventName === 'welcomeTour2') {
            tour.start();
          }
        }
      };
    }

    _chatbotWrapper();
});
