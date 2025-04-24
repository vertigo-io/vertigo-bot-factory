window.addEventListener('vui-after-page-mounted', function () {
	function isComposite(block) {
		return block.statementInputCount > 0;
	}

	function formatVariable(type, nomVar) {
		if (type.includes("global/context")) {
			return ' /user/' + type + ' ';
		} else {
			return ' /user/' + type + '/' + nomVar.replace(/ /g, '').toLowerCase() + ' ';
		}
	}

	function formatString(nomVar) {
		return ' "' + nomVar.replace(/"/g, "'") + '" ';
	}

	function formatString(nomVar, state) {
		state = isNaN(nomVar)
		if (state === true) return ' "' + nomVar.replace(/"/g, "'") + '" ';
		else return ' ' + nomVar + ' ';
	}

	function formatStringOrNumber(nomVar, state) {
		if (state === true) return ' "' + nomVar.replace(/"/g, "'") + '" ';
		else return ' ' + nomVar + ' ';
	}

	function extractParams(block) {
		let params = "";
		block.inputList.forEach(input => {
			input.fieldRow.forEach(param => {
				if (param.EDITABLE && !param.name.startsWith(':')) {
					let value = param.getValue().replace(/\r?\n/g, "<br />").replace('"', '\\"');
					if (value.includes(' ') || value.includes('"')) {
						params += ' "' + value + '"';
					} else {
						params += ' ' + value;
					}
				}
			});
			if (input.type !== Blockly.inputTypes.STATEMENT && input.connection && input.connection.targetBlock()) {
				params += extractParams(input.connection.targetBlock());
			}
		});
		return params;
	}

	function getBlockName(block) {
		let nameParam = block.getFieldValue(':NAME');
		let name = nameParam ? nameParam : block.type.replace(/^cb_/, '');

		let optValue = block.getFieldValue(':QUALIFY');
		if (optValue) {
			name += optValue
		}
		return name;
	}

	function buildBlock(block) {
		let name = getBlockName(block);
		let params = extractParams(block);


		if (isComposite(block)) {
			let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
			return 'begin ' + name + params + '\r\n' + subBlocks + 'end ' + name + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
		}

		return name + params + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	VUiExtensions.methods = {
		...VUiExtensions.methods,
		toCode: function () {
			let topBlocks = Blockly.mainWorkspace.getTopBlocks().filter(b => b.rendered === true && b.disabled === false);
			let topBlocksCount = topBlocks.length;
			let startCount = topBlocks.filter(b => b.type === "cb_start").length;
			let isOnlySequence = topBlocksCount === 1 && topBlocks[0].type === "cb_sequence";

			// document.getElementById('error_blockly').innerHTML = '';
			var code = Blockly.BotScript.workspaceToCode(Blockly.mainWorkspace);
			if (code !== undefined && code !== null && code !== '') {
				if (startCount === 0 && !isOnlySequence) {
					if (code) {
						code = Blockly.BotScript.prefixLines(code, Blockly.BotScript.INDENT);
					}
					code = 'begin sequence\r\n' + code + 'end sequence';
				}
				VUiPage.vueData.scriptIntention.script = code;
			}
		}
	}

	function getCodeDiagram(event) {
		let topBlocks = Blockly.mainWorkspace.getTopBlocks().filter(b => b.rendered === true && b.disabled === false);
		let topBlocksCount = topBlocks.length;
		let startCount = topBlocks.filter(b => b.type === "cb_start").length;
		let isOnlySequence = topBlocksCount === 1 && topBlocks[0].type === "cb_sequence";

		// document.getElementById('error_blockly').innerHTML = '';
		var code = Blockly.BotScript.workspaceToCode(Blockly.mainWorkspace);

		if (startCount === 0 && !isOnlySequence) {
			if (code) {
				code = Blockly.BotScript.prefixLines(code, Blockly.BotScript.INDENT);
			}
			code = 'begin sequence\r\n' + code + 'end sequence';
		}
		return code
	}


	Blockly.BotScript = new Blockly.Generator('BotScript');
	Blockly.BotScript.INDENT = "\t";

	for (name in Blockly.Blocks) { // same rendering function for all CB blocks
		if (name.startsWith('cb_')) Blockly.BotScript[name] = buildBlock;
	}

// - generic arguments
	Blockly.BotScript['cb_:generic_arg'] = function (block) {
		return null;
	}

// *****************************
// *****CATEGORIE SEQUENCE******
// *****************************

// *****************************
// *****CATEGORIE SELECTOR******
// *****************************
	Blockly.BotScript['cb_condition'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let condition = params[0].getValue();
		let name = getBlockName(block);
		let isAFulfilledCondition;
		if (condition == "fulfilled") isAFulfilledCondition = true;
		else isAFulfilledCondition = false;
		let isAStringCondition;
		if (condition == "eq" || condition == "contains") isAStringCondition = true;
		else isAStringCondition = false;
		if (isAFulfilledCondition) {
			return condition + formatVariable(params[1].getValue(), params[2].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
		} else if (isAStringCondition) {
			return condition + formatVariable(params[1].getValue(), params[2].getValue()) + formatString(params[3].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
		} else {
			return condition + formatVariable(params[1].getValue(), params[2].getValue()) + ' ' + params[3].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
		}
	}


// *****************************
// ******CATEGORIE BUTTON*******
// *****************************
	Blockly.BotScript['cb_buttons'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		let isOption = false;
		if (params[3].getValue() === 'nlu') isOption = true;
		return "begin choose:button" + (isOption ? ':' + params[3].getValue() : '') + formatVariable(params[1].getValue(), params[2].getValue()) + formatString(params[0].getValue()) + "\r\n" + subBlocks + 'end ' + "choose:button" + (isOption ? ':' + params[3].getValue() : '') + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_buttonsfile'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		let isOption = false;
		return "begin choose:button:file" + formatVariable(params[1].getValue(), params[2].getValue()) + formatString(params[0].getValue()) + "\r\n" + subBlocks + 'end ' + "choose:button:file" + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_button'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + ' "' + params[0].getValue() + '" ' + params[1].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_buttonfile'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "button:file" + formatString(params[0].getValue()) + params[1].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_buttonurl'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "button:url" + formatString(params[0].getValue()) + ' ' + params[1].getValue() + ' ' + params[2].getValue() + ' ' + (params[3].getValue() === 'true' ? 'true' : '') + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}


// *****************************
// ******CATEGORIE TOPIC********
// *****************************
	Blockly.BotScript['cb_topicstart'] = function (block) {
		return "topic:start\r\n" + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_topicfallback'] = function (block) {
		return "topic:fallback\r\n" + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_topicidle'] = function (block) {
		return "topic:idle\r\n" + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_topicend'] = function (block) {
		return "topic:end\r\n" + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_choose'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "choose:nlu" + formatString(params[0].getValue()) + "\r\n" + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
// *****************************
// *****CATEGORIE SWITCHCASE****
// *****************************
	Blockly.BotScript['cb_switch'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		return 'begin ' + name + formatVariable(params[0].getValue(), params[1].getValue()) + '\r\n' + subBlocks + 'end ' + name + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_case'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		return 'begin ' + name + formatString(params[0].getValue(), true) + '\r\n' + subBlocks + 'end ' + name + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

// *****************************
// ******CATEGORIE MESSAGE******
// *****************************
	Blockly.BotScript['cb_say'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let isOption = true;
		if (params[1].getValue() == "") isOption = false;

		return "say" + (isOption ? ':' + params[1].getValue() : '') + formatString(params[0].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_link'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "link" + formatString(params[0].getValue()) + (params[1].getValue() === 'false' ? 'false' : '') + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_mail'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let isPJMail = params[4].getValue() === 'yespj'
		return "mail" + (!isPJMail ? "" : ":attachment") +
			formatVariable(params[0].getValue(), params[1].getValue()) +
			formatVariable(params[2].getValue(), params[3].getValue()) +
			(!isPJMail ? "" : formatVariable(params[5].getValue(), params[6].getValue())) +
			formatVariable(params[7].getValue(), params[8].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}


// *****************************
// ****CATEGORIE ACTION-KEY*****
// *****************************
	Blockly.BotScript['cb_set'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		let isNumber = params[2].getValue() === 'number'
		return name + (!isNumber ? "" : "Int") + formatVariable(params[0].getValue(), params[1].getValue()) + formatStringOrNumber(params[3].getValue(), !isNumber) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_copy'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + formatVariable(params[0].getValue(), params[1].getValue()) + formatVariable(params[2].getValue(), params[3].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_incrBy'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		let isIncr = params[0].getValue() === 'incrBy'
		return (isIncr ? 'incrBy' : 'decr') + formatVariable(params[1].getValue(), params[2].getValue()) + params[3].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_remove'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + formatVariable(params[0].getValue(), params[1].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

// *****************************
// ******CATEGORIE OTHER*******
// *****************************
	Blockly.BotScript['cb_append'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_inputString'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + formatVariable(params[1].getValue(), params[2].getValue()) + formatString(params[0].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_rating'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let name = getBlockName(block);
		return name + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

	Blockly.BotScript['cb_binaryrating'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "rating:binary" + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue()) +
			formatString(params[3].getValue()) + formatString(params[4].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_cards'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		return "begin choose:card" + formatVariable(params[1].getValue(), params[2].getValue()) + formatString(params[0].getValue()) + "\r\n" + subBlocks + 'end ' + "choose:card" + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_card'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return "card " + params[2].getValue() + ' ' + params[1].getValue() + formatString(params[0].getValue()) + params[3].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

// *****************************
// *****CATEGORIE CONFLUENCE****
// *****************************
	Blockly.BotScript['cb_confluencesearch'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return 'confluence:search' + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue()) + formatString(params[3].getValue()) + formatString(params[4].getValue()) + params[5].getValue() + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_confluencesearchauto'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return 'confluence:search:auto' + formatString(params[0].getValue()) + formatString(params[1].getValue()) + params[2].getValue() + formatString(params[3].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

// *****************************
// ********CATEGORIE JIRA*******
// *****************************
	Blockly.BotScript['cb_jiraissue'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		return "begin jira:issue:create" + formatString(params[0].getValue()) + "\r\n" + subBlocks + 'end ' + "jira:issue:create" + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	Blockly.BotScript['cb_jirafield'] = function (block) {
		let params = VUiExtensions.methods.getBlockParams(block);
		return 'jira:field' + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue()) + formatString(params[3].getValue()) + '\r\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
});