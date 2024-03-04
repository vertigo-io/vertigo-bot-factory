function exportDiagramToPng() {
    var coeffDiagramToPng = 1/workspace.getScale()
    var svgElement = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svgElement.innerHTML = workspace.getCanvas().innerHTML;

    // Stylized the svg element to fit with Blockly zelos constants
    svgElement.querySelectorAll('.blocklyText').forEach(textElement => {
        textElement.setAttribute("style", "fill: white; font-family: 'Helvetica Neue';")
    })
    svgElement.querySelectorAll('rect').forEach(textElement => {
        textElement.setAttribute("style", "fill: white;")
    })
    svgElement.querySelectorAll("g.blocklyEditableText, g.blocklyNonEditableText").forEach(element => {
        element.querySelectorAll('.blocklyText').forEach(textElement =>{
            textElement.setAttribute("style", "fill: black; font-family: 'Helvetica Neue';")
        })
    })

    // Creation of the canvas with the context 2d (image png)
    var canvas = document.createElement("canvas");
    canvas.width = document.getElementsByClassName('blocklyBlockCanvas')[0].getBoundingClientRect().width * coeffDiagramToPng
    canvas.height = document.getElementsByClassName('blocklyBlockCanvas')[0].getBoundingClientRect().height * coeffDiagramToPng
    var context = canvas.getContext("2d");
    context.fillStyle = "#FFFFFF";
    context.fillRect(0, 0, canvas.width, canvas.height);

    var svgString = new XMLSerializer().serializeToString(svgElement);
    var utf8Bytes = new TextEncoder().encode(svgString);
    var base64Svg = Base64.fromUint8Array(utf8Bytes)
    var svgDataUrl = "data:image/svg+xml;base64," + base64Svg;

    var image = new Image();
    image.src = svgDataUrl;
    // Draw the image onto the canvas
    image.onload = function() {
        context.drawImage(image, 0, 0);
        var pngDataUrl = canvas.toDataURL("image/png");

        var link = document.createElement("a");
        link.href = pngDataUrl;
        if(VertigoUi.vueData.topic.title)link.download = VertigoUi.vueData.topic.title.replace(/ /g,'_').toLowerCase().concat(VertigoUi.vueData.locale ==='fr_FR' ? '_intention' : '_topic') + ".png";
        else link.download = 'diagram.png'
        link.click();
    };

}