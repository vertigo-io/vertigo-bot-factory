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

CodeMirror.registerHelper("hint", "chatbot", function(editor, options) {
	let wordAcceptedChars = /[^\s"]/;
	let cur = editor.getCursor(), curLine = editor.getLine(cur.line);
	let end = cur.ch, start = end;
	while (start && wordAcceptedChars.test(curLine.charAt(start - 1))) --start;
	let curWord = start != end && curLine.slice(start, end);

	let fixedKeywords = ["begin", "end", "end"];
	let fixedCommands = ["set", "setInt", "copy", "incr", "decr", "append", "remove", "eq", "eqInt", "gt", "lt",
					   "say", "say:always", "say:once",
					   "topic", "topic:start", "topic:fallback", "fullfiled", "choose:nlu",
					   "inputString", "button"];
	let fixedCompositeCommands = ["random", "switch", "case", "choose:button", "choose:button:nlu"];
	let fixedParams = ["/user/global/", "/user/local/", "/user/local/topic"];
	
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
							
							if (e1 < e2) {return -1;}
							if (e1 > e2) {return 1;}
							return 0;
						});

	return {
		list : filteredList,
		from: CodeMirror.Pos(cur.line, start),
		to: CodeMirror.Pos(cur.line, end)
	}
});

function uniqueFilter(value, index, self) {
	return self.indexOf(value) === index;
}

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