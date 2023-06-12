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

        this.FIELD_TEXT_FONTSIZE = 10   ;
        this.EMPTY_STATEMENT_INPUT_HEIGHT = 80;
        // Override a few properties.
        // /**
        //  * The width of the notch used for previous and next connections.
        //  * @type {number}
        //  * @override
        //  */
        // this.NOTCH_WIDTH = 20;
        //
        // /**
        //  * The height of the notch used for previous and next connections.
        //  * @type {number}
        //  * @override
        //  */
        // this.NOTCH_HEIGHT = 10;
        //
        // /**
        //  * Rounded corner radius.
        //  * @type {number}
        //  * @override
        //  */
        // this.CORNER_RADIUS = 2;
        //
        // /**
        //  * The height of the puzzle tab used for input and output connections.
        //  * @type {number}
        //  * @override
        //  */
        // this.TAB_HEIGHT = 8;
    }

    // init() {
    //     /**
    //      * An object containing sizing and path information about collapsed block
    //      * indicators.
    //      */
    //     // this.JAGGED_TEETH = this.makeJaggedTeeth();
    // }
}

Blockly.blockRendering.register('custom_renderer', CustomRenderer);