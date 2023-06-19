// *****************************
// ******CATEGORIE SEQUENCE*****
// *****************************
Blockly.Blocks['cb_sequence'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "sequence",
                "message0": "Suite d'actions ordonnées %1 %2",
                "args0": [
                    {
                        "type": "input_dummy",
                        "theme": "themeDev"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 330,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE SELECTOR*****
// *****************************
Blockly.Blocks['cb_selector'] = {
    init: function() {
        this.jsonInit(
            {
                "message0": 'Actions selon conditions %1 %2',
                "args0": [
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "style": "selector-block",
                // "colour": 0,
                "tooltip": "Execute each node untill success",
                "previousStatement": null,
                "nextStatement": null
            });
    }
};
Blockly.Blocks['cb_condition'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "condition",
                "message0": "Condition %1 type %2 %3 variable: %4 %5 %6 valeur de comparaison %7",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "condition-type",
                        "options": [
                            [
                                "egalite (mots)",
                                "eq"
                            ],
                            [
                                "egalite (nombre)",
                                "eqInt"
                            ],
                            [
                                "contient",
                                "contains"
                            ],
                            [
                                "plus grand que",
                                "gt"
                            ],
                            [
                                "plus petit que",
                                "lt"
                            ],
                            [
                                "champ vide ou non",
                                "fulfilled"
                            ]
                        ]
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "typeVar",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "condition-variable",
                        "text": "nomvar"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "valeur-variable",
                        "text": ""
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 359,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE BUTTON*****
// *****************************
Blockly.Blocks['cb_buttons'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "choose:button",
                "message0": "Liste boutons %1 question associée: %2 %3 variable: %4 %5 %6 option: %7 %8 %9",
                "args0": [
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "?"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "typeVar",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "",
                        "check": "String"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "optionNlu",
                        "options": [
                            [
                                "Désactivé chat",
                                ""
                            ],
                            [
                                "Activé chat",
                                "nlu"
                            ]
                        ]
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 220,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_button'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "button",
                "message0": "Bouton %1 phrase associée: %2 %3 valeur de la variable si clique: %4",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "value2",
                        "text": ""
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value2",
                        "text": ""
                    }
                ],
                "previousStatement": null,
                "nextStatement": "button",
                "colour": 230,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE MESSAGE******
// *****************************
Blockly.Blocks['cb_say'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "say",
                "message0": "Message %1 option: %2",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "say",
                        "text": "",
                        "check": "String"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "optionSay",
                        "options": [
                            [
                                "aucune",
                                ""
                            ],
                            [
                                "toujours",
                                "always"
                            ],
                            [
                                "une fois",
                                "once"
                            ]
                        ]
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 120,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_link'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "link",
                "message0": "lien:  %1",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "url",
                        "text": "url",
                        "check": "String"
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 120,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_mail'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "mail",
                "message0": "Mail %1 Objet:  %2 %3 %4 Message:  %5 %6 %7 Pièce jointe: %8, si oui: %9 %10 %11 Destinataire:  %12 %13",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "mailobject",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "objet"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "mailmessage",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "message"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "optionPj",
                        "options": [
                            [
                                "oui",
                                "yespj"
                            ],
                            [
                                "non",
                                "nopj"
                            ]
                        ]
                    },
                    {
                        "type": "field_dropdown",
                        "name": "mailpj",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "pj"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "maildest",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "mail"
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 120,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE TOPIC********
// *****************************
Blockly.Blocks['cb_topic'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topic",
                "lastDummyAlign0": "CENTRE",
                "message0": "Redirection vers intention code %1",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "CODE"
                    }
                ],
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_topicstart'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topicstart",
                "lastDummyAlign0": "CENTRE",
                "message0": "Redirection intention départ",
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_topicfallback'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topic",
                "lastDummyAlign0": "CENTRE",
                "message0": "Redirection intention fin discussion",
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_topicidle'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topic",
                "lastDummyAlign0": "CENTRE",
                "message0": "Redirection intention reprise discussion",
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_topicend'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topic",
                "lastDummyAlign0": "CENTRE",
                "message0": "Redirection fin",
                "inputsInline": true,
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_choose'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "topic",
                "lastDummyAlign0": "CENTRE",
                "message0": "Recherche d'intention %1 Question: %2",
                "args0":[
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "En quoi d'autre puis-je vous aider ?"
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 50,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// *****CATEGORIE SWITCHCASE****
// *****************************
Blockly.Blocks['cb_switch'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "switch",
                "message0": "Actions selon  %1 variable:  %2 %3 %4 %5",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "typeVar",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "nameVar",
                        "text": "nomVariable"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "previousStatement": null,
                "nextStatement": null,
                "colour": 20,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_case'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "case",
                "message0": "cas  %1 %2 %3 ",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "valueCase",
                        "text": "valeur"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "colour": 25,
                "tooltip": "",
                "helpUrl": "",
                "previousStatement": null,
                "nextStatement": null,
            });
    }
};
// *****************************
// ******CATEGORIE ACTIONKEY*****
// *****************************
Blockly.Blocks['cb_set'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Définir variable  %1 variable: %2 %3 %4 Valeur: %5 %6",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-variable",
                        "options": [
                            [
                                "local",
                                "local"
                            ],
                            [
                                "global",
                                "global"
                            ],
                            [
                                "context",
                                "global/context"
                            ],
                            [
                                "url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "nomvar"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-valeur",
                        "options": [
                            [
                                "nombre",
                                "number"
                            ],
                            [
                                "caractères",
                                "string"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "set-value",
                        "text": ""
                    }
                ],
                "colour": 260,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_copy'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Copie variable %1 variable source: %2 %3 %4 variable cible: %5 %6",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-var-src",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "nom-var-src"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-var-dest",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "type-var-dest",
                        "text": "nomvar"
                    }
                ],
                "colour": 260,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_incrBy'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "%1crémentation %2 variable: %3 %4 %5 du nombre: %6",
                "args0": [
                    {
                        "type": "field_dropdown",
                        "name": "type-incr",
                        "options": [
                            [
                                "In",
                                "incrBy"
                            ],
                            [
                                "Dé",
                                "decr"
                            ]
                        ]
                    },
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "nomvar"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value",
                        "text": "1"
                    }
                ],
                "colour": 280,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_remove'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Suppression variable %1 variable: %2 %3 %4",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "nomvar"
                    },
                    {
                        "type": "input_dummy"
                    }
                ],
                "colour": 260,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE OTHER*****
// *****************************
Blockly.Blocks['cb_random'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "random",
                "message0": "Aléatoire parmi %1 %2",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "colour": 180,
                "tooltip": "",
                "helpUrl": "",
                "previousStatement": null,
                "nextStatement": null
            });
    }
};
Blockly.Blocks['cb_inputString'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Récupérer réponse dans variable %1 question: %2 %3 variable: %4 %5 ",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": ""
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-var",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "nom-var",
                        "text": "nomvar"
                    }
                ],
                "colour": 180,
                "tooltip": "",
                "previousStatement": null,
                "nextStatement": null,
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_append'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Ajout chaine caractères fin variable %1 variable: %2 %3 %4 Valeur: %5",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "nomvar"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value",
                        "text": ""
                    }
                ],
                "colour": 260,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_rating'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "Evaluation/Note %1 Variable stockage note: %2 %3 %4 Question: %5",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type",
                        "options": [
                            [
                                "type local",
                                "local"
                            ],
                            [
                                "type global",
                                "global"
                            ],
                            [
                                "type context",
                                "global/context"
                            ],
                            [
                                "type url",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "note"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value",
                        "text": "Vous pouvez nous laisser une note de satisfaction"
                    }
                ],
                "colour": 180,
                "previousStatement": null,
                "nextStatement": null,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE CONFLUENCE*****
// *****************************

// *****************************
// ******CATEGORIE JIRA*****
// *****************************
Blockly.Blocks['cb_jira'] = {
    init: function() {
        this.jsonInit(
            {
                "message0": 'Jira1 %1 %2',
                "args0": [
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS"
                    }
                ],
                "style": "jira-block",
                "tooltip": "Execute each node untill error",
                "previousStatement": null,
                "nextStatement": null
            });
    }
};
Blockly.Blocks['cb_ask'] = {
    init: function() {
        this.jsonInit(
            {
                "message0": "Ask user. Field :  %1  Message : \" %2 \"",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "arg0",
                        "text": "/user/msg"
                    },
                    {
                        "type": "field_multilinetext",
                        "name": "arg1",
                        "text": "Message"
                    }
                ],
                "colour": "%{BKY_VARIABLES_HUE}",
                "tooltip": "ask /user/msg \"Message\"",
                "previousStatement": null,
                "nextStatement": null
            });
    }
};

// ********************
// ** Generic blocks **
// ********************

let blockNameValidator = function(inputText) {
    let validatedText = inputText.replace(/[^a-zA-Z0-9:]/g,'');

    if (validatedText !== inputText) this.setWarningText("Invalid characters have been deleted.");
    else this.setWarningText(null);

    return validatedText;
}

Blockly.Blocks['cb_:generic_terminal'] = {
    init: function() {
        // nothing to handle our validator in json format :(
        this.appendDummyInput()
            .appendField(new Blockly.FieldTextInput("blockName", blockNameValidator.bind(this)), ":NAME");
        this.appendValueInput("args");

        this.setInputsInline(true);

        this.setColour("%{BKY_PROCEDURES_HUE}");
        this.setTooltip("Generic block");
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
    }
};

Blockly.Blocks['cb_:generic_composite'] = {
    init: function() {
        Blockly.Blocks['cb_:generic_terminal'].init.bind(this)();
        this.appendStatementInput("SUB_BLOCKS");
    }
};

Blockly.Blocks['cb_:generic_arg'] = {
    init: function() {
        this.jsonInit(
            {
                "message0": "%1 %2",
                "args0": [
                    {
                        "type": "field_multilinetext",
                        "name": "genericArg",
                        "text": "arg"
                    },
                    {
                        "type": "input_value",
                        "name": "nextArg"
                    }
                ],
                "output": null,
                "colour": "%{BKY_PROCEDURES_HUE}",
                "tooltip": "Generic argument",
                "helpUrl": ""
            });
    }
};
