<template>
  <div>
    <q-btn-dropdown class="q-ma-sm" v-if="modeEdit"  color="primary" :label=compositeNode>
      <q-list>
        <q-item clickable v-close-popup @click="addSequence">
          <q-item-section>
            <q-item-label>Sequence</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addChooseButton">
          <q-item-section>
            <q-item-label>choose Button</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addChooseButtonNlu">
          <q-item-section>
            <q-item-label>chooseButtonNlu</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addChooseButtonFile">
          <q-item-section>
            <q-item-label>chooseButtonFile</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addSwitch">
          <q-item-section>
            <q-item-label>switch</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addCase">
          <q-item-section>
            <q-item-label>case</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addIfElse">
          <q-item-section>
            <q-item-label>If else</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addIf">
          <q-item-section>
            <q-item-label>If</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addElse">
          <q-item-section>
            <q-item-label>Else</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-btn-dropdown>

    <q-btn-dropdown class="q-ma-sm" v-if="modeEdit" color="primary" :label=simpleNode>
      <q-list>
        <q-item clickable v-close-popup @click="addButton">
          <q-item-section>
            <q-item-label>Button</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addButtonUrl">
          <q-item-section>
            <q-item-label>Button URL</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addButtonFile">
          <q-item-section>
            <q-item-label>Button File</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addSay">
          <q-item-section>
            <q-item-label>Say</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addInputString">
          <q-item-section>
            <q-item-label>inputString</q-item-label>
          </q-item-section>
        </q-item>

        <q-item clickable v-close-popup @click="addContains">
          <q-item-section>
            <q-item-label>contains</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addMail">
          <q-item-section>
            <q-item-label>mail</q-item-label>
          </q-item-section>
        </q-item>
        <q-item clickable v-close-popup @click="addMailWithAttachment">
          <q-item-section>
            <q-item-label>mail attachment</q-item-label>
          </q-item-section>
        </q-item>
      </q-list>
    </q-btn-dropdown>
    <q-btn v-if="modeEdit" label="Image" color="primary" @click="addImage()"/>
    <q-btn v-if="modeEdit" :label="locale == 'fr_FR' ? 'Lien' : 'Link'" color="primary" @click="addLink()"/>
    <q-btn v-if="modeEdit" label="Emoji" color="primary" @click="addEmoji()"/>
    <div :style="error ? 'border: 2px solid;border-color: #C10015;border-radius:5px;':''">
      <codemirror ref="cm" v-model='script'
                  :disabled="disabled"
                  :indentWithTab="indentWithTab" :tabSize="tabSize"
                  :extensions="extensions"></codemirror>
    </div>
    <input type="hidden" :value="modelValue"/>
    <q-dialog ref="newEmoji" id="newEmoji" >
      <q-card>
        <q-card-section>
          <div class="text-h6" >{{locale == 'fr_FR' ? 'Ajouter un emoji' : 'Add emoji'}}</div>
        </q-card-section>
        <q-card-section>
          <emoji-picker></emoji-picker>
        </q-card-section>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import {
  chatbotHighlighter,
  autocomplete,
  chatbotLinter
} from "@/codemirrorChatbotMode.js"
import {EditorView} from "codemirror";
import {indentUnit} from "@codemirror/language";
import "emoji-picker-element";

export default {
  name: "chatbotCodemirror",
  props: {
    modeEdit: {type: Boolean, 'default': true},
    modelValue: {type: String, required: true},
    compositeNode: {type: String, 'default': 'Noeud composite'},
    simpleNode: {type: String, 'default': 'Noeud simple'},
    locale: {type: String, 'default': 'en_US'},
    error: false
  },
  emits: ["update:modelValue"],
  computed: {
    script: {
      get() {
        return this.modelValue
      },
      set(value) {
        this.$emit('update:modelValue', value)
      }
    }
  },
  data: function () {
    return {
      disabled: !this.modeEdit,
      indentWithTab: true,
      tabSize: 4,
      extensions: [chatbotHighlighter, autocomplete, chatbotLinter, indentUnit.of("\t")]
    }
  },
  methods: {
    //Create the string to add
    //Get only the first string if it's simple node i.e. say
    //Get tabulation for end composite node
    createStringToAdd: function (beginString, endString, currentLine) {
      var stringToAdd = beginString + '\n';
      if (endString == null) {
        return stringToAdd;
      }
      //matchs the tabulation pattern
      var count = (currentLine.text.match(/\t/g) || []).length;
      for (let i = 0; i < count; i++) {
        endString = '\t'.concat(endString);
      }
      return stringToAdd + endString
    },

    //Add string of node into the editor value
    //addEndString must be null for simple node
    modifyValue: function (addBeginString, addEndString) {
      let editor = EditorView.findFromDOM(document);
      let editorState = editor.state;
      let currentLine = editorState.doc.lineAt(editorState.selection.main.head);
      let stringToAdd = this.createStringToAdd(addBeginString, addEndString, currentLine);
      this.insert(editor, stringToAdd);
    },

    addSequence: function () {
      this.modifyValue('begin sequence', 'end sequence');
    },

    addChooseButton: function () {
      this.modifyValue('begin choose:button /user/local ""', 'end choose:button');
    },

    addChooseButtonNlu: function () {
      this.modifyValue('begin choose:button:nlu /user/local ""', 'end choose:button:nlu');
    },

    addChooseButtonFile: function () {
      this.modifyValue('begin choose:button:file /user/local ""', 'end choose:button:file');
    },

    addSwitch: function () {
      this.modifyValue('begin switch /user/local ', 'end switch');
    },

    addCase: function () {
      this.modifyValue('begin case ""', 'end case');
    },
    addIfElse: function () {
      this.modifyValue('begin ifelse', 'end ifelse');
    },
    addIf: function () {
      this.modifyValue('begin if', 'end if');
    },
    addElse: function () {
      this.modifyValue('begin else', 'end else');
    },

    addButton: function () {
      this.modifyValue('button "" value');
    },
    addButtonUrl: function () {
      this.modifyValue('button:url "" value url true');
    },
    addButtonFile: function () {
      this.modifyValue('button:file "" value');
    },

    addContains: function () {
      this.modifyValue('contains /user/local compare');
    },

    addMail: function () {
      this.modifyValue('mail /user/local/subject /user/local/message /user/local/destinataire /user/local/destinatairedeux');
    },
    addMailWithAttachment: function () {
      this.modifyValue('mail:attachment /user/local/subject /user/local/message /user/local/pj /user/local/destinataire /user/local/destinatairedeux ');
    },

    addSay: function () {
      this.modifyValue('say ""');
    },

    addInputString: function () {
      this.modifyValue('inputString /user/local ""');
    },
    addImage: function () {
      this.modifyValue('image "url"');
    },
    addLink: function () {
      this.modifyValue('link "url" true');
    },
    replaceContent: function (text) {
      let editor = EditorView.findFromDOM(document);
      editor.dispatch({
        changes: {
          from: 0,
          to: editor.state.doc.length,
          insert: `${text}`
        }
      });
    },
    addEmoji() {
      this.$refs.newEmoji.show();
      this.$nextTick(() => {
        document.querySelector('emoji-picker').addEventListener('emoji-click', event => this.handleEmoji(event.detail.unicode));
      });
    },
    handleEmoji(emoji) {
      this.$refs.newEmoji.hide();

      let editor = EditorView.findFromDOM(document);
      this.insert(editor, emoji);
    },

    insert(editor, text) {
      const range = editor.state.selection.ranges[0];
      editor.dispatch({
        changes: {
          from: range.from,
          to: range.to,
          insert: `${text}`
        },
        selection: {anchor: range.head}
      })
    }
  }
}
</script>