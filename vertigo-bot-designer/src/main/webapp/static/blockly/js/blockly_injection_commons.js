var workspace;
var varTypeDropdownOptions =[];

// Default values of varTypes (local, global and all contexts)
varTypeDropdownOptions.push(["%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}", "local"])
varTypeDropdownOptions.push(["%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}", "global"])
VertigoUi.vueData.contextValues.forEach(contextValue =>{
    let dropdownOption = []
    dropdownOption.push('context '.concat(contextValue.label))
    dropdownOption.push('global/context/'.concat(contextValue.label))
    varTypeDropdownOptions.push(dropdownOption)
})
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
            },
            'conflu_welcometour_block':{
                'colourPrimary': '#9f9f9f',
            },
            'jirafield_block':{
                'colourPrimary': '#beb419',
                'hat':'cap'
            },
            'jiraissue_block':{
                'colourPrimary': '#b49926',
                'hat':'cap'
            }
        },
        'categoryStyles': {
            // selector category
            'selector-category': {
                'colour': '#eab59d',
            },
            'confluence_category':{
                'colour': '#9f9f9f',
            },
            'jira_category':{
                'colour': '#b49926',
            },
            'template-category':{
                'colour': '#41b998',
            }
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
                "id": "category-other",
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
                    },
                    {
                        "kind": "block",
                        "type": "cb_welcometour"
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
                "categoryStyle": "confluence_category",
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_confluencesearch"
                    }
                ]
            },
            // Catégorie 7: Jira
            {
                "kind": "category",
                "name": "%{BKY_CAT_JIRA}",
                "categoryStyle": "jira_category",
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_jiraissue"
                    },{
                        "kind": "block",
                        "type": "cb_jirafield"
                    }
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
                    },
                    {
                        "kind": "block",
                        "type": "cb_image"
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
            // 	Categorie 11: TEMPLATE
            {
                "kind": "category",
                "name": "%{BKY_CAT_TEMPLATE}",
                "categoryStyle": "template-category",
                "contents": [
                    {
                        "kind": "block",
                        "type": "cb_template_listOfButtons"
                    },
                    {
                        "kind": "block",
                        "type": "cb_template_ifelse"
                    },
                    {
                        "kind": "block",
                        "type": "cb_template_jira"
                    },
                    {
                        "kind": "block",
                        "type": "cb_template_FAQdynamique"
                    }
                ]
            },
        ]
    };
    // if disableNlu, remove the block corresponding to grammar cb_inputString
    if(VertigoUi.vueData.chatbotCustomConfig.disableNlu){
        let indexOfBlockInputString;
        let indexOfCatInputString;
        toolbox.contents.forEach(cat =>{
            if(cat.id==='category-other') indexOfCatInputString = toolbox.contents.indexOf(cat)
        })
        toolbox.contents[indexOfCatInputString].contents.forEach(block =>{
            if(block.type==='cb_inputString') indexOfBlockInputString = toolbox.contents[indexOfCatInputString].contents.indexOf(block)
        })
        toolbox.contents[indexOfCatInputString].contents.splice(indexOfBlockInputString,1)
    }
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
    let blocklyDivName = 'blocklyDiv'
    // **** OPTIONS ****
    var options = {
        collapse: false,
        maxBlocks: 500,
        maxInstances: {},
        media: '/vertigo-bot-designer/static/blockly/media/',
        readOnly: mode,
        renderer: 'custom_renderer',
        scrollbars: true,
        sounds: false,
        theme: custom_theme,
        toolbox: getToolBox(),
        toolboxPosition: "end",
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
        blocklyDiv = document.getElementById(blocklyDivName);
        document.getElementById(blocklyDivName).innerHTML = null
        workspace = Blockly.inject(blocklyDiv,options);
        window.addEventListener('resize', onresize, false);
        onresize();
        if(VertigoUi.vueData.scriptIntention.script!=null)fromCode();
        workspace.addChangeListener(toCode);
        workspace.addChangeListener(importBlocklyTemplate)
        workspace.addChangeListener(nameVarValidator)
    }
}

function  nameVarValidator(event) {
    if (event.type == 'change' && event.name.startsWith("typeVar")) {
        let varName = "nameVar" + event.name.substring(7)
        if (event.newValue.startsWith("global/context/")) {
            workspace.getBlockById(event.blockId).setFieldValue("     ", varName)
            workspace.getBlockById(event.blockId).getField(varName).setEnabled(false)
        } else if (event.oldValue.startsWith("global/context/")) {
            workspace.getBlockById(event.blockId).setFieldValue("name_var", varName)
            workspace.getBlockById(event.blockId).getField(varName).setEnabled(true)
        }
    }
}


function importBlocklyTemplate(event){
    if(event.type == Blockly.Events.CREATE && event.json.type.startsWith('cb_template')){
        let locale = VertigoUi.vueData.locale
        let templateBlock = workspace.getBlockById(event.blockId);
        // the order of adding blocks connection is important (call to function addConnectionSubBlock and addConnectionNext)
        switch (templateBlock.type) {
            case 'cb_template_listOfButtons':

                var sequence = workspace.newBlock('cb_sequence')

                var firstMessage = workspace.newBlock('cb_say')
                firstMessage.setFieldValue((locale==='fr_FR' ? "Voici une liste de boutons" : "List of buttons"),"label")

                var listButtons = workspace.newBlock('cb_buttons')
                listButtons.setFieldValue((locale==='fr_FR' ? "Quelle est votre couleur préférée" : "What is your favorite colour"),"question")
                listButtons.setFieldValue("color","nameVar")

                var firstButton = workspace.newBlock('cb_button')
                firstButton.setFieldValue((locale==='fr_FR' ? "Rouge" : "Red"),"label")
                firstButton.setFieldValue("RED","code")

                var secondButton = workspace.newBlock('cb_button')
                secondButton.setFieldValue((locale==='fr_FR' ? "Bleu" : "Blue"),"label")
                secondButton.setFieldValue("BLUE","code")

                var thirdButton = workspace.newBlock('cb_button')
                thirdButton.setFieldValue((locale==='fr_FR' ? "Vert" : "Green"),"label")
                thirdButton.setFieldValue("GREEN","code")

                addAllConnectionSubBlock(listButtons, [firstButton, secondButton, thirdButton])


                var switchBlock = workspace.newBlock('cb_switch')
                switchBlock.setFieldValue("color","nameVar")

                var firstCase = workspace.newBlock('cb_case')
                firstCase.setFieldValue(firstButton.getFieldValue("code"),"value")

                var messageFirstCase = workspace.newBlock('cb_say')
                messageFirstCase.setFieldValue((locale==='fr_FR' ? "Moi aussi ma couleur préférée est le "+firstButton.getFieldValue("label").toLowerCase()+" !" : "My favorite color is "+firstButton.getFieldValue("label").toLowerCase()+" too!"),"label")

                var secondCase = workspace.newBlock('cb_case')
                secondCase.setFieldValue(secondButton.getFieldValue("code"),"value")

                var messageSecondCase = workspace.newBlock('cb_say')
                messageSecondCase.setFieldValue((locale==='fr_FR' ? "Moi aussi ma couleur préférée est le "+secondButton.getFieldValue("label").toLowerCase()+" !" : "My favorite color is "+secondButton.getFieldValue("label").toLowerCase()+" too!"),"label")

                var thirdCase = workspace.newBlock('cb_case')
                thirdCase.setFieldValue(thirdButton.getFieldValue("code"),"value")

                var messageThirdCase = workspace.newBlock('cb_say')
                messageThirdCase.setFieldValue((locale==='fr_FR' ? "Moi ma couleur préférée est le " + thirdButton.getFieldValue("label").toLowerCase()+" !" : "My favorite color is"+thirdButton.getFieldValue("label").toLowerCase()+" too!"),"label")

                addConnectionSubBlock(firstCase, messageFirstCase)
                addConnectionSubBlock(secondCase, messageSecondCase)
                addConnectionSubBlock(thirdCase, messageThirdCase)
                addAllConnectionSubBlock(switchBlock, [firstCase, secondCase, thirdCase])

                var lastMessage = workspace.newBlock('cb_say')
                lastMessage.setFieldValue((locale==='fr_FR' ? "Tout ceci était un modèle utilisant une liste de boutons" : "All of this was a template using list of buttons"),"label")

                sequence.initSvg();
                sequence.render();
                addAllConnectionSubBlock(sequence, [firstMessage, listButtons, switchBlock, lastMessage])

                templateBlock.dispose()
                break;
            case 'cb_template_ifelse':
                var sequence = workspace.newBlock('cb_sequence')

                var firstMessage = workspace.newBlock('cb_say')
                firstMessage.setFieldValue((locale==='fr_FR' ? "Vous souhaitez consulter la fiche d'information de votre ville" : "You want to consult data of your city"),"label")

                var inputStringBlock = workspace.newBlock('cb_inputString')
                inputStringBlock.setFieldValue((locale==='fr_FR' ? "Ecris le nom de ta ville" : "Give me the name of your city ?"),"question")
                inputStringBlock.setFieldValue("city","nameVar")

                var ifelseBlock = workspace.newBlock('cb_ifelse')

                var firstIfBlock = workspace.newBlock('cb_if')
                var secondIfBlock = workspace.newBlock('cb_if')
                var elseBlock = workspace.newBlock('cb_else')

                var conditionFirstBlock = workspace.newBlock('cb_condition')
                conditionFirstBlock.setFieldValue("eq","condition-type")
                conditionFirstBlock.setFieldValue("Paris","value")
                conditionFirstBlock.setFieldValue("city","nameVar")

                var messageFirstBlock = workspace.newBlock('cb_say')
                var topicFirstBlock = workspace.newBlock('cb_topic')
                messageFirstBlock.setFieldValue((locale==='fr_FR' ? "Ok il s'agit de Paris" : "Ok, it's Paris"), "label")
                topicFirstBlock.setFieldValue("CITY1","code")

                var conditionSecondBlock = workspace.newBlock('cb_condition')
                conditionSecondBlock.setFieldValue("eq","condition-type")
                conditionSecondBlock.setFieldValue("Lyon","value")
                conditionSecondBlock.setFieldValue("city","nameVar")

                addAllConnectionSubBlock(firstIfBlock,[conditionFirstBlock, messageFirstBlock, topicFirstBlock])

                var messageSecondBlock = workspace.newBlock('cb_say')
                var topicSecondBlock = workspace.newBlock('cb_topic')
                messageSecondBlock.setFieldValue((locale==='fr_FR' ? "Ok il s'agit de Lyon" : "Ok, it's Lyon"), "label")
                topicSecondBlock.setFieldValue("CITY2","code")

                addAllConnectionSubBlock(secondIfBlock,[conditionSecondBlock, messageSecondBlock, topicSecondBlock])

                var messageElseBlock = workspace.newBlock('cb_say')
                messageElseBlock.setFieldValue((locale==='fr_FR' ? "Ok tu habites à {{/user/local/city}}" : "Ok you live in {{/user/local/city}}"), "label")

                var chooseBlock =  workspace.newBlock('cb_choose')

                addAllConnectionSubBlock(elseBlock, [messageElseBlock, chooseBlock])

                addAllConnectionSubBlock(ifelseBlock, [firstIfBlock, secondIfBlock, elseBlock])

                sequence.initSvg();
                sequence.render();
                addAllConnectionSubBlock(sequence, [firstMessage, inputStringBlock, ifelseBlock])

                templateBlock.dispose()
                break;
            case 'cb_template_jira':
                var sequence = workspace.newBlock('cb_sequence')

                var firstMessage = workspace.newBlock('cb_say')
                firstMessage.setFieldValue((locale==='fr_FR' ? "Ceci est un exemple de création de ticket Jira suite à une anomalie" : "This is a example of a creation of a jira ticket"),"label")

                var jiraIssueBlock = workspace.newBlock('cb_jiraissue') //reference, scenario, attendu, obtenu, reproductibilite, criticite

                var fieldJira =[]
                fieldJira.push(['reference', (locale==='fr_FR' ? "Bien sûr, quelle est la référence du client impacté par cette anomalie ?" : "Of course, what is the reference number of the customer affected by this anomaly ?")])
                fieldJira.push(['scenario', (locale==='fr_FR' ? "Quel est le scénario de test ?" : "What is the test scenario ?")])
                fieldJira.push(['attendu', (locale==='fr_FR' ? "Quel est le résultat attendu ?" : "What is the expected result ?")])
                fieldJira.push(['obtenu', (locale==='fr_FR' ? "Quel est le résultat obtenu ?" : "What is the real result ?")])
                fieldJira.push(['reproductibilite', (locale==='fr_FR' ? "Quelle est la reproductibilité ?" : "How reproducible is it ?")])
                fieldJira.push(['criticite', (locale==='fr_FR' ? "Quelle est la criticité ?" : "What is the criticality?")])
                var fieldJiraBlocks = []
                for(let i=0; i< fieldJira.length; i++){
                    let fieldJiraBlock = workspace.newBlock('cb_jirafield')
                    fieldJiraBlock.setFieldValue(fieldJira[i][0],"nameVar")
                    fieldJiraBlock.setFieldValue(fieldJira[i][1],"question")
                    fieldJiraBlocks.push(fieldJiraBlock)
                }
                addAllConnectionSubBlock(jiraIssueBlock, fieldJiraBlocks)


                sequence.initSvg();
                sequence.render();
                addAllConnectionSubBlock(sequence, [firstMessage, jiraIssueBlock])

                templateBlock.dispose()
                break;
            case 'cb_template_FAQdynamique':
                var sequence = workspace.newBlock('cb_sequence')

                var firstMessage = workspace.newBlock('cb_say')
                firstMessage.setFieldValue((locale==='fr_FR' ? "Vous êtes sur la FAQ générale" : "You are on the global FAQs page"),"label")

                var buttonsFAQ = workspace.newBlock('cb_buttons') // question
                buttonsFAQ.setFieldValue((locale==='fr_FR' ? "Que souhaitez-vous faire ?" : "What do you want to do ?"), "question")
                buttonsFAQ.setFieldValue("faq", "nameVar")
                let buttons = []
                buttons.push([(locale==='fr_FR' ? "Créer un compte" : "Create an account"), "COMPTE"])
                buttons.push([(locale==='fr_FR' ? "Faire une demande de bourse" : "Apply for a grant"), "BOURSE"])
                buttons.push([(locale==='fr_FR' ? "Consulter un assistant" : "Consult an assistant"), "ASSISTANT"])
                let buttonsBlocks = []
                for(let i=0; i<buttons.length; i++){
                    let buttonBlock = workspace.newBlock('cb_button')
                    buttonBlock.setFieldValue(buttons[i][0],"label")
                    buttonBlock.setFieldValue(buttons[i][1],"code")
                    buttonsBlocks.push(buttonBlock)
                }

                addAllConnectionSubBlock(buttonsFAQ,buttonsBlocks)

                var switchFAQ = workspace.newBlock('cb_switch')
                switchFAQ.setFieldValue(buttonsFAQ.getFieldValue("nameVar"),"nameVar")
                let casesBlocks = []
                for(let i= 0; i<buttons.length; i++){
                    let caseBlock = workspace.newBlock('cb_case')
                    caseBlock.setFieldValue(buttons[i][1], "value")
                    casesBlocks.push(caseBlock)
                }

                var buttonsFAQAccount = workspace.newBlock('cb_buttons')
                buttonsFAQAccount.setFieldValue((locale==='fr_FR' ? "Quel type de compte ?" : "What type of account ?"), "question")
                buttonsFAQAccount.setFieldValue("account", "nameVar")
                let buttonsAccount = []
                buttonsAccount.push([(locale==='fr_FR' ? "Compte Acheteur" : "Buyer account"), "ACHETEUR"])
                buttonsAccount.push([(locale==='fr_FR' ? "Compte Vendeur" : "Sales account"), "VENDEUR"])
                let buttonsBlocksAccount = []
                for(let i=0; i<buttonsAccount.length; i++) {
                        let buttonBlock = workspace.newBlock('cb_button')
                        buttonBlock.setFieldValue(buttonsAccount[i][0],"label")
                        buttonBlock.setFieldValue(buttonsAccount[i][1],"code")
                        buttonsBlocksAccount.push(buttonBlock)
                }
                addAllConnectionSubBlock(buttonsFAQAccount,buttonsBlocksAccount)

                var switchFAQAccount = workspace.newBlock('cb_switch')
                switchFAQAccount.setFieldValue(buttonsFAQAccount.getFieldValue("nameVar"),"nameVar")
                let casesBlocksAccount = []
                for(let i= 0; i<buttonsAccount.length; i++){
                    let caseBlock = workspace.newBlock('cb_case')
                    caseBlock.setFieldValue(buttonsAccount[i][1], "value")
                    casesBlocksAccount.push(caseBlock)
                }

                casesBlocksAccount.forEach(caseAccount => {
                    let topic = workspace.newBlock('cb_topic')
                    topic.setFieldValue(caseAccount.getFieldValue("value").toString().substr(0,3).concat("_COMPTE"),"code")
                    addConnectionSubBlock(caseAccount, topic)
                });

                addAllConnectionSubBlock(switchFAQAccount,casesBlocksAccount)

                // first major case: COMPTE
                addAllConnectionSubBlock(casesBlocks[0], [buttonsFAQAccount, switchFAQAccount])


                // second major case: BOURSE
                var linkBourseBlock = workspace.newBlock('cb_link')
                linkBourseBlock.setFieldValue("https://bourseexample.kleegroup.com","url")
                addConnectionSubBlock(casesBlocks[1], linkBourseBlock)

                // third major case: ASSISTANT
                var linkAssistantBlock = workspace.newBlock('cb_link')
                linkAssistantBlock.setFieldValue("https://assistantexample.kleegroup.com","url")
                addConnectionSubBlock(casesBlocks[2], linkAssistantBlock)

                addAllConnectionSubBlock(switchFAQ,casesBlocks)

                var lastMessage = workspace.newBlock('cb_say')
                lastMessage.setFieldValue((locale==='fr_FR' ? "Vous allez être redirigé vers la FAQ général..." : "You will be redirected to the general FAQ..."),"label")

                var topicIdle = workspace.newBlock('cb_topic')
                topicIdle.setFieldValue("TEMPFAQ","code")

                sequence.initSvg();
                sequence.render();
                addAllConnectionSubBlock(sequence, [firstMessage, buttonsFAQ, switchFAQ, lastMessage, topicIdle])

                templateBlock.dispose()
                break;
            default:
                templateBlock.dispose()
                return;
        }
    }
}

function addConnectionSubBlock(blockParent, blockChildren) {
    blockChildren.initSvg()
    blockChildren.render()
    blockParent.getInput('SUB_BLOCKS').connection.connect(blockChildren.previousConnection)
}

function addConnectionNext(blockParent, blockChildren){
    blockChildren.initSvg()
    blockChildren.render()
    blockChildren.previousConnection.connect(blockParent.nextConnection)
}
// add all blocks of blocksChildren in the input statement ('subblock' of the blockParent
function addAllConnectionSubBlock(blockParent,blocksChildren){
    for(let i=blocksChildren.length-1; i>0; i--){
        addConnectionNext(blocksChildren[i-1], blocksChildren[i])
    }
    addConnectionSubBlock(blockParent,blocksChildren[0])
}