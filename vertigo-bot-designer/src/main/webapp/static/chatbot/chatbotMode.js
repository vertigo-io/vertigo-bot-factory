String.prototype.stripComments = function() {
	let isEscaping = false;
	let isNewParam = true;
	let isQuoting = false;
	let hasFirstDash = false;
	for (let i = 0; i < this.length; i++) {
		let c = this.charAt(i);
		if (isEscaping) {
			isEscaping = false;
		} else if (isQuoting && c === '\\') {
			isEscaping = true;
		} else if (isNewParam && c === '"') {
			isQuoting = true;
			isNewParam = false;
		} else if (isQuoting && c === '"'){
			isQuoting = false;
		} else if (!isQuoting && c.match(/\s/)) {
			isNewParam = true;
		} else if (!isQuoting) {
			isNewParam = false;
			
			if (c === '-') {
				if (!hasFirstDash) {
					hasFirstDash = true;
				} else {
					return this.substr(0, i - 1);
				}
			} else {
				hasFirstDash = false;
			}
		}
	}
	
	return this; // no comment found
}
	

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
		{regex: /--.*/, token: "comment"}
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
	
	let curLineWithoutComment = curLine.stripComments(); // see the start of this file
	if (end > curLineWithoutComment.length) {
		return;
	}
	
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
			let textLine = editor.getLine(i);
			
			let parsedLine = textLine.replace(/^\s*(begin|end)\s+/, '') // remove begin/end
									 .stripComments() // see the start of this file
									 .match(/^\s*[A-Za-z0-9:]+\s+(.+)$/);
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

javascript.language.data.of({})
CodeMirror.registerHelper("lint", "chatbot", function(text, options) {
	return [...checkBeginEnd(text), ...checkQuotes(text)];
});

function checkBeginEnd(text) {
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
}

function checkQuotes(text) {
	let found = [];
	text.split(/\n/).forEach((lineTxt, lineNumber) => {
		let parsedLine = lineTxt.replace(/^\s*(begin|end)\s+/, '') // remove begin/end
								.match(/^\s*[A-Za-z0-9:]+(.*)$/);
		if (!parsedLine || parsedLine[1].length === 0) return; // no args found
		
		let args = parsedLine[1]; // get the arg part
		let beginIndex = lineTxt.length - args.length;
		args = args.stripComments(); // see the start of this file
		
		if (!args[0].match(/\s/)) {
			found.push({
				from: CodeMirror.Pos(lineNumber, beginIndex),
				to: CodeMirror.Pos(lineNumber, beginIndex + 1),
				message: "Invalid command character",
				severity : 'error'
			});
			return;
		}
			
		let isEscaping = false;
		let isNewParam = true;
		let isQuoting = false;
		let isNormalParam = false;
		for (let i = 0; i < args.length; i++) {
			let c = args.charAt(i);
			if (isEscaping) {
				isEscaping = false;
			} else if (c === '\\') {
				isEscaping = true;
			} else if (isNewParam && c === '"') {
				isQuoting = true;
				isNewParam = false;
			} else if (isQuoting && c === '"'){
				isQuoting = false;
			} else if (!isQuoting && c.match(/\s/)) {
				isNewParam = true;
				isNormalParam = false;
			} else if (!isQuoting) {
				if (c === '"') {
					found.push({
						from: CodeMirror.Pos(lineNumber, beginIndex + i),
						to: CodeMirror.Pos(lineNumber, beginIndex + i + 1),
						message: "Quotes only allowed around text. Quotes inside quoted string must be escaped with '\\'.",
						severity : 'warning'
					});
				}
				if (!isNewParam && !isNormalParam) {
					found.push({
						from: CodeMirror.Pos(lineNumber, beginIndex + i),
						to: CodeMirror.Pos(lineNumber, beginIndex + i + 1),
						message: "Missing space after end quote",
						severity : 'warning'
					});
				}
				isNewParam = false;
				isNormalParam = true;
			}
		}
		if (isQuoting) {
			found.push({
				from: CodeMirror.Pos(lineNumber, lineTxt.length - 1),
				to: CodeMirror.Pos(lineNumber, lineTxt.length),
				message: "Quoted string not ended",
				severity : 'error'
			});
		}
		if (isEscaping) {
			found.push({
				from: CodeMirror.Pos(lineNumber, lineTxt.length - 1),
				to: CodeMirror.Pos(lineNumber, lineTxt.length),
				message: "A line can't end with an escaping character",
				severity : 'error'
			});
		}
	});
	return found;
}