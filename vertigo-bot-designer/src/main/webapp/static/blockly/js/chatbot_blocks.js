// *****************************
// ******CATEGORIE SEQUENCE*****
// *****************************
Blockly.Blocks['cb_sequence'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "sequence",
                "message0": "%{BKY_SEQ_SEQ_TITLE} %1 %2",
                "args0": [
                    {
                        "type": "input_dummy",
                        "theme": "themeDev"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "all"
                        ]
                    }
                ],
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": '%{BKY_SELEC_SELEC_TITLE} %1 %2',
                "args0": [
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "all"
                        ]
                    }
                ],
                "style": "selector_block",
                // "colour": 0,
                "tooltip": "Execute each node untill success",
                "previousStatement": "all",
                "nextStatement": "all",
            });
    }
};
Blockly.Blocks['cb_condition'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "condition",
                "message0": "%{BKY_SELEC_COND_TITLE} %1 %{BKY_SELEC_COND_TYPEOFCOND} %2 %3 %{BKY_COMMONS_VARIABLE_TITLE} %4 %5 %6 %{BKY_SELEC_COND_VALUECOMP} %7",
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
                                "%{BKY_SELEC_COND_EQ_TITLE}",
                                "eq"
                            ],
                            [
                                "%{BKY_SELEC_COND_EQINT_TITLE}",
                                "eqInt"
                            ],
                            [
                                "%{BKY_SELEC_COND_CONTAINS_TITLE}",
                                "contains"
                            ],
                            [
                                "%{BKY_SELEC_COND_GT_TITLE}",
                                "gt"
                            ],
                            [
                                "%{BKY_SELEC_COND_LT_TITLE}",
                                "lt"
                            ],
                            [
                                "%{BKY_SELEC_COND_FULFILLED_TITLE}",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "condition-variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value-variable",
                        "text": ""
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "colour": 359,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_ifelse'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "ifelse",
                "message0": "%{BKY_SELEC_IFELSE_TITLE} %1 %2",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "firstif_type"
                        ]
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "style": "ifelse_block",
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_if'] = {
    init: function() {
        this.jsonInit(
        {
            "type": "if",
            "message0": "%{BKY_SELEC_IF_TITLE} %1 %2",
            "args0": [
                {
                    "type": "input_dummy"
                },
                {
                    "type": "input_statement",
                    "name": "SUB_BLOCKS",
                    "check": "all"
                }
            ],
            "previousStatement": [
                "firstif_type",
                "if_type"
            ],
            "nextStatement": [
                "if_type",
                "else_type"
            ],
            "style": "if_block",
            "tooltip": "",
            "helpUrl": ""
        });
    }
};
Blockly.Blocks['cb_else'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "if",
                "message0": "%{BKY_SELEC_ELSE_TITLE} %1 %2",
                "args0": [
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": "all"
                    }
                ],
                "previousStatement": [
                    "if_type",
                ],
                "nextStatement": "none",
                "style": "else_block",
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
                "message0": "%{BKY_BUT_LISTBUT_TITLE} %1 %{BKY_COMMONS_QUEST_ASSOCIATED} %2 %3 %{BKY_COMMONS_VARIABLE_TITLE} %4 %5 %6 %{BKY_COMMONS_OPTION_TITLE} %7 %8 %9",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "%{BKY_COMMONS_SAMPLE_LISTBUT_SENTENCE}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "typeVar",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "condition-variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}",
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
                                "%{BKY_COMMONS_NLU_CHAT_DISABLED}",
                                ""
                            ],
                            [
                                "%{BKY_COMMONS_NLU_CHAT_ENABLED}",
                                "nlu"
                            ]
                        ]
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "button_type"
                        ]
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "colour": 230,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_buttonsfile'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "choose:button",
                "message0": "%{BKY_BUT_LISTBUTFILE_TITLE} %1 %{BKY_COMMONS_QUEST_ASSOCIATED} %2 %3 %{BKY_COMMONS_VARIABLE_TITLE} %4 %5 %6  %7",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "%{BKY_COMMONS_SAMPLE_LISTBUTFILE_SENTENCE}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "typeVar",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}",
                        "check": "String"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check":[
                            "button_type",
                        ]
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "colour": 230,
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
                "message0": "%{BKY_BUT_BUT_TITLE} %1 %{BKY_COMMONS_SENT_ASSOCIATED} %2 %3 %{BKY_BUT_BUT_VALUEIFCLICK} %4",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "value1",
                        "text": "%{BKY_COMMONS_SAMPLE_BUT_SENTENCE}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value2",
                        "text": "%{BKY_COMMONS_SAMPLE_BUT_VALUEIFCLICK}"
                    }
                ],
                "previousStatement": [
                    "button_type",
                ],
                "nextStatement":[
                    "button_type"
                ],
                "colour": 210,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_buttonfile'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "buttonfile",
                "message0": "%{BKY_BUT_BUTFILE_TITLE} %1 %{BKY_COMMONS_SENT_ASSOCIATED} %2 %3 %{BKY_BUT_BUT_VALUEIFCLICK} %4",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "%{BKY_COMMONS_SAMPLE_BUTFILE_SENTENCE}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value2",
                        "text": "%{BKY_COMMONS_SAMPLE_BUTFILE_VALUEIFCLICK}"
                    }
                ],
                "previousStatement": [
                    "button_type",
                ],
                "nextStatement": [
                    "button_type"
                ],
                "colour": 210,
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
                "message0": "%{BKY_MES_SAY_TITLE} %1 %{BKY_COMMONS_OPTION_TITLE} %2",
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
                                "%{BKY_COMMONS_SAY_OPTION_NONE}",
                                ""
                            ],
                            [
                                "%{BKY_COMMONS_SAY_OPTION_ALWAYS}",
                                "always"
                            ],
                            [
                                "%{BKY_COMMONS_SAY_OPTION_ONCE}",
                                "once"
                            ]
                        ]
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_MES_LINK_TITLE}  %1",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "url",
                        "text": "%{BKY_COMMONS_SAMPLE_LINK}",
                        "check": "String"
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "colour": 120,
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_image'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "image",
                "message0": "%{BKY_MES_IMAGE_TITLE}  %1",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "url",
                        "text": "%{BKY_COMMONS_SAMPLE_IMAGE}",
                        "check": "String"
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_MES_MAIL_TITLE} %1 %{BKY_MES_MAIL_SUBJECT_TITLE} %2 %3 %4 %{BKY_MES_MAIL_MESSAGE_TITLE} %5 %6 %7 %{BKY_MES_MAIL_PJ_TITLE} %8 %{BKY_MES_MAIL_PJ_IFACTIVE} %9 %10 %11 %{BKY_MES_MAIL_RECIPIENT_TITLE} %12 %13",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text":  "%{BKY_COMMONS_SAMPLE_MAIL_SUBJECT}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "mailmessage",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "%{BKY_COMMONS_SAMPLE_MESSAGE}",
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "optionPj",
                        "options": [
                            [
                                "%{BKY_COMMONS_OPTION_YES}",
                                "yespj"
                            ],
                            [
                                "%{BKY_COMMONS_OPTION_NO}",
                                "nopj"
                            ]
                        ]
                    },
                    {
                        "type": "field_dropdown",
                        "name": "mailpj",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "%{BKY_COMMONS_SAMPLE_MAIL_PJ}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "maildest",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "%{BKY_COMMONS_SAMPLE_MAIL_RECIPIENT}"
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_TOP_TITLE} %1",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "NAME",
                        "text": "%{BKY_COMMONS_SAMPLE_TOPIC_CODE}"
                    }
                ],
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_TOPSTART_TITLE}",
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_TOPFALLBACK_TITLE}",
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_TOPIDLE_TITLE}",
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_TOPEND_TITLE}",
                "inputsInline": true,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_TOP_CHOOSE_TITLE} %1 %{BKY_COMMONS_QUEST_ASSOCIATED} %2",
                "args0":[
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "%{BKY_COMMONS_SAMPLE_TOPIC_QUEST}"
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_SWITCHCASE_SWITCH_TITLE} %1 %{BKY_COMMONS_VARIABLE_TITLE} %2 %3 %4 %5",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "nameVar",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "case_type"
                        ]
                    }
                ],
                "previousStatement": "all",
                "nextStatement": "all",
                "style": "switch_block",
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
                "message0": "%{BKY_SWITCHCASE_CASE_TITLE} %1 %2 %3 ",
                "args0": [
                    {
                        "type": "field_input",
                        "name": "valueCase",
                        "text": "%{BKY_COMMONS_VALUE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "all"
                        ]
                    }
                ],
                "style": "case_block",
                "tooltip": "",
                "helpUrl": "",
                "previousStatement": "case_type",
                "nextStatement": "case_type",
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
                "message0": "%{BKY_KEY_SET_TITLE}  %1 %{BKY_COMMONS_VARIABLE_TITLE} %2 %3 %4 %{BKY_COMMONS_VALUE_TITLE} %5 %6",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-valeur",
                        "options": [
                            [
                                "%{BKY_COMMONS_TYPE_NUMBER}",
                                "number"
                            ],
                            [
                                "%{BKY_COMMONS_TYPE_STRING}",
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
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_KEY_COPY_TITLE} %1 %{BKY_KEY_COPY_VAR_SRC} %2 %3 %4 %{BKY_KEY_COPY_VAR_TARGET} %5 %6",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_SAMPLE_COPY_SRC}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-var-dest",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "type-var-dest",
                        "text": "%{BKY_COMMONS_SAMPLE_COPY_TARGET}"
                    }
                ],
                "colour": 260,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%1%{BKY_KEY_INCRBY_TITLE} %2 %{BKY_COMMONS_VARIABLE_TITLE} %3 %4 %5 du nombre: %6",
                "args0": [
                    {
                        "type": "field_dropdown",
                        "name": "type-incr",
                        "options": [
                            [
                                "%{BKY_KEY_INCRBY_MOD_INCR}",
                                "incrBy"
                            ],
                            [
                                "%{BKY_KEY_INCRBY_MOD_DECR}",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value",
                        "text": "%{BKY_KEY_INCRBY_VALUE}"
                    }
                ],
                "colour": 260,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_KEY_REMOVE_TITLE} %1 %{BKY_COMMONS_VARIABLE_TITLE} %2 %3 %4",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
                    },
                    {
                        "type": "input_dummy"
                    }
                ],
                "colour": 260,
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_OTHER_RANDOM_TITLE} %1 %2",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "input_statement",
                        "name": "SUB_BLOCKS",
                        "check": [
                            "all"
                        ]
                    }
                ],
                "colour": 180,
                "tooltip": "",
                "helpUrl": "",
                "previousStatement": "all",
                "nextStatement": "all",
            });
    }
};
Blockly.Blocks['cb_inputString'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "%{BKY_OTHER_INPUTSTRING_TITLE} %1 %{BKY_COMMONS_QUEST_ASSOCIATED} %2 %3 %{BKY_COMMONS_VARIABLE_TITLE} %4 %5 ",
                "args0": [
                    {
                        "type": "input_dummy",
                        "align": "CENTRE"
                    },
                    {
                        "type": "field_input",
                        "name": "question",
                        "text": "%{BKY_COMMONS_SAMPLE_INPUTSTRING_QUEST}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_dropdown",
                        "name": "type-var",
                        "options": [
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "nom-var",
                        "text": "%{BKY_COMMONS_SAMPLE_INPUTSTRING_VAR}"
                    }
                ],
                "colour": 180,
                "tooltip": "",
                "previousStatement": "all",
                "nextStatement": "all",
                "helpUrl": ""
            });
    }
};
Blockly.Blocks['cb_append'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "block_type",
                "message0": "%{BKY_OTHER_APPEND_TITLE} %1 %{BKY_COMMONS_VARIABLE_TITLE} %2 %3 %4 %{BKY_COMMONS_STRING_TITLE} %5",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_VARIABLE_NAMEVAR}"
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
                "previousStatement": "all",
                "nextStatement": "all",
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
                "message0": "%{BKY_OTHER_RATING_TITLE} %1 %{BKY_OTHER_RATING_VAR_TITLE} %2 %3 %4 %{BKY_COMMONS_QUEST_ASSOCIATED} %5",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_SAMPLE_RATING_VAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "value",
                        "text": "%{BKY_COMMONS_SAMPLE_RATING_QUEST}"
                    }
                ],
                "colour": 180,
                "previousStatement": "all",
                "nextStatement": "all",
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE CONFLUENCE*****
// *****************************
Blockly.Blocks['cb_confluencesearch'] = {
    init: function() {
        this.jsonInit(
            {
                "type": "confluence",
                "message0": "%{BKY_CONFLUENCE_SEARCH_TITLE} %1 %{BKY_CONFLUENCE_VAR_TITLE} %2 %3 %4 %{BKY_CONFLUENCE_QUEST1_ASSOCIATED} %5 %6 %{BKY_CONFLUENCE_QUEST2_ASSOCIATED} %7 %8 %{BKY_COMMONS_CODE_ASSOCIATED} %9",
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
                                "%{BKY_COMMONS_VARIABLE_TYPE_LOCAL}",
                                "local"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_GLOBAL}",
                                "global"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_CONTEXT}",
                                "global/context"
                            ],
                            [
                                "%{BKY_COMMONS_VARIABLE_TYPE_URL}",
                                "global/context/url"
                            ]
                        ]
                    },
                    {
                        "type": "field_input",
                        "name": "variable",
                        "text": "%{BKY_COMMONS_SAMPLE_CONFLUENCE_VAR}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "intro",
                        "text": "%{BKY_COMMONS_SAMPLE_CONFLUENCE_QUEST1}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "results",
                        "text": "%{BKY_COMMONS_SAMPLE_CONFLUENCE_QUEST2}"
                    },
                    {
                        "type": "input_dummy"
                    },
                    {
                        "type": "field_input",
                        "name": "CODE",
                        "text": "%{BKY_COMMONS_SAMPLE_CONFLUENCE_CODE}"
                    }
                ],
                "colour": -1,
                "previousStatement": "all",
                "nextStatement": "all",
                "tooltip": "",
                "helpUrl": ""
            });
    }
};
// *****************************
// ******CATEGORIE JIRA*****
// *****************************

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
