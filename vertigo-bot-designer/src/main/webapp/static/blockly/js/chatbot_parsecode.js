function fromCode() {
	let workspace = Blockly.mainWorkspace;
	workspace.clear();
	workspace.clearUndo();
	setTimeout(() => Blockly.mainWorkspace.trashcan.emptyContents(), 0); // not working if not asynch

	let text = document.getElementById("txt").value;
	let parsed = parseText(text);
	if (parsed.error) {
		// document.getElementById('error_blockly').innerHTML = parsed.error; //TODO
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

	out = out.map(s => s.replace(/<br\s*\/?>/,'\n'));
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

	if (resolvedType.qualifier) {
		block.setFieldValue(resolvedType.qualifier, ":QUALIFY");
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
