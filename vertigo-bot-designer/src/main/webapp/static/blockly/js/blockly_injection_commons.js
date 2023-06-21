// *********************
// ******* THEME *******
// *********************
const custom_theme = Blockly.Theme.defineTheme('custom_theme', {
        'base': Blockly.Themes.Classic,
        'blockStyles': {
            'ifelse_block':{
                'colourPrimary': '#ce8364',
            },
            'if_block':{
                'colourPrimary': '#d37e5d',
            },
            'else_block':{
                'colourPrimary': '#d37e5d',
            },
            'selector_block': {
                'colourPrimary': '#eab59d',
            },
            'switch_block':{
                'colourPrimary': '#a48173',
            },
            'case_block':{
                'colourPrimary': '#b99283',
            }
        },
        'categoryStyles': {
            // selector category
            'selector-category': {
                'colour': '#eab59d',
            },
        },
        'startHats': true
    });

// *********************
// ****** TOOLBOX ******
// *********************
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
                        "type": "cb_copy"
                    },
                    {
                        "kind": "block",
                        "type": "cb_incrBy"
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
                    },
                    {
                        "kind": "block",
                        "type": "cb_ifelse"
                    },
                    {
                        "kind": "block",
                        "type": "cb_if"
                    },
                    {
                        "kind": "block",
                        "type": "cb_else"
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
                    {
                        "kind": "block",
                        "type": "cb_rating"
                    }
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
                    },
                    {
                        "kind": "block",
                        "type": "cb_buttonsfile"
                    },
                    {
                        "kind": "block",
                        "type": "cb_buttonfile"
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
                        "type": "cb_say"
                    },
                    {
                        "kind": "block",
                        "type": "cb_link"
                    },
                    {
                        "kind": "block",
                        "type": "cb_mail"
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
                        "type": "cb_topicstart"
                    },
                    {
                        "kind": "block",
                        "type": "cb_topicfallback"
                    },
                    {
                        "kind": "block",
                        "type": "cb_topicidle"
                    },
                    {
                        "kind": "block",
                        "type": "cb_topicend"
                    },
                    {
                        "kind": "block",
                        "type": "cb_choose"
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

// *********************
// ***** ONRESIZE ******
// *********************
var onresize = function(e) {
    if(document.contains(document.getElementById('blocklyDiv'))){
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
    }
};

// *********************
// ***** INJECTION *****
// *********************

function injectionBlockly(mode=false){
    // DEBUG CODE
    // console.log(VertigoUi.vueData.scriptIntention.script)
    let blocklyDivName = 'blocklyDiv'
    // **** OPTIONS ****
    var options = {
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
        readOnly: mode,
        // TODO: faire un renderer personnalisé
        // renderer: 'zelos', // default rendering well adapted for the plateform
        renderer: 'custom_renderer',
        // rtl: ,
        scrollbars: true,
        sounds: false,
        theme: custom_theme,
        toolbox: getToolBox(),
        toolboxPosition: "end",
        // trashcan: ,
        // maxTrashcanContents: ,
        // plugins: ,
        // zoom: ,
        zoom:
            {controls: true,
                wheel: false,
                startScale: 0.6,
                maxScale: 1,
                minScale: 0.3,
                scaleSpeed: 1.2,
                pinch: true}
    }
    if(document.contains(document.getElementById(blocklyDivName))){
        // blocklyArea = document.getElementById('blocklyArea');
        blocklyDiv = document.getElementById(blocklyDivName);
        // console.log("injectionBlockly_editable() from blockly_injection_editable")
        // console.log(workspace)
        // blocklyDiv = null;
        document.getElementById(blocklyDivName).innerHTML = null
        workspace = Blockly.inject(blocklyDiv,options);
        window.addEventListener('resize', onresize, false);
        onresize();
        if(VertigoUi.vueData.scriptIntention.script!=null)fromCode();
        workspace.addChangeListener(toCode);
    }
}