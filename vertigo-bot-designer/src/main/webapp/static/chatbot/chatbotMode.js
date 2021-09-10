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