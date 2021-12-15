let Chatbot = {};
document.addEventListener('DOMContentLoaded', function () {
    if (!String.prototype.startsWith) {
      String.prototype.startsWith = function (searchString, position) {
        position = position || 0
        return this.substr(position, searchString.length) === searchString
      }
    }

    function _chatbotWrapper() {

      let _initParam = null
      let _button = null
      let _iframe = null

      function _createFlottingButton() {
        _button = document.createElement('div')

        _button.style.cssText =
          '\
                position: fixed;\
                display: flex;\
                align-items: center;\
                bottom: 140px;\
                right: -90px;\
                width: 160px;\
                height: 63px;\
                transition: right 0.3s ease 0s;\
                background-color: #1aab8d;\
                color: white;\
                border: solid 1px black;\
                border-radius: 10px 0 0 10px;\
                cursor: pointer;\
                overflow: hidden;\
                -webkit-user-select: none;\
                -ms-user-select: none;\
                -moz-user-select: none;\
                user-select: none;\
                font-family: Verdana, Arial, Helvetica, sans-serif;\
            '

        _button.addEventListener('click', Chatbot.show)
        _button.addEventListener('mouseover', _buttonOver)
        _button.addEventListener('mouseout', _buttonOut)

        const img = document.createElement('img')
        img.style.cssText =
          '\
                width: 63px;\
                height: 63px;\
                background-color: white;\
                color: black;\
            '
        img.src = "/chatbot/images/avatar/avatar.png"
        img.alt = _initParam.botName
        _button.appendChild(img)

        const text = document.createElement('div')
        text.style.cssText =
          '\
                flex-grow: 1;\
                text-align: center;\
                font-size: 16px;\
                color: white;\
            '
        text.textContent = _initParam.botName
        _button.appendChild(text)

        document.body.appendChild(_button)
      }

      function _add_image_viewer_modal() {
        const modal = document.createElement("div");
        modal.id = "imageViewerModal"
        modal.className = "modal"

        const spanClose = document.createElement("span");
        spanClose.id = "close";
        spanClose.className = "close";
        spanClose.innerHTML = "&times;"
        spanClose.addEventListener('click', _hide_picture_modal)

        const img = document.createElement("img");
        img.id = "imgToView";
        img.className = "modal-content"
        modal.appendChild(spanClose);
        modal.appendChild(img);

        document.body.prepend(modal);
      }

      function _hide_picture_modal() {
        document.getElementById("imageViewerModal").style.display = "none";
      }

      function _buttonOver() {
        _button.style.right = '0px'
      }

      function _buttonOut() {
        _button.style.right = '-90px'
      }

      function _createIframe() {
        _iframe = document.createElement('iframe')

        _iframe.style.cssText =
          '\
                position: fixed;\
                bottom: 0px;\
                right: 5px;\
                z-index: 10000;\
                max-width: 415px;\
                min-width: 300px;\
                width: 50%;\
                max-height: 750px;\
                min-height: 400px;\
                height: 95%;\
                box-shadow: 0px 0px 15px 5px grey;\
                background-color: white;\
                border: solid 1px black;\
            '

        _iframe.src =
          _initParam.botIHMBaseUrl +
          '?runnerUrl=' +
          _initParam.runnerUrl +
          '&botName=' + _initParam.botName +
          '&useRating=' + _initParam.useRating

        if (_initParam.optionalParameters) {
          _initParam.optionalParameters.forEach(function (optionalParam) {
            _iframe.src += '&' + optionalParam.key + '=' + optionalParam.value
          });
        }

        document.body.appendChild(_iframe)
      }

      function _initIframeListener() {
        window.addEventListener(
          'message',
          function (event) {
            if (event.data === 'Chatbot.minimize') Chatbot.minimize()
            else if (event.data === 'Chatbot.close') Chatbot.close()
            else if (event.data === 'scanContext') {
              const contextPrefix = _initParam.optionalParameters.find(param => param.key === "contextPrefix")
              const map = {}
              if (contextPrefix !== undefined) {
                document
                    .querySelectorAll('input[type=hidden][id^=' + contextPrefix.value + ']')
                    .forEach(function (input) {
                      map[input.getAttribute('name')] = input.getAttribute('value')
                    })
              }
              event.ports[0].postMessage({result : map})
            }
            else if (event.data.pictureModal) {
              Chatbot.show_picture_modal(event.data.pictureModal)
            }
            else if (event.data.jsevent) {
              Chatbot.startJsEvent(event.data.jsevent)
            }
          },
          false
        )
      }

      Chatbot = {
        init: function (param) {
          _initParam = param
          _createFlottingButton()
          _add_image_viewer_modal()
          _initIframeListener()
          if (sessionStorage.showChatbot === 'true') {
            this.show();
          }
        },

        show: function () {
          sessionStorage.showChatbot = true;
          if (_iframe === null) {
            _createIframe()
          } else {
            _iframe.style.visibility = 'visible'
          }
        },

        show_picture_modal: function(src) {
          const modal = document.getElementById("imageViewerModal");
          const modalImg = parent.document.getElementById("imgToView");
          modalImg.src = src;
          modal.style.display = "block";
        },

        minimize: function () {
          sessionStorage.showChatbot = false;
          _iframe.style.visibility = 'hidden'
        },

        close: function () {
          sessionStorage.showChatbot = false;
          document.body.removeChild(_iframe)
          _iframe = null
        },

        startJsEvent : function(eventName) {
          if (eventName === 'welcomeTour1') {
            alert("Starting welcome tour 1");
          } else if (eventName === 'welcomeTour2') {
            alert("Starting welcome tour 2");
          }
        }
      }
    }

    _chatbotWrapper()
});