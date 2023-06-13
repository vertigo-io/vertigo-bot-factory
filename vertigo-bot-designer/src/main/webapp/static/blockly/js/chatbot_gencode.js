function isComposite(block) {
	return block.statementInputCount > 0;
}

function formatVariable(type, nomVar){
	if(type=="global/context/url"){
		// this.getParameter("condition-variable").setText("E");
		return ' /user/'+ type + ' ';
	}
	else{
		return ' /user/'+ type + '/' + nomVar.toLowerCase() + ' ';
	}
}

function formatString(nomVar){
	return ' "' + nomVar+ '" ';
}

function extractParams(block) {
	let params = "";
	block.inputList.forEach(input => {
		input.fieldRow.forEach(param => {
			if (param.EDITABLE && !param.name.startsWith(':')) {
				let value = param.getValue().replace(/\r?\n/g, "<br />").replace('"','\\"');
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
	let name = nameParam ? nameParam : block.type.replace(/^cb_/,'');

	let optValue = block.getFieldValue(':QUALIFY');
	if (optValue) {
		name += optValue
	}
	return name;
}


// function codeSpecBlock(block) {
// 	let sentence;
// 	console.log(name);
// 	switch (name){
// 		case 'switch':
// 			break;
// 		case 'case':
// 			break;
// 		default:
// 			break;
// 	}
//
// 	return sentence;
// }

function buildBlock(block) {
	let name = getBlockName(block);
	let params = extractParams(block);


	if (isComposite(block)) {
		let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
		return 'begin ' + name + params + '\n' + subBlocks + 'end ' + name + '\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}

	return name + params + '\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}

function toCode(event) {
	//console.log(event.type);
	let topBlocks = Blockly.mainWorkspace.getTopBlocks().filter(b => b.rendered === true && b.disabled === false);
	let topBlocksCount = topBlocks.length;
	let startCount = topBlocks.filter(b => b.type === "cb_start").length;
	let isOnlySequence = topBlocksCount === 1 && topBlocks[0].type === "cb_sequence";

	// document.getElementById('error_blockly').innerHTML = '';
	var code = Blockly.BotScript.workspaceToCode(Blockly.mainWorkspace );

	if (startCount === 0 && !isOnlySequence) {
		if (code) {
			code = Blockly.BotScript.prefixLines(code, Blockly.BotScript.INDENT);
		}
		code = 'begin sequence\n' + code + 'end sequence';
	}
	VertigoUi.vueData.scriptIntention.script = code;
}

function getCodeDiagram(event) {
	//console.log(event.type);
	let topBlocks = Blockly.mainWorkspace.getTopBlocks().filter(b => b.rendered === true && b.disabled === false);
	let topBlocksCount = topBlocks.length;
	let startCount = topBlocks.filter(b => b.type === "cb_start").length;
	let isOnlySequence = topBlocksCount === 1 && topBlocks[0].type === "cb_sequence";

	// document.getElementById('error_blockly').innerHTML = '';
	var code = Blockly.BotScript.workspaceToCode(Blockly.mainWorkspace );

	if (startCount === 0 && !isOnlySequence) {
		if (code) {
			code = Blockly.BotScript.prefixLines(code, Blockly.BotScript.INDENT);
		}
		code = 'begin sequence\n' + code + 'end sequence';
	}
	return code
}


Blockly.BotScript = new Blockly.Generator('BotScript');

for (name in Blockly.Blocks) { // same rendering function for all CB blocks
	if (name.startsWith('cb_')) Blockly.BotScript[name] = buildBlock;
}

// exceptions
// - start block
// Blockly.BotScript['cb_start'] = function(block) {
// 	return 'begin sequence\n' + Blockly.BotScript.prefixLines(Blockly.BotScript.blockToCode(block.getNextBlock()), Blockly.BotScript.INDENT) + 'end sequence\n';
// }

// - generic arguments
Blockly.BotScript['cb_:generic_arg'] = function(block) {
	return null;
}

// *****************************
// *****CATEGORIE SEQUENCE******
// *****************************

// *****************************
// *****CATEGORIE SELECTOR******
// *****************************
// cb_condition
// <typeCondition> <key> <value> // si n'est pas condition fulfilled
// fulfilled <key> // si est condition fulfilled
Blockly.BotScript['cb_condition'] = function(block) {
	let params = getBlockParams(block);
	let condition = params[0].getValue();
	let name = getBlockName(block);
	let isAFulfilledCondition;
	if(condition=="fulfilled")isAFulfilledCondition=true;
	else isAFulfilledCondition =false;
	let isAStringCondition;
	if(condition=="eq"||condition=="contains")isAStringCondition=true;
	else isAStringCondition =false;
	// console.log("string:"+isAStringCondition+"|fulfilled:"+isAFulfilledCondition);
	if(isAFulfilledCondition){
		return condition + formatVariable(params[1].getValue(),params[2].getValue())+'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	else if(isAStringCondition){
		return condition + formatVariable(params[1].getValue(),params[2].getValue()) + ' "' + params[3].getValue()+'"'+'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
	else{
		return condition + formatVariable(params[1].getValue(),params[2].getValue()) + ' ' + params[3].getValue()+'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
	}
}



// *****************************
// ******CATEGORIE BUTTON*******
// *****************************
// begin choose:button<:nlu|| > <key> <"question"> ___ end choose:button<:nlu|| >
// variable en miniscule
Blockly.BotScript['cb_buttons'] = function(block) {
	let params = getBlockParams(block);
	let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
	return "begin choose:button" +params[3].getValue()+' /user/'+ params[1].getValue() +'/'+ params[2].getValue().toLowerCase() + ' "'+ params[0].getValue()+'"' +"\n" + subBlocks + 'end ' + "choose:button"+params[3].getValue()  +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_button'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +' "'+ params[0].getValue() +'" ' + params[1].getValue() +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}



// *****************************
// ******CATEGORIE TOPIC********
// *****************************
// cb_topic_start
// topic:start
Blockly.BotScript['cb_topic_start'] = function(block) {
	return "topic:start\n"+ Blockly.BotScript.blockToCode(block.getNextBlock());
}
// cb_topic_start
// topic:fallback
Blockly.BotScript['cb_topic_fallback'] = function(block) {
	return "topic:fallback\n"+ Blockly.BotScript.blockToCode(block.getNextBlock());
}

// *****************************
// *****CATEGORIE SWITCHCASE****
// *****************************
// cb_switch
// begin switch /user/<'local'||'global'>/<nomVariable> ___ end switch
// variable en minuscule
Blockly.BotScript['cb_switch'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
	return 'begin ' + name + ' /user/'+ params[0].getValue() +'/'+ params[1].getValue().toLowerCase() + '\n' + subBlocks + 'end ' + name + '\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}

// cb_case
// begin case <"valeur"> ___ case switch
Blockly.BotScript['cb_case'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
	return 'begin ' + name +' "'+ params[0].getValue()  +'"\n' + subBlocks + 'end ' + name + '\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}

// *****************************
// ******CATEGORIE MESSAGE******
// *****************************
Blockly.BotScript['cb_message'] = function(block) {
	let params = getBlockParams(block);
	return "say" + params[1].getValue()+ ' "' +params[0].getValue() + '"' +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_link'] = function(block) {
	let params = getBlockParams(block);
	return "link" + ' "' +params[0].getValue() + '"' +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}



// *****************************
// ****CATEGORIE ACTION-KEY*****
// *****************************
Blockly.BotScript['cb_set'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name + formatVariable(params[0].getValue(), params[1].getValue()) + formatString(params[2].getValue())+'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_setInt'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +formatVariable(params[0].getValue(), params[1].getValue()) + params[2].getValue() +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());

}
Blockly.BotScript['cb_copy'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +formatVariable(params[0].getValue(), params[1].getValue()) + formatVariable(params[2].getValue(), params[3].getValue()) +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_incrBy'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +formatVariable(params[0].getValue(), params[1].getValue()) + params[2].getValue() +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_decr'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +formatVariable(params[0].getValue(), params[1].getValue()) + params[2].getValue() +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}
Blockly.BotScript['cb_remove'] = function(block) {
	let params = getBlockParams(block);
	let name = getBlockName(block);
	return name +formatVariable(params[0].getValue(), params[1].getValue()) +'\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
}

// *****************************
// ******CATEGORIE OTHER*******
// *****************************
// Blockly.BotScript['cb_random'] = function(block) {
// 	let subBlocks = Blockly.BotScript.statementToCode(block, 'SUB_BLOCKS')
// 	return 'begin random\n' + subBlocks + 'end random\n' + Blockly.BotScript.blockToCode(block.getNextBlock());
// }

