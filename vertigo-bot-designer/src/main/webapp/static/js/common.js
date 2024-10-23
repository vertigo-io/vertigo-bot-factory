window.addEventListener('vui-after-page-mounted', function () {
    VUiExtensions.methods = {
        appendToFormData: function (formData, name, value) {
            if (value != null) {
                formData.append(name, value)
            } else {
                formData.append(name, "")
            }
        },
        vueDataParams: function (keys) {
            var params = new FormData();
            for (var i = 0; i < keys.length; i++) {
                var contextKey = keys[i];
                var vueDataValue = this.$data.vueData[contextKey];
                if (vueDataValue && typeof vueDataValue === 'object' && Array.isArray(vueDataValue) === false) {
                    // object
                    Object.keys(vueDataValue).forEach(function (propertyKey) {
                        if (!propertyKey.startsWith("_")) {
                            // _ properties are private and don't belong to the serialized entity
                            if (Array.isArray(vueDataValue[propertyKey])) {
                                vueDataValue[propertyKey].forEach(function (value, index) {
                                    if (vueDataValue[propertyKey][index] && typeof vueDataValue[propertyKey][index] === 'object') {
                                        this.appendToFormData(params, 'vContext[' + contextKey + '][' + propertyKey + ']', vueDataValue[propertyKey][index]['_v_inputValue']);
                                    } else {
                                        this.appendToFormData(params, 'vContext[' + contextKey + '][' + propertyKey + ']', vueDataValue[propertyKey][index]);
                                    }
                                }.bind(this));
                            } else {
                                if (vueDataValue[propertyKey] && typeof vueDataValue[propertyKey] === 'object') {
                                    this.appendToFormData(params, 'vContext[' + contextKey + '][' + propertyKey + ']', vueDataValue[propertyKey]['_v_inputValue']);
                                } else {
                                    this.appendToFormData(params, 'vContext[' + contextKey + '][' + propertyKey + ']', vueDataValue[propertyKey]);
                                }
                            }
                        }
                    }.bind(this));
                } else {
                    //primitive
                    this.appendToFormData(params, 'vContext[' + contextKey + ']', vueDataValue);
                }
            }
            return params;
        },
        objectToFormData: function (object) {
            const formData = new FormData();
            Object.keys(object).forEach(key => this.appendToFormData(formData, key, object[key]));
            return formData;
        }
    }

    function postChangeLocal(locale) {
        VUiPage.httpPostAjax("_changeLocale", {
                'locale': locale,
            },
            {
                onSuccess: function () {
                    location.reload();
                }
            });
    }

    function saveContextEnvironment() {
        VUiPage.httpPostAjax("_saveContextEnvironment", {
            'cenvIdOpt': VertigoUi.vueData.newContextEnvironment.cenvId,
            'label': VertigoUi.vueData.newContextEnvironment.label
        });
    }

    function saveContextEnvironmentValue() {
        VUiPage.httpPostAjax("_saveContextEnvironmentValue", VUiPage.vueDataParams(['newContextEnvironmentValue']));
    }

    let oldHttpPostAjax = VUiPage.httpPostAjax;
    VUiPage.httpPostAjax = function (url, params, options) {
        let paramsResolved;
        if (params instanceof FormData) {
            paramsResolved = params;
        } else {
            paramsResolved = VUiPage.objectToFormData(params);
        }
        oldHttpPostAjax(url, paramsResolved, options);
    }

    function hideOrShowBotToolbar() {
        const botToolbar = document.getElementById("toolBarBots");
        if (botToolbar !== undefined) {
            if (botToolbar.classList.contains("hidden")) {
                botToolbar.classList.remove("hidden");
            } else {
                botToolbar.classList.add("hidden");
            }
        }
    }
});