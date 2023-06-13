// ****** WORKSPACE ******
var blocklyDiv = document.getElementById('blocklyDiv');
var workspace = Blockly.inject(blocklyDiv,
// Blocky.Options : dictionnaire de paires nom/valeur utilisés pour la configuration
    {
        collapse: false,
        // comments: ,
        // css: ,
        // disable: ,
        // grid: ,
        // horizontalLayout: ,
        maxBlocks: 500,
        maxInstances: {},
        media: '/vertigo-bot-designer/static/blockly/media/',
        // move: ,
        // oneBasedIndex: ,
        readOnly: true,
        // TODO: faire un renderer personnalisé
        renderer: 'zelos',
        // renderer: 'custom_renderer',
        // rtl: ,
        scrollbars: true,
        sounds: false,
        theme: getThemeDev(),
        toolbox: getToolBox(),
        toolboxPosition: "end",
        // trashcan: ,
        // maxTrashcanContents: ,
        // plugins: ,
        // zoom: ,
        zoom:
            {controls: true,
                wheel: false,
                startScale: 0.6,
                maxScale: 1,
                minScale: 0.3,
                scaleSpeed: 1.2,
                pinch: true}
    }
);
window.addEventListener('resize', onresize, false);
onresize();
fromCode(VertigoUi.vueData.scriptIntention.script)