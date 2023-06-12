function getThemeDev(){
    // <!-- *****THEME****** -->
    const themeDev = Blockly.Theme.defineTheme('themeDev', {
        'base': Blockly.Themes.Classic,
        'blockStyles': {
// selector
            'selector-block': {
                'colourPrimary': '#eab59d',
            },
            'condition-block': {
                'colourPrimary': '#b7b7b7',
            },
// button
            'buttons-block': {
                'colourPrimary': '#9EBAC6',
            },
            'button-block': {
                'colourPrimary': '#BAE1F2',
            },
// topic
            'topic-block': {
                'colourPrimary': '#e3e06f',
            },
// sequence
            'sequence-block':{
                'colourPrimary': '#cba6c7',
            },

// message
            'message-block':{
                'colourPrimary': '#b775ae',
            }
        },
        'categoryStyles': {
// selector category
            'selector-category': {
                'colour': '#eab59d',
            },
        },
        'componentStyles': {
// workspaceBackgroundColour:"#FF0000",
// toolboxBackgroundColour:"#FF0000",
// toolboxForegroundColour:"#FF0000",
// flyoutBackgroundColour:"#FF0000",
// flyoutForegroundColour:"#FF0000",
// flyoutOpacity:"#FF0000",
// scrollbarColour:"#FF0000",
// scrollbarOpacity:"#FF0000",
// insertionMarkerColour:"#FF0000",
// insertionMarkerOpacity:"#FF0000",
// markerColour:"#FF0000",
// cursorColour:"#FF0000",
// 'toolboxForegroundColour': "#FF0000",
        },
        'fontStyle': {
// 'family': 'Georgia, serif',
// 'weight': 'bold',
// 'size': 24
        },
        'startHats': true
    });
    return themeDev
}

function getToolBox(){
    // <!-- *****TOOLBOX****** -->
    var toolbox = {
        "kind": "categoryToolbox",
        "contents": [
            // Catégorie 1: SWITCHCASE
            {
                "kind": "category",
                "name": "%{BKY_CAT_SWITCHCASE}",
                "colour": 20,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_switch"
                    },
                    {
                        "kind": "block",
                        "type": "cb_case"
                    }
                ]
            },
            // Catégorie 2: ACTIONKEY
            {
                "kind": "category",
                "name": "%{BKY_CAT_ACTIONKEY}",
                "colour": 260,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_set"
                    },
                    {
                        "kind": "block",
                        "type": "cb_setInt"
                    },
                    {
                        "kind": "block",
                        "type": "cb_copy"
                    },
                    {
                        "kind": "block",
                        "type": "cb_incrBy"
                    },
                    {
                        "kind": "block",
                        "type": "cb_decr"
                    },
                    {
                        "kind": "block",
                        "type": "cb_remove"
                    },
                ]
            },
            // Catégorie 3: SELECTOR
            {
                "kind": "category",
                "name": "%{BKY_CAT_SELECTOR}",
                "colour": 359,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_selector"
                    },
                    {
                        "kind": "block",
                        "type": "cb_condition"
                    }
                ]
            },
            // Catégorie 4: OTHER
            {
                "kind": "category",
                "name": "%{BKY_CAT_OTHER}",
                "colour": 180,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_random"
                    },
                    {
                        "kind": "block",
                        "type": "cb_append"
                    },
                    {
                        "kind": "block",
                        "type": "cb_inputString"
                    },
                ]
            },
            // 	Catégorie 5: BUTTON
            {
                "kind": "category",
                "name": "%{BKY_CAT_BUTTON}",
                "colour": 220,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_buttons"
                    },
                    {
                        "kind": "block",
                        "type": "cb_button"
                    }
                ]
            },
            // Catégorie 6: CONFLUENCE
            {
                "kind": "category",
                "name": "%{BKY_CAT_CONFLUENCE}",
                "contents": [

                ]
            },
            // Catégorie 7: Jira
            {
                "kind": "category",
                "name": "%{BKY_CAT_JIRA}",
                "contents": [

                ]
            },
            // Catégorie 8: MESSAGE
            {
                "kind": "category",
                "name": "%{BKY_CAT_MESSAGE}",
                "colour": 130,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_message"
                    },
                    {
                        "kind": "block",
                        "type": "cb_link"
                    }
                ]
            },
// Catégorie 9: TOPIC
            {
                "kind": "category",
                "name": "%{BKY_CAT_TOPIC}",
                "colour": 50,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_topic"
                    },
                    {
                        "kind": "block",
                        "type": "cb_topic_start"
                    },
                    {
                        "kind": "block",
                        "type": "cb_topic_fallback"
                    }
                ]
            },
            // 	Catégorie 10: SEQUENCE
            {
                "kind": "category",
                "name": "%{BKY_CAT_SEQUENCE}",
                "colour": 280,
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_sequence"
                    }
                ]
            },
        ]
    };
    return toolbox;
}

var onresize = function(e) {
    // Compute the absolute coordinates and dimensions of blocklyArea.
    var element = blocklyArea;
    var x = 0;
    var y = 0;
    do {
        x += element.offsetLeft;
        y += element.offsetTop;
        element = element.offsetParent;
    } while (element);
    // Position blocklyDiv over blocklyArea.
    blocklyDiv.style.left = x + 'px';
    blocklyDiv.style.top = y + 'px';
    blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
    blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
    Blockly.svgResize(workspace);
};

// function injection(mode){
//     console.log("injection-mode-readonly: "+mode);
    // ****** WORKSPACE ******
    var blocklyArea = document.getElementById('blocklyArea');
    var blocklyDiv = document.getElementById('blocklyDiv');
    var workspace = Blockly.inject(blocklyDiv,
// Blocky.Options : dictionnaire de paires nom/valeur utilisés pour la configuration
        {
            collapse: false,
            // comments: ,
            // css: ,
            // disable: ,
            // grid: ,
            // horizontalLayout: ,
            maxBlocks: 500,
            maxInstances: {},
            media: '/vertigo-bot-designer/static/blockly/media/',
            // move: ,
            // oneBasedIndex: ,
            // readOnly: false,
            // TODO: faire un renderer personnalisé
            renderer: 'zelos',
            // renderer: 'custom_renderer',
            // rtl: ,
            scrollbars: true,
            sounds: false,
            theme: getThemeDev(),
            toolbox: getToolBox(),
            toolboxPosition: "end",
            // trashcan: ,
            // maxTrashcanContents: ,
            // plugins: ,
            // zoom: ,
            zoom:
                {controls: true,
                    wheel: false,
                    startScale: 1.0,
                    maxScale: 3,
                    minScale: 0.3,
                    scaleSpeed: 1.2,
                    pinch: true}
        });



    window.addEventListener('resize', onresize, false);
    onresize();
    workspace.addChangeListener(toCode);
