function fromCode() {
	let workspace = Blockly.mainWorkspace;
	workspace.clear();
	workspace.clearUndo();
	setTimeout(() => Blockly.mainWorkspace.trashcan.emptyContents(), 0); // not working if not asynch

	var text = VertigoUi.vueData.scriptIntention.script
	let parsed = parseText(text);
	if (parsed.error) {

	} else {
		doBuildBlocks(parsed);
	}
}

function parseText(txt) {
	// parse text
	let commands = [];
	txt.split('\n').forEach(line => {
		let extract = line.replace(/--.*/,'').match(/\s*(((BEGIN)|(END))\s+)?(\S+)(\s+(.+))?/i);
		if (extract) {
			commands.push({type: extract[5],
				isComposite: extract[2] !== undefined,
				isBegin: extract[3] !== undefined,
				params: extractTxtParams(extract[7])
			});
		}
	});

	// resolve hierarchy
	let compositeStack = [];
	let commandsStack = [];
	let currentCommands = [];
	for (let i = 0; i < commands.length; i++) {
		let command = commands[i];
		if (command.isComposite) {
			if (command.isBegin) {
				commandsStack.push(currentCommands);
				currentCommands = [];
				compositeStack.push(command);
			} else {
				let lastComposite = compositeStack.pop();
				if (!lastComposite) return {error: "End command '" + command.type + "' without begin."};
				if (lastComposite.type !== command.type) return {error: "Command '" + lastComposite.type + "' not ended properly (end '" + command.type + "' found)"};

				lastComposite.commands = currentCommands;
				currentCommands = commandsStack.pop();
				currentCommands.push(lastComposite);
			}
		} else {
			currentCommands.push(command);
		}
	}

	if (compositeStack.length > 0) return {error: "Command '" + compositeStack.pop().type + "' not ended."};

	return currentCommands;
}

function extractTxtParams(paramsTxt) {
	if (!paramsTxt) return [];

	let out = [];
	let wasQuoted = false;
	let isQuoted = false;
	let isEscaping = false;
	let curentArg = "";

	for (let i = 0; i < paramsTxt.length; i++) {
		let c = paramsTxt[i];
		if (wasQuoted) {
			// consume next character after end quote
			wasQuoted = false;
			if (!c.match(/\s/)) i--; // workaround to consume only whitespace in case of malformed parameter
		} else if (!isEscaping && c === '"') {
			// Quote handling
			if (!isQuoted) {
				if (curentArg.length > 0) { // workaround on malformed argument
					out.push(curentArg);
					curentArg = "";
				}
				isQuoted = true;
			} else {
				isQuoted = false;
				wasQuoted = true;
				out.push(curentArg);
				curentArg = "";
			}
		} else {
			isEscaping = isQuoted && !isEscaping && c === '\\';

			if ((isQuoted && !isEscaping) || (!isQuoted && !c.match(/\s/))) {
				curentArg += c;
			}

			if (!isQuoted && c.match(/\s/) && curentArg.length > 0) {
				out.push(curentArg);
				curentArg = "";
			}
		}
	}

	// final arg
	if (curentArg.length > 0) {
		out.push(curentArg);
	}

	return out;
}

function doBuildBlocks(parsed) {
	let previousBlock;
	let firstBlock;
	parsed.forEach(blockToBuild => {
		let newBlock = createBlock(blockToBuild.type, blockToBuild.isComposite, blockToBuild.params);
		if (!firstBlock) firstBlock = newBlock;

		if (blockToBuild.isComposite && blockToBuild.commands.length > 0) {
			let rootSubblock = doBuildBlocks(blockToBuild.commands);
			connectToParent(rootSubblock, newBlock);
		}

		if (previousBlock) {
			connectToNext(previousBlock, newBlock);
		}
		previousBlock = newBlock;
	});
	return firstBlock;
}

function connectToNext(block1, block2) {
	block1.nextConnection.connect(block2.previousConnection);
}

function connectToParent(firstChild, parent) {
	let parentStatementInput = parent.inputList.filter(i => i.type === Blockly.inputTypes.STATEMENT)[0];
	parentStatementInput.connection.connect(firstChild.previousConnection);
}

function resolveCbBlockType(type) {
	let blockTargetTypeName = "cb_" + type;
	// first, search for exact match
	for (name in Blockly.Blocks) {
		if (name === blockTargetTypeName) {
			return {type: blockTargetTypeName};
		}
	}
	switch (type) {
		case 'topic:start':
			return {type: "cb_topicstart"};
		case 'topic:fallback':
			return {type: "cb_topicfallback"};
			break;
		case 'topic:idle':
			return {type: "cb_topicidle"};
			break;
		case 'topic:end':
			return {type: "cb_topicend"};
			break;
		case 'say:once':
			return {type: "cb_say", qualifier: "once"};
		case 'say:always':
			return {type: "cb_say", qualifier: "always"};
		case 'choose:nlu':
			return {type: "cb_choose"};
			break;
		case 'choose:button':
			return {type: "cb_buttons"};
			break;
		case 'choose:button:nlu':
			return {type: "cb_buttons", qualifier: "nlu"};
			break;
		case 'choose:button:file':
			return {type: "cb_buttonsfile"};
			break;
		case 'choose:card':
			return {type: "cb_cards"};
			break;
		case 'eq':
			return {type: "cb_condition", qualifier: "eq"};
			break;
		case 'eqInt':
			return {type: "cb_condition", qualifier: "eqInt"};
			break;
		case 'contains':
			return {type: "cb_condition", qualifier: "contains"};
			break;
		case 'gt':
			return {type: "cb_condition", qualifier: "gt"};
			break;
		case 'lt':
			return {type: "cb_condition", qualifier: "lt"};
			break;
		case 'fulfilled':
			return {type: "cb_condition", qualifier: "fulfilled"};
			break;
		case "set":
			return {type: "cb_set"}
		case "setInt":
			return {type: "cb_set",  qualifier: "setInt"}
		case "decr":
			return {type: "cb_incrBy",  qualifier: "decr"}
		case "mail":
			return {type: "cb_mail"}
		case "mail:attachment":
			return {type: "cb_mail",  qualifier: "attachment"}
		case "button:file":
			return {type: "cb_buttonfile"}
		case "button:url":
			return {type: "cb_buttonurl"}
		case "card":
			return {type: "cb_card"}
		case "confluence:search":
			return {type: "cb_confluencesearch"}
		case "jira:issue:create":
			return {type: "cb_jiraissue"}
		case "jira:field":
			return {type: "cb_jirafield"}
		default:
			break;
	}

	// else search for name without qualifier ("xxx:qualifier")
	let blockSimplifiedTargetTypeName = "cb_" + type.replace(/:.*/,'');
	for (name in Blockly.Blocks) {
		if (name === blockSimplifiedTargetTypeName) {
			let qualifier = type.substring(type.indexOf(':'));
			return {type: blockSimplifiedTargetTypeName, qualifier: qualifier};
		}
	}
	// else, not found
	return null;
}

function paramsFormatVariable(lien) {
	let lienParams = ['','']
	if(lien.includes('/user/global/context')){
		lienParams[0] = lien.substring(6)
	}else if(lien.includes('/user/global')){
		lienParams[0] = 'global'
		lienParams[1] = lien.substring(13);
	}else if(lien.includes('/user/local')){
		lienParams[0] = 'local'
		lienParams[1] = lien.substring(12);
	}
	return lienParams;
}

function createBlock(type, isComposite, params = []) {
	// search for specific block
	// or else insert a generic block
	let resolvedType = resolveCbBlockType(type);

	if (resolvedType === null) {
		return createGenericBlock(type, isComposite, params);
	}

	let block = workspace.newBlock(resolvedType.type);

	let blockParams = getBlockParams(block).filter(p => !p.name.startsWith(':'));
	if (params.length > blockParams.length || checkIsComposite(block) !== isComposite || (resolvedType.qualifier && !checkQualifyOption(block, resolvedType.qualifier))) {
		// parameters do not match the block definition, fallback to generic block
		block.dispose(); // cleaning already created specific block
		return createGenericBlock(type, isComposite, params);
	}



	if(resolvedType.type==='cb_say'){
		if(resolvedType.qualifier)blockParams[1].setValue(resolvedType.qualifier)
	}
	else if(resolvedType.type==='cb_buttons'){
		let temp = params[0]
		params[0] = params[1]
		params[1]= temp;

		let lienParams = paramsFormatVariable(params[1])
		params[1]=lienParams[0]
		params.push(lienParams[1])
		// blocksParams[0] = ;// question
		// blocksParams[1] = ;// typeVar
		// blocksParams[2] = ;// variable
		if(resolvedType.qualifier)params.push(resolvedType.qualifier)
	}else if(resolvedType.type==='cb_switch'){
		let lienParams = paramsFormatVariable(params[0])
		params[0]=lienParams[0]
		params.push(lienParams[1])
	}else if(resolvedType.type==='cb_condition'){
		let lienParams = paramsFormatVariable(params[0])
		if(resolvedType.qualifier)params[0] = resolvedType.qualifier;
		let valueTemp = params[1];
		params[1] = lienParams[0];
		params.push(lienParams[1])
		if(resolvedType.qualifier && resolvedType.qualifier!='fulfilled') params.push(valueTemp)
	}else if(resolvedType.type==='cb_set'){
		let lienParams = paramsFormatVariable(params[0])
		let valueTemp = params[1];
		params[0] = lienParams[0]
		params[1] = lienParams[1]
		if(resolvedType.qualifier && resolvedType.qualifier=='setInt') params.push('number')
		else if(!resolvedType.qualifier) params.push('string')
		params.push(valueTemp)
	}else if(resolvedType.type==='cb_copy'){
		let lienParamsSrc = paramsFormatVariable(params[0])
		let lienParamsDest = paramsFormatVariable(params[1])
		params[0] = lienParamsSrc[0]
		params[1] = lienParamsSrc[1]
		params.push(lienParamsDest[0])
		params.push(lienParamsDest[1])
	}else if(resolvedType.type==='cb_incrBy'){
		let lienParams = paramsFormatVariable(params[0])
		let tempValue = params[1]
		if(resolvedType.qualifier && resolvedType.qualifier=='decr') params[0]='decr'
		else if(!resolvedType.qualifier) params[0]= 'incrBy'
		params[1] = lienParams[0]
		params.push(lienParams[1])
		params.push(tempValue)
	}else if(resolvedType.type==='cb_remove'){
		let lienParams = paramsFormatVariable(params[0])
		params[0] = lienParams[0]
		params.push(lienParams[1])
	}else if(resolvedType.type==='cb_append'){
		let lienParams = paramsFormatVariable(params[0])
		let valueTemp = params[1]
		params[0] = lienParams[0]
		params[1] = lienParams[1]
		params.push(valueTemp)
	}else if(resolvedType.type==='cb_rating'){
		let lienParams = paramsFormatVariable(params[0])
		let valueTemp = params[1]
		params[0] = lienParams[0]
		params[1] = lienParams[1]
		params.push(valueTemp)
	}else if(resolvedType.type==='cb_inputString'){
		let lienParams = paramsFormatVariable(params[0])
		params[0]= params[1]
		params[1] = lienParams[0]
		params.push(lienParams[1])
	}else if(resolvedType.type==='cb_mail'){
		if(params.length==5)params.pop() //todo multi dest
		let lienParamsObject = paramsFormatVariable(params[0])
		let lienParamsMessage = paramsFormatVariable(params[1])
		let lienParamsPJ
		let lienParamsDest
		// isAPJMail = (resolvedType.qualifier && resolvedType.qualifier==='attachment' ? true : false)
		// let nbDest = params.length - (isAPJMail ? 3 : 2)

		params[0] = lienParamsObject[0]; //0
		params[1] = lienParamsObject[1]; //1
		params[2] = lienParamsMessage[0]; //2

		if(resolvedType.qualifier && resolvedType.qualifier==='attachment') {
			lienParamsPJ = paramsFormatVariable(params[2])
			lienParamsDest = paramsFormatVariable(params[3])
			params[3] = lienParamsMessage[1]; //3
			params.push('yespj') //4
			params.push(lienParamsPJ[0]) //5
			params.push(lienParamsPJ[1]) //6
		}
		else {
			lienParamsDest = paramsFormatVariable(params[2])
			params.push(lienParamsMessage[1]); //4
			params.push('nopj')//4
			params.push('')//5
			params.push('')//6
		}
		params.push(lienParamsDest[0]) //7
		params.push(lienParamsDest[1]) //8
	}else if(resolvedType.type==='cb_buttonsfile'){
		let lienParams = paramsFormatVariable(params[0])
		params[0] = params[1]
		params[1] = lienParams[0]
		params.push(lienParams[1])
	}else if(resolvedType.type==='cb_cards'){
		let lienParams = paramsFormatVariable(params[0])
		params[0] = params[1]
		params[1] = lienParams[0]
		params.push(lienParams[1])
	}else if(resolvedType.type==='cb_buttonurl'){
		if(params[3]!='true') params[3]='false'
	}else if(resolvedType.type==='cb_card'){
		let valueTemp = params[2]
		params[2] = params[0]
		params[0] = valueTemp
	}else if(resolvedType.type==='cb_confluencesearch'){
		let lienParams = paramsFormatVariable(params[0])
		let questions = [params[1], params[2]]
		let code = params[3]
		params[0] = lienParams[0]
		params[1] = lienParams[1]
		params[2]=questions[0]
		params[3]=questions[1]
		params.push(code)
	}else if(resolvedType.type==='cb_jirafield'){
		let lienParams = paramsFormatVariable(params[0])
		let question = params[1]
		params[0] = lienParams[0]
		params[1] = lienParams[1]
		params.push(question)
	}

	for (let i = 0; i < params.length; i++) {
		blockParams[i].setValue(params[i]);
	}

	block.initSvg();
	block.render();

	return block;
}

function createGenericBlock(type, isComposite, params) {
	let block = workspace.newBlock(isComposite ? 'cb_:generic_composite' : 'cb_:generic_terminal');
	block.setFieldValue(type.replace(/^cb_/,''), ":NAME");
	block.initSvg();
	block.render();

	let connection = block.inputList.filter(i => i.name === "args")[0].connection;
	for (let i = 0; i < params.length; i++) {
		let argBlock = workspace.newBlock('cb_:generic_arg');
		argBlock.setFieldValue(params[i], 'genericArg');
		argBlock.initSvg();
		argBlock.render();

		connection.connect(argBlock.outputConnection);
		connection = argBlock.inputList[0].connection;
	}

	return block;
}

function getBlockParams(block) {
	let ret = [];
	block.inputList.forEach(input => {
		input.fieldRow.forEach(param => {
			if (param.EDITABLE) {
				ret.push(param);
			}
		});
	});
	return ret;
}

function checkQualifyOption(block, qualifier) {
	let found = false;
	block.inputList.forEach(input => {
		input.fieldRow.forEach(param => {
			// list of all parameters name of qualifiers accepted
			if(param.name==='optionSay' || param.name ==="optionNlu" || param.name==="condition-type" || param.name==="type-value" || param.name==="type-incr" || param.name==="optionPj"){
				found = true;
			}
			if (param.name === ":QUALIFY" && param.EDITABLE && param.getOptions) {
				param.getOptions().forEach(opt => {
					if (opt[1] === qualifier) {
						found = true;
					}
				});
			}
		});
	});
	return found;
}

function checkIsComposite(block) {
	let found = false;
	block.inputList.forEach(input => {
		if (input.type === Blockly.inputTypes.STATEMENT) {
			found = true;
			return;
		}
	});
	return found;
}
