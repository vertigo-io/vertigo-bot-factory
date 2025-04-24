import { simpleMode } from "@codemirror/legacy-modes/mode/simple-mode"
import {StreamLanguage, LanguageSupport, HighlightStyle, syntaxHighlighting, defaultHighlightStyle, foldService} from "@codemirror/language"
import {Tag, tags} from '@lezer/highlight'
import {linter} from "@codemirror/lint";

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

const chatbotSimpleMode = simpleMode({
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
	},
	languageData: {
		name: "chatbot",
		indentOnInput: new RegExp("^\\s*end\\b$")
	}
});

const chatbotModeLanguage = StreamLanguage.define(chatbotSimpleMode);

tags.def = Tag.define("def");

const highlightStyles = HighlightStyle.define([
	...defaultHighlightStyle.specs,
	{ tag: tags.def,
	  color: '#00f'}]);
const chatbotHighlighter = syntaxHighlighting(highlightStyles);


// ******************
// ** Autocomplete **
// ******************

const autocomplete = new LanguageSupport(chatbotModeLanguage, [
	chatbotModeLanguage.data.of({
		autocomplete: getCompletions
	})
]);

const fixedKeywords = [
	{label: "begin", type: "keyword"},
	{label: "end", type: "keyword"}
];
const fixedCommands = [
	{label: "set", type: "keyword"},
	{label: "setInt", type: "keyword"},
	{label: "copy", type: "keyword"},
	{label: "incr", type: "keyword"},
	{label: "decr", type: "keyword"},
	{label: "append", type: "keyword"},
	{label: "remove", type: "keyword"},
	{label: "eq", type: "keyword"},
	{label: "eqInt", type: "keyword"},
	{label: "gt", type: "keyword"},
	{label: "lt", type: "keyword"},
	{label: "say", type: "keyword"},
	{label: "say:always", type: "keyword"},
	{label: "say:once", type: "keyword"},
	{label: "topic", type: "keyword"},
	{label: "topic:start", type: "keyword"},
	{label: "topic:fallback", type: "keyword"},
	{label: "fullfiled", type: "keyword"},
	{label: "choose:nlu", type: "keyword"},
	{label: "inputString", type: "keyword"},
	{label: "button", type: "keyword"}
];
const fixedCompositeCommands = [
	{label: "random", type: "keyword"},
	{label: "switch", type: "keyword"},
	{label: "case", type: "keyword"},
	{label: "sequence", type: "keyword"},
	{label: "choose:button", type: "keyword"},
	{label: "choose:button:nlu", type: "keyword"}
];
const fixedParams = [
	{label: "/user/global/", type: "constant"},
	{label: "/user/local/", type: "constant"},
	{label: "/user/local/topic", type: "constant"}
];

function getCompletions(context) {
	let curWord = context.matchBefore(/\w*/);

	let dynamicParams = getDynamicParameters(context);

	let allList;
	if (context.matchBefore(/^\s*[\w\/\:$]*$/) !== null) {
		allList = [...fixedKeywords, ...fixedCommands];
	} else if (context.matchBefore(/^\s*(begin|end)\s+[\w\/\:$]*$/) !== null) {
		allList = [...fixedCompositeCommands];
	} else { //if (curLine.match(/^\s*((begin|end)\s+)?[\w\/\:$]+\s+.*$/)) {
		allList = [...fixedParams, ...dynamicParams];
	}

	let filteredList = allList
		.filter((value, index, self) => self.indexOf(value) === index) // unique filter
		.sort((e1, e2) => {
			if (e1.label.startsWith(curWord) && !e2.label.startsWith(curWord)) return -1;
			if (!e1.label.startsWith(curWord) && e2.label.startsWith(curWord)) return 1;

			if (e1.label < e2.label) return -1;
			if (e1.label > e2.label) return 1;
			return 0;
		});

	return {
		options : filteredList,
		from: curWord.from,
		to:curWord.to
	}
};

function getDynamicParameters(context) {
	const doc = context.state.doc;
	const pos = context.pos
	if (doc.children !== null) {
		return doc.children.map(child => getDynamicParametersForDoc(child, pos));
	}
	return getDynamicParametersForDoc(doc, pos);
};

function getDynamicParametersForDoc(doc, pos) {
	let result = [];
	if (pos > doc.length) {
		return result;
	}
	let curLine = doc.lineAt(pos);
	for (let i = 0; i < doc.lines; i++) {
		if (i != curLine.number) {
			let textLine = doc.text[i];

			let parsedLine = textLine.replace(/^\s*(begin|end)\s+/, '') // remove begin/end
				.stripComments() // see the start of this file
				.match(/^\s*[A-Za-z0-9:]+\s+(.+)$/);
			if (!parsedLine) continue; // no args found

			parsedLine[1] // get the arg part
				.replace(/"(?:[^"\\]|\\.)*"/g, '') // remove quoted strings
				.trim()
				.split(/\s+/)
				.filter(elem => elem !== '')
				.forEach(elem => result.push({label: elem, type: "variable"}));
		}
	}
	return result;
};


// ***********************
// ** Errors / Warnings **
// ***********************

const chatbotLinter = linter(view => {
	const beginEnd = checkBeginEnd(view);
	const quotes = checkQuotes(view);
		return [...beginEnd, ...quotes];
	});

function checkBeginEnd(view) {
	let found = [];
	let compositeStack = [];
	let doc = view.state.doc;
	for (let i = 0; i < doc.lines; i++) {
		let curLine = doc.line(i+1);
		let lineTxt = curLine.text;
		let matchComposite = lineTxt.match(/^\s*(begin|end)\s+([\w\/\:$]+)/);
		if (matchComposite && matchComposite[1] === "begin") {
			compositeStack.push(matchComposite[2]);
		} else if (matchComposite && matchComposite[1] === "end") {
			let previousBegin = compositeStack.pop();
			if (previousBegin === undefined) {
				found.push({
					from: curLine.from,
					to: curLine.to,
					message: "Unexpected 'end' (no begin found)",
					severity : 'error'
				});
			} else if (previousBegin !== matchComposite[2]) {
				found.push({
					from: curLine.from,
					to: curLine.to,
					message: "Invalid 'end' value, expected '" + previousBegin + "'",
					severity : 'error'
				});
			}
		}

		if (!matchComposite && lineTxt.match(/^\s*(begin|end)\s*$/)) {
			found.push({
				from: curLine.from,
				to: curLine.to,
				message: "Missing command",
				severity : 'error'
			});
		}
	};

	if (compositeStack.length > 0) {
		found.push({
			from: 0,
			to: doc.length,
			message: "Missing 'end', expected 'end " + compositeStack.pop() + "'",
			severity : 'error'
		});
	}

	return found;
}

function checkQuotes(view) {
	let found = [];
	let doc = view.state.doc;
	for (let i = 0; i < doc.lines; i++) {
		let curLine = doc.line(i+1);
		let lineTxt = curLine.text;
		let parsedLine = lineTxt.replace(/^\s*(begin|end)\s+/, '') // remove begin/end
			.match(/^\s*[A-Za-z0-9:]+(.*)$/);
		if (!parsedLine || parsedLine[1].length === 0) return found; // no args found

		let args = parsedLine[1]; // get the arg part
		let beginIndex = lineTxt.length - args.length;
		args = args.stripComments(); // see the start of this file

		if (!args[0].match(/\s/)) {
			found.push({
				from: curLine.from,
				to: curLine.to,
				message: "Invalid command character",
				severity : 'error'
			});
			return found;
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
						from: curLine.from,
						to: curLine.to,
						message: "Quotes only allowed around text. Quotes inside quoted string must be escaped with '\\'.",
						severity : 'warning'
					});
				}
				if (!isNewParam && !isNormalParam) {
					found.push({
						from: curLine.from,
						to: curLine.to,
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
				from: curLine.from,
				to: curLine.to,
				message: "Quoted string not ended",
				severity : 'error'
			});
		}
		if (isEscaping) {
			found.push({
				from: curLine.from,
				to: curLine.to,
				message: "A line can't end with an escaping character",
				severity : 'error'
			});
		}
	};
	return found;
}

export {chatbotModeLanguage, chatbotHighlighter, autocomplete, chatbotLinter};