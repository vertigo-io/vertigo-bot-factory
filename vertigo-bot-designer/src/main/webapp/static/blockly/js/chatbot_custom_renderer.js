class CustomRenderer extends Blockly.zelos.Renderer{
    constructor() {
        super();
    }
    /**
     * @override
     */
    makeConstants_() {
        return new CustomConstantProvider();
    }
}

class CustomConstantProvider extends Blockly.zelos.ConstantProvider{

    // Primitives properties
    constructor() {
        super();

        // FIELD Renderer
        this.FIELD_BORDER_RECT_Y_PADDING = 3;
        this.FIELD_TEXT_FONTSIZE = 12;

        // INPUT STATEMENT Renderer
        this.STATEMENT_INPUT_PADDING_LEFT = 60;
        this.EMPTY_STATEMENT_INPUT_HEIGHT = 30;

    }
}

Blockly.blockRendering.register('custom_renderer', CustomRenderer);