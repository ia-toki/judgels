require(["jquery"], function() {
    $(".spoiler").click(function() {
        var content = this.getElementsByTagName('div')[0];
        content.style.display = (content.style.display == 'block') ? 'none': 'block';
    });
});
