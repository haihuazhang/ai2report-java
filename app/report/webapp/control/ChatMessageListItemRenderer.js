sap.ui.define([
    // "sap/ui/core/RenderManager",
    // "sap/m/ListItemBase",
    "sap/ui/core/Renderer",
    // "./ChatMessageListItem",
    "sap/m/Text",
    // "sap/m/Avatar",
    // "./showdown",
    // "./showdownhighlight"
    "./marked",
    "./showdown"
],
    function (
        // RenderManager, 
        // ListItemBase,
        Renderer,
        //  MessageListItem,
        Text,
        //   Avatar,
        markedImport,
        showdownImport) {
        return Renderer.extend("report.control.ChatMessageListItemRenderer", {
            renderLIContent: function (rm, control) {
                rm.openStart("div").class("sapMMessageListItem").openEnd();
                rm.openStart("div").class("sapMMessageListItemText").openEnd();
                rm.unsafeHtml(this.markdownToHtml(control.getMessage()));
                rm.close("div");

                rm.openStart("div").class("sapMMessageListItemHeader").openEnd();
                rm.renderControl(control.getAggregation("avatar"));

                rm.openStart("div").class("sapMMessageListItemInfo").openEnd();
                rm.renderControl(new Text({ text: control.getSender() }));
                rm.renderControl(new Text({ text: "|" }));
                rm.renderControl(new Text({ text: control.getDate() }));
                rm.openStart("div").class("customMessageListItemRightAlign").openEnd();
                rm.renderControl(control.getAggregation("adopt"));
                rm.renderControl(control.getAggregation("display"));
                rm.close("div");
                rm.close("div");


                rm.close("div");
                rm.close("div");
            },
            markdownToHtml: function (text) {
                // const converter = new showdown.Converter({
                // extensions: [
                // showdownHighlight({
                // pre: true,
                // auto_detection: true,
                // }),
                // ],
                // });
                // converter.setFlavor("github");
                // return converter.makeHtml(text);
                return marked.parse(text);
                // return new showdown.Converter().makeHtml(text);

            }
        });
    });