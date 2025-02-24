sap.ui.define([
    "sap/m/MessageToast",
    "report/util/Helper",
    "report/service/ChatService",
    "report/service/NewMessageHandler",
    "report/util/UIHelper",
], function (MessageToast, Helper, ChatService, NewMessageHandler, UIHelper) {
    'use strict';

    return {
        onPressAIConversation: function (oEvent) {
            this.pDialog ??= this.loadFragment({
                name: "report.ext.AIConversation.fragment.AIConversation"
            });

            const that = this;

            this.pDialog.then((oDialog) => {
                that._dialog = oDialog;
                // oDialog.getContent()[0].getFooter().getContent()[0].attachPost(that.onPostMessage.bind(that));
                oDialog.open();
            });

            this.onAIConversationClose = function (oEvent) {
                this.pDialog.then((oDialog) => oDialog.close());
            };

            this.onPostMessage = function (event) {
                if (!event.getParameter("value")) {
                    return;
                }
                const message = event.getParameter("value");
                const report = this.getEditFlow().getView().getBindingContext();
                const messageList = this._dialog.getContent()[0].getContent()[0].getItems()[0];
                const binding = messageList.getBinding("items");

                const userModel = this.getEditFlow().getView().getModel("user");

                const messageHandler = new NewMessageHandler({
                    report: report,
                    binding: binding,
                    message: message,
                    sender: userModel.getUser().displayName,
                    streamingCallback: function (chunk, replyContext) {
                        if (!chunk) return;
                        replyContext.setProperty("text", `${replyContext.getProperty("text")}${chunk}`);
                        const listEndMarker = this._dialog.getContent()[0].getContent()[0].getItems()[1];
                        UIHelper.scrollToElement(listEndMarker.getDomRef());
                    }.bind(this)
                });

                messageHandler.createMessageAndCompletion();
            };

            this.onBtnAdoptPress = function (event) {
                event.getSource().setBusy(true);
                var context = event.getSource().getBindingContext();
                var contextBinding = this.getEditFlow().getView().getModel().bindContext("ChatService.adopt(...)", context, { $$inheritExpandSelect: true });
                contextBinding.invoke().finally(() => {
                    event.getSource().setBusy(false);
                    this.getEditFlow().getView().getBindingContext().refresh();
                });
            };

            this.onPressSyncChangesToChatList = function (event) {
                const binding = this.getEditFlow().getView().getModel().bindContext("ChatService.appendToChatRecord(...)",
                    this.getEditFlow().getView().getBindingContext()
                    // { $$inheritExpandSelect: true }
                );
                binding.invoke().then(() => {
                    this.getEditFlow().getView().getBindingContext().refresh();
                });
            };
        }
    };
});
