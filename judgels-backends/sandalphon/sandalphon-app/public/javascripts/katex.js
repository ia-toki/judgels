require(["katex-contrib-auto-render"], function(renderMathInElement) {
    renderMathInElement(document.body, {
        delimiters: [
            { left: "\\(", right: "\\)", display: false },
            { left: "$", right: "$", display: false },
            { left: "\\[", right: "\\]", display: true },
        ]
    });
});
