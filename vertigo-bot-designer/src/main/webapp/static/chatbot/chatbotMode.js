// ***********
// ** Color **
// ***********

CodeMirror.defineSimpleMode("chatbot", {
	start: [
		{regex: /(\s*)(begin\b)(\s+[A-Za-z0-9:]+)?/, sol: true, token: [null, "keyword", "def strong"], indent: true},
		{regex: /(\s*)(end\b)(\s+[A-Za-z0-9:]+)?/, sol: true, token: [null, "keyword", "def strong"], dedent: true},
		{regex: /(\s*)([A-Za-z0-9:]+)/, sol: true, token: [null, "def strong"]},
		{regex: /"(?:[^"\\]|\\.)*"/, token: "tag"},
		{regex: /(\s)(\/\S*)/, token: [null, "string-2"]},
		{regex: /#.*/, token: "comment"}
	],
	meta: {
		electricInput: /end/
	}
});

// ******************
// ** Autocomplete **
// ******************

const fixedKeywords = ["begin", "end", "end"];
const fixedCommands = ["set", "setInt", "copy", "incr", "decr", "append", "remove", "eq", "eqInt", "gt", "lt",
				   "say", "say:always", "say:once",
				   "topic", "topic:start", "topic:fallback", "fullfiled", "choose:nlu",
				   "inputString", "button"];
const fixedCompositeCommands = ["random", "switch", "case", "sequence", "choose:button", "choose:button:nlu"];
const fixedParams = ["/user/global/", "/user/local/", "/user/local/topic"];
	
CodeMirror.registerHelper("hint", "chatbot", function(editor, options) {
	let wordAcceptedChars = /[^\s"]/;
	let cur = editor.getCursor(), curLine = editor.getLine(cur.line);
	let end = cur.ch, start = end;
	while (start && wordAcceptedChars.test(curLine.charAt(start - 1))) --start;
	let curWord = start != end && curLine.slice(start, end);
	
	let dynamicParams = getDynamicParameters(editor);

	let allList;
	if (curLine.match(/^\s*[\w\/\:$]*$/)) {
		allList = [...fixedKeywords, ...fixedCommands];
	} else if (curLine.match(/^\s*(begin|end)\s+[\w\/\:$]*$/)) {
		allList = [...fixedCompositeCommands];
	} else { //if (curLine.match(/^\s*((begin|end)\s+)?[\w\/\:$]+\s+.*$/)) {
		allList = [...fixedParams, ...dynamicParams];
	}

	let filteredList = allList
						.filter((value, index, self) => self.indexOf(value) === index) // unique filter
						.filter(word => curWord === false || word.includes(curWord))
						.sort((e1, e2) => {
							if (e1.startsWith(curWord) && !e2.startsWith(curWord)) return -1;
							if (!e1.startsWith(curWord) && e2.startsWith(curWord)) return 1;
							
							if (e1 < e2) return -1;
							if (e1 > e2) return 1;
							return 0;
						});

	return {
		list : filteredList,
		from: CodeMirror.Pos(cur.line, start),
		to: CodeMirror.Pos(cur.line, end)
	}
});


function getDynamicParameters(editor) {
	let cur = editor.getCursor();
	let result = [];
	for (let i = editor.firstLine(); i < editor.lastLine(); i++) {
		if (i != cur.line) {
			let textLine = editor.getLine(i).trim();
			
			let parsedLine = textLine.replace(/^(begin|end)\s+/, '') // remove begin/end
									 .match(/^[A-Za-z0-9:]+\s+(.+)$/);
			if (!parsedLine) continue; // no args found
			
			parsedLine[1] // get the arg part
			  .replace(/"(?:[^"\\]|\\.)*"/g, '') // remove quoted strings
			  .trim()
			  .split(/\s+/)
			  .filter(elem => elem !== '')
			  .forEach(elem => result.push(elem));
		}
	}
	
	return result;
}

// ***********************
// ** Errors / Warnings **
// ***********************

CodeMirror.registerHelper("lint", "chatbot", function(text, options) {
	let found = [];
	let compositeStack = [];
	let lastLineNumber = 0;
	text.split(/\n/).forEach((lineTxt, lineNumber) => {
		let matchComposite = lineTxt.match(/^\s*(begin|end)\s+([\w\/\:$]+)/);
		if (matchComposite && matchComposite[1] === "begin") {
			compositeStack.push(matchComposite[2]);
		} else if (matchComposite && matchComposite[1] === "end") {
			let previousBegin = compositeStack.pop();
			if (previousBegin === undefined) {
				found.push({
					from: CodeMirror.Pos(lineNumber, lineTxt.indexOf("end")),
					to: CodeMirror.Pos(lineNumber, lineTxt.length),
					message: "Unexpected 'end' (no begin found)",
					severity : 'error'
				});
			} else if (previousBegin !== matchComposite[2]) {
				found.push({
					from: CodeMirror.Pos(lineNumber, lineTxt.indexOf("end")),
					to: CodeMirror.Pos(lineNumber, lineTxt.length),
					message: "Invalid 'end' value, expected '" + previousBegin + "'",
					severity : 'error'
				});
			}
		}
		
		if (!matchComposite && lineTxt.match(/^\s*(begin|end)\s*$/)) {
			found.push({
				from: CodeMirror.Pos(lineNumber, lineTxt.indexOf("begin") || lineTxt.indexOf("end")),
				to: CodeMirror.Pos(lineNumber, lineTxt.length),
				message: "Missing command",
				severity : 'error'
			});
		}
		
		lastLineNumber = lineNumber;
	});
	
	if (compositeStack.length > 0) {
		found.push({
			from: CodeMirror.Pos(lastLineNumber, 0),
			to: CodeMirror.Pos(lastLineNumber, 0),
			message: "Missing 'end', expected 'end " + compositeStack.pop() + "'",
			severity : 'error'
		});
	}

	return found;
});