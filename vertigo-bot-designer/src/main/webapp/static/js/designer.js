quasarConfig = {
    loadingBar: { 'skip-hijack' : true } // disable quasar's ajaxbar
};

if (typeof VUiExtensions === 'undefined') {
    VUiExtensions = { methods: {}, dataX: {} };
}
if (!VUiExtensions.methods) {
    VUiExtensions.methods = {};
}

VUiExtensions.methods = {
    ...VUiExtensions.methods,

    /**
     * Indique si la ligne peut monter : un voisin de sequence strictement inferieure existe.
     * @param {{sequence: number}} row - Ligne courante
     * @param {{sequence: number}[]} list - Elements du meme perimetre
     * @returns {boolean} true s'il existe un voisin precedent
     */
    canMoveUp: function(row, list) {
        return Array.isArray(list) && row != null && list.some(function(other) {
            return other != null && other.sequence < row.sequence;
        });
    },

    /**
     * Indique si la ligne peut descendre : un voisin de sequence strictement superieure existe.
     * @param {{sequence: number}} row - Ligne courante
     * @param {{sequence: number}[]} list - Elements du meme perimetre
     * @returns {boolean} true s'il existe un voisin suivant
     */
    canMoveDown: function(row, list) {
        return Array.isArray(list) && row != null && list.some(function(other) {
            return other != null && other.sequence > row.sequence;
        });
    }
};