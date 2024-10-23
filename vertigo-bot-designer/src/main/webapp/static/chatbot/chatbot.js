let Chatbot = {};
document.addEventListener('DOMContentLoaded', function () {
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
      let contextMap = {}

      function _createFlottingButton() {
        _button = document.createElement('div');

        _button.className = 'floating-button';

        _button.addEventListener('click', Chatbot.show);
        _button.addEventListener('mouseover', _buttonOver);
        _button.addEventListener('mouseout', _buttonOut);

        const img = document.createElement('img');
        img.className = 'avatar-img';
        img.src = _initParam.avatarUrl;
        img.alt = _initParam.botName;
        _button.appendChild(img);

        const text = document.createElement('div');
        text.className = 'avatar-text';
        text.textContent = _initParam.botName;
        _button.appendChild(text);

        document.body.appendChild(_button);
      }

      function addImageViewerModal() {
        const modal = document.createElement('div');
        modal.id = 'imageViewerModal';
        modal.className = 'modalChatbot';

        const spanClose = document.createElement('span');
        spanClose.id = 'close';
        spanClose.className = 'close';
        spanClose.innerHTML = '&times;';
        spanClose.addEventListener('click', hidePictureModal);

        const img = document.createElement('img');
        img.id = 'imgToView';
        img.className = 'modal-image-chatbot';
        modal.appendChild(spanClose);
        modal.appendChild(img);

        document.body.prepend(modal);
      }

      function addHtmlViewerModal() {
        const modal = document.createElement('div');
        modal.id = 'htmlViewerModal';
        modal.className = 'modalChatbot';

        const spanClose = document.createElement('span');
        spanClose.id = 'close';
        spanClose.className = 'close';
        spanClose.innerHTML = '&times;';
        spanClose.addEventListener('click', hideHtmlModal);

        const contentDiv = document.createElement('div');
        contentDiv.id = 'htmlContent';
        contentDiv.className = 'modal-page-content-chatbot';
        modal.appendChild(spanClose);
        modal.appendChild(contentDiv);

        document.body.prepend(modal);
      }

      function hidePictureModal() {
        document.getElementById('imageViewerModal').style.display = 'none';
      }

      function hideHtmlModal() {
        document.getElementById('htmlViewerModal').style.display = 'none';
      }

      function _buttonOver() {
        _button.style.right = '0px';
      }

      function _buttonOut() {
        _button.style.right = '-90px';
      }

      function _createIframe() {
        _iframe = document.createElement('iframe');

        _iframe.className = 'iframe';
        _iframe.id = 'chatbotIframe'
        _iframe.src = `${_initParam.botIHMBaseUrl}?runnerUrl=${_initParam.runnerUrl}&botName=${_initParam.botName}&useRating=${_initParam.useRating}`;

        if (_initParam.optionalParameters) {
          _initParam.optionalParameters.forEach(function (optionalParam) {
            _iframe.src += '&' + optionalParam.key + '=' + optionalParam.value;
          });
        }
        _iframe.style.visibility = 'visible';

        document.getElementById('botDrawerContent')?.appendChild(_iframe);
      }

      function checkIfConversationAlreadyExists() {
        _iframe.contentWindow.postMessage('conversationExist', '*');
      }

      function _initIframeListener() {
        window.addEventListener(
          'message',
          function (event) {
            if (event.data === 'Chatbot.minimize') {
              Chatbot.minimize();
            }
            else if (event.data.conversationExist !== undefined) {
              if (event.data.conversationExist) {
                Chatbot.show();
              }
            }
            else if (event.data.context) {
              event.ports[0].postMessage({result : contextMap});
            }
            else if (event.data.pictureModal) {
              Chatbot.showPictureModal(event.data.pictureModal);
            }
            else if (event.data.htmlModal) {
              Chatbot.showHtmlModal(event.data.htmlModal)
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
          addImageViewerModal();
          addHtmlViewerModal();
          _initIframeListener();
          _createIframe();
          _iframe.addEventListener('load', function() {
            if (sessionStorage.showChatbot !== undefined) {
              if (sessionStorage.showChatbot === 'true') {
                document.getElementById('botDrawerButton').click();
              }
            } else {
              checkIfConversationAlreadyExists();
            }
          });
        },

        show() {
          sessionStorage.showChatbot = true;
          _iframe.contentWindow.postMessage('start', '*');
        },

        refresh() {
          sessionStorage.showChatbot = true;
          _iframe.contentWindow.postMessage('refresh', '*');
        },

        showPictureModal(src) {
          const modal = document.getElementById('imageViewerModal');
          const modalImg = parent.document.getElementById('imgToView');
          modalImg.src = src;
          modal.style.display = 'block';
        },

        showHtmlModal(innerHTML) {
          const modal = document.getElementById('htmlViewerModal');
          const modalHtml = parent.document.getElementById('htmlContent');
          modalHtml.innerHTML = innerHTML;
          modal.style.display = 'block';
        },

        minimize() {
          sessionStorage.showChatbot = false;
          document.getElementById('botDrawerButton').click();
        },

        hideDrawer() {
          sessionStorage.showChatbot = false;
        },

        clearSessionStorage() {
          sessionStorage.clear();
          _iframe.contentWindow.postMessage('clearSessionStorage', '*');
        },

        updateContextMap(map) {
          contextMap = map;
          Chatbot.refresh()
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
